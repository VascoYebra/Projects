package com.example.talk2me.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.talk2me.R;
import com.example.talk2me.adapter.MessageAdapter;
import com.example.talk2me.config.FirebaseConfig;
import com.example.talk2me.helper.Base64Custom;
import com.example.talk2me.helper.Preferencias;
import com.example.talk2me.model.Chat;
import com.example.talk2me.model.MessageToSend;
import com.example.talk2me.model.Upload;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EventListener;
import java.util.List;
import java.util.SortedMap;

public class ChatActivity extends AppCompatActivity {

    public static boolean isChatActivityRunning;

    private Toolbar toolbar;
    private EditText editMensagem;
    private ImageButton btMensagem;
    private DatabaseReference firebase;
    private ListView listView;
    private ArrayList<MessageToSend> mensagens;
    private ArrayAdapter<MessageToSend> adapter;
    private ValueEventListener valueEventListenerMessage;
    // para as imagens
    private ImageButton bt_add_imagem;
    private ImageView imageViewToERASE;
    private int PICK_IMAGE_REQUEST = 1;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;
    private Uri mImgUri;
    //user destino
    private String nameDestinyReceiver;
    private String idUserDestinyReceiver;
    //user original, que envia
    private String idUserSender;
    private String nomeUserSender;
    //para o microfone e audio
    private ImageButton micButton;
    private MediaRecorder recorder;
    private String fileName = null;
    private StorageReference audioStorageRef;
    private DatabaseReference audioDatabaseRef;
    private ProgressDialog mProgress;
    private static final String LOG_TAG = "Record_log";
    private String lastAudioUrl;
    //flag de VIP
    private DatabaseReference databaseReferenceVIP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = findViewById(R.id.tb_conversa);
        editMensagem = findViewById(R.id.edit_mensagem);
        btMensagem = findViewById(R.id.bt_send);
        listView = findViewById(R.id.lv_conversas);
        bt_add_imagem = findViewById(R.id.bt_add_image);
        imageViewToERASE = findViewById(R.id.overwriteMe);
        //para o microfone:
        micButton = findViewById(R.id.bt_mic);
        //ver se o user é VIP ou não:

        Preferencias preferencias = new Preferencias(ChatActivity.this);
        idUserSender = preferencias.getIndentifier();
        nomeUserSender = preferencias.getNome();        //tenho o nome do sender ! finalmente

        databaseReferenceVIP = FirebaseDatabase.getInstance().getReference("VIPUsers");
        databaseReferenceVIP.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Toast.makeText(ChatActivity.this, "valor dentro --->"+snapshot.child(idUserSender).getValue(), Toast.LENGTH_LONG).show();
                if(snapshot.child(idUserSender).getValue() != null){
                    if( snapshot.child(idUserSender).getValue().toString().equals("true")){
                        micButton.setVisibility(View.VISIBLE);
                    }
                    else{
                        micButton.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        fileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        fileName += "/"+System.currentTimeMillis();      //para mudar aqui a extensaom tenho que mudar em baixo, nos metodos no record audio também!
        mProgress = new ProgressDialog(this);           //bara circular de progresso para me dizer quando e que o upload do ficheiro vai estar concluido
        isChatActivityRunning = true;

        Bundle extra = getIntent().getExtras();            //nesta variavel tenho os extras que guardei da activity ContactosFragment como o nome e o email
        if(extra != null){
            nameDestinyReceiver = extra.getString("nome");
            String emailDestiny = extra.getString("email");
            idUserDestinyReceiver = Base64Custom.codeBase64(emailDestiny);      //este id e o email mas codificado em base 64 porque o firebase só aceita isso RIP
        }

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");        //Controla onde guardo as imagens na REALTIME DATABASE!!!
        audioStorageRef = FirebaseStorage.getInstance().getReference("Audio");         //para o audio
        audioDatabaseRef = FirebaseDatabase.getInstance().getReference("Audio");

        //configuracao geral da toolbar, imagem, etc...
        toolbar.setTitle(nameDestinyReceiver);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);
        //fazer a listView das mensagens e adapter
        mensagens = new ArrayList<>();
        //adapater custom, como fiz para a lista tenho que fazer um adapter custom....F
        /*
        adapter = new ArrayAdapter(
                ChatActivity.this,
                android.R.layout.simple_list_item_1,
                texts
        );*/
        adapter = new MessageAdapter(ChatActivity.this, mensagens);
        listView.setAdapter(adapter);

        //sacar as mensagens do Firebase
        //firebase = FirebaseConfig.getFirebase().child("messages").child(idUserSender).child(idUserDestinyReceiver);     //tenho todas as mensagens trocadas entre Sender e Receiver aqui
        firebase = FirebaseConfig.getFirebase().child("messages").child(idUserDestinyReceiver).child(idUserSender);
        //um listener para as mensagens
        valueEventListenerMessage = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mensagens.clear();      //AI O QUE NAO VALE ESTA LINHA! SEM ISTO FICO COM O CHAT DUPLICADO RIP IN PEACE
                for(DataSnapshot data : snapshot.getChildren()){
                    MessageToSend mensagem = data.getValue(MessageToSend.class);         //
                    mensagens.add( mensagem );
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        firebase.addValueEventListener(valueEventListenerMessage);

        //enviar mensagem
        btMensagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textoMensagem = editMensagem.getText().toString();

                if(textoMensagem.isEmpty()){
                    //se a mensagem estiver vazia mando um aviso a dizer para o user enviar alguma mensagem !
                    Toast.makeText(ChatActivity.this, "Type a message to send", Toast.LENGTH_LONG).show();
                }
                else{
                    //se for diferente de null, e que gravo no firebase!
                    MessageToSend message = new MessageToSend();
                    message.setIdUser(idUserSender);
                    message.setMessage(textoMensagem);
                    message.setTime(getCurrentTime());
                    listView.setSelection(adapter.getCount()-1);      //para ir automaticamente para a ultima mensagem

                    //guardo a mensagem para o enviador:
                    Boolean returnMessageSender = saveMessageFirebase(idUserSender, idUserDestinyReceiver, message);
                    if(!returnMessageSender){
                        Toast.makeText(ChatActivity.this, "Error sending message, try again", Toast.LENGTH_LONG).show();
                    }else{
                        //guardo tambem a mensagem para o que recebe!
                        Boolean returnMessageDestiny = saveMessageFirebase(idUserDestinyReceiver, idUserSender, message);
                        if(!returnMessageDestiny){
                            Toast.makeText(ChatActivity.this, "Error receiving message, try again", Toast.LENGTH_LONG).show();
                        }
                    }

                    //gravar os dados no sender,
                    Chat conversa = new Chat();
                    conversa.setIdUser(idUserDestinyReceiver);
                    conversa.setName(nameDestinyReceiver);
                    conversa.setMessage(textoMensagem);
                    conversa.setTime(getCurrentTime());
                    listView.setSelection(adapter.getCount()-1);      //para ir automaticamente para a ultima mensagem

                    Boolean saveConversaBoolean = saveConversa(idUserSender, idUserDestinyReceiver, conversa);
                    if( !saveConversaBoolean){
                        //caso falhe o save da conversa no sender:
                        Toast.makeText(ChatActivity.this, "Error saving Chat, try again", Toast.LENGTH_LONG).show();
                    }else{
                        //senao falhar esta tudo OK (menos a minha sanidade mental...) e posso gravar no destinatario
                        conversa = new Chat();
                        conversa.setIdUser(idUserSender);       //TINHA UM BUG AQUI.... FOI UMA TARDE INTEIRA !!!!!!!! PLEASE
                        conversa.setName(nomeUserSender);
                        conversa.setMessage(textoMensagem);
                        conversa.setTime(getCurrentTime());
                        listView.setSelection(adapter.getCount()-1);      //para ir automaticamente para a ultima mensagem


                        Boolean returnConversaDestinatario = saveConversa(idUserDestinyReceiver, idUserSender, conversa);
                        if(!returnConversaDestinatario ){
                            Toast.makeText(ChatActivity.this, "Error saving this chat to sender, try again", Toast.LENGTH_LONG).show();

                        }

                    }

                    //tenho que gravar tambem no user destino

                    editMensagem.setText("");       //depois de enviar uma mensagem, pohno  a string a ""
                    //vou ter esta estrutura no firebase para as mensagens:
                    /*messages
                        +vasco.cruz97@gmail.com     //este user enviou mensagem para os dois de baixo:
                            +naruto@gmail.com
                                +01mensagem             //cada mensagem tem que ter um genero de ID para as distinguir caso contrario o no fica com o mesmo nome e o firebase mantem o no e cria um filho em baixo
                                +02mensagem             //se enviar 100 mensagems tenho 100 filhos em que cada um e filho doutro....nao pode ser...tenho que user PUSH
                                +03mensagem
                            +mariana@gmail.com
                                +mensagens
                     */

                }
            }
        });

        bt_add_imagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //fazer o file chooser, o UI para escolher o ficheiro
                Intent intent = new Intent();        //para abrir as imagens
                intent.setType("image/*");          //vejo ficheiros do tipo imagens, png e jpeg
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });


        micButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN){       //carregou, pressionou para baixo
                    startRecording();
                    Toast.makeText(ChatActivity.this, "Recording Started!", Toast.LENGTH_LONG).show();
                }
                else if(event.getAction() == MotionEvent.ACTION_UP){    //se largar, e porque estive a carregar e ja acabei
                    stopRecording();
                    Toast.makeText(ChatActivity.this, "Recording Stopped!", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        
        askPermissions();       //audio and storage 

    }

    private void askPermissions() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //Toast.makeText(ChatActivity.this, "Permissions Granted!", Toast.LENGTH_SHORT).show();
                Log.i(String.valueOf(ChatActivity.this), "Permissions granted!");
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(ChatActivity.this, "RIP....you refused the permissions...sad :(", Toast.LENGTH_LONG).show();
            }
        };

        //  https://github.com/ParkSangGwon/TedPermission       broken! bem mais facil fazer assim
        TedPermission.with(ChatActivity.this)
                .setPermissionListener(permissionListener)
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.INTERNET, Manifest.permission.SEND_SMS)
                .check();

    }

    //MIC RECORDING START
    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);     //extensao do ficheiro, se mudar para MP3 tenho que mexer aqui tambem. para mp3 preciso de um coder e de um decoder...isso nao e free...RIP
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed. stack trace de erro-->"+e.getStackTrace() );
            Log.e(LOG_TAG, "prepare() failed. mensagem de erro-->"+e.getMessage() );
        }

        recorder.start();
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;

        uploadAudio();
    }

    private void uploadAudio() {
        mProgress.setMessage("Uploading Audio....");
        mProgress.show();

        Log.i(String.valueOf(ChatActivity.this), "filename(path)-> "+fileName);
        //este getAbsolutePath para o filename da uma string um bocado chata para gravar na firebase storage...vou corta-la de modo a ficar so mesmo o nome do ficheiro audio
        //StorageReference fileReference = mStorageRef.child(idUserDestinyReceiver).child(System.currentTimeMillis() + "."+ getFileExtension(mImgUri));
        StorageReference filepath = audioStorageRef.child(idUserDestinyReceiver).child(System.currentTimeMillis()
                + ".3gp" );        //ponho numa pasta com o user do destino. extensão 3gp :(

        Uri uri = Uri.fromFile(new File(fileName));
        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                //*******************************************************************************************************
                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!urlTask.isSuccessful());
                Uri downloadUrl = urlTask.getResult();

                //Log.d(TAG, "onSuccess: firebase download url: " + downloadUrl.toString()); //use if testing...don't need this line.
                Upload upload = new Upload("Received From: "+Base64Custom.decodeBase64(idUserSender),downloadUrl.toString());
                Log.i(String.valueOf(ChatActivity.this), "onSuccess: firebase download url: " + downloadUrl.toString()); //use if testing...don't need this line.
                /*
                lastAudioUrl = downloadUrl.toString();
                savePreferences();

                 */

                String uploadId = audioDatabaseRef.push().getKey();
                audioDatabaseRef.child(idUserDestinyReceiver).child(uploadId).setValue(upload);         //VAI BUSCAR A REAL TIME DATABASE.Ver estrutura em baixo
                //*******************************************************************************************************

                mProgress.dismiss();
                Toast.makeText(ChatActivity.this, "Uploading Finished!", Toast.LENGTH_LONG).show();
            }
        });
    }

    //MIC RECORDING STOP

    private void savePreferences(){
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lastAudioURL", lastAudioUrl);
        editor.commit();

    }


    @Override
    //isto e chamado quando recebo o ficheiro, quando escolho a pic
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST ){
            if(resultCode == RESULT_OK){
                if(data != null && data.getData() != null){
                    mImgUri = data.getData();            //contem o Uri da imagem
                    Log.i(String.valueOf(ChatActivity.this), "imgUri-->"+mImgUri, null);
                    imageViewToERASE.setImageURI(mImgUri);       //WORKS !!!!!!!!!
                    if(mUploadTask != null && mUploadTask.isInProgress()) {
                        //se tiver um upload em curso nao faço upload logo a seguir! so quando acabar
                        Toast.makeText(ChatActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                    }else{
                        uploadFile();
                    }
                }
            }
        }
    }

    private String getFileExtension (Uri uri){
        //retorna a extensao do ficheiro
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));

    }

    private void uploadFile() {
        //garanto que escolho uma imagem
        if(mImgUri != null){                    //Mudo este mudo a entrada na REALTIME DATABASE
            StorageReference fileReference = mStorageRef.child(idUserDestinyReceiver).child(System.currentTimeMillis()
                    + "."+ getFileExtension(mImgUri));                              //isto cria o nome do ficheiro com o tempo em que foi enviado!|! isto esta a escrever no uploads. fica tipo uploads/132167132.jpeg

            mUploadTask = fileReference.putFile(mImgUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(ChatActivity.this, "Upload Successful", Toast.LENGTH_LONG).show();

                            /*
                            Upload upload = new Upload("UploadedImage", taskSnapshot.getUploadSessionUri().toString());     //ERRO PODE ESTAR AQUI o getDownloadURi esta obsoleto :(

                            //**********************************************
                            //tirar comments se for preciso
                             //*********************************************

                            String uploadId = mDatabaseRef.push().getKey();     //cria um novo nó na database
                            mDatabaseRef.child(uploadId).setValue(upload);      //pego neste com o meu novo upload
                            */


                            //------------------TESTE 22 Agosto-------------------
                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful());
                            Uri downloadUrl = urlTask.getResult();

                            //Log.d(TAG, "onSuccess: firebase download url: " + downloadUrl.toString()); //use if testing...don't need this line.
                            Upload upload = new Upload("Received From: "+Base64Custom.decodeBase64(idUserSender),downloadUrl.toString());

                            String uploadId = mDatabaseRef.push().getKey();
                            mDatabaseRef.child(idUserDestinyReceiver).child(uploadId).setValue(upload);         //VAI BUSCAR A REAL TIME DATABASE.Ver estrutura em baixo

                            /*
                            vasco.cruz97@gmail.com
                                +naruto@gmail.com
                                    +imagem
                                    +imagem
                                +sasuke@gmail.com
                                    +imagem
                             */

                            //------------------FIM DO TESTE, descomentar os /* para ficar com a funcionalidade anterior

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            //se quiser por uma progress bar
                            /*
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getBytesTransferred() );
                            mProgressBar.setProgress((int)progress);
                             */
                        }
                    });
        }else{
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void PutImageInStorage(){

    }

    public String getCurrentTime(){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return formatter.format(date);
    }


    private boolean saveMessageFirebase(String idSender, String idDestiny, MessageToSend message) {     //idSender e o email de quem envia, idDestiny o email de quem recebe;
        try {
            firebase = FirebaseConfig.getFirebase().child("messages");
            firebase.child(idSender).child(idDestiny).push().setValue(message);         //ler o codigo com a estrutura de cima! este push é um life saver !! ALL HAIL THE MIGHTY PUUUUUUUUUSH
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


    private Boolean saveConversa(String idSender, String idDestiny, Chat conversaIndividual){
        try {
            firebase = FirebaseConfig.getFirebase().child("chats");
            firebase.child(idSender).child(idDestiny).setValue(conversaIndividual);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        isChatActivityRunning = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isChatActivityRunning = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isChatActivityRunning = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isChatActivityRunning = false;
        firebase.removeEventListener(valueEventListenerMessage);        //remover o listener, poupa bateria
    }

}






















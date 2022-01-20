package com.example.talk2me.activity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.talk2me.R;
import com.example.talk2me.config.FirebaseConfig;
import com.example.talk2me.helper.Base64Custom;
import com.example.talk2me.helper.Preferencias;
import com.example.talk2me.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.EventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private TextView passwordForgot;
    private Button SigninBT;
    private User user;
    private FirebaseAuth autenticacao;
    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerUser;
    private String loggedUserIndentifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //checkIfUserisLoggedIn();

        email = findViewById(R.id.editEmailAddressLogin);
        password = findViewById(R.id.editPasswordLogin);
        SigninBT = findViewById(R.id.bt_login);
        passwordForgot = findViewById(R.id.forgotPassword);

        SigninBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = new User();
                user.setEmail(email.getText().toString() );
                user.setPassword(password.getText().toString() );
                verifyLogin();
            }
        });

        passwordForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetMail = new EditText(v.getContext());
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder( v.getContext() );
                passwordResetDialog.setTitle("Password Reset");
                passwordResetDialog.setMessage("Enter your Email to receive the Password Reset Link");
                passwordResetDialog.setView(resetMail);
                passwordResetDialog.setPositiveButton("Reset my Password", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String emailExtracted = resetMail.getText().toString();     //email que o user pos no alertDialog
                        FirebaseConfig.getFirebaseAuth().sendPasswordResetEmail(emailExtracted).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(LoginActivity.this, "Reset email has been sent! Also check your spam fodler...", Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this, "Error sending the Link to the email...", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
                passwordResetDialog.setNegativeButton("Changed my mind", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                passwordResetDialog.create().show();

            }
        });

    }

    private void checkIfUserisLoggedIn() {
        autenticacao = FirebaseConfig.getFirebaseAuth();
        if(autenticacao.getCurrentUser() != null){
            //se o meu utilizador ja estiver loggado, passo logo para o main screen inves de estar sempre a pedir para ele introduzir os dados
            openMainScreen();
        }
    }

    private void verifyLogin() {
        //quando me registo não passo por aqui
        autenticacao = FirebaseConfig.getFirebaseAuth();
        autenticacao.signInWithEmailAndPassword(
                user.getEmail(),
                user.getPassword()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {

                    FirebaseUser userDiff = FirebaseConfig.getFirebaseAuth().getCurrentUser();
                    if (!userDiff.isEmailVerified()) {
                        Toast.makeText(LoginActivity.this, "Email isnt verified...Check your spam folder...", Toast.LENGTH_LONG).show();
                    }
                    else if (userDiff.isEmailVerified()) {
                        loggedUserIndentifier = Base64Custom.codeBase64(user.getEmail());
                        //Log.i(String.valueOf(this), "user.getEmail--> "+user.getEmail()+" LoggeduserIdentifier--> "+loggedUserIndentifier );

                        firebase = FirebaseConfig.getFirebase().child("users").child(loggedUserIndentifier);
                        valueEventListenerUser = new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                User userRecovered = snapshot.getValue(User.class);
                                Log.i(String.valueOf(this), "snapShotValue--> " + snapshot.getValue());  /// isto da onData Change, mas o snapShot em si

                                Preferencias preferencias = new Preferencias(LoginActivity.this);
                                preferencias.saveData(loggedUserIndentifier, userRecovered.getNome());  //Changed! HARDCODED

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        };
                        firebase.addListenerForSingleValueEvent(valueEventListenerUser);
                        //se a autenticação for bem bem sucedida, posso ir para a proxima actividade
                        openMainScreen();
                        Toast.makeText(LoginActivity.this, "Log in was sucessfull!", Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(LoginActivity.this, "Error Logging in... ", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }

    private void openMainScreen() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("emailJustInCase", Base64Custom.codeBase64(email.getText().toString()) );
        //Log.i(String.valueOf(this), "hopeThereEmailHere LogIN SEND----> "+Base64Custom.codeBase64(email.getText().toString()) );        //WORKS!
        startActivity(intent);
        finish();
    }

    public void openUserRegisterActivity(View v){       //metodos chamado pelo onClick do texto de criar conta
        Intent intent = new Intent(LoginActivity.this, RegisterUser.class);
        startActivity(intent);
    }

}






























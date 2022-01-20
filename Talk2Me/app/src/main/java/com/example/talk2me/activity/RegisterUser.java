package com.example.talk2me.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.talk2me.R;
import com.example.talk2me.config.FirebaseConfig;
import com.example.talk2me.helper.Base64Custom;
import com.example.talk2me.helper.Preferencias;
import com.example.talk2me.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class RegisterUser extends AppCompatActivity {
    private EditText nome;
    private EditText email;
    private EditText password;
    private Button btSignin;
    private User user;
    //aimation:

    private FirebaseAuth firebaseAuth;
    private DatabaseReference firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        nome = findViewById(R.id.editTextTextPersonName);
        email = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);
        btSignin = findViewById(R.id.button_singin);

        btSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = new User();
                user.setNome(nome.getText().toString());
                user.setEmail(email.getText().toString());
                user.setPassword(password.getText().toString());

                registaUtilizador();
            }
        });

        //para a animacao!!! bem louco fazer animacoes ! :D
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim);
        findViewById(R.id.attackVirusImg).startAnimation(animation);

    }


    private void registaUtilizador() {
        firebaseAuth = FirebaseConfig.getFirebaseAuth();
        firebaseAuth.createUserWithEmailAndPassword(
                user.getEmail(),
                user.getPassword()
        ).addOnCompleteListener(RegisterUser.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful() ){
                    Toast.makeText(RegisterUser.this, "User Successfully Registered!", Toast.LENGTH_LONG).show();
                    //cenas de mandar o email de VERIFICACAO:
                    FirebaseUser userDiff = FirebaseConfig.getFirebaseAuth().getCurrentUser();
                    userDiff.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(RegisterUser.this, "Email Verification has been sent", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterUser.this, "Error sending verification email...please try again..."+e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                    // fim do verify email
                    FirebaseUser userFirebase = task.getResult().getUser();

                    String indentifierUser = Base64Custom.codeBase64(user.getEmail() );         //vou guardar o email na firebase como base64 pois ele nao deixa em plain text :(
                    user.setId( indentifierUser );
                    user.save();

                    Preferencias preferencias = new Preferencias(RegisterUser.this);
                    preferencias.saveData(indentifierUser, user.getNome() );

                    openLoginUser();
                }
                else{
                    String errorMessage = "";
                    try {
                        throw task.getException();          //vou lancar a excepção para depois dar uma mensagem de erro mais precisa, se ja estiver registado, password demasiado fraca...
                    } catch (FirebaseAuthWeakPasswordException e){
                        errorMessage = "Choose a Stronger Password. Longer and/or with uppercaseLetters and special symbols";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        errorMessage = "Invalid Email...make sure your email is correct...";
                    }
                    catch (FirebaseAuthUserCollisionException e){
                        errorMessage = "Email adress already registered";
                    }
                    catch (Exception e) {
                        errorMessage = "Error registering user";
                        e.printStackTrace();
                    }
                    Toast.makeText(RegisterUser.this, "Error: "+errorMessage, Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void openLoginUser() {
        Intent intent = new Intent(RegisterUser.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}































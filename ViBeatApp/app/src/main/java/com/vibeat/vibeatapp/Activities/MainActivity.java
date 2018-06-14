package com.vibeat.vibeatapp.Activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.vibeat.vibeatapp.Managers.AuthenticationManager;
import com.vibeat.vibeatapp.Managers.ClientManager;
import com.vibeat.vibeatapp.Managers.DBManager;
import com.vibeat.vibeatapp.Managers.GUIManager;
import com.vibeat.vibeatapp.MyApplication;
import com.vibeat.vibeatapp.R;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    public MyApplication app;

    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        app = (MyApplication) this.getApplication();

        DBManager.startDBManager();
        app.gui_manager = new GUIManager(MainActivity.this, (List<Adapter>) null);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION},
                0);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Check if a user has already signed in with google
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        Log.d("after auth", "onCreate:11111 ");
        SignInButton signInButton = findViewById(R.id.googleLogin);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 123);
                }
                });

        if  (account != null && app.client_manager == null) {
            app.client_manager = new ClientManager(AuthenticationManager.getGoogleUserFromAccount(account), app);
            app.semaphore.release();
            try {
                Log.d("Test8", "before acquire");
                app.semaphoreSender.acquire();
                Log.d("Test8", "after acquire");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            app.gui_manager.login();
            Log.d("if authentication null", "3333333");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 123) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                app.client_manager = new ClientManager(AuthenticationManager.getGoogleUserFromAccount(account), app);
                app.semaphore.release();
                try {
                    Log.d("Test8", "before acquire");
                    app.semaphoreSender.acquire();
                    Log.d("Test8", "after acquire");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                app.gui_manager.login();


            } catch (ApiException e) {
                Log.w("TAG", "signInResult:failed code=" + e.getStatusCode());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed(){
        Intent homeIntene = new Intent(Intent.ACTION_MAIN);
        homeIntene.addCategory(Intent.CATEGORY_HOME);
        homeIntene.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntene);
    }
}
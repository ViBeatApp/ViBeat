package com.vibeat.vibeatapp.Activities;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.vibeat.vibeatapp.HelperClasses.AuthenticationManager;
import com.vibeat.vibeatapp.HelperClasses.ClientManager;
import com.vibeat.vibeatapp.HelperClasses.ListenerThread;
import com.vibeat.vibeatapp.HelperClasses.MediaPlayerManager;
import com.vibeat.vibeatapp.MyApplication;
import com.vibeat.vibeatapp.Objects.User;
import com.vibeat.vibeatapp.R;


public class MainActivity extends AppCompatActivity {

    public MyApplication app;

    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        app = (MyApplication) this.getApplication();

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
        if( account != null ){
            app.client_manager = new ClientManager(AuthenticationManager.getGoogleUserFromAccount(account), app);
            login();
        }

        SignInButton signInButton = findViewById(R.id.googleLogin);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                                                startActivityForResult(signInIntent, 123);
                                            }
                                        }
        );
    }

    public void login() {
        app.media_manager = new MediaPlayerManager();
        app.listener_thread = new ListenerThread(MainActivity.this);
        app.listener_thread.start();
        Intent intent = new Intent(this, EnterPartyActivity.class);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 123) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                app.client_manager = new ClientManager(AuthenticationManager.getGoogleUserFromAccount(account), app);
                login();

            } catch (ApiException e) {
                Log.w("TAG", "signInResult:failed code=" + e.getStatusCode());
            }
        }
    }
}
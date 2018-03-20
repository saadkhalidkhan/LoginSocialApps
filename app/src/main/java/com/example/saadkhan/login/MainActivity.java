package com.example.saadkhan.login;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private  EditText username, password;
    private Button signIn,logoutfb,gmailSignout;
    String userPattern;
    private LoginManager mLoginManager;
    private TextView textView;
    String passPattern;
    private ImageView fb, gmail;
    private GoogleSignInClient mGoogleSignInClient;
    private  int RC_SIGN_IN = 1;
    private String TAG = "Sign In";
    int blue;
    GoogleApiClient mGoogleApiClient ;
    AccessTokenTracker mAccessTokenTracker;
    CallbackManager mFacebookCallbackManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gmail_init();
        init();
        setupFacebookStuff();

        gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
                //handleFacebookLogin();
            }
        });
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleFacebookLogin();
            }
        });
        //loginButton.setReadPermissions(Arrays.asList(
          //      "public_profile", "email", "user_birthday", "user_friends"));
        signIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
               validation();
            }
        });
        logoutfb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLoginManager.logOut();
                logoutfb.setVisibility(View.GONE);
                fb.setVisibility(View.VISIBLE);
            }
        });

        gmailSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                 mGoogleApiClient.disconnect();
//                 mGoogleApiClient.connect();
                signOut();
                gmail.setVisibility(View.VISIBLE);
                gmailSignout.setVisibility(View.GONE);

            }
        });

    }

    public void init(){
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        fb = (ImageView) findViewById(R.id.fb);
        gmail = (ImageView) findViewById(R.id.gmail);
        signIn = (Button) findViewById(R.id.signin);
        logoutfb = (Button) findViewById(R.id.signout);
        gmailSignout = findViewById(R.id.signoutgmail);
        logoutfb.setVisibility(View.GONE);
        gmailSignout.setVisibility(View.GONE);
    }

    private void setupFacebookStuff() {

        // This should normally be on your application class
        FacebookSdk.sdkInitialize(getApplicationContext());

        mAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,AccessToken currentAccessToken) {
               // updateFacebookButtonUI();
            }
        };

        mLoginManager = LoginManager.getInstance();
        mFacebookCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mFacebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
//                updateFacebookButtonUI();
                Toast.makeText(getApplicationContext(),"Login Success",Toast.LENGTH_LONG).show();
                fb.setVisibility(View.INVISIBLE);
                blue = Color.BLUE;
                //logoutfb.setBackgroundColor(blue);
                logoutfb.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }


    private void handleFacebookLogin() {
        if (AccessToken.getCurrentAccessToken() != null){
            mLoginManager.logOut();
        }else{
            mAccessTokenTracker.startTracking();
            mLoginManager.logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile", "email", "user_birthday", "user_friends"));
        }

    }

    public void gmail_init(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void validation(){
        String email = username.getEditableText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z.]+";
        String pass = password.getEditableText().toString().trim();
        String PassPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";

        if (email.matches(emailPattern) && pass.matches(PassPattern))
        {
            Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
        }

        else if(username.getText().toString().equals(""))
            Toast.makeText(getApplicationContext(),"Enter User Name",Toast.LENGTH_LONG).show();

        else if(password.getText().toString().equals(""))
            Toast.makeText(getApplicationContext(),"Enter Password",Toast.LENGTH_LONG).show();

        else if (!email.matches(emailPattern) )
        {
            Toast.makeText(getApplicationContext(),"invalid email address",Toast.LENGTH_SHORT).show();
        }

        else if (!pass.matches(PassPattern) )
        {
            Toast.makeText(getApplicationContext(),"invalid Password",Toast.LENGTH_SHORT).show();
        }
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onStart() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
        super.onStart();

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            blue = Color.RED;
           // gmailSignout.setBackgroundColor(blue);
            gmail.setVisibility(View.GONE);
            gmailSignout.setVisibility(View.VISIBLE);
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        if(account!=null){

        }
        else
        {

        }
    }
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }
}

//Reference: https://github.com/medyo/custom-facebook-login-button
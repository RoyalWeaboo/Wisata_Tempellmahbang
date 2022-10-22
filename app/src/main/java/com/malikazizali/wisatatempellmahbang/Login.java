package com.malikazizali.wisatatempellmahbang;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.malikazizali.wisatatempellmahbang.api.ServerApi;

public class Login extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private String username, password;
    private Button btn_login;
    private TextView register;
    private ImageView google;
    private LoginButton fb;

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = password = "";
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btn_login = findViewById(R.id.btn_login);
        register = findViewById(R.id.register);
        google = findViewById(R.id.login_google);
        fb = findViewById(R.id.login_button);

        //google login
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();


        //facebook login
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        fb.setReadPermissions("email");
        // Callback registration
        fb.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = etUsername.getText().toString().trim();
                password = etPassword.getText().toString().trim();
                if(!username.equals("") && !password.equals("")){
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerApi.URL_Login, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("res", response);
                            if (response.equals("success")) {
                                Intent intent = new Intent(Login.this, Dashboard.class);
                                intent.putExtra("username", username);
                                startActivity(intent);
                                finish();
                            } else if (response.equals("failure")) {
                                Toast.makeText(Login.this, "Username/Password tidak terdaftar", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(Login.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> data = new HashMap<>();
                            data.put("username", username);
                            data.put("password", password);
                            return data;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    requestQueue.add(stringRequest);
                }else{
                    Toast.makeText(Login.this, "Username dan password tidak terdaftar", Toast.LENGTH_SHORT).show();
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
                finish();
            }
        });
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            Exception exception = task.getException();
            if(task.isSuccessful()){
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    Log.v("Error", "Sign In Failed code=" + e.getStatusCode());
                }
            }
            else {
                Log.d("Sign In Google", exception.toString());
            }

        }

        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("Sign In Google", "signInWithCredential:success");
                            startActivity(new Intent(Login.this, Dashboard.class));
                        } else {
                            Log.w("Sign In Google", "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(Login.this, Dashboard.class));
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
    }

}
package com.sisiame.parseinstagrammpk;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";

    private EditText etUsername, etPassword;
    private Button btnLogin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.username);
        etPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.login);

        /*
        could be converted to lambda
        btnLogin.setOnClickListener(v -> {
            Log.i(TAG, "onClick Login Button");
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();
            loginUser(username, password);
        });
         */

        checkLoggedUser();

    }

    private void checkLoggedUser() {

        if(ParseUser.getCurrentUser() != null) {
            goToMainActivity();
        } else {
            btnLogin.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Log.i(TAG, "onClick Login Button");
                    String username = etUsername.getText().toString();
                    String password = etPassword.getText().toString();
                    loginUser(username, password);
                }

            });
        }

    }

    private void loginUser(String username, String password) {
        Log.i(TAG, "Attempting to sign in user " + username);

        /*
        can be replaced with lambda
         */
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(e != null) {
                    Log.e(TAG, "Error signing in", e);
                    Toast.makeText(LoginActivity.this, "Sign in failed", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.i(TAG, "Sign in successful");
                goToMainActivity();
                Toast.makeText(LoginActivity.this, "Sign in successful", Toast.LENGTH_SHORT).show();
                
            }
        });
    }

    private void goToMainActivity() {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }
}
package com.littlestudio.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.littlestudio.R;
import com.littlestudio.data.datasource.UserLocalDataSource;
import com.littlestudio.data.datasource.UserRemoteDataSource;
import com.littlestudio.data.dto.UserLoginRequestDto;
import com.littlestudio.data.repository.UserRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    String usernameToStr;
    String passwordToStr;
    UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView username = (TextView) findViewById(R.id.id);
        TextView password = (TextView) findViewById(R.id.pw);

        userRepository = UserRepository.getInstance(
                UserRemoteDataSource.getInstance(),
                UserLocalDataSource.getInstance(getApplicationContext())
        );

        AppCompatButton loginBtn = (AppCompatButton) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameToStr = username.getText().toString();
                passwordToStr = password.getText().toString();
                Log.d("IDPW sent", " ID: " + usernameToStr + " PW:" + passwordToStr);
                userLogin(usernameToStr, passwordToStr);

                Log.d("test", "working?");
/*
                if (username.getText().toString().equals("admin") && password.getText().toString().equals("admin")) {
                    //correct
                    Toast.makeText(LoginActivity.this, "LOGIN SUCCESSFUL", Toast.LENGTH_SHORT).show();
                    //Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                    //startActivity(intent);

                } else
                    //incorrect
                    Toast.makeText(LoginActivity.this, "LOGIN FAILED !!!", Toast.LENGTH_SHORT).show();
            }

        });
  */

                //Send user to Sign-up Page
            }
        });
        TextView signUpLink = (TextView) findViewById(R.id.signUpLink);
        signUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                    startActivity(intent);

                }
            }

        });
    }

    /*
    public void userLogin(String ID, String PW){
        userRepository.userCreateRequest(
                new UserLoginRequestDto(ID, PW), new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        Log.d("TETE success", response.body().toString());
                    }
                    @Override
                    public void onFailure(Call call, Throwable t) {
                        Log.e("TETE error", t.toString());
                    }
                }

                //full_name username password gender type
        );
    }
*/
    private void userLogin(String ID, String PW) {
        Intent intent = new Intent(this, MainActivity.class);
        userRepository.login(
                new UserLoginRequestDto(ID, PW), new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        if (response.isSuccessful() && response.body() != null) {
                            // Handle successful login here
                            Log.d("TETE success", response.body().toString());
                            startActivity(intent);
                            // You might want to parse the response body and retrieve the login token or user details
                        } else {
                            // Handle unsuccessful login attempt here, maybe due to incorrect credentials or other issues
                            Log.e("TETE error here", response.message() + " ID: " + ID + " PW:" + PW);
                        }
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {
                        // Handle failure to communicate with the server or other errors here
                        Log.e("TETE failure", t.toString());
                    }
                }
        );
    }


}
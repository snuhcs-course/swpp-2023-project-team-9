package com.littlestudio.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.littlestudio.R;
import com.littlestudio.data.datasource.UserLocalDataSource;
import com.littlestudio.data.datasource.UserRemoteDataSource;
import com.littlestudio.data.dto.UserLoginRequestDto;
import com.littlestudio.data.repository.UserRepository;
import com.littlestudio.ui.constant.ErrorMessage;

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
                userLogin(usernameToStr, passwordToStr);
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

    private void userLogin(String ID, String PW) {
        Intent intent = new Intent(this, MainActivity.class);
        userRepository.login(
                new UserLoginRequestDto(ID, PW), new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {
                        Toast.makeText(getApplicationContext(), ErrorMessage.LOGIN, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }


}
package com.littlestudio.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.littlestudio.R;
import com.littlestudio.data.datasource.UserLocalDataSource;
import com.littlestudio.data.datasource.UserRemoteDataSource;
import com.littlestudio.data.dto.UserCreateRequestDto;
import com.littlestudio.data.repository.UserRepository;
import com.littlestudio.ui.constant.ErrorMessage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {
    //TODO: Username 중복 처리
    private RadioGroup radioFamilyGroup;
    private RadioGroup radioGenderGroup;
    private RadioButton radioFamilyButton;
    private RadioButton radioGenderButton;
    private String fullnameToStr;
    private String usernameToStr;
    private String passwordToStr;
    private String confirmPasswordToStr;
    private String genderToStr;
    private String familyToStr;
    private int inputState;
    private boolean isInputValid;
    private String inputCheckMessage;
    UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        // currently checking password >= 4 & password == confirmPassword,
        // len(fullname) < 20, len(username) < 15
        //TODO: Username 중복 처리

        AppCompatButton signupBtn = (AppCompatButton) findViewById(R.id.signupBtn);

        userRepository = UserRepository.getInstance(
                UserRemoteDataSource.getInstance(),
                UserLocalDataSource.getInstance(getApplicationContext())
        );
        userRadioInput();
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userInput();
                //collect User Input: fullname, username, pw, confirmpw, gender, family.
                inputValidate();
                if (isInputValid) {
                    userCreate(fullnameToStr, usernameToStr, passwordToStr, genderToStr, familyToStr);
                } else {
                    Toast.makeText(SignupActivity.this, inputCheckMessage, Toast.LENGTH_SHORT).show();
                }
            }
            //USER CREATE 조건 성공시 (with TOAST) or 실패시 에러 메세지 TOAST
        });


        TextView logInLink = (TextView) findViewById(R.id.logInLink);
        logInLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


    }

    private int checkInput_result(String fullname, String username, String pw, String pw_check) {
        if (pw.length() < 4) return 1;
        if (!pw.equals(pw_check)) return 2;
        if (fullname.length() > 20) return 3;
        if (username.length() > 15) return 4;
        return 0;
    }

    private String checkInput_message(int cases) {
        if (cases == 0) return "";
        if (cases == 1) return "PASSWORD LENGTH < 4";
        if (cases == 2) return "CONFIRM PASSWORD DO NOT MATCH";
        if (cases == 3) return "FULLNAME LENGTH > 20";
        if (cases == 4) return "USERNAME LENGTH > 15";
        return "";
    }

    private Boolean checkInput_valid(int cases) {
        return cases == 0;
    }

    private void userInput() {
        TextView fullname = (TextView) findViewById(R.id.name_signUp);
        TextView username = (TextView) findViewById(R.id.id_signUp);
        TextView password = (TextView) findViewById(R.id.pw_signUp);
        TextView confirmPassword = (TextView) findViewById(R.id.confirmPw);
        fullnameToStr = fullname.getText().toString();
        usernameToStr = username.getText().toString();
        passwordToStr = password.getText().toString();
        confirmPasswordToStr = confirmPassword.getText().toString();

    }

    private void userRadioInput() {

        radioGenderGroup = (RadioGroup) findViewById(R.id.checkGender);
        radioFamilyGroup = (RadioGroup) findViewById(R.id.parentOrChild);
        radioGenderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int selectedId_gender = radioGenderGroup.getCheckedRadioButtonId();
                radioGenderButton = (RadioButton) findViewById(selectedId_gender);
                genderToStr = radioGenderButton.getText().toString().toUpperCase();
            }
        });
        radioFamilyGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int selectedId_family = radioFamilyGroup.getCheckedRadioButtonId();
                radioFamilyButton = (RadioButton) findViewById(selectedId_family);
                familyToStr = radioFamilyButton.getText().toString().toUpperCase();
            }
        });
    }


    private void inputValidate() {
        inputState = checkInput_result(fullnameToStr, usernameToStr, passwordToStr, confirmPasswordToStr);
        inputCheckMessage = checkInput_message(inputState);
        isInputValid = checkInput_valid(inputState);
    }

    //TODO: Username 중복 처리
    private void userCreate(String full_name, String username, String password, String gender, String family) {
        userRepository.signup(
                new UserCreateRequestDto(full_name, username, password, gender, family), new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        Toast.makeText(SignupActivity.this, "Account successfully created", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {
                        Toast.makeText(getApplicationContext(), ErrorMessage.DEFAULT, Toast.LENGTH_SHORT).show();
                    }
                }

                //full_name username password gender type
        );

    }
    //CREATE USER
}

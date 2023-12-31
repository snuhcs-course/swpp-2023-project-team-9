package com.littlestudio.ui;

import android.os.Bundle;
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
    private boolean isInputValid;
    private String inputCheckMessage;
    UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

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
                validateInput();
                if (isInputValid) {
                    userCreate(fullnameToStr, usernameToStr, passwordToStr, genderToStr, familyToStr);
                } else {
                    Toast.makeText(SignupActivity.this, inputCheckMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });


        TextView logInLink = (TextView) findViewById(R.id.logInLink);
        logInLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private String checkInput_result(String fullname, String username, String pw, String pw_check, String gender, String type) {
        if (fullname.length() == 0 || username.length() == 0 || pw.length() == 0 || pw_check.length() == 0 || gender == null || type == null) return ErrorMessage.EMPTY_FIELDS;
        if (pw.length() < 4) return ErrorMessage.PASSWORD_LENGTH;
        if (!pw.equals(pw_check)) return ErrorMessage.PASSWORD_MISMATCH;
        if (fullname.length() > 20) return ErrorMessage.FULLNAME_LENGTH;
        if (username.length() > 15) return ErrorMessage.USERNAME_LENGTH;
        return "validated";
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


    private void validateInput() {
        inputCheckMessage = checkInput_result(fullnameToStr, usernameToStr, passwordToStr, confirmPasswordToStr, genderToStr, familyToStr);
        if (inputCheckMessage == "validated")
            isInputValid = true;
        else
            isInputValid = false;
    }

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
                        if (t.getMessage().equals("{\"error\":\"Username already taken\"}"))
                            Toast.makeText(getApplicationContext(), ErrorMessage.USERNAME_DUPLICATE, Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getApplicationContext(), ErrorMessage.DEFAULT, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}

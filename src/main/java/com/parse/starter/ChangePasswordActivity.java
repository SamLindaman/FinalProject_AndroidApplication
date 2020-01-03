package com.parse.starter;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {
        EditText passwordEditText;
        EditText newPasswordEditText;
        EditText verifyPasswordEditText;
        Button changePasswordButton;



        public void showUsers(){
                Intent intent = new Intent(getApplicationContext(), ShowUsersActivity.class);
                startActivity(intent);
        }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        changePasswordButton = (Button) findViewById(R.id.changePasswordButton);
        //changePasswordButton.setOnClickListener(this);
        ImageView iconimageView = (ImageView) findViewById(R.id.iconimageView);
        RelativeLayout changepasswordlayout = (RelativeLayout) findViewById(R.id.changepasswordlayout);
        changepasswordlayout.setOnClickListener(this);

        passwordEditText = (EditText)  findViewById(R.id.passwordEditText);
        newPasswordEditText = (EditText) findViewById(R.id.newpasswordEditText1);
        verifyPasswordEditText = (EditText) findViewById(R.id.newpasswordEditText2);
        verifyPasswordEditText.setOnKeyListener(this);

    }

        public void changePasswordClicked(View view) {

                if (passwordEditText.getText().toString().matches("") || newPasswordEditText.getText().toString().matches("")
                || verifyPasswordEditText.getText().toString().matches("")) {
                        Toast.makeText(this, "All fields are required.",Toast.LENGTH_SHORT).show();
                        Log.i("click","1");

                } else if(passwordEditText.getText().toString().matches(newPasswordEditText.getText().toString())){
                        Toast.makeText(this,"New password must be different from old password",Toast.LENGTH_SHORT).show();
                        Log.i("click","2");
                }else if(newPasswordEditText.getText().toString().matches(verifyPasswordEditText.getText().toString())) {
                        ParseUser currentUser = ParseUser.getCurrentUser();
                        currentUser.setPassword(newPasswordEditText.getText().toString());
                        currentUser.saveInBackground();
                        Toast.makeText(ChangePasswordActivity.this,"please wait a moment",Toast.LENGTH_SHORT).show();
                        ParseUser.logInInBackground(currentUser.getUsername().toString(), newPasswordEditText.getText().toString(), new LogInCallback() {
                                @Override
                                public void done(ParseUser user, ParseException e) {
                                        if (user != null) {
                                                Log.i("Login","ok!");
                                                showUsers();
                                        } else {
                                                Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                }
                        });
                        Log.i("click", "3");
                } else{
                        Toast.makeText(this,"Passwords do not match",Toast.LENGTH_SHORT).show();
                        Log.i("click","4");
                }
        }

        @Override
        public void onClick(View view) {

                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromInputMethod(getCurrentFocus().getWindowToken(),0);
        }

        @Override
        public boolean onKey(View view, int i, KeyEvent key) {
                if(i==KeyEvent.KEYCODE_ENTER && key.getAction()==KeyEvent.ACTION_DOWN){
                        changePasswordClicked(view);
                }
                return false;
        }

}

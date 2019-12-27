/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

  Boolean signUpModeActive = true;
  TextView loginTextView;
  Button signUpButton;
  EditText usernameEditText;
  EditText passwordEditText;

  @Override
  public void onClick(View view) {
    if (view.getId() == R.id.loginTextView) {

      //View signUpButton = (Button) findViewById(R.id.signUpButton);

      if (signUpModeActive) {
        signUpModeActive = false;
        signUpButton.setText("Login");
        loginTextView.setText("or, Sign Up");
      } else {
        signUpModeActive = true;
        signUpButton.setText("Sign Up");
        loginTextView.setText("or, Login");
      }

    } else if(view.getId()==R.id.Loginlayout||view.getId()==R.id.iconimageView){
      InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
      inputMethodManager.hideSoftInputFromInputMethod(getCurrentFocus().getWindowToken(),0);
    }
  }

  public void signUpClicked(View view) {

    if (usernameEditText.getText().toString().matches("") || passwordEditText.getText().toString().matches("")) {
      Toast.makeText(this, "A username and a password are required.",Toast.LENGTH_SHORT).show();

    } else {
      if (signUpModeActive) {
        ParseUser user = new ParseUser();
        user.setUsername(usernameEditText.getText().toString());
        user.setPassword(passwordEditText.getText().toString());

        user.signUpInBackground(new SignUpCallback() {
          @Override
          public void done(ParseException e) {
            if (e == null) {
              Log.i("Signup", "Success");
            } else {
              Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
          }
        });
      } else {
        // Login
        ParseUser.logInInBackground(usernameEditText.getText().toString(), passwordEditText.getText().toString(), new LogInCallback() {
          @Override
          public void done(ParseUser user, ParseException e) {
            if (user != null) {
              Log.i("Login","ok!");
            } else {
              Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
          }
        });
      }
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    loginTextView = (TextView) findViewById(R.id.loginTextView);
    loginTextView.setOnClickListener(this);
    signUpButton = (Button) findViewById(R.id.signUpButton);
    usernameEditText = (EditText) findViewById(R.id.usernameEditText);
    passwordEditText = (EditText) findViewById(R.id.passwordEditText);
    passwordEditText.setOnKeyListener(this);
    ImageView iconimageView = (ImageView) findViewById(R.id.iconimageView);
    RelativeLayout loginLayout = (RelativeLayout) findViewById(R.id.Loginlayout);
    loginLayout.setOnClickListener(this);
    iconimageView.setOnClickListener(this);

    ParseAnalytics.trackAppOpenedInBackground(getIntent());

  }

  @Override
  public boolean onKey(View view, int i, KeyEvent key) {
    if(i==KeyEvent.KEYCODE_ENTER && key.getAction()==KeyEvent.ACTION_DOWN){
      signUpClicked(view);
    }
    return false;
  }
}
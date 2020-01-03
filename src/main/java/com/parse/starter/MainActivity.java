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

  //if the user logs in successfully, this is called and the home activity is shown.
  public void showUsers(){
    Intent intent = new Intent(getApplicationContext(), ShowUsersActivity.class);
    startActivity(intent);
  }


  @Override
  public void onClick(View view) {
    //switches between login/signup views
    if (view.getId() == R.id.loginTextView) {
      if (signUpModeActive) {
        signUpModeActive = false;
        signUpButton.setText("Login");
        loginTextView.setText("or, Sign Up");
      } else {
        signUpModeActive = true;
        signUpButton.setText("Sign Up");
        loginTextView.setText("or, Login");
      }

    } // close they keyboard if the user clicks in the background
    else if(view.getId()==R.id.Loginlayout||view.getId()==R.id.iconimageView){
      InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
      inputMethodManager.hideSoftInputFromInputMethod(getCurrentFocus().getWindowToken(),0);
    }
  }

  //called if the signup button is clicked, and signup mode is active
  public void signUpClicked(View view) {
  // first checks if both username and password fields are not null
    if (usernameEditText.getText().toString().matches("") || passwordEditText.getText().toString().matches("")) {
      Toast.makeText(this, "A username and a password are required.",Toast.LENGTH_SHORT).show();

    } else {
      if (signUpModeActive) {
        //create new user with selected username and password in edittext fields
        ParseUser user = new ParseUser();
        user.setUsername(usernameEditText.getText().toString());
        user.setPassword(passwordEditText.getText().toString());

        //attempt signing the user up with parse server
        user.signUpInBackground(new SignUpCallback() {
          @Override
          public void done(ParseException e) {
            if (e == null) {
              Log.i("Signup", "Success");
              showUsers();
            } else {
              Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
          }
        });
      } else {
        // Login mode is active. attempt to log the user in by checking existing usernames and passwords in database
        ParseUser.logInInBackground(usernameEditText.getText().toString(), passwordEditText.getText().toString(), new LogInCallback() {
          @Override
          public void done(ParseUser user, ParseException e) {
            if (user != null) {
              Log.i("Login","ok!");
              showUsers();
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

    //set the used views to othe layout
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

    //if a user has previously signed in, and hasn't signed out, don't require them to sign in again
    if(ParseUser.getCurrentUser()!=null){
      showUsers();
    }
    //allows parse to view the project
    ParseAnalytics.trackAppOpenedInBackground(getIntent());

  }

  //if the enter key is pressed then the next field is selected
  @Override
  public boolean onKey(View view, int i, KeyEvent key) {
    if(i==KeyEvent.KEYCODE_ENTER && key.getAction()==KeyEvent.ACTION_DOWN){
      signUpClicked(view);
    }
    return false;
  }
}
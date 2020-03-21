package com.example.android_client;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class StartScreen extends AppCompatActivity {

  ImageView iw1;
  ImageView iw2;
  ImageView iw3;
  ImageView iw4;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_start_screen);

    iw1 = findViewById(R.id.signImg1);
    iw2 = findViewById(R.id.signImg2);
    iw3 = findViewById(R.id.signImg3);
    iw4 = findViewById(R.id.signImg4);

    Button signUpBtn = (Button) findViewById(R.id.signUpBtn);
    Button signInBtn = (Button) findViewById(R.id.signInBtn);

    signInBtn.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v) {
        Intent signInPage = new Intent(getApplicationContext(), SignInScreen.class);
        startActivity(signInPage);
      }
    });

    signUpBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent signUpIntent = new Intent(getApplicationContext(), SignUpScreen.class);
        startActivity(signUpIntent);
      }
    });
  }
}

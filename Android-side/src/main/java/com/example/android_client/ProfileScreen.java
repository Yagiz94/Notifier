package com.example.android_client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ProfileScreen extends AppCompatActivity {
  protected TextView name_text;
  protected TextView surname_text;
  protected TextView userName_text;
  protected TextView email_text;
  protected TextView credits_text;
  protected Button main_menu;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_profile_screen);

    name_text = (TextView) findViewById(R.id.name_text_profile);
    surname_text = (TextView) findViewById(R.id.surname_text_profile);
    userName_text = (TextView) findViewById(R.id.username_text_profile);
    email_text = (TextView) findViewById(R.id.email_text_profile);
    credits_text = (TextView) findViewById(R.id.credit_text_profile);
    main_menu = findViewById(R.id.profile_main_menu);

    /* Get user_data coming from sign_in */
    SharedPreferences profile_data = getSharedPreferences("user_profile", MODE_PRIVATE);
    String name = profile_data.getString("name", "No name defined");
    String surname = profile_data.getString("surname", "No surname defined");
    String userName = profile_data.getString("username", "No userName defined");
    String email = profile_data.getString("email", "No email defined");
    int credits = profile_data.getInt("credit", 0);

    name_text.setText("Name: " + name);
    surname_text.setText("Surname: " + surname);
    userName_text.setText("Username: "+ userName);
    email_text.setText("E-Mail: " + email);
    credits_text.setText("Credits: " + credits);

    name_text.setTextColor(Color.BLACK);
    surname_text.setTextColor(Color.BLACK);
    email_text.setTextColor(Color.BLACK);
    userName_text.setTextColor(Color.BLACK);
    credits_text.setTextColor(Color.BLACK);

    main_menu.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent main_menu = new Intent(getApplicationContext(),MainMenu.class);
        startActivity(main_menu);
      }
    });

  }
}

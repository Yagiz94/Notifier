package com.example.android_client;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;
import java.util.HashMap;

public class SignUpScreen extends AppCompatActivity {

  private EditText name;
  private EditText surname;
  private EditText username;
  private EditText password;
  private EditText email;
  private Button signUpButton;
  protected Network network;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sign_up_screen);

    name = findViewById(R.id.signupNameEdit);
    surname = findViewById(R.id.signUpSurnameEdit);
    username = findViewById(R.id.signupUsernameEdit);
    email = findViewById(R.id.signupEmailEdit);
    password = findViewById(R.id.signupPasswordEdit);
    signUpButton = findViewById(R.id.signUpPageBtn);
    network = new Network();

    signUpButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        signUp(name.getText().toString(), surname.getText().toString(),
          username.getText().toString(), email.getText().toString(), password.getText().toString());
      }
    });
  }

  public void signUp(String name, String surname, String username, String email, String password) {
    String reqURL = "http://" + network.localIP + ":3000/api/People";
    HashMap data = new HashMap();
    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
    data.put("name", name);
    data.put("surname", surname);
    data.put("userName", username);
    data.put("email", email);
    data.put("password", password);

    JSONObject newUserObject = new JSONObject(data);
    JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, reqURL, new JSONObject(data), new Response.Listener<JSONObject>() {
      @Override
      public void onResponse(JSONObject response) {
        // TODO: handle success
        Log.e("REST response: ", response.toString());

        /* On successful scenario, direct the user to signIn page */
        Intent directSignIn = new Intent(getApplicationContext(), SignInScreen.class);
        startActivity(directSignIn);
      }
    }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        error.printStackTrace();
        Log.e("REST response: ", error.toString());

        /* On error scenario, give message and start timer
        * when the timer is done refresh the activity screen */
        Toast toast = Toast.makeText(getApplicationContext(), "E-mail address already exists on " +
          "another account, please enter different email address", Toast.LENGTH_SHORT);
        toast.show();
        new CountDownTimer(5000, 1000) {
          public void onTick(long millisUntilFinished) {
            // TODO
          }
          public void onFinish() {
            finish();
            startActivity(getIntent());
          }
        }.start();
      }
    });
    requestQueue.add(jsonRequest);
  }

}

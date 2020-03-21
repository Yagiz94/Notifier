package com.example.android_client;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SignInScreen extends AppCompatActivity {
  boolean boolResult = false;
  TextView email_text;
  TextView password_text;
  EditText email_input;
  EditText password_input;
  Button signInButton;
  Network network;
  protected boolean key = false;
  protected JSONObject user_data = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sign_in_screen);

    user_data = new JSONObject();

    email_text = (TextView) findViewById(R.id.email_text_user);
    password_text = (TextView) findViewById(R.id.passsword_text_user);

    email_input = (EditText) findViewById(R.id.email_input);
    password_input = (EditText) findViewById(R.id.password_input);

    email_text.setText("Please enter your email address below");
    password_text.setText("Please enter your password below");

    network = new Network();
    signInButton = (Button) findViewById(R.id.signInBtnn);
    signInButton.setText("sign in");

    signInButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        /* The reason why a timer is added to the activity is that all the requests
        and JSON Data in Volley Library comes with a delay of a few seconds
        so first the JSOn data must be waited to come and then
        controlled according to the following code segment  */
        new CountDownTimer(3000, 1000) {
          public void onTick(long millisUntilFinished) {
            if (checkLogin(email_input.getText().toString(), password_input.getText().toString()))
              key = true;
          }

          public void onFinish() {
            if (key) {
              /* share user_id with other activities where it will be needed in the future */
              SharedPreferences.Editor editor = getSharedPreferences("user_sign_in", MODE_PRIVATE).edit();
              SharedPreferences.Editor editor3 = getSharedPreferences("user_profile", MODE_PRIVATE).edit();
              try {
                /* pass needed data to related activities using SharedPreferences */
                editor.putString("user_id", user_data.get("id").toString());
                editor3.putString("name", user_data.get("name").toString());
                editor3.putString("surname", user_data.get("surname").toString());
                editor3.putString("username", user_data.get("userName").toString());
                editor3.putString("email", user_data.get("email").toString());
                editor3.putInt("credit", user_data.getInt("credit"));
              } catch (JSONException e) {
                e.printStackTrace();
              }
              editor.apply();
              editor3.apply();
              Intent gotoMainMenu = new Intent(getApplicationContext(), MainMenu.class);
              startActivity(gotoMainMenu);
            } else {
              Toast toast = Toast.makeText(getApplicationContext(), "Sign-in unsuccessful, please re-enter your email and password correctly", Toast.LENGTH_SHORT);
              toast.show();
            }
          }
        }.start();

      }
    });

  }

  public synchronized boolean checkLogin(String email, String password) {

    String reqURL = "http://" + network.localIP + ":3000/api/People";
    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

    final String emailCheck = email;
    final String passwordCheck = password;

    JsonArrayRequest objectRequest;
    JSONArray response;
    {
      objectRequest = new JsonArrayRequest(
        Request.Method.GET,
        reqURL,
        new JSONArray(),
        new Response.Listener<JSONArray>() {
          @Override
          public synchronized void onResponse(JSONArray response) {
            synchronized (response) {
              try {
                JSONObject object = new JSONObject();
                for (int i = 0; i < response.length(); i++) {
                  object = response.getJSONObject(i);
                  if (emailCheck.equals(object.get("email").toString()) && passwordCheck.equals(object.get("password").toString())) {
                    boolResult = true;
                    response.notifyAll();
                    Log.w("SignInOperation ", object.toString());
                    user_data = object;
                  }
                }

              } catch (JSONException e) {
                e.printStackTrace();
              }
            }
          }
        },
        new Response.ErrorListener() {
          @Override
          public void onErrorResponse(VolleyError error) {
            Log.e("REST response: ", error.toString());
          }
        });
      requestQueue.add(objectRequest);
    }
    return boolResult;
  }


}

package com.example.android_client;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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
import java.util.ArrayList;

public class UserCheckInsList extends AppCompatActivity {

  protected JSONArray notificationsList = new JSONArray();
  protected Network network;
  protected String userId;
  protected Constants constants;
  protected ArrayList<String> listItem;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_user_check_ins_list);

    SharedPreferences prefs = getSharedPreferences("user_sign_in", MODE_PRIVATE);
    userId = prefs.getString("user_id", "No user_id defined");

    Button menuButton = (Button) findViewById(R.id.mainMenuButton);
    final Button show_btn = findViewById(R.id.show_btn);
    show_btn.setVisibility(View.INVISIBLE);
    network = new Network();
    constants = new Constants();
    listItem = new ArrayList<>();


    new CountDownTimer(1000, 1000){
      public void onTick(long millisUntilFinished){
        constants.arr = getNotifications();
      }
      public  void onFinish(){
        Log.d("NOTIFICATIONS",notificationsList.toString());
        for(int i = 0; i < notificationsList.length();i++){
          try {
            /* print only the public info of the item (not id, etc.) */
            listItem.add(i+1 + ") " + notificationsList.getJSONObject(i).get("address").toString() + "\nTime: " +
              notificationsList.getJSONObject(i).get("date").toString());
          } catch (JSONException e) {
            e.printStackTrace();
          }
        }
        show_btn.setVisibility(View.VISIBLE);
      }
    }.start();

    show_btn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ListView checkInsList = (ListView) findViewById(R.id.checkInsList);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(UserCheckInsList.this,
          android.R.layout.simple_list_item_1, android.R.id.text1, listItem);
        checkInsList.setAdapter(adapter);

        checkInsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            // TODO Auto-generated method stub
            String value = adapter.getItem(position);
            Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT).show();
          }
        });
        show_btn.setVisibility(View.GONE);
      }
    });

    menuButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent returnMainMenu = new Intent(getApplicationContext(), MainMenu.class);
        startActivity(returnMainMenu);
      }
    });
  }

  public JSONArray getNotifications() {
    String reqURL = "http://" + network.localIP + ":3000/api/People/" + userId + "/notifications";
    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
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
                  if (object.has("personId")) { // check if person id exists
                    if (object.get("personId").toString().equals(userId)) {
                      response.notifyAll();
                      notificationsList.put(object);
                      Log.w("object", object.toString());
                    }
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
    return notificationsList;
  }
}

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

public class MyItems extends AppCompatActivity {
  protected Network network;
  protected Constants constant;
  protected String userId;
  protected ArrayList<String> myItems;
  protected Button show;
  protected Button main_menu;
  protected ListView my_items;
  protected JSONArray arr;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_my_items);

    my_items = findViewById(R.id.my_items);
    show = findViewById(R.id.show_my_items);
    main_menu = findViewById(R.id.main_menu_my_items);
    show.setVisibility(View.INVISIBLE);
    main_menu.setVisibility(View.VISIBLE);
    network = new Network();
    constant = new Constants();
    arr = new JSONArray();
    myItems = new ArrayList<>();

    /* get user id from sign in activity */
    SharedPreferences prefs = getSharedPreferences("user_sign_in", MODE_PRIVATE);
    userId = prefs.getString("user_id", "No user_id defined");

    new CountDownTimer(1000, 1000){
      public void onTick(long millisUntilFinished){
        constant.arr = getMyItems();
      }
      public  void onFinish(){
        for(int i = 0; i < constant.arr.length(); i++){
          try {
            /* print only the public info of the item (not id, etc.) */
            myItems.add(i+1 + ")ID: " + constant.arr.getJSONObject(i).get("id").toString() + "\nName: " +
              constant.arr.getJSONObject(i).get("name").toString() + "\nPurchaseTime: " +
              constant.arr.getJSONObject(i).get("purchaseTime").toString());
          } catch (JSONException e) {
            e.printStackTrace();
          }
        }
        show.setVisibility(View.VISIBLE);
      }
    }.start();

    show.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(MyItems.this,
          android.R.layout.simple_list_item_1, android.R.id.text1, myItems);
        my_items.setAdapter(adapter);

        my_items.setOnItemClickListener(new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            // TODO Auto-generated method stub
            String value=adapter.getItem(position);
            Toast.makeText(getApplicationContext(),value,Toast.LENGTH_SHORT).show();
          }
        });
        show.setVisibility(View.GONE);
      }
    });

    main_menu.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent main_menu = new Intent(getApplicationContext(),MainMenu.class);
        startActivity(main_menu);
      }
    });
  }

  protected JSONArray getMyItems(){
    String reqURL = "http://" + network.localIP + ":3000/api/People/" + userId + "/items";
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
                  arr.put(object);
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
    return arr;
  }
}

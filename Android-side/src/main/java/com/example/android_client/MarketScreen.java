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

public class MarketScreen extends AppCompatActivity {

  protected Network network;
  protected Constants constants;
  protected ListView marketListView;
  protected Button showBtn;
  protected ArrayList<String> listItem;
  protected Button main_menu_Btn;
  protected ArrayList <Character> array2;
  protected String concatinated_id;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_market_screen);

    network = new Network();
    constants = new Constants();
    marketListView = (ListView) findViewById(R.id.market_list_view);
    showBtn = (Button) findViewById(R.id.show_btn);
    main_menu_Btn = (Button) findViewById(R.id.main_menu_market);
    showBtn.setVisibility(View.INVISIBLE);
    listItem = new ArrayList<>();
    array2 = new ArrayList<>();

    new CountDownTimer(1000, 1000) {

      public void onTick(long millisUntilFinished) {
        constants.arr = getMarket();
      }

      public void onFinish() {
        for(int i = 0;i < constants.arr.length();i++){
          try {
            /* '/' and '.' chars are used to parse the string and get id easily */
            listItem.add(i+1 + ") id:/" + constants.arr.getJSONObject(i).get("id").toString() + ".\nItem Name: " +
                constants.arr.getJSONObject(i).get("name").toString() + "\nPrice: " +
                constants.arr.getJSONObject(i).get("price").toString());
          } catch (JSONException e) {
            e.printStackTrace();
          }
        }
        showBtn.setVisibility(View.VISIBLE);
      }
    }.start();

    showBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(MarketScreen.this,
          android.R.layout.simple_list_item_1, android.R.id.text1, listItem);
        marketListView.setAdapter(adapter);
        marketListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            // TODO Auto-generated method stub
            String value = adapter.getItem(position);
            /* extract id from value */
            int start = value.indexOf("/");
            int end = value.indexOf(".");
            String parsed_id = value.substring(start+1, end);
            Log.d("ITEM_ID:", parsed_id);
            SharedPreferences.Editor editor = getSharedPreferences("market_items", MODE_PRIVATE).edit();
            editor.putString("id", parsed_id);
            editor.apply();

            Intent purchasePage = new Intent(getApplicationContext(),PurchaseRequestPage.class);
            startActivity(purchasePage);
          }
        });
        showBtn.setVisibility(View.GONE);
      }
    });

    main_menu_Btn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent main_menu_intent = new Intent(getApplicationContext(),MainMenu.class);
        startActivity(main_menu_intent);
      }
    });
  }

  protected JSONArray getMarket() {
    final JSONArray items = new JSONArray();
    String reqURL = "http://" + network.localIP + ":3000/api/Markets";
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
                int length = response.getJSONObject(0).getJSONArray("items").length();
                for (int i = 0; i < length; i++) {
                  object = response.getJSONObject(0).getJSONArray("items").getJSONObject(i);
                  items.put(object);
                }
                Log.d("ITEMS", items.toString());

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
    return items;
  }
}

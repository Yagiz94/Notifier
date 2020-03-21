package com.example.android_client;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class PurchaseRequestPage extends AppCompatActivity {
  protected TextView question;
  protected Button purchase;
  protected Button decline;
  protected Network network;
  protected String userId;
  protected JSONObject itemObject;
  protected String itemId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_purchase_request_page);

    /* get user id */
    SharedPreferences prefs = getSharedPreferences("user_sign_in", MODE_PRIVATE);
    userId = prefs.getString("user_id", "No user_id defined");

    question = findViewById(R.id.purchase_question);
    purchase = findViewById(R.id.purchase_btn);
    decline = findViewById(R.id.decline_btn);
    network = new Network();
    itemObject = new JSONObject();

    question.setTextColor(Color.BLACK);
    question.setText("Are you sure you want to purchase this item?");

    purchase.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        SharedPreferences preference ;
        preference = getSharedPreferences("market_items",MODE_PRIVATE);
        itemId = preference.getString("id","no item id defined");
        new CountDownTimer(1000, 1000) {

          public void onTick(long millisUntilFinished) {
            JSONObject obj = getItems();

          }
          public void onFinish() {
            Toast toast = Toast.makeText(getApplicationContext(),"Purchased successfully",Toast.LENGTH_SHORT);
            toast.show();
            Intent myItems = new Intent(getApplicationContext(),MyItems.class);
            startActivity(myItems);
            try {
              purchaseItem(itemId);
            } catch (JSONException e) {
              e.printStackTrace();
            }
          }
        }.start();

      }
    });

    decline.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent returnMarket = new Intent(getApplicationContext(),MarketScreen.class);
        startActivity(returnMarket);
      }
    });
  }

  protected JSONObject getItems(){
    final JSONArray items = new JSONArray();
    String reqURL1 = "http://" + network.localIP + ":3000/api/Markets";
    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
    JsonArrayRequest objectRequest;
    JSONArray response;
    {
      objectRequest = new JsonArrayRequest(
        Request.Method.GET,
        reqURL1,
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
                  /* if the itemId matches an item in the list pass the item data to itemObject*/
                  if (object.get("id").equals(itemId)) {
                    itemObject = object;
                  }
                }
                Log.d("ITEM", itemObject.toString());

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
    return itemObject;
  }

  protected void purchaseItem(final String itemId) throws JSONException {
    /*
     * first get all the items in the market object
     * then post the item information on the itemsList attribute of the related person object
     * then delete the item with the given id from the items list in the market
     */

    Log.d("ITEMID", itemId);

    String reqURL3 = "http://" + network.localIP + ":3000/api/People/" + userId + "/items";
    Date currentTime = Calendar.getInstance().getTime();
    HashMap data = new HashMap();
    RequestQueue requestQueue3 = Volley.newRequestQueue(getApplicationContext());
    try {
      data.put("id", itemId);
      data.put("name", itemObject.get("name").toString());
      data.put("price", itemObject.get("price"));
      data.put("purchaseTime", currentTime);
    }catch (JSONException e){
      e.printStackTrace();
    }
    JSONObject parameters = new JSONObject(data);
    JsonObjectRequest jsonRequest3 = new JsonObjectRequest(Request.Method.POST, reqURL3, new JSONObject(data), new Response.Listener<JSONObject>() {
      @Override
      public void onResponse(JSONObject response) {
        // TODO: handle success
        Log.d("Item post successful: ", response.toString());

      }
    }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        error.printStackTrace();
        Log.e("Item cannot be posted: ", error.toString());
      }
    });

    requestQueue3.add(jsonRequest3);

    /* since there will be only one market object on the server side, market id is directly typed to the url */
    String reqURL2 = "http://" + network.localIP + ":3000/api/Markets/5e554b083cf1c109043eca6f/itemsList/" + itemId;
    RequestQueue requestQueue2 = Volley.newRequestQueue(getApplicationContext());
    JsonObjectRequest jsonrequest2 = new JsonObjectRequest(Request.Method.DELETE, reqURL2, new JSONObject(),
      new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
          Log.d("Message", "Delete successful");
        }
      },
      new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
        }
      }
    );
    requestQueue2.add(jsonrequest2);
  }
}


package com.example.android_client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;

public class MainMenu extends AppCompatActivity {
  DrawerLayout drawerLayout;
  ActionBarDrawerToggle mToggle;
  NavigationView navigationView;
  protected String userId;
  protected SharedPreferences prefs;
  protected SharedPreferences prefs2;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main_menu);

    /* Get user_id coming from sign_in */
    prefs = getSharedPreferences("user_sign_in", MODE_PRIVATE);

    /* When logout button is pressed delete all the shared preferences on the system.
    So prefs2 is also called to clear all the data when logout */
    prefs2 = getSharedPreferences("user_profile", MODE_PRIVATE);

    userId = prefs.getString("user_id", "No user_id defined");

    /* Beginning og navigation view code */
    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setHomeAsUpIndicator(R.drawable.menu);

    drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayoutMenu);
    mToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
    drawerLayout.addDrawerListener(mToggle);
    mToggle.syncState();

    navigationView = findViewById(R.id.navigation_view_menu);
    navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
          case R.id.db:
            menuItem.setChecked(true);
            Intent gotoCheckInsList = new Intent(getApplicationContext(), UserCheckInsList.class);
            startActivity(gotoCheckInsList);
            drawerLayout.closeDrawers();
            return true;

          case R.id.Map:
            menuItem.setChecked(true);
            Intent gotoMapScreen = new Intent(getApplicationContext(), MapActivity.class);
            startActivity(gotoMapScreen);
            drawerLayout.closeDrawers();
            return true;

          case R.id.market:
            menuItem.setChecked(true);
            Intent gotoMarket = new Intent(getApplicationContext(), MarketScreen.class);
            startActivity(gotoMarket);
            drawerLayout.closeDrawers();
            return true;

          case R.id.profile:
            menuItem.setChecked(true);
            Intent gotoProfile = new Intent(getApplicationContext(), ProfileScreen.class);
            startActivity(gotoProfile);
            drawerLayout.closeDrawers();
            return true;

          case R.id.my_items:
            menuItem.setChecked(true);
            Intent gotoSettings = new Intent(getApplicationContext(), MyItems.class);
            startActivity(gotoSettings);
            drawerLayout.closeDrawers();
            return true;

          case R.id.logout:
            menuItem.setChecked(true);
            /* When logout is pressed, delete all shared preferences */
            SharedPreferences.Editor editor = prefs.edit();
            SharedPreferences.Editor editor2 = prefs2.edit();
            editor.clear();
            editor2.clear();
            editor.commit();
            editor2.commit();
            Intent gotoSignIn = new Intent(getApplicationContext(), StartScreen.class);
            startActivity(gotoSignIn);
            /*finish the current activity */
            finish();
            drawerLayout.closeDrawers();
            return true;

        }
        return false;
      }
    });    /*end of navigation view code*/

  }

  /* navigation view clickable menu listMock-up */
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        drawerLayout.openDrawer(GravityCompat.START);
        return true;
    }

    return super.onOptionsItemSelected(item);
  }
  /* end of navigation view clickable menu listMock-up */

  @Override
  public void onBackPressed() {
    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
      drawerLayout.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }

}

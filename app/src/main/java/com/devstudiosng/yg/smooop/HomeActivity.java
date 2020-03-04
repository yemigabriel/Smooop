package com.devstudiosng.yg.smooop;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        HomeFragment fragment = new HomeFragment();
        ft.add(R.id.container, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            super.onBackPressed();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = null;

        if (id == R.id.nav_home) {
            fragment = new HomeFragment();
            if (getSupportActionBar()!=null)
                getSupportActionBar().setTitle("Smooop");
        }
//        else if (id == R.id.nav_profile) {
////            fragment = new ProfileFragment();
//            if (getSupportActionBar()!=null)
//                getSupportActionBar().setTitle("Profile");
//        }
        else if (id == R.id.nav_contacts) {
            fragment = new ContactFragment();
            if (getSupportActionBar()!=null)
                getSupportActionBar().setTitle("Emergency Contacts");
        } else if (id == R.id.nav_notifications) {
            fragment = new NotificationFragment();
            if (getSupportActionBar()!=null)
                getSupportActionBar().setTitle("Notifications");
        } else if (id == R.id.nav_traffic_updates) {
            fragment = new TwitterFragment();
            if (getSupportActionBar()!=null)
                getSupportActionBar().setTitle("Traffic Updates");
        }
//        else if (id == R.id.nav_settings) {
//
//        }
        else if (id == R.id.nav_share) {
            shareApp();
        }
//        else if (id == R.id.nav_faq) {
//
//        }
        else if (id == R.id.nav_feedback) {
            sendEmailIntent("help@smooop.com");
        }

        if (fragment!=null) {
            ft.replace(R.id.container, fragment).commit();
        }
//        if (navigationView != null) {
//            navigationView.setCheckedItem(item.getItemId());
//        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void sendEmailIntent(String email) {
        String[] emailAddress = new String[] {email};
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, emailAddress);
        if (intent.resolveActivity(HomeActivity.this.getPackageManager()) != null) {
            startActivity(intent);
        }

    }

    private void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Download 'Smooop' app on Google Play - https://play.google.com/store/apps/details?id=" + Smooop.APP_PLAY_STORE_ID);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Share App"));
    }

    public void showRateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rate us...")
                .setMessage("If you've enjoyed using this app, kindly rate us on Google Play")
                .setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + Smooop.APP_PLAY_STORE_ID)));
                    }
                })
                .setNegativeButton("Later", null);
        AlertDialog dialog = builder.create();
        dialog.show();



    }

}

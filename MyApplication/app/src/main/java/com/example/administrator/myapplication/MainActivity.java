package com.example.administrator.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import utils.Logout;
import viewpage.viewpager;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    public  String user_name = "jason";   //default name
    public String avatar_url = "drawable://" + R.drawable.test ;
   public  ImageLoader imageLoader ;
    public SharedPreferences sharedPreferences ;
    public  SharedPreferences.Editor editor ;
   public Intent intent ;
    public ImageView imagewView ;
    public boolean Install_state = true ;
   public  Fragment_Map fragment_map  ;
    TextView textView ;
    Toolbar toolbar ;
  public FrameLayout mapFrameLayout,otherFrameLayout ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
        .build();
        ImageLoader.getInstance().init(config);
       getWindow().setFormat(PixelFormat.TRANSLUCENT);
        sharedPreferences =this.getApplicationContext().getSharedPreferences("user_state",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        //跳转至第一次安装的引导页
        if(sharedPreferences.getBoolean("first_install_state",true)){
            editor.putBoolean("first_install_state",false);
            editor.commit();

        Intent intent = new Intent(this,viewpager.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        } else {
            user_name = sharedPreferences.getString("user_name","jason");
            avatar_url = sharedPreferences.getString("avatar_url","drawable://" + R.drawable.test);
          initView();
        }









    }


    public void initView(){
    fragment_map = new Fragment_Map();
        mapFrameLayout = (FrameLayout)findViewById(R.id.map);
        otherFrameLayout = (FrameLayout)findViewById(R.id.frame_layout);
        imageLoader = ImageLoader.getInstance();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        imagewView = (ImageView)findViewById(R.id.imageView);
        imagewView.setOnClickListener(this);
        textView = (TextView)findViewById(R.id.text);


        textView.setText(user_name);
        imageLoader.displayImage(avatar_url,imagewView);



        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            /**
             * 浮动按钮的事件
             * @param view
             */

            @Override
            public void onClick(View view) {




            }
        });



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_camara) {

            getSupportFragmentManager().beginTransaction().replace(R.id.map,fragment_map).commit();
            mapFrameLayout.setVisibility(View.VISIBLE);
            otherFrameLayout.setVisibility(View.INVISIBLE);
             toolbar.setTitle("寻找吃货");

            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,new Fragment_getFruirts()).commit();
            toolbar.setTitle("挑选水果");
            mapFrameLayout.setVisibility(View.INVISIBLE);
            otherFrameLayout.setVisibility(View.VISIBLE);
        } else if (id == R.id.myinfo) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,new Fragment_myInfo()).commit();
            toolbar.setTitle("个人信息");
        } else if (id == R.id.logout) {

                Logout logout = new Logout();
               logout.clean_sharedPrefernces(getApplicationContext());

        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * 头部头像的点击事件
     * @param v   avatar
     */
    @Override
    public void onClick(View v) {
       Intent intent = new Intent(this,login.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}

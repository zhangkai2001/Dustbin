package com.kai.dustbin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.Set;

public class SettingActivity extends AppCompatActivity
{
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar_setting);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.exit_setting_menu);
        //<TODO---
        navController= Navigation.findNavController(this,R.id.fragment_setting);
        DrawerLayout drawerLayout=(DrawerLayout) findViewById(R.id.drawer_setting);
        appBarConfiguration=new AppBarConfiguration
                .Builder(R.id.overwatch,R.id.electric,R.id.dustbin,R.id.network,R.id.sensor)
                .setDrawerLayout(drawerLayout)
                .build();
        NavigationUI.setupActionBarWithNavController(this,navController,appBarConfiguration);
        NavigationView navigationView=(NavigationView) findViewById(R.id.setting_menu);
        NavigationUI.setupWithNavController(navigationView,navController);
    }
    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController,appBarConfiguration)
                ||super.onSupportNavigateUp();
    }
    @Override
    public void onBackPressed()
    {
        DrawerLayout drawerSetting=(DrawerLayout)findViewById(R.id.drawer_setting);
        if (drawerSetting.isDrawerOpen(GravityCompat.START)) {
            drawerSetting.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    public void save(View view)
    {
        Toast.makeText(this,"saved",Toast.LENGTH_LONG).show();
    }
}
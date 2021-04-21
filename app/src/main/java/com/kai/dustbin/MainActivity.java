package com.kai.dustbin;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.drawerlayout.widget.DrawerLayout;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
    public void translate(View view)
    {
        DrawerLayout drawer=(DrawerLayout) findViewById(R.id.drawer_run);
        drawer.openDrawer(Gravity.START);
        Toast.makeText(MainActivity.this,"aa",Toast.LENGTH_LONG).show();
    }
    public void setting(View view)
    {
        Intent intent =new Intent(this,SettingActivity.class);
        startActivity(intent);
    }
}
package com.kai.dustbin;

import android.content.Intent;
import android.media.MediaPlayer;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity
{
    MediaPlayer mediaPlayer=MediaPlayer.create(this,R.raw.mediatest);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mediaPlayer.start();
        mediaPlayer.setLooping(true);
    }
    public void translate(View view)
    {
        DrawerLayout drawer=(DrawerLayout) findViewById(R.id.drawer_run);
        drawer.openDrawer(GravityCompat.START);
    }
    public void setting(View view)
    {
        Intent intent =new Intent(this,SettingActivity.class);
        startActivity(intent);
    }
    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_run);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        } else
        {
            super.onBackPressed();
        }
    }
    public void volume(View view)
    {
        FloatingActionButton voloum=findViewById(R.id.volume);
        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.stop();
            voloum.setImageResource(R.drawable.ic_baseline_volume_off_24);
        }else
        {
            mediaPlayer.start();
            voloum.setImageResource(R.drawable.ic_baseline_volume_up_24);
        }

    }

}
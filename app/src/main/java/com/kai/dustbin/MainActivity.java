package com.kai.dustbin;

import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.friendlyarm.FriendlyThings.HardwareControler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "SerialPort";//串口通信定义
    private final int BUFSIZE = 512;
    MediaPlayer mediaPlayer;
    private String devName = "/dev/ttyAMA3";
    private int speed = 9600;
    private int dataBits = 8;
    private int stopBits = 1;
    private int devfd = -1;//配置USART参数
    private String str, str1;
    private int a = 0, b = 0, c = 0, d = 0;
    private TextView title;
    private byte[] buf = new byte[BUFSIZE];
    private Timer timer = new Timer();
    private String confirm;
    private String cancel;
    private ImageView dustbinImage;
    private BluetoothAdapter bluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;
    BluetoothService mService;
    boolean mBound = false;

    private Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            if(a>=100)
            {
                dustbinImage=findViewById(R.id.harmfulImageView);
                dustbinImage.setImageResource(R.drawable.ic_baseline_harmful_24);
            }
            if(b>=100)
            {
                dustbinImage=findViewById(R.id.recycleImageView);
                dustbinImage.setImageResource(R.drawable.ic_baseline_recycle_24);
            }
            if(c>=100)
            {
                dustbinImage=findViewById(R.id.unrecycleImageView);
                dustbinImage.setImageResource(R.drawable.ic_baseline_unrecycle_24);
            }
            if(d>=100)
            {
                dustbinImage=findViewById(R.id.kicthenImageView);
                dustbinImage.setImageResource(R.drawable.ic_baseline_kicthen_24);
            }
            switch (msg.what)
            {
                case 1:
                    if (HardwareControler.select(devfd, 0, 0) == 1)
                    {
                        int retSize = HardwareControler.read(devfd, buf, BUFSIZE);
                        if (retSize > 0)
                        {
                            str = new String(buf, 0, retSize);
                            str1 = str.substring(0, 1);
                            if (str1.equals("R"))
                            {

                                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                                alertDialog.setTitle(R.string.start_throw);
                                alertDialog.setMessage(getString(R.string.harmful_throw));
                                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, cancel, new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) { }
                                });
                                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, confirm, new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        String str = "r";
                                        int ret = HardwareControler.write(devfd, str.getBytes());
                                        a = a + 10;
                                    }
                                });
                                alertDialog.show();
                            }
                            if (str1.equals("G"))
                            {
                                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                                alertDialog.setTitle(R.string.start_throw);
                                alertDialog.setMessage(getString(R.string.recycle_throw));
                                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, cancel, new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) { }
                                });
                                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, confirm, new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        String str = "g";
                                        int ret = HardwareControler.write(devfd, str.getBytes());
                                        b = b + 10;
                                    }
                                });
                                alertDialog.show();
                            }
                            if (str1.equals("B"))
                            {
                                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                                alertDialog.setTitle(R.string.start_throw);
                                alertDialog.setMessage(getString(R.string.non_recycle_throw));
                                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, cancel, new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) { }
                                });
                                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, confirm, new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        String str = "b";
                                        int ret = HardwareControler.write(devfd, str.getBytes());
                                        c = c + 10;
                                    }
                                });
                                alertDialog.show();
                            }
                            if (str1.equals("p"))
                            {
                                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                                alertDialog.setTitle(R.string.start_throw);
                                alertDialog.setMessage(getString(R.string.start_throw));
                                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, cancel, new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) { }
                                });
                                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, confirm, new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        String str = "s";
                                        int ret = HardwareControler.write(devfd, str.getBytes());
                                    }
                                });
                                alertDialog.show();
                            }
                        }
                    }
                    View decorView = getWindow().getDecorView();
                    int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                    decorView.setSystemUiVisibility(uiOptions);
                    break;
            }

            super.handleMessage(msg);
        }
    };
    private TimerTask task = new TimerTask()
    {
        public void run()
        {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mediaPlayer = MediaPlayer.create(this, R.raw.mediatest);
        title = (TextView) findViewById(R.id.title_texview);
        mediaPlayer.start();
        mediaPlayer.setLooping(true);
        confirm=getString(R.string.confirm);
        cancel=getString(R.string.cancel);
        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        devfd = HardwareControler.openSerialPort(devName, speed, dataBits, stopBits);
        NavigationView navigationView=findViewById(R.id.translate_drawer);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
            {
                int itemId = menuItem.getItemId();
                switch (itemId)
                {
                    case R.id.chinese:
                        Log.d("bluetooth1","chinese");
                        break;
                    case R.id.english:
                        Log.d("bluetooth1","english");
                        break;
                }
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_run);
                if (drawer.isDrawerOpen(GravityCompat.START))
                {
                    drawer.closeDrawer(GravityCompat.START);
                }
                return true;
            }
        });
        if (devfd <= 0)
            title.setText(getString(R.string.error));
        else
            timer.schedule(task, 0, 500);

    }

    @Override
    protected void onResume()
    {
        mediaPlayer.seekTo(0);
        mediaPlayer.start();
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(uiOptions);
        Intent intent=new Intent(this,BluetoothService.class);
        bindService(intent,connection, Context.BIND_AUTO_CREATE);
        super.onResume();
        startService(new Intent(this,BluetoothService.class));
    }
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service)
        {
            BluetoothService.LocalBinder binder = (BluetoothService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
    @Override
    protected void onPause()
    {
        mediaPlayer.pause();
        super.onPause();
    }

    public void translate(View view)
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_run);
        drawer.openDrawer(GravityCompat.START);
    }

    public void setting(View view)
    {
        if(bluetoothAdapter!=null)
        {
            if (bluetoothAdapter.isEnabled())
            {
                if (mBound)
                {
                    if(mService.findDevices())
                    {
                        HardwareControler.write(devfd, "a".getBytes());
                        Intent settingIntent = new Intent(this, SettingActivity.class);

                        startActivity(settingIntent);
                    }else
                    {
                        Toast.makeText(this, "请打开手机蓝牙", Toast.LENGTH_SHORT).show();
                    }
                }

            }else
            {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }else
        {
            Toast.makeText(this,"蓝牙错误",Toast.LENGTH_SHORT).show();
        }

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
        FloatingActionButton voloum = findViewById(R.id.volume);
        if (mediaPlayer.isPlaying())
        {
            mediaPlayer.pause();
            voloum.setImageResource(R.drawable.ic_baseline_volume_off_24);
        } else
        {
            mediaPlayer.seekTo(0);
            mediaPlayer.start();
            voloum.setImageResource(R.drawable.ic_baseline_volume_up_24);
        }
    }

    public void kitchen(View view)
    {
        d = d+ 10;
        String str = "c";
        int ret = HardwareControler.write(devfd , str.getBytes());
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(R.string.start_throw);
        alertDialog.setMessage(getString(R.string.kitchen_throw));
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, confirm, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });
        alertDialog.show();
    }
    @Override
    protected void onDestroy()
    {
        timer.cancel();
        if (devfd != -1)
        {
            HardwareControler.close(devfd);
            devfd = -1;
        }
        if (mediaPlayer != null)
            mediaPlayer.release();
        super.onDestroy();
    }
}
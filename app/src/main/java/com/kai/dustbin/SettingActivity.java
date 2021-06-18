package com.kai.dustbin;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.friendlyarm.FriendlyThings.HardwareControler;
import com.google.android.material.navigation.NavigationView;

import java.util.Timer;
import java.util.TimerTask;

public class SettingActivity extends AppCompatActivity
{
    private static final String TAG = "SerialPort";//串口通信定义
    private final int MAXLINES = 200;
    private final int BUFSIZE = 512;
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;
    private String devName = "/dev/ttyAMA3";
    private int speed = 9600;
    private int dataBits = 8;
    private int stopBits = 1;
    private int devfd = -1;//配置USART参数
    private TextView color;
    private Button duoji1, duoji2, duoji3, duoji4;
    private ImageButton update;
    private String str;
    private byte[] buf = new byte[BUFSIZE];
    private Timer timer = new Timer();
    private Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 1:
                    if (HardwareControler.select(devfd, 0, 0) == 1)
                    {
                        int retSize = HardwareControler.read(devfd, buf, BUFSIZE);
                        if (retSize > 0)
                        {
                            str = new String(buf, 0, retSize);
                            if(str.length()>1)
                                color.setText(str);
                        }
                    }
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
        setContentView(R.layout.activity_setting);
        color = findViewById(R.id.sensor_textview);
        duoji1 = findViewById(R.id.button1);
        duoji2 = findViewById(R.id.button2);
        duoji3 = findViewById(R.id.button3);
        duoji4 = findViewById(R.id.button4);
        update = findViewById(R.id.update);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_setting);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.exit_setting_menu);
        navController = Navigation.findNavController(this, R.id.navHost_setting);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_setting);
        appBarConfiguration = new AppBarConfiguration
                .Builder(R.id.overwatch, R.id.electric, R.id.dustbin, R.id.network, R.id.sensor)
                .setDrawerLayout(drawerLayout)
                .build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationView navigationView = (NavigationView) findViewById(R.id.setting_menu);
        NavigationUI.setupWithNavController(navigationView, navController);
        devfd = HardwareControler.openSerialPort(devName, speed, dataBits, stopBits);
        if (devfd >= 0)
        {
            timer.schedule(task, 0, 500);
        } else
        {
            devfd = -1;
        }
        duoji1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String str = "r";
                int ret = HardwareControler.write(devfd, str.getBytes());
            }
        });
        duoji2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String str = "g";
                int ret = HardwareControler.write(devfd, str.getBytes());
            }
        });
        duoji3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String str = "b";
                int ret = HardwareControler.write(devfd, str.getBytes());
            }
        });
        duoji4.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String str = "c";
                int ret = HardwareControler.write(devfd, str.getBytes());
            }
        });
        update.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String str = "a";
                int ret = HardwareControler.write(devfd, str.getBytes());
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawerSetting = (DrawerLayout) findViewById(R.id.drawer_setting);
        if (drawerSetting.isDrawerOpen(GravityCompat.START))
        {
            drawerSetting.closeDrawer(GravityCompat.START);
        } else
        {
            super.onBackPressed();
        }
    }

    public void onDestroy()
    {
        timer.cancel();
        if (devfd != -1)
        {
            HardwareControler.close(devfd);
            devfd = -1;
        }
        super.onDestroy();
    }

    public void onPause()
    {
        if (devfd != -1)
        {
            HardwareControler.close(devfd);
            devfd = -1;
        }
        super.onPause();
    }

    public void onStop()
    {
        if (devfd != -1)
        {
            HardwareControler.close(devfd);
            devfd = -1;
        }
        super.onStop();
    }

    public void save(View view)
    {
        Toast.makeText(this, "保存成功", Toast.LENGTH_LONG).show();
    }
}
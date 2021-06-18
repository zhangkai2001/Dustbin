package com.kai.dustbin;

import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
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

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "SerialPort";//串口通信定义
    private static final int REQUEST_ENABLE_BT = 1;
    private final int BUFSIZE = 512;
    MediaPlayer mediaPlayer;
    MediaPlayer voice;
    BluetoothService mService;
    boolean mBound = false;
    SharedPreferences SPUtil;
    private final String devName = "/dev/ttyAMA3";
    private final int speed = 9600;
    private final int dataBits = 8;
    private final int stopBits = 1;
    private int devfd = -1;//配置USART参数
    private String str, str1;
    private int a = 0, b = 0, c = 0, d = 0;
    private TextView title;
    private final byte[] buf = new byte[BUFSIZE];
    private final Timer timer = new Timer();
    private String confirm;
    private String cancel;
    private ImageView dustbinImage;
    private BluetoothAdapter bluetoothAdapter;
    private boolean enLabel;
    private boolean distance;
    private final Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            if (a >= 100)
            {
                dustbinImage = findViewById(R.id.harmfulImageView);
                dustbinImage.setImageResource(R.drawable.ic_baseline_harmful_24);
            }
            if (b >= 100)
            {
                dustbinImage = findViewById(R.id.recycleImageView);
                dustbinImage.setImageResource(R.drawable.ic_baseline_recycle_24);
            }
            if (c >= 100)
            {
                dustbinImage = findViewById(R.id.unrecycleImageView);
                dustbinImage.setImageResource(R.drawable.ic_baseline_unrecycle_24);
            }
            if (d >= 100)
            {
                dustbinImage = findViewById(R.id.kicthenImageView);
                dustbinImage.setImageResource(R.drawable.ic_baseline_kicthen_24);
            }

            switch (msg.what)
            {
                case 1:
                    if (HardwareControler.select(devfd, 0, 0) == 1)
                    {
                        int retSize = HardwareControler.read(devfd, buf, BUFSIZE);
                        if (retSize > 0 )
                        {
                            str = new String(buf, 0, retSize);
                            str1 = str.substring(0, 1);
                            Log.d("bluetooth1", str);
                            Log.d("bluetooth1", str.substring(10));
                            if(Integer.parseInt(str.substring(10))<100)
                            {
                                distance=false;
                                if (str1.equals("R"))
                                {
                                    mediaPlayer.pause();
                                    if (enLabel)
                                        voice = MediaPlayer.create(getApplicationContext(), R.raw.harmful_confirm_en);
                                    else
                                        voice = MediaPlayer.create(getApplicationContext(), R.raw.harmful_confirm_zh);
                                    voice.start();
                                    voice.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
                                    {
                                        @Override
                                        public void onCompletion(MediaPlayer mp)
                                        {
                                            voice.release();
                                        }
                                    });

                                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                                    alertDialog.setTitle(R.string.start_throw);
                                    alertDialog.setMessage(getString(R.string.harmful_throw));
                                    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, cancel, new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                        }
                                    });
                                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, confirm, new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            String str = "r";
                                            int ret = HardwareControler.write(devfd, str.getBytes());
                                            a = a + 10;

                                            mediaPlayer.seekTo(0);
                                            mediaPlayer.start();
                                        }
                                    });
                                    alertDialog.show();
                                }
                                if (str1.equals("G"))
                                {
                                    mediaPlayer.pause();
                                    if (enLabel)
                                        voice = MediaPlayer.create(getApplicationContext(), R.raw.recycle_confirm_en);
                                    else
                                        voice = MediaPlayer.create(getApplicationContext(), R.raw.recycle_confirm_zh);
                                    voice.start();
                                    voice.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
                                    {
                                        @Override
                                        public void onCompletion(MediaPlayer mp)
                                        {
                                            voice.release();
                                        }
                                    });
                                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                                    alertDialog.setTitle(R.string.start_throw);
                                    alertDialog.setMessage(getString(R.string.recycle_throw));
                                    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, cancel, new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                        }
                                    });
                                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, confirm, new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            String str = "g";
                                            int ret = HardwareControler.write(devfd, str.getBytes());
                                            b = b + 10;
                                            mediaPlayer.seekTo(0);
                                            mediaPlayer.start();
                                        }
                                    });
                                    alertDialog.show();
                                }
                                if (str1.equals("B"))
                                {
                                    mediaPlayer.pause();
                                    if (enLabel)
                                        voice = MediaPlayer.create(getApplicationContext(), R.raw.non_recycle_confirm_en);
                                    else
                                        voice = MediaPlayer.create(getApplicationContext(), R.raw.non_recycle_confirm_zh);
                                    voice.start();
                                    voice.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
                                    {
                                        @Override
                                        public void onCompletion(MediaPlayer mp)
                                        {
                                            voice.release();
                                        }
                                    });
                                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                                    alertDialog.setTitle(R.string.start_throw);
                                    alertDialog.setMessage(getString(R.string.non_recycle_throw));
                                    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, cancel, new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                        }
                                    });
                                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, confirm, new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            String str = "b";
                                            int ret = HardwareControler.write(devfd, str.getBytes());
                                            c = c + 10;

                                            mediaPlayer.seekTo(0);
                                            mediaPlayer.start();
                                        }
                                    });
                                    alertDialog.show();
                                }
                                if (str1.equals("p"))
                                {
                                    mediaPlayer.pause();
                                    if (enLabel)
                                        voice = MediaPlayer.create(getApplicationContext(), R.raw.confirm_en);
                                    else
                                        voice = MediaPlayer.create(getApplicationContext(), R.raw.confirm_zh);
                                    voice.start();
                                    voice.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
                                    {
                                        @Override
                                        public void onCompletion(MediaPlayer mp)
                                        {
                                            voice.release();
                                        }
                                    });

                                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                                    alertDialog.setTitle(R.string.start_throw);
                                    alertDialog.setMessage(getString(R.string.start_throw));
                                    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, cancel, new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                        }
                                    });
                                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, confirm, new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            String str = "s";
                                            int ret = HardwareControler.write(devfd, str.getBytes());

                                            mediaPlayer.seekTo(0);
                                            mediaPlayer.start();
                                        }
                                    });
                                    alertDialog.show();
                                }
                            }else if(distance==false)
                            {
                                distance=true;
                                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                                alertDialog.setTitle(R.string.start_throw);
                                alertDialog.setMessage(getString(R.string.too_far));
                                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, confirm, new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
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
    private final TimerTask task = new TimerTask()
    {
        public void run()
        {
            HardwareControler.write(devfd, "a".getBytes());
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    };
    private final ServiceConnection connection = new ServiceConnection()
    {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service)
        {
            BluetoothService.LocalBinder binder = (BluetoothService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0)
        {
            mBound = false;
        }
    };

    public static final void changeLanguage(Context context, String language, String country)
    {
        if (context == null || TextUtils.isEmpty(language))
        {
            return;
        }
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.locale = new Locale(language, country);
        resources.updateConfiguration(config, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        SPUtil = getPreferences(Context.MODE_PRIVATE);
        String currentCountry = (String) SPUtil.getString("CURRENT_COUNTRY", "us");
        String currentLanguage = (String) SPUtil.getString("CURRENT_LANGUAGE", "EN");

        changeLanguage(MainActivity.this, currentLanguage, currentCountry);
        setContentView(R.layout.activity_main);
        int voiceId;
       // Log.d("bluetooth1", currentLanguage);
        if(currentLanguage.equals("en"))
        {
            enLabel=true;
            voiceId = R.raw.voice_en;
        }
        else
        {
            enLabel=false;
            voiceId = R.raw.vioce_zh;
        }
        mediaPlayer = MediaPlayer.create(this, voiceId);
        title = (TextView) findViewById(R.id.title_texview);
        mediaPlayer.start();
        mediaPlayer.setLooping(true);
        confirm = getString(R.string.confirm);
        cancel = getString(R.string.cancel);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        devfd = HardwareControler.openSerialPort(devName, speed, dataBits, stopBits);
        NavigationView navigationView = findViewById(R.id.translate_drawer);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
            {
                SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();

                int itemId = menuItem.getItemId();
                switch (itemId)
                {
                    case R.id.chinese:
           //             Log.d("bluetooth1", "chinese_c");
                        editor.putString("CURRENT_COUNTRY", "cn");
                        editor.putString("CURRENT_LANGUAGE", "ZH");
                        editor.apply();
                        break;
                    case R.id.english:
             //           Log.d("bluetooth1", "english_c");
                        editor.putString("CURRENT_COUNTRY", "US");
                        editor.putString("CURRENT_LANGUAGE", "en");
                        editor.apply();
                        break;
                }
             //   Log.d("bluetooth1", SPUtil.getString("CURRENT_COUNTRY", "  "));
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_run);
                if (drawer.isDrawerOpen(GravityCompat.START))
                {
                    drawer.closeDrawer(GravityCompat.START);
                }
                recreate();
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
        Intent intent = new Intent(this, BluetoothService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        super.onResume();
        startService(new Intent(this, BluetoothService.class));
    }

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
        if (bluetoothAdapter != null)
        {
            if (bluetoothAdapter.isEnabled())
            {
                if (mBound)
                {
                    if (mService.findDevices())
                    {
                        HardwareControler.write(devfd, "a".getBytes());
                        Intent settingIntent = new Intent(this, SettingActivity.class);

                        startActivity(settingIntent);
                    } else
                    {
                        Toast.makeText(this, "请打开手机蓝牙", Toast.LENGTH_SHORT).show();
                    }
                }

            } else
            {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        } else
        {
            Toast.makeText(this, "蓝牙错误", Toast.LENGTH_SHORT).show();
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
        d = d + 10;
        String str = "c";
        int ret = HardwareControler.write(devfd, str.getBytes());
        mediaPlayer.pause();
        if(enLabel)
            voice=MediaPlayer.create(getApplicationContext(),R.raw.kitchen_confirm_en);
        else
            voice=MediaPlayer.create(getApplicationContext(),R.raw.kitchen_confirm_zh);
        voice.start();
        voice.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
            @Override
            public void onCompletion(MediaPlayer mp)
            {
                voice.release();
            }
        });
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(R.string.start_throw);
        alertDialog.setMessage(getString(R.string.kitchen_throw));
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, confirm, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                mediaPlayer.seekTo(0);
                mediaPlayer.start();
            }
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
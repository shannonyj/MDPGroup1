package lab.mdp.grp01.main;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import static lab.mdp.grp01.main.Utils.*;


public class MainActivity extends ActionBarActivity implements SensorEventListener {
    private static final String TAG = "MainService";
    private static boolean D = true;

    private static int msgCounter = 0;

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    private Button mSendButton1;
    private Button mSendButton2;
    private Button mSendButton3;
    private Button mSendButton4;
    private Button mSendButton5;
    private ToggleButton btnDebug;
    private ImageButton btnStart;
    private ImageButton btnStop;
    private RadioGroup startType;
    private ToggleButton btnMode;
    private boolean autoMode = false;
    private AutoThread autoModeThread;
    private Button btnUpdateMap;

    private String mConnectedDeviceName = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothChatService mChatService = null;
    private MenuItem btStatus;
    private MenuItem initRobotMenu;

    private LinearLayout mapPanel;
    private MapSurface mapSurface;
    private ImageButton controlUp;
    private ImageButton controlDown;
    private ImageButton controlLeft;
    private ImageButton controlRight;
    private SharedPreferences spf;

    private SensorManager sensorMgr;
    private boolean sensorRegistered = false;
    private ToggleButton controlSensor;

    private TextView viewRx;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (D)
            Log.e(TAG, "++ ON START ++");
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
            if (mChatService == null)
                setupChat();
        }
        setupChat();
        autoModeThread = new AutoThread();
        autoModeThread.start();
        mapPanel = (LinearLayout) findViewById(R.id.mapPanel);
        mapSurface = new MapSurface(MainActivity.this);
        mapPanel.addView(mapSurface);
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        if (D)
            Log.e(TAG, "+ ON RESUME +");
         /*if (mChatService != null) {
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                mChatService.start();
            }
        }*/
        if(sensorRegistered)
            sensorMgr.registerListener(MainActivity.this, sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void setupChat() {
        Log.d(TAG, "setupChat()");


        spf = getSharedPreferences(PREF_DB, Context.MODE_PRIVATE);
        mSendButton1 = (Button) findViewById(R.id.button_send1);
        mSendButton1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String message = spf.getString(SET_CMD1, SET_CMD1_DEFAULT);
                sendMessage(message, true);
            }
        });
        mSendButton2 = (Button) findViewById(R.id.button_send2);
        mSendButton2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String message = spf.getString(SET_CMD2, SET_CMD2_DEFAULT);
                sendMessage(message, true);
            }
        });

        mSendButton3 = (Button) findViewById(R.id.button_send3);
        mSendButton3.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String message = spf.getString(SET_CMD3, SET_CMD3_DEFAULT);
                sendMessage(message, true);
            }
        });

        mSendButton4 = (Button) findViewById(R.id.button_send4);
        mSendButton4.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String message = spf.getString(SET_CMD4, SET_CMD4_DEFAULT);
                sendMessage(message, true);
            }
        });

        mSendButton5 = (Button) findViewById(R.id.button_send5);
        mSendButton5.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String message = spf.getString(SET_CMD5, SET_CMD5_DEFAULT);
                sendMessage(message, true);
            }
        });

        controlUp = (ImageButton) findViewById(R.id.controlUp);
        controlUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(spf.getString(SET_UP, SET_UP_DEFAULT), true);
            }
        });

        controlDown = (ImageButton) findViewById(R.id.controlDown);
        controlDown.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(spf.getString(SET_DOWN, SET_DOWN_DEFAULT), true);
            }
        });
        controlLeft = (ImageButton) findViewById(R.id.controlLeft);
        controlLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(spf.getString(SET_LEFT, SET_LEFT_DEFAULT), true);
            }
        });
        controlRight = (ImageButton) findViewById(R.id.controlRight);
        controlRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(spf.getString(SET_RIGHT, SET_RIGHT_DEFAULT), true);
            }
        });

        startType = (RadioGroup) findViewById(R.id.startType);

        btnStart = (ImageButton) findViewById(R.id.controlStart);
        btnStart.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                int selectedType = startType.getCheckedRadioButtonId();
                if(selectedType == R.id.startExplore) {
                    Toast.makeText(MainActivity.this, "Start pressed for EXPLORING", Toast.LENGTH_SHORT).show();
                    sendMessage(spf.getString(SET_EXPLORE, SET_EXPLORE_DEFAULT), true);
                }else if(selectedType == R.id.startRace){
                    Toast.makeText(MainActivity.this, "Start pressed for RACING", Toast.LENGTH_SHORT).show();
                    sendMessage(spf.getString(SET_RACE, SET_RACE_DEFAULT), true);
                }
            }
        });

        btnStop = (ImageButton) findViewById(R.id.controlStop);
        btnStop.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Stop action", Toast.LENGTH_SHORT).show();
                sendMessage(spf.getString(SET_STOP, SET_STOP_DEFAULT), true);
            }
        });

        btnMode = (ToggleButton) findViewById(R.id.controlMode);
        btnMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    btnUpdateMap.setClickable(false);
                    autoMode = true;
//                    autoUpdate.setAutoMode(true);
                    Toast.makeText(MainActivity.this, "Auto mode is on", Toast.LENGTH_SHORT).show();
                } else {
                    btnUpdateMap.setClickable(true);
                    autoMode = false;
//                    autoUpdate.setAutoMode(false);
                    Toast.makeText(MainActivity.this, "Manual mode is on", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnUpdateMap = (Button) findViewById(R.id.controlUpdate);
        btnUpdateMap.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Update map", Toast.LENGTH_SHORT).show();
                sendMessage("GRID", true);
            }
        });

        mChatService = new BluetoothChatService(this, mHandler);

        controlSensor = (ToggleButton) findViewById(R.id.controlSensor);
        controlSensor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(sensorMgr != null) {
                        Toast.makeText(MainActivity.this, "Sensor control mode on", Toast.LENGTH_SHORT).show();
                        sensorMgr.registerListener(MainActivity.this, sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
                        sensorRegistered = true;
                        controlUp.setClickable(false);
                        controlDown.setClickable(false);
                        controlLeft.setClickable(false);
                        controlRight.setClickable(false);
                    }
                }else{
                    if(sensorMgr != null) {
                        Toast.makeText(MainActivity.this, "Sensor control mode on", Toast.LENGTH_SHORT).show();
                        sensorRegistered = false;
                        sensorMgr.unregisterListener(MainActivity.this);
                        controlUp.setClickable(true);
                        controlDown.setClickable(true);
                        controlLeft.setClickable(true);
                        controlRight.setClickable(true);
                    }
                }
            }
        });
        sensorMgr = (SensorManager)getSystemService(SENSOR_SERVICE);

        btnDebug = (ToggleButton) findViewById(R.id.button_debug);
        btnDebug.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    D = true;
                else
                    D = false;
            }
        });

        viewRx = (TextView) findViewById(R.id.viewRx); viewRx.setMovementMethod(new ScrollingMovementMethod());

    }

    @Override
    public synchronized void onPause() {
        super.onPause();
        if (D) Log.e(TAG, "- ON PAUSE -");
        if(sensorRegistered)
            sensorMgr.unregisterListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (D) Log.e(TAG, "-- ON STOP --");
        if(sensorRegistered)
            sensorMgr.unregisterListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatService != null)
            mChatService.stop();
        if (D)
            Log.e(TAG, "--- ON DESTROY ---");
        if(sensorRegistered)
            sensorMgr.unregisterListener(this);
    }

    private void ensureDiscoverable() {
        if (D) Log.d(TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    private void sendMessage(String message, boolean ack) {
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Log.d(TAG, R.string.not_connected + "");
            return;
        }
        if (message.length() > 0) {
//            if(ack)
//                msgCounter ++;
//            message = msgCounter + "," + message;
            byte[] send = message.getBytes();
            mChatService.write(send);
            Log.d(TAG, "MSG sent: " + new String(send));
        }
    }

    private final void setStatus(int resId, boolean status) {
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle(resId);
        if(btStatus != null && status)
            btStatus.setIcon(R.drawable.bt_on);
        else if(btStatus != null)
            btStatus.setIcon(R.drawable.bt_off);
    }

    private final void setStatus(CharSequence subTitle, boolean status) {
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle(subTitle);
        if(btStatus != null && status)
            btStatus.setIcon(R.drawable.bt_on);
        else if(btStatus != null)
            btStatus.setIcon(R.drawable.bt_off);
    }

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if (D)
                        Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to,
                                    mConnectedDeviceName), true);
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting, false);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                            setStatus("Listening ...", false);
                            break;
                        case BluetoothChatService.STATE_NONE:
                            setStatus(R.string.title_not_connected, false);
                            break;
                        default:
                            setStatus(R.string.title_not_connected, false);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
//                    byte[] writeBuf = (byte[]) msg.obj;
//                    String writeMessage = new String(writeBuf);
                    break;
                case MESSAGE_READ:
                   // String ack ="-,";
                   // MainActivity.this.sendMessage(ack, true);
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    updateMap(readMessage, true);
                    if(D)
                        Toast.makeText(MainActivity.this, "Receive from BT: " + readMessage, Toast.LENGTH_SHORT).show();

                    if(readMessage.length() >= 4) {
                        if ((readMessage.substring(0, 3)).equals("[V]")) {
                            viewRx.append(readMessage.substring(3) + "\n");
                            final int scrollAmount = viewRx.getLayout().getLineTop(viewRx.getLineCount()) - viewRx.getHeight(); // if there is no need to scroll, scrollAmount will be <=0
                            if (scrollAmount > 0) viewRx.scrollTo(0, scrollAmount); else viewRx.scrollTo(0, 0);
                        }
                    }
                    break;
                case MESSAGE_DEVICE_NAME:
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public void updateMap(String mapInfo, boolean init){
        mapSurface.robotChange(mapInfo);
        if(!init) {
           // sendMessage("Robot_init(" + mapInfo.substring(5, 18)+")", true);
            initRobotMenu.setIcon(R.drawable.location_off);
            initRobotMenu.setEnabled(false);
        }
        Log.d(TAG, "Map changed.");
    }

    public void SendStartPos(int x,int y,String head) {
        sendMessage("robot_init(x=" + x + ",y=" + y + ",head=" + head + ")", true);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (D)
            Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    setupChat();
                } else {
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
            default:
                //onActivityResult_VoiceRecognition(requestCode, requestCode, data);
                break;
        }
    }

    private void connectDevice(Intent data, boolean secure) {
        String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        mChatService.connect(device, secure);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        btStatus = menu.findItem(R.id.bt_status);
        initRobotMenu = menu.findItem(R.id.startRobot);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent serverIntent;
        switch (item.getItemId()) {
            case R.id.startRobot:
                RobotSettingActivity initRobot = new RobotSettingActivity();
                initRobot.show(getSupportFragmentManager(), "initRobot");
                return true;
            case R.id.connect_scan:
                serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                return true;
            case R.id.settings:
                KeysSettingActivity ks = new KeysSettingActivity();
                ks.show(getSupportFragmentManager(), "settings");
                return true;
            case R.id.discoverable:
                ensureDiscoverable();
                return true;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            Log.d(TAG, "SENSOR: " + x + ", " + y);

            if(x < -3) {
                if(D)
                    Toast.makeText(MainActivity.this, "Turn right", Toast.LENGTH_SHORT).show();
                sendMessage(spf.getString(SET_RIGHT, SET_RIGHT_DEFAULT), true);
            }
            else if(x > 3) {
                if(D)
                    Toast.makeText(MainActivity.this, "Turn left", Toast.LENGTH_SHORT).show();
                sendMessage(spf.getString(SET_LEFT, SET_LEFT_DEFAULT), true);
            }

            if(y < -3) {
                if(D)
                    Toast.makeText(MainActivity.this, "Go forward", Toast.LENGTH_SHORT).show();
                sendMessage(spf.getString(SET_UP, SET_UP_DEFAULT), true);
            }
            else if(y > 5) {
                if(D)
                    Toast.makeText(MainActivity.this, "Go backward", Toast.LENGTH_SHORT).show();
                sendMessage(spf.getString(SET_DOWN, SET_DOWN_DEFAULT), true);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    class AutoThread extends Thread {
        @Override
        public void run(){
            while(true)
                if(autoMode) {
                    sendMessage("GRID", true);
                    try {
                        Thread.sleep(1000);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
        }
    }


}

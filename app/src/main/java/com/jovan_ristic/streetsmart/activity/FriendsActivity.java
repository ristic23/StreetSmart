package com.jovan_ristic.streetsmart.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jovan_ristic.streetsmart.R;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class FriendsActivity extends AppCompatActivity implements View.OnClickListener
{
    private final int BLUETOOTH_CODE = 56;
    ImageView btnProfile, btnMap, btnRankList;


    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mmSocket;
    private BluetoothDevice mmDevice;
    private OutputStream mmOutputStream;
    private InputStream mmInputStream;
    private Thread workerThread;
    private  byte[] readBuffer;
    private int readBufferPosition;
    private int counter;
    private volatile boolean stopWorker;

    private ImageView bluetootheSwitcher;
    private boolean bluetoothOnOff;
    private ListView listViewBluetooth;
    private List<BluetoothDevice> devicesBluetooth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            setContentView(R.layout.activity_friends);
        }
        catch(Exception | OutOfMemoryError e)
        {
            Toast.makeText(this, getResources().getString(R.string.errorMsg), Toast.LENGTH_SHORT).show();
        }
        bluetoothOnOff = false;
        devicesBluetooth = new ArrayList<>();
//        Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//        startActivityForResult(turnOn, BLUETOOTH_CODE);
        initLayout();
        initListeners();

    }

    private void initListeners() {
        btnProfile.setOnClickListener(this);
        btnMap.setOnClickListener(this);
        btnRankList.setOnClickListener(this);
        bluetootheSwitcher.setOnClickListener(this);
    }

    private void initLayout() {
        btnProfile = findViewById(R.id.profile_btn);
        btnMap = findViewById(R.id.map_btn);
        btnRankList = findViewById(R.id.rankList_btn);

        bluetootheSwitcher = findViewById(R.id.bluetoothSwitcher);

        listViewBluetooth = findViewById(R.id.listFriendsRecyclerView);
    }


    @Override
    public void onClick(View view)
    {
        Intent intent;
        switch (view.getId())
        {
            case R.id.bluetoothSwitcher:
            {
                if(!bluetoothOnOff) {
                    findBT();
                }
                else
                {
                    try {
                    closeBT();
                }
                catch (IOException e)
                {
                    //
                }
                }
                break;
            }
            case R.id.profile_btn:
            {
                intent = new Intent(FriendsActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.map_btn:
            {
                intent = new Intent(FriendsActivity.this, MapActivity.class);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.rankList_btn:
            {
                intent = new Intent(FriendsActivity.this, RankListActivity.class);
                startActivity(intent);
                finish();
                break;
            }
        }
    }
    void findBT()
    {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null)
        {
            Toast.makeText(this,"No bluetooth adapter available", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!mBluetoothAdapter.isEnabled())
        {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0)
        {
            List<String> items = new ArrayList<>();
            for(BluetoothDevice device : pairedDevices)
            {
                items.add(device.getName());
                devicesBluetooth.add(device);

            }
            ArrayAdapter<String> itemsAdapter =
                    new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
            listViewBluetooth.setAdapter(itemsAdapter);
            listViewBluetooth.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    mmDevice = devicesBluetooth.get(i);
                    try {
                        openBT();
                        sendData("BFFs???");
                    }
                    catch (IOException e)
                    {
                        //
                    }
                }
            });
        }
        bluetoothOnOff =true;
    }

    void openBT() throws IOException
    {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();

        beginListenForData();


    }

    void beginListenForData()
    {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        int bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == delimiter)
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {
//                                            myLabel.setText(data);
                                            Toast.makeText(FriendsActivity.this,data, Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                }
                                else
                                {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    void sendData(String message) throws IOException
    {
        String msg = message;
        msg += "\n";
        mmOutputStream.write(msg.getBytes());

    }

    void closeBT() throws IOException
    {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
            bluetoothOnOff = false;
        }
        catch (NullPointerException e)
        {
            //
        }
    }
}

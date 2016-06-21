package com.example.kashif.lab3;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends AppCompatActivity {

    TextView LatValue;
    TextView LongValue;
    TextView DistValue;
    TextView AvgSpeedValue;
    Button startService;
    Button stopService;
    Button update;
    Button close;
    Button enable;


    double latitudeValue,longitudeValue,Distance,speed;
    boolean ServiceStopped = false;
    IMyService Service;
    protected LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Referencing

        LatValue = (TextView) findViewById(R.id.LatValue);
        LongValue = (TextView) findViewById(R.id.LongValue);
        DistValue = (TextView) findViewById(R.id.DistValue);
        AvgSpeedValue = (TextView) findViewById(R.id.AvgSpeedValue);
        enable = (Button) findViewById(R.id.enableGPS);
        startService = (Button) findViewById(R.id.StartService);
        stopService = (Button) findViewById(R.id.StopTheService);
        update = (Button) findViewById(R.id.Update);
        close = (Button) findViewById(R.id.close);

        hideButtons();

        // Checking ths Status of the GPS
        enable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);
                        builder.setTitle("GPS is not Active");
                        builder.setMessage("Please Enable GPS");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);

                            }
                        });
                        Dialog alert = builder.create();
                    alert.setCanceledOnTouchOutside(false);
                    alert.show();

                } else {
                    Toast.makeText(getBaseContext(), "GPS is already enable", Toast.LENGTH_SHORT).show();
                    showButtons();
                }

            }
        });

        startService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServiceStopped = false;

                // Binding to the Service
                ServiceBinding();

            }
        });

        // Updating the values of the Latitude, Longitude , Distance and Speed
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ServiceStopped) {
                    updateValues();
                }else{
                    Toast.makeText(getApplicationContext(), "Service not started yet", Toast.LENGTH_SHORT).show();
                }

            }
        });

        // Stopping the service
        stopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    ServiceStopped = true;
                    unBindAndStop();
                }catch (Exception e) {
                    Toast.makeText(getApplicationContext(),"Service Disconnected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });

    }


    private void ServiceBinding() {
        Intent intent = new Intent(Main.this,MyService.class);
        bindService(intent, myServiceConnection, Context.BIND_AUTO_CREATE);

    }
    private void updateValues() {
        try {

            latitudeValue = Service.getLatitude();
            LatValue.setText(String.valueOf(latitudeValue));
            longitudeValue = Service.getLongitude();
            LongValue.setText(String.valueOf(longitudeValue));
            Distance = Service.getDistance();
            DistValue.setText(String.valueOf(Distance));
            speed = Service.getSpeed();
            AvgSpeedValue.setText(String.valueOf(speed));

        } catch (RemoteException e) {
            e.printStackTrace();
        }


    }
    private void unBindAndStop() {
        Intent intent = new Intent(getBaseContext(), MyService.class);
        stopService (intent);
        unbindService(myServiceConnection);
    }

    private void hideButtons() {
        enable.setEnabled(true);
        startService.setEnabled(false);
        stopService.setEnabled(false);
        update.setEnabled(false);
        close.setEnabled(false);
    }

    private void showButtons() {
        enable.setEnabled(false);
        startService.setEnabled(true);
        stopService.setEnabled(true);
        update.setEnabled(true);
        close.setEnabled(true);
    }


    private ServiceConnection myServiceConnection = new ServiceConnection() {

        // after establishing the connection with the service
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            Toast.makeText(getApplicationContext(),"Service Connected",Toast.LENGTH_SHORT).show();
            Service = IMyService.Stub.asInterface(service);
        }

        // Disconnecting the connection with the service
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Service = null;
            Toast.makeText(getApplicationContext(),"Service Disconnected",Toast.LENGTH_SHORT).show();
        }
    } ;

}


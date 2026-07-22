package com.mytechia.robobo.framework.remotecontrol;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.remote_control.remotemodule.IRemoteControlModule;

import com.mytechia.robobo.framework.remotecontrol.remote_mdns.RoboboMDNSServer;

public class MDNSTestActivity extends AppCompatActivity {
    private static final String TAG="ServerTestActivity";
    private RoboboManager manager;
    IRemoteControlModule remoteModule;
    private TextView textview;

    private RoboboMDNSServer roboboMDNSServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        textview = (TextView) findViewById(R.id.textView);
        startapp();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        roboboMDNSServer.stop();
    }

    public void startapp(){
        roboboMDNSServer = new RoboboMDNSServer();
        roboboMDNSServer.start(getApplicationContext());
    }
}
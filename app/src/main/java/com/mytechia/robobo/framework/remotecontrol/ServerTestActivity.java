package com.mytechia.robobo.framework.remotecontrol;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.exception.ModuleNotFoundException;
import com.mytechia.robobo.framework.hri.sound.noiseMetering.INoiseMeterModule;
import com.mytechia.robobo.framework.hri.sound.pitchDetection.IPitchDetectionModule;
import com.mytechia.robobo.framework.hri.sound.soundDispatcherModule.ISoundDispatcherModule;
import com.mytechia.robobo.framework.hri.sound.soundStream.ISoundStreamModule;
import com.mytechia.robobo.framework.remote_control.remotemodule.IRemoteControlModule;
import com.mytechia.robobo.framework.remote_control.remotemodule.Status;
import com.mytechia.robobo.framework.remotecontrol.ws.WebsocketRemoteControlModule;
import com.mytechia.robobo.framework.service.RoboboServiceHelper;
import com.mytechia.robobo.rob.BluetoothRobInterfaceModule;
import com.mytechia.robobo.rob.util.RoboboDeviceSelectionDialog;



import org.java_websocket.client.WebSocketClient;

import java.net.URI;
import java.net.URISyntaxException;


public class ServerTestActivity extends AppCompatActivity {
    private static final String TAG="ServerTestActivity";

    public ServerTestActivity() throws URISyntaxException {
    }
    private RoboboServiceHelper roboboHelper;
    private RoboboManager manager;


    private TextView tv;
    URI uri = new URI("ws://localhost:40404");
    Integer i = 1;
    private ProgressDialog waitDialog;
    private IRemoteControlModule remoteModule;
    private WebsocketRemoteControlModule wsRemoteProxy;
    private WebSocketClient ws ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sample);
        this.tv = (TextView) findViewById(R.id.textView) ;

        RoboboServiceHelper serviceHelper = new RoboboServiceHelper(this, new RoboboServiceHelper.Listener() {
            @Override
            public void onRoboboManagerStarted(RoboboManager roboboManager) {
                manager = roboboManager;
                startapp();

                try {
                    remoteModule = manager.getModuleInstance(IRemoteControlModule.class);
                    wsRemoteProxy = new WebsocketRemoteControlModule();
                    remoteModule.registerRemoteControlProxy(wsRemoteProxy);
                } catch (ModuleNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(Throwable ex) {

            }
        });
        Bundle options = new Bundle();
        serviceHelper.bindRoboboService(options);
    }

    public void startapp(){
        Log.d("TEST","TEST");
    }
}
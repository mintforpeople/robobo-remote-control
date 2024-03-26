package com.mytechia.robobo.framework.remotecontrol;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.exception.ModuleNotFoundException;
import com.mytechia.robobo.framework.remote_control.remotemodule.IRemoteControlModule;
import com.mytechia.robobo.framework.remotecontrol.ws.WebsocketRemoteControlModule;
import com.mytechia.robobo.framework.service.RoboboServiceHelper;

import org.java_websocket.WebSocket;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.DefaultSSLWebSocketServerFactory;
import org.java_websocket.server.WebSocketServer;

public class ServerTestActivity extends AppCompatActivity {
    private static final String TAG="ServerTestActivity";
    private RoboboManager manager;
    IRemoteControlModule remoteModule;
    WebsocketRemoteControlModule wsRemoteProxy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        RoboboServiceHelper serviceHelper = new RoboboServiceHelper(this, new RoboboServiceHelper.Listener() {
            @Override
            public void onRoboboManagerStarted(RoboboManager roboboManager) {
                manager = roboboManager;
                startapp();


            }

            @Override
            public void onError(Throwable ex) {

            }
        });
        Bundle options = new Bundle();
        serviceHelper.bindRoboboService(options);

    }


    public void startapp(){
        try {
            remoteModule = manager.getModuleInstance(IRemoteControlModule.class);
            wsRemoteProxy = new WebsocketRemoteControlModule();
            remoteModule.registerRemoteControlProxy(wsRemoteProxy);
            Log.d("TEST","TEST");
        } catch (ModuleNotFoundException e) {
            e.printStackTrace();
        }
    }
}
package com.mytechia.robobo.framework.remotecontrol;

import static java.lang.String.format;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.exception.ModuleNotFoundException;
import com.mytechia.robobo.framework.remote_control.remotemodule.IRemoteControlModule;
import com.mytechia.robobo.framework.remotecontrol.remote_mdns.IRemoteMDNSModule;
import com.mytechia.robobo.framework.remotecontrol.remote_mdns.RemoteMDNSModule;
import com.mytechia.robobo.framework.remotecontrol.robobo_discovery.IRoboboDiscoveryModule;
import com.mytechia.robobo.framework.remotecontrol.robobo_discovery.RoboboDiscoveryModule;
import com.mytechia.robobo.framework.service.RoboboServiceHelper;

public class ServerTestActivity extends AppCompatActivity {
    private static final String TAG="ServerTestActivity";
    private RoboboManager manager;
    IRemoteControlModule remoteModule;
    private TextView textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        textview = (TextView) findViewById(R.id.textView);

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
            RoboboDiscoveryModule discoveryModule = (RoboboDiscoveryModule)manager.getModuleInstance(IRoboboDiscoveryModule.class);
            RemoteMDNSModule mdnsModule = (RemoteMDNSModule) manager.getModuleInstance(IRemoteMDNSModule.class);

            mdnsModule.setRoboboBTName("ROB-7VH");
            // We EXPLICITLY start the mdns server after setting the BT Name!!!
            mdnsModule.startMDNSServer();
        } catch (ModuleNotFoundException e) {
            throw new RuntimeException(e);
        }
    }



}
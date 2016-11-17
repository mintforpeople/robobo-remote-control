package com.mytechia.robobo.framework.remotecontrol;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.TextureView;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.exception.ModuleNotFoundException;


import com.mytechia.robobo.framework.remote_control.Command;
import com.mytechia.robobo.framework.remote_control.GsonConverter;
import com.mytechia.robobo.framework.remote_control.IRemoteControlModule;
import com.mytechia.robobo.framework.remote_control.Status;
import com.mytechia.robobo.framework.service.RoboboServiceHelper;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.WebSocketListener;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.w3c.dom.Text;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;


public class SampleActivity extends AppCompatActivity implements ITestListener  {
    private static final String TAG="RemoteControlActivity";

    public SampleActivity() throws URISyntaxException {
    }
    private RoboboServiceHelper roboboHelper;
    private RoboboManager roboboManager;

    private TextView tv;
    URI uri = new URI("ws://localhost:22226");
    Integer i = 1;




        private IRemoteControlModule remoteModule;
    private DummyModule dummyModule = new DummyModule();
    private WebSocketClient ws ;


    public boolean onTouchEvent(MotionEvent event){

        Status s = new Status("TapNumber");
        s.putContents("Taps",i.toString());
        i = i+1;
        remoteModule.postStatus(s);

        return true;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ws = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.d(TAG,"on open client");
            }

            @Override
            public void onMessage(String message) {
                Log.d(TAG,"on message client");
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.d(TAG,"on close client");
            }

            @Override
            public void onError(Exception ex) {

            }
        };

        ws.connect();

        setContentView(R.layout.activity_sample);
        this.tv = (TextView) findViewById(R.id.textView) ;

        roboboHelper = new RoboboServiceHelper(this, new RoboboServiceHelper.Listener() {
            @Override
            public void onRoboboManagerStarted(RoboboManager robobo) {

                //the robobo service and manager have been started up
                roboboManager = robobo;


                //dismiss the wait dialog


                //start the "custom" robobo application
                startRoboboApplication();

            }

            @Override
            public void onError(String errorMsg) {

                final String error = errorMsg;


            }

        });

        //start & bind the Robobo service
        Bundle options = new Bundle();
        roboboHelper.bindRoboboService(options);
    }
    private void startRoboboApplication() {

        try {

            this.remoteModule = this.roboboManager.getModuleInstance(IRemoteControlModule.class);


        } catch (ModuleNotFoundException e) {
            e.printStackTrace();
        }

        dummyModule.subscribe(this);

        remoteModule.registerCommand("C1",dummyModule);
        remoteModule.registerCommand("C2",dummyModule);






    }


    @Override
    public void onThingsHappen(final String things) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

               tv.setText(things);


            }
        });
    }
}
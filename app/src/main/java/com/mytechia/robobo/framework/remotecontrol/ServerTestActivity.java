package com.mytechia.robobo.framework.remotecontrol;

import static java.lang.String.format;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.mytechia.robobo.framework.LogLvl;
import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.exception.ModuleNotFoundException;
import com.mytechia.robobo.framework.remote_control.remotemodule.Command;
import com.mytechia.robobo.framework.remote_control.remotemodule.GsonConverter;
import com.mytechia.robobo.framework.remote_control.remotemodule.IRemoteControlModule;
import com.mytechia.robobo.framework.remote_control.remotemodule.Status;
import com.mytechia.robobo.framework.remotecontrol.ws.WebsocketRemoteControlModule;
import com.mytechia.robobo.framework.service.RoboboServiceHelper;

import org.java_websocket.WebSocket;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.DefaultSSLWebSocketServerFactory;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Properties;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

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

        AssetManager assetManager = manager.getApplicationContext().getAssets();
        Properties properties = new Properties();
        try {
            InputStream inputStream = assetManager.open("remote.properties");
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        WebSocketServerImpl wsServer = new WebSocketServerImpl(Integer.parseInt(properties.getProperty("wsport","40404")));
        wsServer.setWebSocketFactory( new DefaultSSLWebSocketServerFactory( getSSLConextFromAndroidKeystore(manager.getApplicationContext()) ));
        wsServer.start();
    }

    private SSLContext getSSLConextFromAndroidKeystore(Context c) {
        // load up the key store
        String storePassword = "robpass";
        String keyPassword = "robpass";

        KeyStore ks;
        SSLContext sslContext;
        try {
            KeyStore keystore = KeyStore.getInstance("BKS");
            InputStream in = c.getResources().openRawResource(R.raw.keystore);
            try {
                keystore.load(in, storePassword.toCharArray());
            } finally {
                in.close();
            }
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("X509");
            keyManagerFactory.init(keystore, keyPassword .toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
            tmf.init(keystore);

            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), tmf.getTrustManagers(), null);
        } catch (KeyStoreException | IOException | CertificateException | NoSuchAlgorithmException |
                 KeyManagementException | UnrecoverableKeyException e) {
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
        return sslContext;
    }

    private class WebSocketServerImpl extends  WebSocketServer {

        public WebSocketServerImpl(int port) {
            super(new InetSocketAddress(port));
        }

        @Override
        public void onOpen(WebSocket conn, ClientHandshake handshake) {
            manager.log(LogLvl.DEBUG, TAG, format("Open websocket connection %s", conn.getRemoteSocketAddress()));
        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {
            manager.log(LogLvl.DEBUG, TAG, format("Closed websocket connection"));
        }

        @Override
        public void onMessage(WebSocket webSocketConnection, String message) {
            if((message==null) || (message.length()==0)){
                return;
            }
            manager.log(LogLvl.TRACE, TAG, format("Received message:%s|%s| from %s", message, message.substring(10), webSocketConnection.getRemoteSocketAddress()));
        }

        @Override
        public void onError(WebSocket conn, Exception ex) {
            ex.printStackTrace();
            if (conn != null) {
                Log.e(TAG, format("Error WebSocket[local=%s, remote=%s]", conn.getLocalSocketAddress(), conn.getRemoteSocketAddress()), ex);
                manager.log(LogLvl.ERROR, TAG, format("Error WebSocket[local=%s, remote=%s]", conn.getLocalSocketAddress(), conn.getRemoteSocketAddress()));

            }else{
                Log.e(TAG, "Error WebSocket, connection is null");
                manager.log(LogLvl.ERROR, TAG, "Error WebSocket, connection is null");
            }
        }
    }
}
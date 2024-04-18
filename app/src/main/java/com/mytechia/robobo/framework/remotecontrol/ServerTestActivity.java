package com.mytechia.robobo.framework.remotecontrol;

import static java.lang.String.format;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

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
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import fi.iki.elonen.NanoHTTPD;

public class ServerTestActivity extends AppCompatActivity {
    private static final String TAG="ServerTestActivity";
    private RoboboManager manager;
    IRemoteControlModule remoteModule;
    WebsocketRemoteControlModule wsRemoteProxy;

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
        AssetManager assetManager = manager.getApplicationContext().getAssets();
        Properties properties = new Properties();
        try {
            InputStream inputStream = assetManager.open("remote.properties");
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        WebSocketServerImpl wsServer = new WebSocketServerImpl(Integer.parseInt(properties.getProperty("wssport","44304")));

        String KEYSTORE_TYPE = "BKS";
        String KEYSTORE_PASS = "robobo-pass";
        String KEY_PASS = "robobo-pass";
        try{
            InputStream is = getApplicationContext().getResources().openRawResource(R.raw.robobowss_test);
            KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
            keyStore.load(is, KEYSTORE_PASS.toCharArray());
            is.close();

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("X509");
            kmf.init(keyStore, KEY_PASS.toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
            tmf.init(keyStore);

            SSLContext sslContext = null;
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            wsServer.setWebSocketFactory(new DefaultSSLWebSocketServerFactory(sslContext));
            wsServer.start();

            System.out.println("Finished starting WSS server");
        } catch (Exception ex){
            System.out.println(ex);
        }
    }
    /*
    private SSLContext getSSLContextFromAndroidKeystore(Context c) {
        // load up the key store
        String storePassword = "robobowss-pass";
        String keyPassword = "robobowss-pass";

        KeyStore ks;
        SSLContext sslContext;
        try {
            KeyStore keystore = KeyStore.getInstance("BKS");
            InputStream in = c.getResources().openRawResource(R.raw.robobowss_keystore);
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
    }*/

    private class WebSocketServerImpl extends  WebSocketServer {

        public WebSocketServerImpl(int port) {
            super(new InetSocketAddress(port));
        }

        @Override
        public void onOpen(WebSocket conn, ClientHandshake handshake) {
            System.out.println("New connection from: " + conn.getRemoteSocketAddress());
        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {
            System.out.println("Closed connection to: " + conn.getRemoteSocketAddress());
        }

        @Override
        public void onMessage(WebSocket conn, String message) {
            System.out.println("Received message: " + message + " from: " + conn.getRemoteSocketAddress());
            conn.send("Good boy, " + message + "!");
        }

        @Override
        public void onError(WebSocket conn, Exception ex) {
            ex.printStackTrace();
        }
    }
}
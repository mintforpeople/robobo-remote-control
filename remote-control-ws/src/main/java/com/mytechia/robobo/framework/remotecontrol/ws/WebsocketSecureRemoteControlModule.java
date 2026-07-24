/*******************************************************************************
 * Copyright 2016 Mytech Ingenieria Aplicada <http://www.mytechia.com>
 * Copyright 2016 Luis Llamas <julio.gomez@mytechia.com>
 * <p>
 * This file is part of Robobo Remote Control Module.
 * <p>
 * Robobo Remote Control Module is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * Robobo Remote Control Module is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with Robobo Remote Control Module.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.mytechia.robobo.framework.remotecontrol.ws;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.IModule;
import com.mytechia.robobo.framework.LogLvl;
import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.remote_control.remotemodule.Command;
import com.mytechia.robobo.framework.remote_control.remotemodule.GsonConverter;
import com.mytechia.robobo.framework.remote_control.remotemodule.IRemoteControlModule;
import com.mytechia.robobo.framework.remote_control.remotemodule.IRemoteControlProxy;
import com.mytechia.robobo.framework.remote_control.remotemodule.Response;
import com.mytechia.robobo.framework.remote_control.remotemodule.Status;
import org.java_websocket.WebSocket;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.DefaultSSLWebSocketServerFactory;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import com.robobo.pki.RoboboManifest;
import com.robobo.pki.SSLContextFactory;

import static java.lang.String.format;


/**
 * Implementation of the remote control module using websockets
 */
public class WebsocketSecureRemoteControlModule implements IWebsocketSecureRemoteControlModule, IRemoteControlProxy {

    public static final String PASSWORD = "PASSWORD";

    private RoboboManager roboboManager;

    private String TAG = "Websocket Secure RC Module";

    private String roboboBTName = "ROB-???";

    //all modifications to this collection must be synchronized
    private HashMap<Integer,WebSocket> connections= new HashMap<>();

    //all modifications to this collection must be synchronized
    private HashMap<Integer,WebSocket> connectionsAuthenticated= new HashMap<>();

    private IRemoteControlModule remoteControlModule;

    private WebSocketServer webSocketServer;
    private WebSocketServer webSocketSecureServer;

    private boolean active = false;
    private boolean shuttingDown = false;

    private Properties properties;

    public WebsocketSecureRemoteControlModule() {}

    @Override
    public String getRoboboBTName() {
        return roboboBTName;
    }

    @Override
    public void setRoboboBTName(String roboboBTName) {
        String oldName = this.roboboBTName;
        this.roboboBTName = roboboBTName;

        if (roboboBTName != null && !roboboBTName.trim().isEmpty() && !roboboBTName.equals("ROB-???")) {
            if (!roboboBTName.equals(oldName) || webSocketSecureServer == null) {
                startWssServer();
            }
        }
    }


    @Override
    public void notifyStatus(Status status) {

        if (this.active) {

            // Serialize status to send via websocket
            String jsonStatus = GsonConverter.statusToJson(status);

            roboboManager.log(TAG, "Status: " + jsonStatus);

            Iterator<Map.Entry<Integer, WebSocket>> it = connectionsAuthenticated.entrySet().iterator();

            // Iterate ver all the authenticated connections
            while (it.hasNext()) {

                Map.Entry<InetSocketAddress, WebSocket> pair = (Map.Entry) it.next();
                WebSocket webSocket = pair.getValue();

                // If the connection is closed, pass
                if (webSocket.isClosed()) {
                    continue;
                }

                // Else try to send the status
                try {
                    webSocket.send(jsonStatus);
                } catch (WebsocketNotConnectedException e) {
                    Log.e(TAG, format("Error notifying status: %s", jsonStatus), e);
                    roboboManager.log(LogLvl.ERROR, TAG, format("Error notifying status: %s", jsonStatus));
                }

            }

        }

    }

    @Override
    public void notifyReponse(Response response) {

        if (this.active) {

            String jsonResponse = GsonConverter.responseToJson(response);

            roboboManager.log(TAG, "Response: " + jsonResponse);

            Iterator<Map.Entry<Integer, WebSocket>> it = connectionsAuthenticated.entrySet().iterator();
            // Iterate ver all the authenticated connections

            while (it.hasNext()) {

                Map.Entry<Integer, WebSocket> pair = it.next();

                WebSocket webSocket = pair.getValue();

                // If the connection is closed, pass
                if (webSocket.isClosed()) {
                    continue;
                }

                // Else try to send the status
                try {
                    webSocket.send(jsonResponse);
                } catch (WebsocketNotConnectedException e) {
                    Log.e(TAG, format("Error notifying response: %s", jsonResponse), e);
                    roboboManager.log(LogLvl.ERROR, TAG, format("Error notifying response: %s", jsonResponse));
                }
            }

        }

    }

    /**
     * Put a connection in the active connections list
     * @param conn Websocket connection
     */
    private synchronized void putSocketConnection(WebSocket conn) {

        connections.put(conn.hashCode(), conn);

    }
    /**
     * Put a connection in the authenticated connections list
     * @param conn Websocket connection
     */
    private synchronized void putAuthenticatedConnection(WebSocket conn) {

        connectionsAuthenticated.put(conn.hashCode(), conn);

    }

    /**
     * Remove a connection from the authenticated connections list
     * @param conn Websocket connection to be removed
     */
    private synchronized void removeSocketConnection(WebSocket conn) {

        connections.remove(conn.hashCode());

        connectionsAuthenticated.remove(conn.hashCode());

        if (connections.size() == 0) {
            setActive(false);
        }

    }


    private void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Notify that the module is shutting down
     * @param shuttingDown True if the module is shutting down
     */
    private void setShuttingDown(boolean shuttingDown) {
        this.shuttingDown = shuttingDown;
        if (isShuttingDown()) {
            setActive(false);
        }
    }

    private boolean isShuttingDown() {
        return this.shuttingDown;
    }

    /**
     * Websocket server class implementation
     */
    private class WebSocketServerImpl extends  WebSocketServer {

        public WebSocketServerImpl(int port) {
            super(new InetSocketAddress(port));
        }

        @Override
        public void onOpen(WebSocket conn, ClientHandshake handshake) {

            if (!isShuttingDown()) {
                // Store connection  on the list
                putSocketConnection(conn);
                // Send notification of a new connection
                notifyConnection(conn.hashCode());

                setActive(true);

                roboboManager.log(LogLvl.DEBUG, TAG, format("Open websocket connection %s", conn.getRemoteSocketAddress()));

            }

        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {

            if (!isShuttingDown()) {
                // remove connection from the list
                removeSocketConnection(conn);
                // Send notification of the disconnection
                notifyDisconnection(conn.hashCode());
                Log.d("WSS", "Closed: " + code + " / " + reason + " / remote=" + remote);
                roboboManager.log(LogLvl.DEBUG, TAG, format("Closed websocket connection"));
            }
        }

        @Override
        public void onMessage(WebSocket webSocketConnection, String message) {


            if(remoteControlModule==null){
                return;
            }

            if((message==null) || (message.length()==0)){
                return;
            }

            roboboManager.log(LogLvl.TRACE, TAG, format("Received message:%s|%s| from %s", message, message.substring(10), webSocketConnection.getRemoteSocketAddress()));

            // Check if we are receiving an authentication attempt
            if (message.startsWith(PASSWORD)) {

                // If the password is correct, add connection to the authenticated connections list
                if (message.substring(10).equals(remoteControlModule.getPassword())) {

                    putAuthenticatedConnection(webSocketConnection);

                    roboboManager.log(LogLvl.DEBUG, TAG, connectionsAuthenticated.toString());
                }
                // Else send a status with the error
                else{

                    roboboManager.log(LogLvl.ERROR, TAG, "Incorrect password");

                    Status statusError= new Status("ERROR");

                    statusError.putContents("error", "Incorrect password");

                    WebsocketSecureRemoteControlModule.this.notifyStatus(statusError);

                    WebsocketSecureRemoteControlModule.this.notifyStatus(new Status("DIE"));

                }
            } else if (connectionsAuthenticated.containsKey(webSocketConnection.hashCode())) {

                // Deserialize command
                Command command = GsonConverter.jsonToCommand(message);
                // Queue command for execution
                remoteControlModule.queueCommand(command);

            }


        }

        @Override
        public void onError(WebSocket conn, Exception ex) {
            ex.printStackTrace();
            if (conn != null) {
                Log.e(TAG, format("Error WebSocket[local=%s, remote=%s]", conn.getLocalSocketAddress(), conn.getRemoteSocketAddress()), ex);
                roboboManager.log(LogLvl.ERROR, TAG, format("Error WebSocket[local=%s, remote=%s]", conn.getLocalSocketAddress(), conn.getRemoteSocketAddress()));

            }else{
                Log.e(TAG, "Error WebSocket, connection is null");
                roboboManager.log(LogLvl.ERROR, TAG, "Error WebSocket, connection is null");
            }
        }

        @Override
        public void onStart() {
            try {
                roboboManager.log(LogLvl.DEBUG, TAG, format("Starting websocket server on %s:%s", getLocalIpv4().toString(), getAddress().getPort()));
            } catch (SocketException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private String getLocalIpv4() throws SocketException {
        for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {
            for (InetAddress addr : Collections.list(ni.getInetAddresses())) {
                if (!addr.isLoopbackAddress() && addr instanceof Inet4Address) {
                    return addr.getHostAddress();
                }
            }
        }
        return null;
    }

    @Override
    public void startWssServer() {
        if (roboboManager == null) {
            Log.e(TAG, "Cannot start WSS server: RoboboManager is null.");
            return;
        }

        if (roboboBTName == null || roboboBTName.trim().isEmpty() || roboboBTName.equals("ROB-???")) {
            roboboManager.log(LogLvl.DEBUG, TAG, "WSS server deferred: waiting for valid Robobo BT Name to be set.");
            return;
        }

        if (webSocketSecureServer != null) {
            try {
                webSocketSecureServer.stop();
            } catch (Exception ignored) {}
        }

        Context context = roboboManager.getApplicationContext();
        String storePassword = properties != null ? properties.getProperty("keystore_pass", "robobo-pass") : "robobo-pass";
        int wssPort = properties != null ? Integer.parseInt(properties.getProperty("wssport", "44304")) : 44304;

        RoboboManifest manifest = null;
        try (InputStream manifestStream = context.getAssets().open("manifest.json")) {
            manifest = RoboboManifest.fromInputStream(manifestStream);
        } catch (Exception e) {
            // Optional manifest file
        }

        String targetRobotId = roboboBTName;

        try {
            InputStream bksIn = context.getResources().openRawResource(R.raw.robobo_local_ks);
            SSLContext sslContext = SSLContextFactory.createSSLContextFromBks(
                    bksIn,
                    storePassword.toCharArray(),
                    targetRobotId,
                    manifest
            );

            this.webSocketSecureServer = new WebSocketServerImpl(wssPort);
            this.webSocketSecureServer.setWebSocketFactory(new DefaultSSLWebSocketServerFactory(sslContext));
            this.webSocketSecureServer.setReuseAddr(true);
            this.webSocketSecureServer.start();

            roboboManager.log(LogLvl.DEBUG, TAG, "WSS server started using identity for: " + targetRobotId);
        } catch (Exception e) {
            Log.e(TAG, "Error starting WSS server", e);
            roboboManager.log(LogLvl.ERROR, TAG, "Error starting WSS server: " + e.getMessage());
        }
    }

    @Override
    public void startup(RoboboManager manager) throws InternalErrorException {

        this.remoteControlModule=manager.getModuleInstance(IRemoteControlModule.class);

        if(this.remoteControlModule==null){
            throw new InternalErrorException("No found instance IRemoteControlModule.");
        }

        this.roboboManager= manager;

        this.remoteControlModule.registerRemoteControlProxy(this);

        AssetManager assetManager = manager.getApplicationContext().getAssets();
        properties = new Properties();
        try {
            InputStream inputStream = assetManager.open("remote.properties");
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.webSocketServer= new WebSocketServerImpl(Integer.parseInt(properties.getProperty("wsport","40404")));
        this.webSocketServer.setReuseAddr(true);
        this.webSocketServer.start();

        startWssServer();
    }

    @Override
    public void shutdown() {

        setShuttingDown(true);

        try {
            Iterator it = connections.entrySet().iterator();
            while (it.hasNext()){
                Map.Entry pair = (Map.Entry)it.next();
                ((WebSocket)pair.getValue()).close(1000, "Normal closure");
            }
            webSocketServer.stop();
            if (webSocketSecureServer != null) {
                webSocketSecureServer.stop();
            }
        } catch (Exception ex) {
            Log.e(TAG, format("Error closing WebSocketServer", ex));
            roboboManager.log(LogLvl.ERROR, TAG, "Error closing WebSocketServer");
        }
    }

    protected void notifyConnection(int connNumber){
        if(this.remoteControlModule!=null){
            this.remoteControlModule.notifyConnection(connNumber);
        }

    }

    protected void notifyDisconnection(int connNumber){

        if(this.remoteControlModule!=null){
            this.remoteControlModule.notifyDisconnection(connNumber);
        }
    }

    @Override
    public String getModuleInfo() {
        return "WebSocket Remote Control Module";
    }

    @Override
    public String getModuleVersion() {
        return "1.1.1-SNAPSHOT";
    }
}

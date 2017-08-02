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

package com.mytechia.robobo.framework.remote_control.remotemodule.websocket;

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
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import static java.lang.String.format;



public class WebsocketRemoteControlModule implements IRemoteControlProxy, IModule {

    public static final String PASSWORD = "PASSWORD";

    private RoboboManager roboboManager;

    private String TAG = "Websocket RC Module";

    private HashMap<Integer,WebSocket> connections= new HashMap<>();

    private HashMap<Integer,WebSocket> connectionsAuthenticated= new HashMap<>();

    private IRemoteControlModule remoteControlModule;

    private WebSocketServer webSocketServer;

    private int port = 40404;



    public WebsocketRemoteControlModule() {}


    @Override
    public void notifyStatus(Status status) {

        String jsonStatus=GsonConverter.statusToJson(status);

        roboboManager.log(TAG,"Status: "+jsonStatus);

        Iterator<Map.Entry<Integer, WebSocket>> it = connectionsAuthenticated.entrySet().iterator();

        while (it.hasNext()) {

            Map.Entry<InetSocketAddress,WebSocket> pair = (Map.Entry) it.next();

            WebSocket webSocket = pair.getValue();

            if(webSocket.isClosed()){
                continue;
            }

            try {
                webSocket.send(jsonStatus);
            } catch (WebsocketNotConnectedException e) {
                Log.e(TAG, format("Error notifying status: %s", jsonStatus), e);
                roboboManager.log(LogLvl.ERROR, TAG, format("Error notifying status: %s", jsonStatus));
            }

        }

    }

    @Override
    public void notifyReponse(Response response) {

        String jsonResponse = GsonConverter.responseToJson(response);

        roboboManager.log(TAG,"Response: "+jsonResponse);

        Iterator<Map.Entry<Integer, WebSocket>> it = connectionsAuthenticated.entrySet().iterator();

        while (it.hasNext()) {

            Map.Entry<Integer, WebSocket> pair = it.next();

            WebSocket webSocket= pair.getValue();

            if(webSocket.isClosed()){
                continue;
            }

            try{
                webSocket.send(jsonResponse);
            } catch (WebsocketNotConnectedException e) {
                Log.e(TAG, format("Error notifying response: %s", jsonResponse), e);
                roboboManager.log(LogLvl.ERROR, TAG, format("Error notifying response: %s", jsonResponse));
            }
        }

    }


    private class WebSocketServerImpl extends  WebSocketServer {

        public WebSocketServerImpl(int port) {
            super(new InetSocketAddress(port));
        }

        @Override
        public void onOpen(WebSocket conn, ClientHandshake handshake) {

            connections.put(conn.hashCode(), conn);

            notifyConnection(conn.hashCode());

            roboboManager.log(LogLvl.DEBUG, TAG, format("Open websocket connection %s", conn.getRemoteSocketAddress()));

        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {

            connections.remove(conn.hashCode());

            connectionsAuthenticated.remove(conn.hashCode());

            notifyDisconnection(conn.hashCode());

            roboboManager.log(LogLvl.DEBUG, TAG, format("Closed websocket connection"));
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

            if (message.startsWith(PASSWORD)) {

                if (message.substring(10).equals(remoteControlModule.getPassword())) {

                    connectionsAuthenticated.put(webSocketConnection.hashCode(), webSocketConnection);

                    roboboManager.log(LogLvl.DEBUG, TAG, connectionsAuthenticated.toString());
                }else{

                    roboboManager.log(LogLvl.ERROR, TAG, "Incorrect password");

                    Status statusError= new Status("ONERROR");

                    statusError.putContents("error", "Incorrect password");

                    WebsocketRemoteControlModule.this.notifyStatus(statusError);

                    WebsocketRemoteControlModule.this.notifyStatus(new Status("DIE"));

                }
            } else if (connectionsAuthenticated.containsKey(webSocketConnection.hashCode())) {

                Command command = GsonConverter.jsonToCommand(message);

                remoteControlModule.queueCommand(command);

            }


        }

        @Override
        public void onError(WebSocket conn, Exception ex) {
            Log.e(TAG, format("Error WebSocket[local=%s, remote=%s]", conn.getLocalSocketAddress(), conn.getRemoteSocketAddress()), ex);
            roboboManager.log(LogLvl.ERROR, TAG, format("Error WebSocket[local=%s, remote=%s]", conn.getLocalSocketAddress(), conn.getRemoteSocketAddress()));
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

        this.webSocketServer= new WebSocketServerImpl(port);

        this.webSocketServer.start();

    }

    @Override
    public void shutdown() throws InternalErrorException {

        try {
            Iterator it = connections.entrySet().iterator();

            while (it.hasNext()){
                Map.Entry pair = (Map.Entry)it.next();
                ((WebSocket)pair.getValue()).close();
            }

            webSocketServer.stop();
        } catch (IOException ex) {
            Log.e(TAG, format("Error closing WebSocketServer", ex));
            roboboManager.log(LogLvl.ERROR, TAG, "Error closing WebSocketServer");
        } catch (InterruptedException ex) {
            roboboManager.log(LogLvl.ERROR, TAG, "Error closing WebSocketServer. InterruptedException.");
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
        return "0.3.1";
    }
}

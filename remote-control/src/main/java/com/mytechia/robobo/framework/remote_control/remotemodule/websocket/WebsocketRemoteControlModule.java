package com.mytechia.robobo.framework.remote_control.remotemodule.websocket;

import android.util.Log;

import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.remote_control.remotemodule.ARemoteControlModule;
import com.mytechia.robobo.framework.remote_control.remotemodule.Command;
import com.mytechia.robobo.framework.remote_control.remotemodule.GsonConverter;
import com.mytechia.robobo.framework.remote_control.remotemodule.ICommandExecutor;
import com.mytechia.robobo.framework.remote_control.remotemodule.Response;
import com.mytechia.robobo.framework.remote_control.remotemodule.Status;

import org.java_websocket.WebSocket;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/*******************************************************************************
 * Copyright 2016 Mytech Ingenieria Aplicada <http://www.mytechia.com>
 * Copyright 2016 Luis Llamas <luis.llamas@mytechia.com>
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

/**
 * Implementation of the IRemoteControlModule with websockets.
 * This implementation creates a websocket server who can be accessed at the port 40404 of
 * the smartphone IP address
 */
public class WebsocketRemoteControlModule extends ARemoteControlModule {

    private WebSocketServer wsServer;
    private HashMap<String,ICommandExecutor> commands;
    private final WebsocketRemoteControlModule modulo = this;
    private RoboboManager m;
    private String TAG = "Websocket RC Module";
    //private String password = "passwd";

    private HashMap<Integer,WebSocket> connections;
    private HashMap<Integer,WebSocket> connectionsAuthenticated;


    @Override
    public void registerCommand(String commandName, ICommandExecutor module) {
        m.log(TAG,"Registering command: "+commandName);
        commands.put(commandName,module);
    }

    @Override
    public void postStatus(Status status) {
        Map.Entry pair=null;
        Iterator it = connectionsAuthenticated.entrySet().iterator();
        while (it.hasNext()) {
            try {
                pair = (Map.Entry) it.next();
                if ((((WebSocket) pair.getValue()).isClosed()) || (((WebSocket) pair.getValue()).isClosed())) {

                } else {
                    try {
                        ((WebSocket) pair.getValue()).send(GsonConverter.statusToJson(status));
                    } catch (WebsocketNotConnectedException e) {
                        //NOPER
                    }

                }

            } catch (ConcurrentModificationException e) {
                //NOPE
            }

        }

    }

    @Override
    public void postResponse(Response response) {
        //Log.d(TAG,"Response: "+response.toString());
        Iterator it = connectionsAuthenticated.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String resp = GsonConverter.responseToJson(response);

            ((WebSocket)pair.getValue()).send(resp);
        }
    }



    @Override
    public void startup(RoboboManager manager) throws InternalErrorException {

        connections = new HashMap<>();
        connectionsAuthenticated = new HashMap<>();
        commands = new HashMap<>();
        m = manager;


        int port = 40404;


        wsServer = new WebSocketServer(new InetSocketAddress(port)) {
            @Override
            public void onOpen(WebSocket conn, ClientHandshake handshake) {
                connections.put(conn.hashCode(),conn);
                notifyConnection(connections.size());
                //conn.send("Connection Stablished");
                //Log.d(TAG,"Connection: "+connections.toString());
                m.log(TAG,"Open: "+conn.hashCode());

            }

            @Override
            public void onClose(WebSocket conn, int code, String reason, boolean remote) {
                connections.remove(conn.hashCode());
                notifyDisconnection(connections.size());

                m.log(TAG,"Close: "+conn.hashCode());
                connectionsAuthenticated.remove(conn.hashCode());
                //conn.send(GsonConverter.statusToJson(new Status("DIE")));

                //Log.d(TAG,connectionsAuthenticated.toString());

                //Log.d(TAG,"Close connection: "+conn.hashCode());
            }

            @Override
            public void onMessage(WebSocket conn, String message) {
                //conn.send(message);
                //Log.d(TAG, "Message:"+message+"|"+message.substring(10)+"|");

                if (message.startsWith("PASSWORD")){
                    //Log.d(TAG, message);
                    if (message.substring(10).equals(password)){

                        connectionsAuthenticated.put(conn.hashCode(),conn);


                        m.log(TAG,connectionsAuthenticated.toString());
                    }else{

                        m.log(TAG, "Incorrect password");
                        Status error = new Status("ONERROR");
                        error.putContents("error","Incorrect password");
                        conn.send(GsonConverter.statusToJson(error));
                        conn.send(GsonConverter.statusToJson(new Status("DIE")));
                    }
                }else if (connectionsAuthenticated.containsKey(conn.hashCode())) {
                    m.log(TAG, "Message " + message);

                    Command c = GsonConverter.jsonToCommand(message);
                    if (commands.containsKey(c.getName())) {
                        m.log(TAG, "Executing command "+c.getName());
                        commands.get(c.getName()).executeCommand(c, modulo);
                    }
                }


            }

            @Override
            public void onError(WebSocket conn, Exception ex) {
                m.log(TAG, "On Error: "+ex.getMessage());
            }
        };

        wsServer.start();


    }

    @Override
    public void shutdown() throws InternalErrorException {
        try {
            Iterator it = connections.entrySet().iterator();

            while (it.hasNext()){
                Map.Entry pair = (Map.Entry)it.next();
                ((WebSocket)pair.getValue()).close();
            }

            wsServer.stop();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getModuleInfo() {
        return "Remote Control Module";
    }

    @Override
    public String getModuleVersion() {
        return "0.2.5";
    }
}

package com.mytechia.robobo.framework.remote_control.websocket;

import android.util.Log;

import com.google.gson.Gson;
import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.remote_control.ARemoteControlModule;
import com.mytechia.robobo.framework.remote_control.Command;
import com.mytechia.robobo.framework.remote_control.GsonConverter;
import com.mytechia.robobo.framework.remote_control.ICommandExecutor;
import com.mytechia.robobo.framework.remote_control.Response;
import com.mytechia.robobo.framework.remote_control.Status;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.awt.font.TextAttribute;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
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
public class WebsocketRemoteControlModule extends ARemoteControlModule {

    private WebSocketServer wsServer;
    private HashMap<String,ICommandExecutor> commands;
    private final WebsocketRemoteControlModule modulo = this;
    private String TAG = "Websocket RC Module";

    private HashMap<InetSocketAddress,WebSocket> connections;
    @Override
    public void registerCommand(String commandName, ICommandExecutor module) {
        commands.put(commandName,module);
    }

    @Override
    public void postStatus(Status status) {
        Iterator it = connections.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            ((WebSocket)pair.getValue()).send(GsonConverter.statusToJson(status));
        }
    }

    @Override
    public void postResponse(Response response) {
        Log.d(TAG,"Response: "+response.toString());
        Iterator it = connections.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String resp = GsonConverter.responseToJson(response);

            ((WebSocket)pair.getValue()).send(resp);
        }
    }



    @Override
    public void startup(RoboboManager manager) throws InternalErrorException {

        connections = new HashMap<>();
        commands = new HashMap<>();


            int port = 22226;


            wsServer = new WebSocketServer(new InetSocketAddress(port)) {
                @Override
                public void onOpen(WebSocket conn, ClientHandshake handshake) {
                    connections.put(conn.getRemoteSocketAddress(),conn);
                    //conn.send("Connection Stablished");
                    Log.d(TAG,"Connection");
                }

                @Override
                public void onClose(WebSocket conn, int code, String reason, boolean remote) {
                    connections.remove(conn.getRemoteSocketAddress());
                    Log.d(TAG,"Close connection");
                }

                @Override
                public void onMessage(WebSocket conn, String message) {
                    //conn.send(message);
                    Log.d(TAG,"Message "+message);

                        Command c = GsonConverter.jsonToCommand(message);
                        if (commands.containsKey(c.getName())) {
                            commands.get(c.getName()).executeCommand(c, modulo);
                        }


                }

                @Override
                public void onError(WebSocket conn, Exception ex) {
                    Log.d(TAG, "On Error: "+ex.getMessage());
                }
            };

        wsServer.start();


    }

    @Override
    public void shutdown() throws InternalErrorException {

    }

    @Override
    public String getModuleInfo() {
        return null;
    }

    @Override
    public String getModuleVersion() {
        return null;
    }
}

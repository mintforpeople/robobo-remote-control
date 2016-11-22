package com.mytechia.robobo.framework.remote_control.remotemodule.gozirra;

import android.content.res.AssetManager;
import android.util.Log;

import com.hi3project.vineyard.comm.stomp.gozirraws.Listener;
import com.hi3project.vineyard.comm.stomp.gozirraws.Server;
import com.hi3project.vineyard.comm.stomp.gozirraws.Stomp;
import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.remote_control.remotemodule.ARemoteControlModule;
import com.mytechia.robobo.framework.remote_control.remotemodule.Command;
import com.mytechia.robobo.framework.remote_control.remotemodule.GsonConverter;
import com.mytechia.robobo.framework.remote_control.remotemodule.ICommandExecutor;
import com.mytechia.robobo.framework.remote_control.remotemodule.Response;
import com.mytechia.robobo.framework.remote_control.remotemodule.Status;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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
public class GozirraRemoteControlModule extends ARemoteControlModule{

    private HashMap<String,ICommandExecutor> commands;
    private Server server;
    private Stomp client;
    private int port;
    private String TAG = "GOZIRRA";

    @Override
    public void startup(RoboboManager manager) throws InternalErrorException {
        Properties properties = new Properties();
        AssetManager assetManager = manager.getApplicationContext().getAssets();
        final GozirraRemoteControlModule modulo = this;
        commands = new HashMap<>();

        try {
            InputStream inputStream = assetManager.open("remote.properties");
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();

        }
        this.port= Integer.parseInt(properties.getProperty("stompport"));

        try {
            server = new Server(port);
//            new Server(port, new Authenticator() {
//                @Override
//                public Object connect(String user, String pass) throws LoginException {
//                    return null;
//                }
//
//                @Override
//                public boolean authorizeSend(Object token, String channel) {
//                    return false;
//                }
//
//                @Override
//                public boolean authorizeSubscribe(Object token, String channel) {
//                    return false;
//                }
//            });

            client = server.getClient();

        } catch (IOException e) {
            e.printStackTrace();
        }
        client.subscribe("/commands",new Listener() {
            public void message( Map header, String body ) {
                Log.d(TAG, header.toString());
                Log.d(TAG,body);
                Command c = GsonConverter.jsonToCommand(body);
                if (commands.containsKey(c.getName())){
                    commands.get(c.getName()).executeCommand(c,modulo);
                }
            }
        }
                );
        client.subscribe("/status",new Listener() {
            public void message( Map header, String body ) {
                Log.d(TAG, header.toString());
                Log.d(TAG,body);
                notifyStatus(GsonConverter.jsonToStatus(body));
                Log.d(TAG,body);
            }
        });
        client.subscribe("/responses",new Listener() {
            public void message( Map header, String body ) {
                Log.d(TAG, header.toString());
                Log.d(TAG,body);
                notifyResponse(GsonConverter.jsonToResponse(body));
                Log.d(TAG,body);
            }
        });

    }

    @Override
    public void shutdown() throws InternalErrorException {

    }

    @Override
    public String getModuleInfo() {
        return "Gozirra Remote Control Module";
    }

    @Override
    public String getModuleVersion() {
        return "v0.1";
    }

    @Override
    public void registerCommand(String commandName, ICommandExecutor module) {
        commands.put(commandName,module);
    }

    @Override
    public void postStatus(Status status) {
        client.send("/status",GsonConverter.statusToJson(status));
    }

    @Override
    public void postResponse(Response response) {
        client.send("/responses",GsonConverter.responseToJson(response));
    }


    public void postTestCommand() {
        Log.d(TAG,"postTestCommand");
        HashMap<String,String> par = new HashMap<>();
        par.put("Test","content");
        par.put("Test2","content");
        Command c = new Command("C1",1,par);
        client.send("/commands",GsonConverter.commandToJson(c));
    }


}

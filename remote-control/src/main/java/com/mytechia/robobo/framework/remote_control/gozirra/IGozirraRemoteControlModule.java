package com.mytechia.robobo.framework.remote_control.gozirra;

import android.content.res.AssetManager;

import com.hi3project.vineyard.comm.stomp.gozirraws.Client;
import com.hi3project.vineyard.comm.stomp.gozirraws.Listener;
import com.hi3project.vineyard.comm.stomp.gozirraws.Server;
import com.hi3project.vineyard.comm.stomp.gozirraws.Stomp;
import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.remote_control.ICommandExecutor;
import com.mytechia.robobo.framework.remote_control.IRemoteControlModule;
import com.mytechia.robobo.framework.remote_control.JsonConverter;
import com.mytechia.robobo.framework.remote_control.Response;
import com.mytechia.robobo.framework.remote_control.Status;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.security.auth.login.LoginException;

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
public class IGozirraRemoteControlModule implements IRemoteControlModule, Listener {

    private HashMap<String,ICommandExecutor> commands;
    private Server server;
    private Stomp client;
    private int port;

    @Override
    public void startup(RoboboManager manager) throws InternalErrorException {
        Properties properties = new Properties();
        AssetManager assetManager = manager.getApplicationContext().getAssets();

        try {
            InputStream inputStream = assetManager.open("remote.properties");
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();

        }
        this.port= Integer.parseInt(properties.getProperty("stompport"));

        try {
            server = new Server(port);
            client = new Client("localhost",port,"ser","ser");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (LoginException e) {
            e.printStackTrace();;
        }
        client.subscribe("/commands");
        client.subscribe("/status");
        client.subscribe("/responses");


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
        client.send("/status",JsonConverter.statusToJson(status));
    }

    @Override
    public void postResponse(Response response) {
        client.send("/responses",JsonConverter.responseToJson(response));
    }

    @Override
    public void message(Map headers, String body) {
        //TODO Mirar que son los headers
    }
}

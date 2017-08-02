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

package com.mytechia.robobo.framework.remote_control.remotemodule;

import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.RoboboManager;
import java.util.HashSet;
import java.util.Objects;



public class RemoteControlModule implements IRemoteControlModule{

    private HashSet<IRemoteListener> listeners = new HashSet<>();

    private HashSet<IRemoteControlProxy> remoteControlProxies= new HashSet<>();

    private String password = "";

    private CommandQueueProcessor commandQueueProcessor;



    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public void suscribe(IRemoteListener listener) {
        listeners.add(listener);
    }

    @Override
    public void unsuscribe(IRemoteListener listener) {
        listeners.remove(listener);
    }


    @Override
    public void registerRemoteControlProxy(IRemoteControlProxy proxy) {
        remoteControlProxies.add(proxy);

    }

    @Override
    public void unregisterRemoteControlProxy(IRemoteControlProxy proxy) {
        remoteControlProxies.remove(proxy);
    }



    @Override
    public void notifyConnection(int connNumber){
        for(IRemoteListener listener:listeners){
            listener.onConnection(connNumber);
        }
    }

    @Override
    public void notifyDisconnection(int connNumber){
        for(IRemoteListener listener:listeners){
            listener.onDisconnection(connNumber);
        }
    }



    @Override
    public void registerCommand(String commandName, ICommandExecutor module) {

        Objects.requireNonNull(this.commandQueueProcessor, "The RemoteControlModule was not started properly. ");

        commandQueueProcessor.registerCommandExecutor(commandName, module);


    }

    @Override
    public void postStatus(Status status) {

        if(status==null){
            return;
        }

        for (IRemoteControlProxy remoteControlProxy: this.remoteControlProxies) {
            remoteControlProxy.notifyStatus(status);
        }

    }

    @Override
    public void postResponse(Response response) {

        if(response==null){
            return;
        }

        for (IRemoteControlProxy remoteControlProxy: this.remoteControlProxies) {
            remoteControlProxy.notifyReponse(response);
        }

    }


    @Override
    public void queueCommand(Command commmand) {

        if(commandQueueProcessor==null){
            return;
        }

        if(commmand==null){
            return;
        }

        commandQueueProcessor.put(commmand);

    }

    @Override
    public void startup(RoboboManager manager) throws InternalErrorException {

        commandQueueProcessor= new CommandQueueProcessor(this);

        commandQueueProcessor.start();

    }

    @Override
    public void shutdown() throws InternalErrorException {
        commandQueueProcessor.dispose();
    }

    @Override
    public String getModuleInfo() {
        return "Remote Control Module";
    }

    @Override
    public String getModuleVersion() {
        return "0.3.1";
    }
}

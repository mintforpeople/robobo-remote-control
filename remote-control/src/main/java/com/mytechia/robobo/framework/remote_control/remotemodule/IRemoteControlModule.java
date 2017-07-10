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
package com.mytechia.robobo.framework.remote_control.remotemodule;

import com.mytechia.robobo.framework.IModule;

/**
 * Public interface of the remote control module
 */
public interface IRemoteControlModule extends IModule {

    /**
     * Register a new command to be detected
     * @param commandName Name of the command
     * @param module Executor class for the command
     */
    void registerCommand(String commandName, ICommandExecutor module);

    /**
     * Posts a status
     * @param status The status to be posted
     */
    void postStatus(Status status);

    /**
     * Posts a response to a command
     * @param response The response to be posted
     */
    void postResponse(Response response);


    /**
     * Sets the password to the remote control system
     * @param password The password
     */
    void setPassword(String password);

    String getPassword();

    /**
     * Suscribes a listener to the remote control notifications
     * @param listener Listener to be added
     */
    void suscribe(IRemoteListener listener);

    /**
     * Unsuscribes a listener to the remote control notifications
     * @param listener Listener to be removed
     */
    void unsuscribe(IRemoteListener listener);

    void registerRemoteControlProxy(IRemoteControlProxy proxy);

    void unregisterRemoteControlProxy(IRemoteControlProxy proxy);

    void notifyStatus(Status status);

    void notifyResponse(Response response);

    void queueCommand(Command commmand);
}

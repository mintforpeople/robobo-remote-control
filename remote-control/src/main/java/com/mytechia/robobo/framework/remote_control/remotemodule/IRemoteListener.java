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

/**
 * Remote control listener interface
 */
public interface IRemoteListener {
    /**
     * Called when a response is received
     * @param r The response
     */
    void onResponse(Response r);

    /**
     * Called when a status is received
     * @param s The status
     */
    void onStatus(Status s);

    /**
     * Called when a client connects to the module
     * @param connNumber the number of active connections
     */
    void onConnection(int connNumber);

    /**
     * Called when a client disconnects from the module
     * @param connNumber the number of active connections
     */
    void onDisconnection(int connNumber);
}

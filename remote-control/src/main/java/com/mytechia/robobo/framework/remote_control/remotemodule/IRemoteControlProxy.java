/*******************************************************************************
 * Copyright 2016 Mytech Ingenieria Aplicada <http://www.mytechia.com>
 * Copyright 2016 Julio Gomez <julio.gomez@mytechia.com>
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
 * Interface that must be implemented by every 'network' implementation of the Robobo Remote Control Protocol.
 *
 * This interface provides methods to comunicate status and responses to the clients
 * connected remotely to a Robobo robot.
 *
 */
public interface IRemoteControlProxy {

    /** Tells the proxy to send a status message over the network
     *
     * @param status the status message
     */
    void notifyStatus(Status status);

    /** Tells the proxy to send a repose message over the network
     *
     * @param response the response message
     */
    void notifyReponse(Response response);

}

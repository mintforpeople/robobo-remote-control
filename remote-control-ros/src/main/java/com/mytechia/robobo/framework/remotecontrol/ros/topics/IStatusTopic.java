/*******************************************************************************
 *
 *   Copyright 2018 Mytech Ingenieria Aplicada <http://www.mytechia.com>
 *   Copyright 2018 Gervasio Varela <gervasio.varela@mytechia.com>
 *
 *   This file is part of Robobo Ros Module.
 *
 *   Robobo Ros Module is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Robobo Ros Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with Robobo Ros Module.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package com.mytechia.robobo.framework.remotecontrol.ros.topics;

import com.mytechia.robobo.framework.remote_control.remotemodule.Status;

/**
 * A status topic represent a publish in ROS that is in charge for converting and publishing
 * a particular type of status messages.
 *
 */

public interface IStatusTopic {

    String getSupportedStatus();

    String getTopicName();

    void publishStatus(Status status);

    void start();

}

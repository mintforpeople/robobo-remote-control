/*******************************************************************************
 * Copyright 2017 Mytech Ingenieria Aplicada <http://www.mytechia.com>
 * Copyright 2017 Gervasio Varela <gervasio.varela@mytechia.com>
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

import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.power.PowerMode;

/**
 * When this command is received the framwork is forced to NORMAL power mode.
 *
 * This command is used by remote clients in order to keep the robot from going
 * to LOW-POWER or sleep mode.
 *
 */
public class KeepAliveCommandExecutor implements ICommandExecutor
{

    private RoboboManager roboboManager;

    /**
     * Public constructor of the KeepAliveCommandExecutor
     * @param roboboManager the current robobo manager
     */
    public KeepAliveCommandExecutor(RoboboManager roboboManager) {
        super();
        this.roboboManager = roboboManager;
    }

    @Override
    public void executeCommand(Command c, IRemoteControlModule rcmodule) {
        if (this.roboboManager != null) {//just keep the robot alive
            roboboManager.changePowerModeTo(PowerMode.NORMAL);
        }
    }
}

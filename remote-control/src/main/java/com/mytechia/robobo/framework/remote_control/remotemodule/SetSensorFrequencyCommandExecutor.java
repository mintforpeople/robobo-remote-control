package com.mytechia.robobo.framework.remote_control.remotemodule;

import com.mytechia.robobo.framework.LogLvl;
import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.frequency.FrequencyMode;

import java.util.HashMap;

/*******************************************************************************
 *
 *   Copyright 2016 Mytech Ingenieria Aplicada <http://www.mytechia.com>
 *   Copyright 2016 Luis Llamas <luis.llamas@mytechia.com>
 *
 *   This file is part of Robobo Remote Control Module.
 *
 *   Robobo Remote Control Module is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Robobo Remote Control Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with Robobo Remote Control Module.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/
public class SetSensorFrequencyCommandExecutor implements ICommandExecutor {
    private RoboboManager roboboManager;
    private static String TAG = "SET-SENSOR-FREQUENCY";

    public SetSensorFrequencyCommandExecutor(RoboboManager roboboManager){
        super();
        this.roboboManager = roboboManager;
    }

    @Override
    public void executeCommand(Command c, IRemoteControlModule rcmodule) {
        HashMap<String, String> par = c.getParameters();
        String freq = par.get("frequency");
        switch (freq){
            case "LOW":
                roboboManager.changeFrequencyModeTo(FrequencyMode.LOW);
                break;
            case "NORMAL":
                roboboManager.changeFrequencyModeTo(FrequencyMode.NORMAL);
                break;
            case "FAST":
                roboboManager.changeFrequencyModeTo(FrequencyMode.FAST);
                break;
            case "MAX":
                roboboManager.changeFrequencyModeTo(FrequencyMode.MAX);
                break;
            default:
                roboboManager.log(LogLvl.ERROR,TAG,"Bad parameter passed to SET_SENSOR_FREQUENCY command: "+freq);

        }
    }

}

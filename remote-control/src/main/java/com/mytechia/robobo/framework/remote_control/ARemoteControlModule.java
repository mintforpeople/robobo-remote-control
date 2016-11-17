package com.mytechia.robobo.framework.remote_control;

import java.util.HashSet;

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
public abstract class ARemoteControlModule implements IRemoteControlModule {
    private HashSet<IRemoteListener> listeners = new HashSet<>();


    @Override
    public void suscribe(IRemoteListener listener) {
        listeners.add(listener);
    }

    @Override
    public void unsuscribe(IRemoteListener listener) {
        listeners.remove(listener);
    }

    protected void notifyResponse(Response r){
        for(IRemoteListener listener:listeners){
            listener.onResponse(r);
        }
    }

    protected void notifyStatus(Status s){
        for(IRemoteListener listener:listeners){
            listener.onStatus(s);
        }
    }


}

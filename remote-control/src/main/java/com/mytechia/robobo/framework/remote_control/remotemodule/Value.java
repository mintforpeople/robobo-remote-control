package com.mytechia.robobo.framework.remote_control.remotemodule;

import java.util.HashMap;
import java.util.Map;

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

/**
 * Class representing the optional values that can be carried with a command, a status or a response
 */
public class Value {
    private HashMap<String,String> value;

    public Value(){
        value = new HashMap<>();
    }

    /**
     * Put new contents on the value containes
     * @param key The key to be indexed
     * @param val The value
     */
    public void putContents(String key, String val){

        value.put(key,val);
    }

    public HashMap<String, String> getValue() {
        return value;
    }
}

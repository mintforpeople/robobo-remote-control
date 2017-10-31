/*******************************************************************************
 *
 *   Copyright 2017 Mytech Ingenieria Aplicada <http://www.mytechia.com>
 *   Copyright 2017 Gervasio Varela <gervasio.varela@mytechia.com>
 *   Copyright 2017 Julio Gomez <julio.gomez@mytechia.com>
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

package com.mytechia.robobo.framework.remotecontrol.ros;


import android.util.Log;

import org.ros.message.MessageFactory;
import org.ros.node.NodeConfiguration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com_mytechia_robobo_ros_msgs.KeyValue;



public class MapperListKeyValueMap {

    private static final String TAG = "Robobo Mapper KeyValue";


    private final MessageFactory messageFactory;


    public MapperListKeyValueMap(){
        NodeConfiguration nodeConfiguration = NodeConfiguration.newPrivate();

        this.messageFactory = nodeConfiguration.getTopicMessageFactory();
    }


    public List<KeyValue> mapToListKeyValue(Map<String, String> statusValue) {


        List<KeyValue> keyValues = new ArrayList<>(statusValue.size());

        Set<String> keys = statusValue.keySet();

        for (String key: keys)  {

            String value = statusValue.get(key);

            value= (value==null ? "": value);

            KeyValue keyValue = messageFactory.newFromType(KeyValue._TYPE);

            if(key==null){
                Log.w(TAG, "Discarded KeyValue. The key is null");
                continue;
            }

            keyValue.setKey(key);

            keyValue.setValue(value);

            keyValues.add(keyValue);

        }

        return keyValues;

    }


    public HashMap<String, String> listKeyValueToMap(List<KeyValue> keyValues){

        HashMap<String, String> map= new HashMap<>();

        for (KeyValue keyValue :keyValues) {
            map.put(keyValue.getKey(), keyValue.getValue());
        }

        return map;

    }



}

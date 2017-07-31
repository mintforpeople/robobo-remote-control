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

package com.mytechia.robobo.framework.remote_control.remotemodule.ros.topics;

import android.util.Log;

import com.mytechia.robobo.framework.remote_control.remotemodule.GsonConverter;
import com.mytechia.robobo.framework.remote_control.remotemodule.ros.MapperListKeyValueMap;
import com.mytechia.util.NodeNameUtility;

import org.ros.message.Time;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

import java.util.HashMap;
import java.util.List;

import com_mytechia_robobo_ros_msgs.KeyValue;
import com_mytechia_robobo_ros_msgs.Status;
import std_msgs.Header;



/**
 * Created by julio on 12/07/17.
 */

public class StatusTopic extends AbstractNodeMain {

    private static final String TAG = "Robobo Status Topic";

    private static final String NAME_NODE_ROB_STATUS ="robobo_topic_status";

    private static final String TOPIC_ROB_STATUS ="status";

    private String roboName="";

    private Publisher<Status> statusPublisher;

    private MapperListKeyValueMap mapperListKeyValueMap= new MapperListKeyValueMap();



    public StatusTopic(String roboName){


        if(roboName==null){
            this.roboName= roboName;
        }

    }


    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of(NodeNameUtility.createNodeName(roboName, NAME_NODE_ROB_STATUS));
    }


    @Override
    public void onStart(ConnectedNode connectedNode) {

        super.onStart(connectedNode);

        this.statusPublisher = connectedNode.newPublisher(NodeNameUtility.createNodeAction(roboName, TOPIC_ROB_STATUS), Status._TYPE);

    }



    public void publishStatusMessage(com.mytechia.robobo.framework.remote_control.remotemodule.Status status) {


        if(statusPublisher==null){

            String jsonStatus= GsonConverter.statusToJson(status);

            Log.w(TAG, String.format("Ros status publisher is null. Maybe the ros node is not yet connected. Message %s will not be published", jsonStatus));

            return;
        }

        Status statusMessage =statusPublisher.newMessage();

        statusMessage.setName(status.getName());

        statusMessage.setValue(this.generateListKeyValue(status));

        this.statusPublisher.publish(statusMessage);


    }


    private List<KeyValue> generateListKeyValue(com.mytechia.robobo.framework.remote_control.remotemodule.Status status) {

        HashMap<String, String> statusValue = status.getValue();

        return this.mapperListKeyValueMap.mapToListKeyValue(statusValue);

    }


    private void setSatusHeaderTimestamp(Header header) {
        long time = System.currentTimeMillis();
        long sec = (long) Math.floor(time / 1000.0);
        long nsec = time - (sec*1000);
        header.setSeq((int) sec);
        header.setStamp(Time.fromMillis(nsec));
    }

}

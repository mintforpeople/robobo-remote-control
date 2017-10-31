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

package com.mytechia.robobo.framework.remotecontrol.ros.topics;

import android.util.Log;

import com.mytechia.robobo.framework.remote_control.remotemodule.GsonConverter;
import com.mytechia.robobo.framework.remotecontrol.ros.MapperListKeyValueMap;
import com.mytechia.robobo.framework.remotecontrol.ros.util.NodeNameUtility;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import java.util.HashMap;
import java.util.List;

import com_mytechia_robobo_ros_msgs.KeyValue;
import com_mytechia_robobo_ros_msgs.Response;



/**
 * Created by julio on 12/07/17.
 */
public class ResponseTopic extends AbstractNodeMain {

    private static final String TAG = "Robobo Response Topic";

    private static final String NAME_NODE_ROB_RESPONSE = "robobo_topic_response";

    private static final String TOPIC_ROB_RESPONSE ="response";

    private  String roboName;

    private Publisher<Response> responsePublisher;

    private MapperListKeyValueMap mapperListKeyValueMap= new MapperListKeyValueMap();


    public ResponseTopic(String roboName){

        if(roboName!=null){
            this.roboName= roboName;
        }

    }


    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of(NodeNameUtility.createNodeName(roboName, NAME_NODE_ROB_RESPONSE));
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        super.onStart(connectedNode);

        this.responsePublisher = connectedNode.newPublisher(NodeNameUtility.createNodeAction(roboName, TOPIC_ROB_RESPONSE), Response._TYPE);

    }


    public void publishResponseMessage(com.mytechia.robobo.framework.remote_control.remotemodule.Response response) {

        if(responsePublisher==null){

            String jsonStatus= GsonConverter.responseToJson(response);

            Log.w(TAG, String.format("Ros status publisher is null. Maybe the ros node is not yet connected. Message %s will not be published", jsonStatus));

            return;
        }

        Response responseMessage = responsePublisher.newMessage();

        responseMessage.setCommandId(response.getCommandId());

        responseMessage.setValue(this.generateListKeyValue(response));

        this.responsePublisher.publish(responseMessage);

    }


    private List<KeyValue> generateListKeyValue(com.mytechia.robobo.framework.remote_control.remotemodule.Response response) {

        HashMap<String, String> reponseValue = response.getValue();

        return this.mapperListKeyValueMap.mapToListKeyValue(reponseValue);

    }


}

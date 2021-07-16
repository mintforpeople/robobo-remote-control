package com.mytechia.robobo.framework.remotecontrol.ros.topics;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mytechia.robobo.framework.remote_control.remotemodule.Status;
import com.mytechia.robobo.framework.remotecontrol.ros.util.NodeNameUtility;

import org.ros.message.Time;
import org.ros.node.topic.Publisher;

import vision_msgs.Detection2D;
import vision_msgs.Detection2DArray;
import vision_msgs.ObjectHypothesisWithPose;

/**
 * Status Topic for detected objects by the vision module.
 * <p>
 * The topic is robot/detected_object
 */
public class DetectedObjectStatusTopic extends AStatusTopic {

    private static final String TOPIC = "detected_object";
    public static final String STATUS = "DETECTED_OBJECT";
    public static final String TAG = "DETECTED_OBJECT_TOPIC";


    private Publisher<Detection2DArray> topic;


    public DetectedObjectStatusTopic(StatusNode node) {
        super(node, TOPIC, STATUS, null);
    }


    public void start() {
        this.topic = this.node.getConnectedNode().newPublisher(NodeNameUtility.createNodeAction(this.node.getRoboboName(), this.getTopicName()), Detection2DArray._TYPE);
    }


    @Override
    public void publishStatus(Status status) {

        if (status.getName().equals(this.getSupportedStatus())) {
            Detection2DArray msg = this.topic.newMessage();

            String frame_id = status.getValue().get("frame_id"),
                    count = status.getValue().get("count"),
                    detections = status.getValue().get("detections");
            //Log.d(TAG,"Detected "+detections);



            if (frame_id != null &&
                    count != null &&
                    detections != null &&
                    Integer.parseInt(count) > 0
            ) {
                Time time = Time.fromMillis(System.currentTimeMillis());
                JsonArray jarr = (JsonArray) new JsonParser().parse(detections);
                msg.getHeader().setFrameId(frame_id);
                msg.getHeader().setStamp(time);

                for (int i = 0; i < jarr.size(); i++) {
                    JsonObject jobj = jarr.get(i).getAsJsonObject();
                    //{"boundingBox":{"bottom":350.16495,"left":10.285315,"right":284.35538,"top":26.003353},"confidence":0.51916367,"id":"1","label":"person"}
                    JsonObject bbox = jobj.get("boundingBox").getAsJsonObject();
                    msg.getDetections().add(node.getConnectedNode().getTopicMessageFactory().<Detection2D>newFromType(Detection2D._TYPE));
                    msg.getDetections().get(i).getHeader().setFrameId(frame_id);
                    msg.getDetections().get(i).getHeader().setStamp(time);
                    msg.getDetections().get(i).getBbox().getCenter().setX((bbox.get("right").getAsDouble() + bbox.get("left").getAsDouble()) / 2);
                    msg.getDetections().get(i).getBbox().getCenter().setY((bbox.get("bottom").getAsDouble() + bbox.get("left").getAsDouble()) / 2);
                    msg.getDetections().get(i).getBbox().setSizeX(bbox.get("right").getAsDouble() - bbox.get("left").getAsDouble());
                    msg.getDetections().get(i).getBbox().setSizeY(bbox.get("bottom").getAsDouble() - bbox.get("left").getAsDouble());
                    msg.getDetections().get(i).getResults().add(node.getConnectedNode().getTopicMessageFactory().<ObjectHypothesisWithPose>newFromType(ObjectHypothesisWithPose._TYPE));
                    msg.getDetections().get(i).getResults().get(0).setScore(jobj.get("confidence").getAsDouble());
                    msg.getDetections().get(i).getResults().get(0).setId(jobj.get("id").getAsInt());
                    Log.d("OBJECTTOPIC", String.valueOf(msg.getDetections().get(i).getResults().get(0).getId()));

                }

                this.topic.publish(msg);

            }

        }

    }
}

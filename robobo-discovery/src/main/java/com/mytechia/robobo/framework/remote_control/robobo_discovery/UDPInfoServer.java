package com.mytechia.robobo.framework.remote_control.robobo_discovery;

import com.mytechia.robobo.framework.RoboboManager;

import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPInfoServer extends Thread{

    RoboboManager manager;
    int serverPort;
    int serverBufferSize;
    String PASSPHRASE = "NOBOBO-PASS";

    public String getRoboboBTName() {
        return RoboboBTName;
    }

    public void setRoboboBTName(String roboboBTName) {
        RoboboBTName = roboboBTName;
    }

    String RoboboBTName = "ROB-???";

    public UDPInfoServer(int port, int bufferSize, RoboboManager manager){
        this.manager = manager;
        this.serverPort = port;
        this.serverBufferSize = bufferSize;
    }
    @Override
    public void run() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(serverPort);
            byte[] buffer = new byte[serverBufferSize];

            while (!isInterrupted()) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String receivedData = new String(packet.getData(), 0, packet.getLength());

                manager.log("INFO-SERVER", "Received: " + receivedData + " from " + packet.getAddress());

                // Use a simple passphrase, maybe later we can trace which client has tried to discover robobos
                if (receivedData.equals(PASSPHRASE)){

                    // Create the response data
                    byte[] responseData = getRoboboBTName().getBytes();
                    DatagramPacket responsePacket = new DatagramPacket(
                            responseData, responseData.length,
                            packet.getAddress(), packet.getPort());
                    socket.send(responsePacket);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }
}

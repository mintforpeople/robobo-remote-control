package com.mytechia.robobo.framework.remotecontrol.remote_mdns;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.net.InetAddress;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

public class RoboboMDNSServer {

    private static final String TAG = "Robobo MDNS Server";

    private WifiManager.MulticastLock multicastLock;
    private JmDNS jmdns;

    private String serviceName = "rob-000";
    private final String serviceAddress = "_robobo._tcp.local.";
    private String robotName = "rob-000";
    private String roboboBTName = "ROB-000";

    public void setRoboboBTName(String roboboBTName) {
        this.roboboBTName = roboboBTName;
        // Update derived values
        if (roboboBTName.startsWith("ROB-")) {
            this.serviceName = "rob-" + roboboBTName.substring(4).toLowerCase();
            this.robotName = "rob-" + roboboBTName.substring(4).toLowerCase();
        } else {
            this.serviceName = "rob-" + roboboBTName.toLowerCase();
            this.robotName = "rob-" + roboboBTName.toLowerCase();
        }
    }

    public String getRoboboBTName(){
        return this.roboboBTName;
    }

    public void start(Context context) {

        try {

            WifiManager wifi =
                    (WifiManager) context.getApplicationContext()
                            .getSystemService(Context.WIFI_SERVICE);

            multicastLock = wifi.createMulticastLock("jmdns");
            multicastLock.setReferenceCounted(false);
            multicastLock.acquire();

            WifiInfo info = wifi.getConnectionInfo();

            int ip = info.getIpAddress();

            byte[] ipBytes = new byte[] {
                    (byte) (ip & 0xff),
                    (byte) ((ip >> 8) & 0xff),
                    (byte) ((ip >> 16) & 0xff),
                    (byte) ((ip >> 24) & 0xff)
            };

            InetAddress address = InetAddress.getByAddress(ipBytes);

            Log.i(TAG, "Binding to " + address.getHostAddress());

            jmdns = JmDNS.create(address, serviceName);

            ServiceInfo service = ServiceInfo.create(
                    serviceAddress,
                    robotName,
                    44304,
                    "id=" + roboboBTName
            );

            jmdns.registerService(service);

            Log.i(TAG, "Service registered");

        } catch (Exception e) {
            Log.e(TAG, "Failed", e);
        }
    }

    public void stop() {

        try {

            if (jmdns != null) {
                jmdns.unregisterAllServices();
                jmdns.close();
                jmdns = null;
            }

        } catch (Exception ignored) {
        }

        if (multicastLock != null && multicastLock.isHeld()) {
            multicastLock.release();
        }
    }
}
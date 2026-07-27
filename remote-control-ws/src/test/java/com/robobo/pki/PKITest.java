package com.robobo.pki;

import org.junit.Test;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

import static org.junit.Assert.*;

public class PKITest {

    private static final String PASS = "oNgN_uUvAolJJ-JKFWPiwVYt";

    @Test
    public void testTLSHandshakeWithCustomKeyManager() throws Exception {
        File p12File = new File("src/main/assets/pkcs12/rob-7vt.p12");
        File caFile = new File("src/main/res/raw/ca.crt");

        // Force RoboboKeyManager wrapping with chosenAlias = "rob-7vt"
        KeyStore ks = KeyStore.getInstance("PKCS12");
        try (InputStream is = new FileInputStream(p12File)) {
            ks.load(is, PASS.toCharArray());
        }

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, PASS.toCharArray());

        KeyManager[] kmfManagers = kmf.getKeyManagers();
        KeyManager[] customManagers = new KeyManager[kmfManagers.length];
        for (int i = 0; i < kmfManagers.length; i++) {
            if (kmfManagers[i] instanceof javax.net.ssl.X509KeyManager) {
                customManagers[i] = new RoboboKeyManager((javax.net.ssl.X509KeyManager) kmfManagers[i], "rob-7vt");
            } else {
                customManagers[i] = kmfManagers[i];
            }
        }

        SSLContext serverSslContext = SSLContext.getInstance("TLS");
        serverSslContext.init(customManagers, null, null);

        SSLContext clientSslContext = SSLContext.getInstance("TLS");
        clientSslContext.init(null, new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                    public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                }
        }, null);

        SSLServerSocketFactory ssf = serverSslContext.getServerSocketFactory();
        SSLServerSocket serverSocket = (SSLServerSocket) ssf.createServerSocket(0, 1, InetAddress.getLoopbackAddress());

        int port = serverSocket.getLocalPort();

        Thread serverThread = new Thread(() -> {
            try {
                SSLSocket socket = (SSLSocket) serverSocket.accept();
                socket.startHandshake();
                socket.close();
            } catch (Exception e) {
                System.err.println("Server exception during handshake: " + e);
                e.printStackTrace();
            }
        });
        serverThread.start();

        SSLSocketFactory csf = clientSslContext.getSocketFactory();
        SSLSocket clientSocket = (SSLSocket) csf.createSocket(InetAddress.getLoopbackAddress(), port);
        try {
            clientSocket.startHandshake();
            System.out.println("Handshake with RoboboKeyManager successful! Cipher: " + clientSocket.getSession().getCipherSuite());
        } catch (Exception e) {
            System.err.println("Client handshake failed: " + e);
            throw e;
        } finally {
            clientSocket.close();
            serverSocket.close();
            serverThread.join();
        }
    }
}

package com.mytechia.robobo.framework.remotecontrol.ws;

import fi.iki.elonen.NanoHTTPD;
import android.content.Context;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class RoboboHttpsServer extends NanoHTTPD {

    private static final String PAGE_HTML =
            "<!DOCTYPE html>" +
                    "<html><head><title>Robobo Secure Access</title>" +
                    "<style>body{font-family:sans-serif;text-align:center;margin-top:80px;}" +
                    ".box{display:inline-block;padding:20px;border:2px solid #00a6ff;" +
                    "border-radius:10px;max-width:500px;}</style></head>" +
                    "<body><div class='box'>" +
                    "<h2>Secure Connection Established</h2>" +
                    "<p>The Robobo’s certificate is now trusted.</p>" +
                    "<p>You may safely return to the Scratch interface.</p>" +
                    "</div></body></html>";

    public RoboboHttpsServer(int port, Context context, int keystoreResId,
                             String storePassword, String keyPassword) throws IOException {
        this(port, context, keystoreResId, storePassword, keyPassword, null, null);
    }

    public RoboboHttpsServer(int port, Context context, int keystoreResId,
                             String storePassword, String keyPassword,
                             String robotId, com.robobo.pki.RoboboManifest manifest) throws IOException {
        super(port);

        try {
            InputStream in = context.getResources().openRawResource(keystoreResId);
            SSLContext sslContext = com.robobo.pki.SSLContextFactory.createSSLContextFromBks(
                    in,
                    storePassword.toCharArray(),
                    robotId,
                    manifest
            );

            // Pass SSLContext's factory to NanoHTTPD
            makeSecure(sslContext.getServerSocketFactory(), null);

        } catch (Exception e) {
            throw new IOException("Failed to initialize HTTPS", e);
        }
    }

    @Override
    public Response serve(IHTTPSession session) {
        return newFixedLengthResponse(Response.Status.OK, "text/html", PAGE_HTML);
    }
}

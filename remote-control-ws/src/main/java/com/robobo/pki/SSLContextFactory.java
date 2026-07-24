package com.robobo.pki;

import java.io.InputStream;
import java.security.KeyStore;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;

/**
 * Factory utility for creating TLS SSLContext instances for Android applications
 * connecting to or serving Robobo fleet robots using BKS keystores.
 */
public class SSLContextFactory {

    /**
     * Creates an SSLContext for TLS using a BKS keystore bundle, checking
     * manifest availability in advance for the targeted robot identity if provided.
     *
     * @param bksInputStream InputStream for the generated BKS file (e.g., robobo_local_ks.bks)
     * @param bksPassword    Password for the BKS keystore
     * @param targetRobotId  Target robot identity ID (e.g. "7VH" or "rob-7vh"), or null for default alias
     * @param manifest       Parsed RoboboManifest instance (optional, may be null)
     * @return Initialized SSLContext ready for HTTPS / WSS connections
     */
    public static SSLContext createSSLContextFromBks(
            InputStream bksInputStream,
            char[] bksPassword,
            String targetRobotId,
            RoboboManifest manifest
    ) throws Exception {
        if (targetRobotId != null && !targetRobotId.trim().isEmpty() && manifest != null && !manifest.isRobotAvailable(targetRobotId)) {
            throw new IllegalArgumentException(
                    "Robot identity '" + targetRobotId + "' is not available in the PKI manifest. Cannot proceed with TLS connection."
            );
        }

        String chosenAlias = null;
        if (targetRobotId != null && !targetRobotId.trim().isEmpty()) {
            String cleanId = targetRobotId.trim();
            if (cleanId.toUpperCase().startsWith("ROB-")) {
                cleanId = cleanId.substring(4);
            }
            if (manifest != null) {
                chosenAlias = manifest.getRobotAlias(cleanId);
                if (chosenAlias == null) {
                    chosenAlias = manifest.getRobotAlias(targetRobotId);
                }
            }
            if (chosenAlias == null) {
                chosenAlias = cleanId.toLowerCase();
                if (!chosenAlias.startsWith("rob-")) {
                    chosenAlias = "rob-" + chosenAlias;
                }
            }
        }

        android.util.Log.d("SSLContextFactory", "Creating TLS SSLContext - Target Robot ID: '" + targetRobotId + "', Resolved Alias: '" + chosenAlias + "'");

        // 2. Load BKS KeyStore containing client/server certificates and Root CA
        KeyStore bksStore = KeyStore.getInstance("BKS");
        try {
            bksStore.load(bksInputStream, bksPassword);
        } finally {
            if (bksInputStream != null) {
                bksInputStream.close();
            }
        }

        // 3. Initialize KeyManagerFactory for certificate authentication
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(bksStore, bksPassword);

        KeyManager[] kmfManagers = kmf.getKeyManagers();
        KeyManager[] customManagers = new KeyManager[kmfManagers.length];
        for (int i = 0; i < kmfManagers.length; i++) {
            if (kmfManagers[i] instanceof X509KeyManager) {
                customManagers[i] = new RoboboKeyManager((X509KeyManager) kmfManagers[i], chosenAlias);
            } else {
                customManagers[i] = kmfManagers[i];
            }
        }

        // 4. Initialize TrustManagerFactory trusting the Root CA stored in the BKS keystore
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(bksStore);

        // 5. Build TLS SSLContext
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(customManagers, tmf.getTrustManagers(), null);

        return sslContext;
    }

    /**
     * Creates an SSLContext trusting the custom Robobo Root CA certificate from PEM input.
     */
    public static SSLContext createSSLContextFromCaCertificate(InputStream caCertInputStream) throws Exception {
        java.security.cert.CertificateFactory cf = java.security.cert.CertificateFactory.getInstance("X.509");
        java.security.cert.Certificate ca;
        try {
            ca = cf.generateCertificate(caCertInputStream);
        } finally {
            if (caCertInputStream != null) {
                caCertInputStream.close();
            }
        }

        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null, null);
        trustStore.setCertificateEntry("robobo-root-ca", ca);

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);

        return sslContext;
    }
}

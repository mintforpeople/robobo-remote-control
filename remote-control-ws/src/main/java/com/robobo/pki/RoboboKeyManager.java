package com.robobo.pki;

import android.os.Build;
import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import javax.net.ssl.ExtendedSSLSession;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import javax.net.ssl.StandardConstants;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509KeyManager;

/**
 * Custom X509ExtendedKeyManager allowing dynamic selection of specific robot client/server identity aliases (e.g., "rob-7vh").
 * Supports configured alias, SNI (Server Name Indication) hostname extraction, or default keystore selection.
 */
public class RoboboKeyManager extends X509ExtendedKeyManager {

    private final X509KeyManager delegate;
    private final String chosenAlias;

    public RoboboKeyManager(X509KeyManager delegate, String chosenAlias) {
        this.delegate = delegate;
        this.chosenAlias = chosenAlias;
    }

    @Override
    public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
        if (chosenAlias != null) {
            return chosenAlias;
        }
        return delegate.chooseClientAlias(keyType, issuers, socket);
    }

    @Override
    public String chooseEngineClientAlias(String[] keyType, Principal[] issuers, SSLEngine engine) {
        if (chosenAlias != null) {
            return chosenAlias;
        }
        if (delegate instanceof X509ExtendedKeyManager) {
            return ((X509ExtendedKeyManager) delegate).chooseEngineClientAlias(keyType, issuers, engine);
        }
        return chooseClientAlias(keyType, issuers, null);
    }

    private static final String TAG = "RoboboKeyManager";

    @Override
    public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
        if (chosenAlias != null) {
            if (delegate.getPrivateKey(chosenAlias) != null) {
                android.util.Log.d(TAG, "Serving certificate for configured alias: " + chosenAlias);
                return chosenAlias;
            } else {
                android.util.Log.w(TAG, "Configured alias '" + chosenAlias + "' requested, but private key was NOT found in BKS Keystore!");
            }
        }
        String fallback = delegate.chooseServerAlias(keyType, issuers, socket);
        android.util.Log.w(TAG, "FALLBACK: Serving default certificate alias: " + fallback);
        return fallback;
    }

    @Override
    public String chooseEngineServerAlias(String keyType, Principal[] issuers, SSLEngine engine) {
        // 1. Explicitly configured robot alias takes top priority if valid key exists
        if (chosenAlias != null) {
            if (delegate.getPrivateKey(chosenAlias) != null) {
                android.util.Log.d(TAG, "Serving certificate for configured alias: " + chosenAlias);
                return chosenAlias;
            } else {
                android.util.Log.w(TAG, "Configured alias '" + chosenAlias + "' requested, but private key was NOT found in BKS Keystore!");
            }
        }

        // 2. SNI (Server Name Indication) dynamic extraction during TLS handshake
        if (engine != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                SSLSession session = engine.getHandshakeSession();
                if (session instanceof ExtendedSSLSession) {
                    ExtendedSSLSession extSession = (ExtendedSSLSession) session;
                    for (SNIServerName name : extSession.getRequestedServerNames()) {
                        if (name.getType() == StandardConstants.SNI_HOST_NAME && name instanceof SNIHostName) {
                            String hostname = ((SNIHostName) name).getAsciiName();
                            String aliasFromSni = extractAliasFromHostname(hostname);
                            if (aliasFromSni != null && delegate.getPrivateKey(aliasFromSni) != null) {
                                android.util.Log.d(TAG, "Serving certificate for SNI hostname '" + hostname + "' -> alias: " + aliasFromSni);
                                return aliasFromSni;
                            }
                        }
                    }
                }
            } catch (Throwable ignored) {}
        }

        // 3. Fallback to default Java KeyManager selection
        String fallbackAlias = null;
        if (delegate instanceof X509ExtendedKeyManager) {
            fallbackAlias = ((X509ExtendedKeyManager) delegate).chooseEngineServerAlias(keyType, issuers, engine);
        } else {
            fallbackAlias = chooseServerAlias(keyType, issuers, null);
        }
        android.util.Log.w(TAG, "FALLBACK: Serving default certificate alias: " + fallbackAlias);
        return fallbackAlias;
    }

    /**
     * Extracts robot keystore alias from hostname (e.g. "rob-7vh.local" -> "rob-7vh", "7vh" -> "rob-7vh").
     */
    private String extractAliasFromHostname(String hostname) {
        if (hostname == null || hostname.trim().isEmpty()) {
            return null;
        }
        String clean = hostname.trim().toLowerCase();
        if (clean.contains(".")) {
            clean = clean.substring(0, clean.indexOf('.'));
        }
        if (!clean.startsWith("rob-")) {
            clean = "rob-" + clean;
        }
        return clean;
    }

    @Override
    public X509Certificate[] getCertificateChain(String alias) {
        return delegate.getCertificateChain(alias);
    }

    @Override
    public String[] getClientAliases(String keyType, Principal[] issuers) {
        return delegate.getClientAliases(keyType, issuers);
    }

    @Override
    public String[] getServerAliases(String keyType, Principal[] issuers) {
        return delegate.getServerAliases(keyType, issuers);
    }

    @Override
    public PrivateKey getPrivateKey(String alias) {
        return delegate.getPrivateKey(alias);
    }
}

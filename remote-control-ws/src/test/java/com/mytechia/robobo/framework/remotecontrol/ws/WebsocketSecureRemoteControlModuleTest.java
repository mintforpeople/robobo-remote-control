package com.mytechia.robobo.framework.remotecontrol.ws;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class WebsocketSecureRemoteControlModuleTest {

    private WebsocketSecureRemoteControlModule module;

    @Before
    public void setUp() {
        module = new WebsocketSecureRemoteControlModule();
    }

    @Test
    public void testModuleInfoAndVersion() {
        assertNotNull(module.getModuleInfo());
        assertEquals("WebSocket Remote Control Module", module.getModuleInfo());
        assertNotNull(module.getModuleVersion());
        assertEquals("1.5.0-SNAPSHOT", module.getModuleVersion());
    }

    @Test
    public void testRoboboBTNameGetterSetter() {
        assertEquals("ROB-???", module.getRoboboBTName());
        module.setRoboboBTName("ROB-500");
        assertEquals("ROB-500", module.getRoboboBTName());
    }
}

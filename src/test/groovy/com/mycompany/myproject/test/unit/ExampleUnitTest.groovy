package com.mycompany.myproject.test.unit

import org.apache.vysper.mina.TCPEndpoint
import org.apache.vysper.storage.inmemory.MemoryStorageProviderRegistry
import org.apache.vysper.xmpp.server.XMPPServer;

import static org.junit.Assert.*;

import groovy.util.GroovyTestCase;
import org.eu.galaxie.vertx.mod.xmpp.MainVerticle;
import org.junit.Test;

public class ExampleUnitTest extends GroovyTestCase {

    void testVerticle() {

        MainVerticle mainVerticle = new MainVerticle()

        assertNotNull(mainVerticle)
    }
}

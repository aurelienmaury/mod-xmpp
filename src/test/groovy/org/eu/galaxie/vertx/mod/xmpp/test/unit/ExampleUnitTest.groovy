package org.eu.galaxie.vertx.mod.xmpp.test.unit

import static org.junit.Assert.*

import org.eu.galaxie.vertx.mod.xmpp.MainVerticle
import org.junit.Test

public class ExampleUnitTest {

    @Test
    void testVerticle() {

        MainVerticle mainVerticle = new MainVerticle()

        assertNotNull(mainVerticle)
    }
}

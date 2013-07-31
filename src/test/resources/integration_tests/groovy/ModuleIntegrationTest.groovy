import static org.vertx.testtools.VertxAssert.*

import org.vertx.groovy.testtools.VertxTests

def testPing() {
    container.logger.info("in testPing()")
    println "vertx is ${vertx.getClass().getName()}"
    vertx.eventBus.send("xmpp.send", "ping!", { reply ->
        assertEquals("pong!", reply.body())

        /*
        If we get here, the test is complete
        You must always call `testComplete()` at the end. Remember that testing is *asynchronous* so
        we cannot assume the test is complete by the time the test method has finished executing like
        in standard synchronous tests
        */
        testComplete()
    })
}

// Make sure you initialize
VertxTests.initialize(this)

// The script is execute for each test, so this will deploy the module for each one
// Deploy the module - the System property `vertx.modulename` will contain the name of the module so you
// don't have to hardecode it in your tests
println "Module name = ${System.getProperty('vertx.modulename')}"
container.deployModule(System.getProperty('vertx.modulename'), { asyncResult ->
    println ""
    assert asyncResult.succeeded && asyncResult.result()
    VertxTests.startTests(this)
})

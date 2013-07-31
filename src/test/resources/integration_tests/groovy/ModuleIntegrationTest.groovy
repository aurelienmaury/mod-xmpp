import static org.vertx.testtools.VertxAssert.*

import org.vertx.groovy.testtools.VertxTests

def testMessageExchange() {
    container.logger.info("in testPing()")

    vertx.eventBus.send('xmpp.login', [login: 'user1', password: 'password1'])
    vertx.eventBus.send('xmpp.login', [login: 'user2', password: 'password2'])

    vertx.eventBus.registerHandler('xmpp.incoming.user1@localhost') { message ->
        assertTrue message.body.from == 'user2@localhost'
        assertTrue message.body.content.text == 'pong!'
        testComplete()
    }

    vertx.eventBus.registerHandler('xmpp.incoming.user2@localhost') { message ->
        assertTrue message.body.from == 'user1@localhost'
        assertTrue message.body.content.text == 'ping!'

        vertx.eventBus.send('xmpp.send', [from: 'user2', to: message.body.from, content: [text: 'pong!']])
    }

    vertx.eventBus.send('xmpp.send', [from: 'user1', to: 'user2@localhost', content: [text: 'ping!']])
}

VertxTests.initialize(this)
container.deployModule(System.getProperty('vertx.modulename'), { asyncResult ->
    assertTrue(asyncResult.succeeded)
    assertNotNull("deploymentID should not be null", asyncResult.result())
    VertxTests.startTests(this)
})

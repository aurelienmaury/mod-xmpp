package org.eu.galaxie.vertx.mod.xmpp

import org.codehaus.groovy.runtime.memoize.LRUCache
import org.jivesoftware.smack.XMPPConnection
import org.vertx.groovy.platform.Verticle
import org.vertx.java.core.json.impl.Json

class MainVerticle extends Verticle {

    private static final DEFAULT_CONF = [
            host: 'localhost',
            port: 5222
    ]

    ConnectionFactory connectionFactory
    LRUCache connectionCache
    XMPPMessageListener listener

    Map readConf(Map conf) {
        [
                host: conf.host ?: DEFAULT_CONF.host,
                port: conf.port?.toInteger() ?: DEFAULT_CONF.port
        ]
    }

    def start() {
        try {
            def conf = readConf(container.config)

            connectionCache = new LRUCache(20)
            listener = new XMPPMessageListener(vertx.eventBus)
            connectionFactory = new ConnectionFactory(conf.host, conf.port, listener)

            vertx.eventBus.registerHandler('xmpp.login') { message ->
                String login = message.body.login
                String password = message.body.password
                connectionCache.put(login, connectionFactory.connect(login, password))
            }

            vertx.eventBus.registerHandler('xmpp.send') { message ->
                def payload = Json.encode([
                        from: message.body.from + '@' + conf.host,
                        to: message.body.to,
                        content: Json.encode(message.body.content)
                ])

                XMPPConnection connection = connectionCache.get(message.body.from)
                connection.getChatManager().createChat(message.body.to, listener).sendMessage(payload)
            }
        } catch (Exception e) {
            e.printStackTrace()
        }
    }
}

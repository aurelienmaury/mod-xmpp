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
            listener = new XMPPMessageListener(eventBus: vertx.eventBus)
            connectionFactory = new ConnectionFactory(conf.host, conf.port, listener)

            vertx.eventBus.registerHandler('xmpp.login') { loginMessage ->
                println "CONNECTION REQUIRED for ${loginMessage.body.login}"
                String login = loginMessage.body.login
                String password = loginMessage.body.password
                connectionCache.put(login, connectionFactory.connect(login, password))
            }

            vertx.eventBus.registerHandler('xmpp.send') { msg ->
                println "SEND REQUIRED for ${msg.body.from}"

                def payload = Json.encode([
                        from: msg.body.from + '@' + conf.host,
                        to: msg.body.to,
                        content: Json.encode(msg.body.content)
                ])

                XMPPConnection connection = connectionCache.get(msg.body.from)
                connection.getChatManager().createChat(msg.body.to, listener).sendMessage(payload)
            }
        } catch (Exception e) {
            e.printStackTrace()
        }
    }
}

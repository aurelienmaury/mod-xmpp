package org.eu.galaxie.vertx.mod.xmpp

import org.vertx.groovy.platform.Verticle
import org.vertx.java.core.json.impl.Json

class MainVerticle extends Verticle {

    def start() {

        def conf = [
                host: container.config.host ?: 'localhost',
                port: container.config.port?.toInteger() ?: 5222,
                login: container.config.login ?: 'user1@localhost',
                password: container.config.password ?: 'user1'
        ]
        
        XmppConnector connector = new XmppConnector(conf.host, conf.port, conf.login, conf.password, vertx.eventBus)

        vertx.eventBus.registerHandler('xmpp.send') { msg ->

            def payload = Json.encode([target: msg.body.target, replyTo: msg.body.replyTo, body: Json.encode(msg.body.body)])

            connector.sendMessage(payload, msg.body.to)
            println "SENT"
        }
    }
}

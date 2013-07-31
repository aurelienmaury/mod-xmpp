package org.eu.galaxie.vertx.mod.xmpp

import org.jivesoftware.smack.Chat
import org.jivesoftware.smack.MessageListener
import org.jivesoftware.smack.packet.Message
import org.vertx.groovy.core.eventbus.EventBus
import org.vertx.java.core.json.impl.Json

class XMPPMessageListener implements MessageListener {

    EventBus eventBus

    void processMessage(Chat chat, Message message) {

        if (message.type == Message.Type.chat) {

            def fromUser = message.from
            println "RECEIVED from ${fromUser}"


            def jsonContent = Json.decodeValue(message.body, Map.class)

            eventBus.send('xmpp.incoming.' + jsonContent.to, [
                    from: jsonContent.from,
                    to: jsonContent.to,
                    content: Json.decodeValue(jsonContent.content, Map.class)
            ])
        }
    }
}

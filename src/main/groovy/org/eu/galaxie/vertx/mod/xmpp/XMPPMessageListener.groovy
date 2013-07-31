package org.eu.galaxie.vertx.mod.xmpp

import org.jivesoftware.smack.Chat
import org.jivesoftware.smack.MessageListener
import org.jivesoftware.smack.packet.Message
import org.vertx.groovy.core.eventbus.EventBus
import org.vertx.java.core.json.impl.Json

class XMPPMessageListener implements MessageListener {

    private EventBus eventBus

    XMPPMessageListener(EventBus eventBus) {
        this.eventBus = eventBus
    }

    void processMessage(Chat chat, Message message) {

        if (message.type == Message.Type.chat) {
            try {
                def jsonContent = decodeMessage(message.body)
                eventBus.send('xmpp.incoming.' + jsonContent.to, jsonContent)
            } catch (Exception e) {
                e.printStackTrace()
            }
        }
    }

    Map decodeMessage(String xmppMessageContent) {

        def jsonContent = Json.decodeValue(xmppMessageContent, Map.class)
        jsonContent.content = Json.decodeValue(jsonContent.content, Map.class)

        if (!jsonContent.from || !jsonContent.to || !jsonContent.content) {
            throw new IllegalArgumentException("Malformed incoming message")
        }

        jsonContent
    }
}

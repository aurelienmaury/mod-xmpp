package org.eu.galaxie.vertx.mod.xmpp

import org.jivesoftware.smack.Chat
import org.jivesoftware.smack.MessageListener
import org.jivesoftware.smack.packet.Message
import org.vertx.groovy.core.eventbus.EventBus
import org.vertx.java.core.json.impl.Json

class XmppMessageListener implements MessageListener {

    EventBus eventBus

    void processMessage(Chat chat, Message message) {

        if (message.type == Message.Type.chat) {
            def fromUser = message.from
            def jsonContent = Json.decodeValue(message.body, Map.class)
            def target = jsonContent.target
            def body = Json.decodeValue(jsonContent.body, Map.class)
            def replyTo = jsonContent.replyTo

            if (replyTo) {
                println "replying TO ${fromUser}"
                println "send TO ${target} => ${body}"
                eventBus.send(target, body) { replyMsg ->
                    println "got reply for incoming xmpp message, sending response"
                    sendMessage(Json.encode([target: replyTo, body: Json.encode(replyMsg.body)]), fromUser)
                }
            } else {
                println "NOT replying"
                println "publishing TO ${target}"
                eventBus.publish(target, body)
            }
        }
    }
}

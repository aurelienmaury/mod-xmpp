package org.eu.galaxie.vertx.mod.xmpp

import org.jivesoftware.smack.Chat
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.MessageListener
import org.jivesoftware.smack.ChatManagerListener
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smack.XMPPException
import org.jivesoftware.smack.packet.Message

public class XmppConnector implements MessageListener {

    XMPPConnection connection
    def chatMap = [:]
    def eventBus

    public XmppConnector(String host, int port, String login, String password, def eventBus) {
        connection = new XMPPConnection(new ConnectionConfiguration(host, port))
        this.eventBus = eventBus

        connection.getChatManager().addChatListener([chatCreated: { chat, locallyCreated ->
            chat.addMessageListener(this)
        }] as ChatManagerListener)

        connection.connect()
        connection.login(login, password)
    }

    void sendMessage(String message, String to) throws XMPPException {
        if (!chatMap[to]) {
            chatMap[to] = connection.getChatManager().createChat(to, this)
        }
        chatMap[to].sendMessage(message)
    }

    void disconnect() {
        connection.disconnect()
    }

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
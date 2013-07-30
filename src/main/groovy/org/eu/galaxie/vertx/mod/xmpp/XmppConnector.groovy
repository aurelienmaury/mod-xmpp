package org.eu.galaxie.vertx.mod.xmpp

import org.codehaus.groovy.runtime.memoize.LRUCache
import org.jivesoftware.smack.Chat
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.MessageListener
import org.jivesoftware.smack.ChatManagerListener
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smack.XMPPException
import org.jivesoftware.smack.packet.Message
import org.vertx.java.core.json.impl.Json

public class XmppConnector {

    final ChatKeeper chatKeeper

    final XmppMessageListener messageListener

    final XMPPConnection connection

    public XmppConnector(String host, int port, String login, String password, def eventBus) {

        connection = new XMPPConnection(new ConnectionConfiguration(host, port))

        messageListener = new XmppMessageListener(eventBus: eventBus)

        connection.getChatManager().addChatListener([chatCreated: { chat, locallyCreated ->
            chat.addMessageListener(messageListener)
        }] as ChatManagerListener)

        connection.connect()

        connection.login(login, password)

        chatKeeper = new ChatKeeper(connection: connection, messageListener: messageListener)
    }

    void sendMessage(String message, String to) throws XMPPException {

        chatKeeper[to].sendMessage(message)
    }

    void disconnect() {
        connection.disconnect()
    }
}
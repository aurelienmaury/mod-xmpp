package org.eu.galaxie.vertx.mod.xmpp

import org.jivesoftware.smack.ChatManagerListener
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.XMPPConnection

class ConnectionFactory {

    private final ConnectionConfiguration connectionConfiguration

    private final XMPPMessageListener listener

    ConnectionFactory(String host, Integer port, XMPPMessageListener listener) {
        this.connectionConfiguration = new ConnectionConfiguration(host, port)
        this.listener = listener
    }

    XMPPConnection connect(String login, String password) {

        XMPPConnection connection = new XMPPConnection(connectionConfiguration)

        connection.getChatManager().addChatListener([chatCreated: { chat, locallyCreated ->
            chat.addMessageListener(listener)
        }] as ChatManagerListener)

        connection.connect()
        connection.login(login, password)

        connection
    }
}

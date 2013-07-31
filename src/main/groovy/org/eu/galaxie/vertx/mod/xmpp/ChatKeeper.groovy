package org.eu.galaxie.vertx.mod.xmpp

import org.codehaus.groovy.runtime.memoize.LRUCache
import org.jivesoftware.smack.Chat
import org.jivesoftware.smack.XMPPConnection

class ChatKeeper {

    private static final Integer MAX_ACTIVE = 25

    LRUCache chatCache = new LRUCache(MAX_ACTIVE)

    XMPPConnection connection

    XMPPMessageListener messageListener

    Chat getAt(String key) {
        if (!chatCache.get(key)) {
            chatCache.put(key, connection.getChatManager().createChat(key, messageListener))
        }
        chatCache.get(key)
    }
}

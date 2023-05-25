package org.vitaliistf.sessions;

import java.util.HashMap;
import java.util.Map;

public class UserSessionManager {
    private static final Map<Long, Session> sessions = new HashMap<>();

    public static Session getSession(long chatId) {
        if(sessions.get(chatId) == null) {
            sessions.put(chatId, new Session());
        }
        return sessions.get(chatId);
    }
}

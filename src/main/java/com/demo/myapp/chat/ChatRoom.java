package com.demo.myapp.chat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Mediator Pattern — Concrete Mediator
// The ONLY class that knows about all users; routes messages between them
// Users never talk to each other directly — they only talk to ChatRoom
public class ChatRoom implements ChatMediator {

    private final Map<String, User> users = new LinkedHashMap<>(); // name → User
    private final List<String> messageLog = new ArrayList<>();     // audit trail

    @Override
    public void addUser(User user) {
        users.put(user.getName(), user);
        String event = user.getName() + " joined the chat room";
        messageLog.add("[SYSTEM] " + event);
        System.out.println("[CHATROOM] " + event);

        // Mediator coordinates: notify existing users that someone new joined
        users.values().stream()
                .filter(u -> !u.getName().equals(user.getName()))
                .forEach(u -> u.receive(user.getName() + " has joined", "SYSTEM"));
    }

    @Override
    public void removeUser(String name) {
        users.remove(name);
        String event = name + " left the chat room";
        messageLog.add("[SYSTEM] " + event);
        System.out.println("[CHATROOM] " + event);
    }

    @Override
    public void sendMessage(String message, User sender) {
        String logEntry = sender.getName() + ": " + message;
        messageLog.add(logEntry);

        // Route to all users EXCEPT the sender — mediator controls the routing logic
        users.values().stream()
                .filter(u -> !u.getName().equals(sender.getName()))
                .forEach(u -> u.receive(message, sender.getName()));
    }

    @Override
    public void sendPrivateMessage(String message, User sender, String recipientName) {
        String logEntry = "[DM] " + sender.getName() + " → " + recipientName + ": " + message;
        messageLog.add(logEntry);

        User recipient = users.get(recipientName);
        if (recipient != null) {
            recipient.receive("[DM] " + message, sender.getName());
        } else {
            System.out.println("[CHATROOM] User '" + recipientName + "' not found");
        }
    }

    public List<String> getMessageLog() { return List.copyOf(messageLog); }
    public List<String> getActiveUsers() { return new ArrayList<>(users.keySet()); }
}

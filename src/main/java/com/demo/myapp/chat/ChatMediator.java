package com.demo.myapp.chat;

// Mediator Pattern — interface the ChatRoom implements
// Users talk ONLY to this; they never hold references to each other
public interface ChatMediator {
    void addUser(User user);
    void removeUser(String name);
    void sendMessage(String message, User sender);        // broadcast to all others
    void sendPrivateMessage(String message, User sender, String recipientName); // DM
}

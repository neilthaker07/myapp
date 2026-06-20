package com.demo.myapp.chat;

// Concrete Colleague — same interface as RegularUser but labelled as admin in messages
// The ChatRoom (mediator) could give admins special routing rules if needed
public class AdminUser extends User {

    public AdminUser(String name, ChatMediator mediator) {
        super(name, mediator);
    }

    @Override
    public void send(String message) {
        System.out.println("[ADMIN:" + name + "] announces: " + message);
        mediator.sendMessage("[ANNOUNCEMENT] " + message, this);
    }

    @Override
    public void sendPrivate(String message, String recipientName) {
        System.out.println("[ADMIN:" + name + "] DM to " + recipientName + ": " + message);
        mediator.sendPrivateMessage(message, this, recipientName);
    }

    @Override
    public void receive(String message, String senderName) {
        System.out.println("[ADMIN:" + name + "] received from " + senderName + ": " + message);
    }
}

package com.demo.myapp.chat;

// Concrete Colleague — can send broadcasts and private messages
// Knows nothing about other users; delegates everything to the mediator
public class RegularUser extends User {

    public RegularUser(String name, ChatMediator mediator) {
        super(name, mediator);
    }

    @Override
    public void send(String message) {
        System.out.println("[" + name + "] broadcasts: " + message);
        mediator.sendMessage(message, this); // mediator decides who gets it
    }

    @Override
    public void sendPrivate(String message, String recipientName) {
        System.out.println("[" + name + "] DM to " + recipientName + ": " + message);
        mediator.sendPrivateMessage(message, this, recipientName);
    }

    @Override
    public void receive(String message, String senderName) {
        System.out.println("[" + name + "] received from " + senderName + ": " + message);
    }
}

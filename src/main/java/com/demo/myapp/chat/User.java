package com.demo.myapp.chat;

// Mediator Pattern — Colleague
// Holds ONLY a reference to the mediator, never to other users
// Sending = tell the mediator; receiving = mediator calls this
public abstract class User {

    protected final String name;
    protected final ChatMediator mediator;

    public User(String name, ChatMediator mediator) {
        this.name = name;
        this.mediator = mediator;
    }

    public String getName() { return name; }

    public abstract void send(String message);
    public abstract void sendPrivate(String message, String recipientName);
    public abstract void receive(String message, String senderName);
}

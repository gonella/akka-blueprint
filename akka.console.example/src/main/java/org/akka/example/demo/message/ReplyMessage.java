package org.akka.example.demo.message;

public class ReplyMessage extends BaseMessage
{

    private final String sender;

    public ReplyMessage(String sender)
    {
        this.sender = sender;

    }

    public String getSender()
    {
        return sender;
    }

}

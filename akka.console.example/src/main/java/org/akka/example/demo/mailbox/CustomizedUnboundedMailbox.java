package org.akka.example.demo.mailbox;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.typesafe.config.Config;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.dispatch.Envelope;
import akka.dispatch.MailboxType;
import akka.dispatch.MessageQueue;
import akka.dispatch.ProducesMessageQueue;
import scala.Option;

public class CustomizedUnboundedMailbox
        implements MailboxType, ProducesMessageQueue<CustomizedUnboundedMailbox.CustomizedMessageQueue>
{
    private static final Logger LOGGER = LogManager.getLogger(CustomizedUnboundedMailbox.class.getName());
    private CustomizedMessageQueue messageQueue;

    // This is the MessageQueue implementation
    public static class CustomizedMessageQueue implements MessageQueue, CustomizedUnboundedMessageQueueSemantics
    {
        private final Queue<Envelope> queue = new ConcurrentLinkedQueue<Envelope>();

        // these must be implemented; queue used as example
        @Override
        public void enqueue(ActorRef receiver, Envelope handle)
        {
            LOGGER.info("enqueue {}", receiver.toString());
            getQueue().offer(handle);
        }

        @Override
        public Envelope dequeue()
        {
            return getQueue().poll();
        }

        @Override
        public int numberOfMessages()
        {
            return getQueue().size();
        }

        @Override
        public boolean hasMessages()
        {
            return !getQueue().isEmpty();
        }

        @Override
        public void cleanUp(ActorRef owner, MessageQueue deadLetters)
        {
            for (final Envelope handle : getQueue())
            {
                deadLetters.enqueue(owner, handle);
            }
        }

        public Queue<Envelope> getQueue()
        {
            return queue;
        }
    }

    // This constructor signature must exist, it will be called by Akka
    public CustomizedUnboundedMailbox(ActorSystem.Settings settings, Config config)
    {

    }

    // The create method is called to create the MessageQueue
    @Override
    public MessageQueue create(Option<ActorRef> owner, Option<ActorSystem> system)
    {
        setMessageQueue(new CustomizedMessageQueue());
        return getMessageQueue();
    }

    public CustomizedMessageQueue getMessageQueue()
    {
        return messageQueue;
    }

    public void setMessageQueue(CustomizedMessageQueue messageQueue)
    {
        this.messageQueue = messageQueue;
    }

}

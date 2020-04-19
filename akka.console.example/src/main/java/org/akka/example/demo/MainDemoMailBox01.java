package org.akka.example.demo;

import org.akka.example.demo.mailbox.CustomizedUnboundedMailbox;
import org.akka.example.demo.mailbox.UntypedActorWorkerWithMailbox;
import org.akka.example.demo.message.WorkerClaimOperationMessage;
import org.akka.example.demo.message.WorkerConfigOperationMessage;
import org.akka.example.demo.message.WorkerOperationMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.dispatch.Mailboxes;

public class MainDemoMailBox01 extends Constants
{
    private static final Logger LOGGER = LogManager.getLogger(MainDemoMailBox01.class.getName());

    public static void main(String args[])
            throws InterruptedException
    {
        LOGGER.info("System message ");

        final ActorSystem system = ActorSystem.create(SYSTEM);

        final Props props = Props.create(UntypedActorWorkerWithMailbox.class);
        final ActorRef actorSelection = system.actorOf(props
                .withDispatcher("prio-dispatcher")
                .withMailbox("custom-dispatcher-mailbox")
        //.withMailbox("priority-mailbox"),
                , WORKER_0);

        actorSelection.tell(new WorkerClaimOperationMessage(1), ActorRef.noSender());
        actorSelection.tell(new WorkerConfigOperationMessage(), ActorRef.noSender());
        actorSelection.tell(new WorkerOperationMessage(), ActorRef.noSender());
        actorSelection.tell(new WorkerOperationMessage(), ActorRef.noSender());

        final Mailboxes mailboxes = system.mailboxes();

        final boolean hasRequiredType = mailboxes.hasRequiredType(UntypedActorWorkerWithMailbox.class);
        LOGGER.info("hasRequiredType {}", hasRequiredType);

        final CustomizedUnboundedMailbox mailboxType = (CustomizedUnboundedMailbox) mailboxes
                .getMailboxType(props, system.dispatchers().defaultDispatcherConfig());

        LOGGER.info("Mailbox : {}", mailboxType);

        LOGGER.info("Waiting to terminate");
        Thread.sleep(2000);
        system.terminate();
        LOGGER.info("Terminated");
    }
}

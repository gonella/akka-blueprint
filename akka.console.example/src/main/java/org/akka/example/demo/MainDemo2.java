package org.akka.example.demo;

import org.akka.example.demo.message.WorkerClaimOperationMessage;
import org.akka.example.demo.message.WorkerConfigOperationMessage;
import org.akka.example.demo.message.WorkerCreateMessage;
import org.akka.example.demo.message.WorkerOperationMessage;
import org.akka.example.demo.message.ServerRefreshMessage;
import org.akka.example.demo.untyped.UntypedActorServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class MainDemo2
{
    private static final Logger LOGGER = LogManager.getLogger(MainDemo2.class.getName());

    public static void main(String args[])
            throws InterruptedException
    {
        LOGGER.info("Sending message directly to server");

        final ActorSystem system = ActorSystem.create(Constants.SYSTEM);

        final ActorRef server = system.actorOf(Props.create(UntypedActorServer.class), Constants.SERVER);

        //Talk to server, but not send to worker
        server.tell(new WorkerCreateMessage("0"), ActorRef.noSender());
        server.tell(new ServerRefreshMessage(), ActorRef.noSender());

        // Sending directly to worker
        ////System/user/Server/Worker0
        final String address0 = Constants.BASE_PATH + Constants.WORKER_0;
        LOGGER.info("Path " + address0);

        final ActorSelection actor = system.actorSelection(address0);

        actor.tell(new WorkerClaimOperationMessage(), ActorRef.noSender());
        actor.tell(new WorkerConfigOperationMessage(), ActorRef.noSender());
        actor.tell(new WorkerOperationMessage(), ActorRef.noSender());
        actor.tell(new WorkerOperationMessage(), ActorRef.noSender());

        LOGGER.info("Waiting to terminate");
        Thread.sleep(2000);
        system.terminate();
        LOGGER.info("Terminated");
    }
}

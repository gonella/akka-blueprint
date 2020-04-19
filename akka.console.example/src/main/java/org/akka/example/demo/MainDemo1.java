package org.akka.example.demo;

import org.akka.example.demo.abstractlogging.ActorServer;
import org.akka.example.demo.message.WorkerCreateMessage;
import org.akka.example.demo.message.WorkerJobMessage;
import org.akka.example.demo.message.ServerRefreshMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class MainDemo1 
{
    private static final Logger LOGGER = LogManager.getLogger(MainDemo1.class.getName());

    public static void main(String[] args)
            throws Exception
    {
        final ActorSystem system = ActorSystem.create(Constants.SYSTEM);

        final ActorRef actorServer = system.actorOf(Props.create(ActorServer.class), Constants.SERVER);

        //Passing the parent, you are saying that the child can reply to parent automatically.
        //System/user/Server
        actorServer.tell(new WorkerCreateMessage("0"), ActorRef.noSender());
        actorServer.tell(new WorkerCreateMessage("1"), ActorRef.noSender());
        actorServer.tell(new ServerRefreshMessage(), ActorRef.noSender());

        //Sending through the server is way to syncronize the calls, make sure preprocess is required.
        //System/user/Server/Worker0
        actorServer.tell(new WorkerJobMessage(Constants.WORKER_0), actorServer);
        actorServer.tell(new WorkerJobMessage(Constants.WORKER_0), actorServer);
        actorServer.tell(new WorkerJobMessage(Constants.WORKER_0), actorServer);
        actorServer.tell(new WorkerJobMessage(Constants.WORKER_0), actorServer);
        actorServer.tell(new WorkerJobMessage(Constants.WORKER_0), actorServer);
        actorServer.tell(new WorkerJobMessage(Constants.WORKER_0), actorServer);
        actorServer.tell(new WorkerJobMessage(Constants.WORKER_0), actorServer);
        actorServer.tell(new WorkerJobMessage(Constants.WORKER_0), actorServer);

        LOGGER.info("Waiting to terminate");
        Thread.sleep(2000);
        system.terminate();
        LOGGER.info("Terminated");

    }
}

package org.akka.example.demo.untyped;

import org.akka.example.demo.Constants;
import org.akka.example.demo.message.WorkerCreateMessage;
import org.akka.example.demo.message.WorkerJobMessage;
import org.akka.example.demo.message.ServerRefreshMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class UntypedActorServer extends UntypedActor
{
    private int counter = 0;

    private static final Logger LOGGER = LogManager.getLogger(UntypedActorServer.class.getName());

    @Override
    public void onReceive(Object arg0)
            throws Throwable
    {

        if (arg0 instanceof WorkerCreateMessage)
        {
            final WorkerCreateMessage converted = (WorkerCreateMessage) arg0;

            LOGGER.info("Message received by server - WorkerCreateMessage - Counter [" + counter + "]");
            final String id = Constants.WORKER + converted.getResourceId();
            final ActorRef child = getContext()
                    .actorOf(Props.create(UntypedActorWorkerNoWait.class), id);

            child.tell(new WorkerJobMessage(id), getSelf());
            counter++;
        }
        else if (arg0 instanceof ServerRefreshMessage)
        {
            LOGGER.info("Message received by server - ServerRefreshMessage - Counter [" + counter + "]");
            counter++;
        }
        else
        {
            LOGGER.info("Message received by server - Unhandled message :" + arg0);
            unhandled(arg0);
        }
    }

}

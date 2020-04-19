package org.akka.example.demo.untyped;

import org.akka.example.demo.message.WorkerClaimOperationMessage;
import org.akka.example.demo.message.WorkerConfigOperationMessage;
import org.akka.example.demo.message.WorkerJobMessage;
import org.akka.example.demo.message.WorkerOperationMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import akka.actor.UntypedActor;

public class UntypedActorWorker extends UntypedActor
{
    private int counterJobMessage = 0;

    private static final Logger LOGGER = LogManager.getLogger(UntypedActorWorker.class.getName());

    @Override
    public void onReceive(Object arg0)
            throws Throwable
    {
        process(arg0);

        LOGGER.info("Sleeping");
        Thread.sleep(3000);
        LOGGER.info("Waking up");

    }

    protected void process(Object arg0)
    {
        if (arg0 instanceof WorkerJobMessage)
        {
            final WorkerJobMessage message = (WorkerJobMessage) arg0;

            LOGGER.info("Worker Job - received - "
                    + message.getResourceId()
                    + " - counter ["
                    + counterJobMessage
                    + "]");

            counterJobMessage++;
        }
        else if (arg0 instanceof WorkerOperationMessage)
        {
            LOGGER.info(WorkerOperationMessage.class.getSimpleName()
                    + " - received - "
                    + " - counter ["
                    + counterJobMessage
                    + "]");

            counterJobMessage++;
        }
        else if (arg0 instanceof WorkerClaimOperationMessage)
        {
            LOGGER.info(WorkerClaimOperationMessage.class.getSimpleName()
                    + " - received - "
                    + " - counter ["
                    + counterJobMessage
                    + "]");

            counterJobMessage++;
        }
        else if (arg0 instanceof WorkerConfigOperationMessage)
        {
            LOGGER.info(WorkerConfigOperationMessage.class.getSimpleName()
                    + " - received - "
                    + " - counter ["
                    + counterJobMessage
                    + "]");

            counterJobMessage++;
        }
        else
        {
            LOGGER.info("Worker received unhandled message :" + arg0);
            unhandled(arg0);
        }
    }

}

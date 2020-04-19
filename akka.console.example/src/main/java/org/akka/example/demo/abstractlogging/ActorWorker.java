package org.akka.example.demo.abstractlogging;

import org.akka.example.demo.message.WorkerJobMessage;
import org.akka.example.demo.message.WorkerOperationMessage;
import org.akka.example.demo.message.ReplyMessage;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class ActorWorker extends AbstractLoggingActor
{
    private int counterOperation = 0;
    private int counterMessage = 0;

    public ActorWorker()
    {
        final PartialFunction<Object, BoxedUnit> build = ReceiveBuilder
                .match(WorkerJobMessage.class, this::onMessage)
                .match(WorkerOperationMessage.class, this::onMessage)
                .build();

        receive(build);
    }

    private void onMessage(WorkerJobMessage message)
    {
        counterMessage++;
        final ActorRef self = getContext().self();

        log().info("Message received by worker - Job message " + counterMessage);

        getContext().parent().tell(new ReplyMessage(message.getResourceId()), self);
    }

    private void onMessage(WorkerOperationMessage message)
    {
        counterOperation++;
        log().info("Operation :" + counterOperation);

    }
}

package org.akka.example.demo.abstractlogging;

import org.akka.example.demo.Constants;
import org.akka.example.demo.message.WorkerCreateMessage;
import org.akka.example.demo.message.WorkerJobMessage;
import org.akka.example.demo.message.ServerRefreshMessage;
import org.akka.example.demo.message.ReplyMessage;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import akka.japi.pf.UnitPFBuilder;
import scala.Option;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class ActorServer extends AbstractLoggingActor
{
    private int counter = 0;

    public ActorServer()
    {
        final UnitPFBuilder<Object> match = ReceiveBuilder
                .match(WorkerCreateMessage.class, this::onMessage)
                .match(ServerRefreshMessage.class, this::onMessage)
                .match(WorkerJobMessage.class, this::processJob)
                .match(ReplyMessage.class, this::callback);

        final PartialFunction<Object, BoxedUnit> build = match.build();

        receive(build);
    }

    private void onMessage(WorkerCreateMessage message)
    {
        final ActorRef actor = getContext().actorOf(
                Props.create(ActorWorker.class),
                Constants.WORKER + counter);

        final ActorRef parent = getContext().parent();

        actor.tell(
                new WorkerJobMessage(Constants.WORKER + message.getResourceId()),
                parent);

        counter++;
        log().info("Message received by server - WorkerCreateMessage counter " + counter);
    }

    private void onMessage(ServerRefreshMessage message)
    {
        counter++;
        log().info("Message received by server - ServerRefreshMessage counter " + counter);
    }

    private void processJob(WorkerJobMessage jobMessage)
    {
        final Option<ActorRef> child = getContext().child(jobMessage.getResourceId());

        final ActorRef actorRef = child.get();

        final ActorRef parent = getContext().parent();

        actorRef.tell(jobMessage, parent);

        log().info("Message received by server - WorkerJobMessage(Pass throught) counter " + counter);
    }

    private void callback(ReplyMessage replyMessage)
    {
        log().info("Callback done for " + replyMessage.getSender());

    }

    /*
     * @Override
     * public SupervisorStrategy supervisorStrategy() {
     *
     * OneForOneStrategy strategy = new OneForOneStrategy(10,
     * Duration.create("10"),
     * DeciderBuilder.match(RuntimeException.class, ex -> restart()).build());
     *
     * return strategy;
     * }
     */
}

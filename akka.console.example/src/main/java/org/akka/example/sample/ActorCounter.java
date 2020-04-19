package org.akka.example.sample;

import akka.actor.AbstractLoggingActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class ActorCounter extends AbstractLoggingActor
{
    private int counter = 0;

    {
        final PartialFunction<Object, BoxedUnit> build = ReceiveBuilder
                .match(CustomizedMessage.class, this::onMessage)
                .build();

        receive(build);
    }

    private void onMessage(CustomizedMessage message)
    {
        counter++;
        log().info("Increased counter " + counter);
    }

    public static Props props()
    {
        return Props.create(ActorCounter.class);
    }
}

package org.akka.example.sample1;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import akka.testkit.JavaTestKit;

public class SampleTest
{

    public static final class PingActor extends AbstractActor
    {
        {
            receive(ReceiveBuilder
                    .matchEquals("ping", ping -> sender().tell("pong", self()))
                    .build());
        }
    }

    static ActorSystem system;

    @BeforeClass
    public static void setup()
    {
        system = ActorSystem.create();
    }

    @AfterClass
    public static void teardown()
    {
        JavaTestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void testPingPongInteraction()
    {
        new JavaTestKit(system)
        {
            {

                new Within(duration("3 seconds"))
                {
                    @Override
                    protected void run()
                    {
                        final Props props = Props.create(PingActor.class);
                        final ActorRef actor = system.actorOf(props, "ping-pong");

                        actor.tell("ping", getRef());

                        expectMsgEquals("pong");

                    }
                };

            }
        };
    }

}

package org.akka.example.sample;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

/**
 * Actor that keeps state
 */
public class MainSample1
{
    private static final Logger LOGGER = LogManager.getLogger(MainSample1.class.getName());

    public static void main(String[] args)
    {
        final ActorSystem system = ActorSystem.create("sample1");

        final ActorRef counter = system.actorOf(ActorCounter.props(), "counter");

        for (int i = 0; i < 100; i++)
        {
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    for (int j = 0; j < 5; j++)
                    {
                        counter.tell(new CustomizedMessage(), ActorRef.noSender());
                    }
                }
            }).start();
        }

        LOGGER.info("ENTER to terminate");

    }
}

package org.akka.example.demo.mailbox;

import org.akka.example.demo.message.BaseMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.typesafe.config.Config;

import akka.actor.ActorSystem;
import akka.dispatch.PriorityGenerator;
import akka.dispatch.UnboundedStablePriorityMailbox;

public class CustomizedPriorityMailbox extends UnboundedStablePriorityMailbox
{

    private static final Logger LOGGER = LogManager.getLogger(CustomizedPriorityMailbox.class.getName());

    // needed for reflective instantiation
    public CustomizedPriorityMailbox(ActorSystem.Settings settings, Config config)
    {
        // Create a new PriorityGenerator, lower prio means more important
        super(new PriorityGenerator()
        {
            @Override
            public int gen(Object message)
            {
                Integer result = 0;
                if (message instanceof BaseMessage)
                {

                    final BaseMessage baseMessage = (BaseMessage) message;

                    final Integer priority = baseMessage.getPriority();

                    // Poor priority design, this is temporary.

                    LOGGER.info("Message with priority {} received", priority);

                    if (priority == 0)
                        result = 0; // 'highpriority messages should be treated
                                    // first if possible
                    else if (priority == 1)
                        result = 2; // 'lowpriority messages should be treated
                                    // last if possible
                    else if (priority > 1)
                        result = 3; // PoisonPill when no other left
                    else
                        result = 1; // By default they go between high and low
                                    // prio
                }
                return result;
            }
        });
    }
}

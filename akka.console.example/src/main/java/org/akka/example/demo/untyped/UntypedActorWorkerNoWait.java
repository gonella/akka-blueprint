package org.akka.example.demo.untyped;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UntypedActorWorkerNoWait extends UntypedActorWorker
{
    private static final Logger LOGGER = LogManager.getLogger(UntypedActorWorkerNoWait.class.getName());

    @Override
    public void onReceive(Object arg0)
            throws Throwable
    {
        process(arg0);

    }
}

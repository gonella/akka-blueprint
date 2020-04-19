package org.akka.example.demo.message;

public class WorkerCreateMessage  extends BaseMessage
{
    private final String resourceId;

    public WorkerCreateMessage(String id)
    {
        this.resourceId = id;
    }

    public String getResourceId()
    {
        return resourceId;
    }
}

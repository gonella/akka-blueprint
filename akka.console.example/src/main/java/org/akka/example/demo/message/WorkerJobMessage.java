package org.akka.example.demo.message;

public class WorkerJobMessage  extends BaseMessage
{
    private final String resourceId;

    public WorkerJobMessage(String id)
    {
        this.resourceId = id;
    }

    public String getResourceId()
    {
        return resourceId;
    }
}

package org.akka.example.demo.message;

public class WorkerConfigOperationMessage  extends BaseMessage
{
    private Integer number = -1;

    public WorkerConfigOperationMessage()
    {

    }

    public WorkerConfigOperationMessage(Integer number)
    {
        this.number = number;

    }

    public Integer getNumber()
    {
        return number;
    }

}

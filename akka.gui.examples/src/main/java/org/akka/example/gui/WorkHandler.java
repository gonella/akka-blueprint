package org.akka.example.gui;

import java.util.Iterator;
import java.util.Queue;

import org.akka.example.demo.mailbox.CustomizedUnboundedMailbox;
import org.akka.example.demo.message.BaseMessage;
import org.akka.example.demo.message.WorkerCreateMessage;
import org.akka.example.demo.message.WorkerJobMessage;
import org.akka.example.demo.untyped.UntypedActorWorker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.dispatch.Envelope;
import akka.dispatch.Mailboxes;

public class WorkHandler
{	
	private static final String OPERATION_REMOVE = "Remove [P:0]";
	private static final String OPERATION_CONFIG = "Config [P:1]";
	private static final String OPERATION_RECOVERY = "Recovery [P:2]";

	private static final Logger LOGGER = LogManager.getLogger(Main.class.getName());

    public static final String LABEL = "Worker";

    private List list;

    private String id;

    private Integer bay;

    private Integer counter = 0;

    private ActorSystem actorSystem;

    private CustomizedUnboundedMailbox mailboxType;

	private Composite composite;

    public WorkHandler(Integer bay, Composite parentComposite, ActorSystem actorSystem)
    {
        this.setActorSystem(actorSystem);
        this.setBay(bay);
        setId(LABEL + bay);

        setList(createComposite(parentComposite, getId()));

        final WorkerCreateMessage message = new WorkerCreateMessage(getId());

        final ActorRef actor = createActor(message);

        LOGGER.info("Create actor handler {}", actor);
    }

    private List createComposite(Composite parentComposite, final String id)
    {
        setComposite(new Composite(parentComposite, SWT.NONE));
        
        final RowLayout rl_composite_5 = new RowLayout(SWT.VERTICAL);
        rl_composite_5.center = true;
        getComposite().setLayout(rl_composite_5);

        final Composite composite_7 = new Composite(getComposite(), SWT.NONE);
        composite_7.setLayout(new FillLayout(SWT.HORIZONTAL));
        composite_7.setLayoutData(new RowData(143, SWT.DEFAULT));

        final Label lblNewLabel_1 = new Label(composite_7, SWT.NONE);
        //lblNewLabel_1.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));
        lblNewLabel_1.setAlignment(SWT.CENTER);
        lblNewLabel_1.setText(id);

        new Label(getComposite(), SWT.SEPARATOR | SWT.HORIZONTAL);

        final Composite composite_6 = new Composite(getComposite(), SWT.NONE);
        composite_6.setLayout(new FillLayout(SWT.VERTICAL));
        composite_6.setLayoutData(new RowData(169, SWT.DEFAULT));

        final Button btnNewButton = new Button(composite_6, SWT.NONE);
        btnNewButton.setText(OPERATION_REMOVE);
        btnNewButton.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doDelete(id);
            }
        });

        final Button btnConfig = new Button(composite_6, SWT.NONE);
        btnConfig.setText(OPERATION_CONFIG);
        btnConfig.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doConfig(id);
            }
        });

        final Button btnExtra = new Button(composite_6, SWT.NONE);
        btnExtra.setText(OPERATION_RECOVERY);
        btnExtra.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doOther(id);
            }
        });

        new Label(getComposite(), SWT.SEPARATOR | SWT.HORIZONTAL);

        final Label lblNewLabel = new Label(getComposite(), SWT.NONE);
        lblNewLabel.setText("Queue processing");

        new Label(getComposite(), SWT.SEPARATOR | SWT.HORIZONTAL);

        final List jobList = new List(getComposite(), SWT.BORDER);
        jobList.setLayoutData(new RowData(192, 395));

        updateUIComponentsPeriodically(id, getComposite());

        return jobList;
    }

    private void updateUIComponentsPeriodically(final String id, Composite composite)
    {
        final Display display = composite.getDisplay();

        final Runnable timer = new Runnable()
        {
            public void run()
            {
                LOGGER.info("Updating worker for {}", id);
                populatingQueueUI(id);
                display.timerExec(1000, this);
            }
        };
        display.timerExec(1000, timer);
    }

    public ActorRef createActor(WorkerCreateMessage message)
    {

        LOGGER.info("Receiving message - WorkerCreateMessage - {}", message);

        final Props props = Props.create(UntypedActorWorker.class)
                .withDispatcher("prio-dispatcher")
                .withMailbox("custom-dispatcher-mailbox");

        final ActorRef actor = getActorSystem().actorOf(props, message.getResourceId());

        final Mailboxes mailboxes = getActorSystem().mailboxes();

        setMailboxType((CustomizedUnboundedMailbox) mailboxes.getMailboxType(
                props,
                getActorSystem().dispatchers().defaultDispatcherConfig()));

        return actor;
    }

    protected void doDelete(String id)
    {

        createMessage("Delete", id, 0);
    }

    protected void doConfig(String id)
    {
        createMessage("Config", id, 1);

    }

    protected void doOther(String id)
    {
        createMessage("Recovery", id, 2);

    }

    private void createMessage(String type, String id, Integer priority)
    {

        final String actorPath = buildChildPath(id);

        LOGGER.info("Sending message to worker {}", actorPath);
        final ActorSelection actorSelection = getActorSystem().actorSelection(actorPath);

        final WorkerJobMessage msg = new WorkerJobMessage(id);
        msg.setId(counter);
        msg.setPriority(priority);
        msg.setType(type);

        actorSelection.tell(msg, ActorRef.noSender());

        populatingQueueUI(id);

        counter++;
    }

    private String buildChildPath(String id)
    {
        final String PATH = "//" + Main.SYSTEM + getBay() + "/user/";

        return PATH + id;
    }

    public void populatingQueueUI(String id)
    {
        if (getMailboxType() == null)
        {
            return;
        }

        final Queue<Envelope> queue = getMailboxType().getMessageQueue().getQueue();

        LOGGER.info("Queue size {} - {}", queue.size(), getId());

        getList().removeAll();

        final Iterator<Envelope> iterator = queue.iterator();
        while (iterator.hasNext())
        {
            final Envelope next = iterator.next();

            final Object message = next.message();

            if (message instanceof BaseMessage)
            {

                final BaseMessage messageConverted = (BaseMessage) message;

                if (messageConverted instanceof WorkerJobMessage)
                {

                    final WorkerJobMessage sent = (WorkerJobMessage) messageConverted;

                    final String string = "T:["
                            + sent.getType()
                            + "] ID ["
                            + messageConverted.getId()
                            + "] P["
                            + messageConverted.getPriority()
                            + "]";
                    getList().add(string);
                    LOGGER.info("Message in queue {}", string);
                }
            }

        }
        // parentComposite.layout(true, true);

    }

    public List getList()
    {
        return list;
    }

    public void setList(List list)
    {
        this.list = list;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public Integer getBay()
    {
        return bay;
    }

    public void setBay(Integer bay)
    {
        this.bay = bay;
    }

    public Integer getCounter()
    {
        return counter;
    }

    public void setCounter(Integer counter)
    {
        this.counter = counter;
    }

    public CustomizedUnboundedMailbox getMailboxType()
    {
        return mailboxType;
    }

    public void setMailboxType(CustomizedUnboundedMailbox mailboxType)
    {
        this.mailboxType = mailboxType;
    }

    public ActorSystem getActorSystem()
    {
        return actorSystem;
    }

    public void setActorSystem(ActorSystem actorSystem)
    {
        this.actorSystem = actorSystem;
    }

	public Composite getComposite() {
		return composite;
	}

	public void setComposite(Composite composite) {
		this.composite = composite;
	}
}

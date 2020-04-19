package org.akka.example.gui;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import akka.actor.ActorSystem;

public class Main
{
    private static final int APP_SIZE_HEIGHT = 742;
	private static final int APP_SIZE_WIDTH = 1148;
	private static final String APP_TITLE = "AKKA server - queue worker(s)";

	private static final Logger LOGGER = LogManager.getLogger(Main.class.getName());

    public static final String SYSTEM = "System";
    private Integer counter = 0;
    private final List<WorkHandler> handlers = new ArrayList<WorkHandler>();

    protected Shell shell;
    private Composite parentComposite;
	private ScrolledComposite scrolledComposite;

    /**
     * Launch the application.
     *
     * @param args
     */
    public static void main(String[] args)
    {
        try
        {
            final Main window = new Main();
            window.open();
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Open the window.
     */
    public void open()
    {
        final Display display = Display.getDefault();

        createContents();
        shell.open();
        shell.layout();
        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch())
            {
                display.sleep();
            }
        }
    }

    /**
     * Create contents of the window.
     */
    protected void createContents()
    {
        shell = new Shell();
        shell.addDisposeListener(new DisposeListener()
        {
            public void widgetDisposed(DisposeEvent arg0)
            {
                System.exit(0);
            }
        });
        shell.setSize(APP_SIZE_WIDTH, APP_SIZE_HEIGHT);
        shell.setText(APP_TITLE);
        shell.setLayout(new FillLayout(SWT.VERTICAL));
        
        shell.setLayout(new GridLayout(1, false));
        GridData shellGridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        shell.setLayoutData(shellGridData);

        //Button composite
        final Composite buttonComposite = new Composite(shell, SWT.NONE);        
        buttonComposite.setLayout(new GridLayout(1, false));
        GridData buttonCompositeGridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        buttonComposite.setLayoutData(buttonCompositeGridData);
        
        final Button buttonAdd = new Button(buttonComposite, SWT.NONE);        
        buttonAdd.setText("Add Worker");
        buttonAdd.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doAdd();
            }
        });
        
        
        //Button composite
        final Composite groupComposite = new Composite(shell, SWT.NONE);        
        groupComposite.setLayout(new GridLayout(1, false));
        GridData groupGridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        groupComposite.setLayoutData(groupGridData);
        
        Group group = new Group(groupComposite, SWT.NONE);
        group.setText("Group");
        group.setLayout(new GridLayout(1, false));
        GridData firstData = new GridData(SWT.FILL, SWT.FILL, true, false);
        firstData.heightHint = APP_SIZE_HEIGHT;
        group.setLayoutData(firstData);

        
        scrolledComposite = new ScrolledComposite(group, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);        
        scrolledComposite.setLayout(new GridLayout(1, false));
        scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        //Worker layout. Where workers will be added.      
        parentComposite = new Composite(scrolledComposite, SWT.NONE);        
        parentComposite.setLayout(new GridLayout(5, false));
        parentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
                
        showScrollpane();
    }

	private void showScrollpane() {
		scrolledComposite.setMinSize(parentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        scrolledComposite.setContent(parentComposite);
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setAlwaysShowScrollBars(true);
	}

    protected void doAdd()
    {    	
    	String actorCounter=SYSTEM + counter;
    	
        LOGGER.info("Adding worker :{}", actorCounter);

        final ActorSystem actorSystem = ActorSystem.create(actorCounter);

        final WorkHandler handler = new WorkHandler(counter, parentComposite, actorSystem);

        handlers.add(handler);
        
        parentComposite.layout(true, true);
        parentComposite.pack();
        
        showScrollpane();

        LOGGER.info("Worker Added");
        counter++;
    }

}

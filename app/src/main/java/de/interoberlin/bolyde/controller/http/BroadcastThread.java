package de.interoberlin.bolyde.controller.http;

import de.interoberlin.bolyde.controller.Simulation;
import de.interoberlin.bolyde.model.settings.Settings;
import de.interoberlin.bolyde.view.activities.BolydeActivity;
import de.interoberlin.bolyde.view.panels.DrawingPanel;
import de.interoberlin.mate.lib.model.Log;

public class BroadcastThread
{
    private static BroadcastThread instance;

    private static Thread	  thread;

    private BroadcastThread()
    {
	thread = new Thread()
	{
	    @Override
	    public void run()
	    {
		while (!isInterrupted())
		{
		    if (Simulation.getX() != Integer.MAX_VALUE && Simulation.getY() != Integer.MAX_VALUE)
		    {
			if (Settings.getFrequency() > 0)
			{
			    try
			    {
				Thread.sleep(1000 / Settings.getFrequency());
			    } catch (InterruptedException e)
			    {
				e.printStackTrace();
			    }
			}

			if (Settings.isPost())
			{
			    Http.sendPost(Settings.getUrl(), Simulation.getX(), Simulation.getY());
			}

			if (Settings.isGet())
			{
			    Http.sendGet(Settings.getUrl(), Simulation.getX(), Simulation.getY());
			}
		    }

		    DrawingPanel.requestBlink();
		}
	    }
	};
    }

    public static BroadcastThread getInstance()
    {
	if (instance == null)
	{
	    instance = new BroadcastThread();
	}

	return instance;
    }

    public void start()
    {
	BolydeActivity.uiToast("Broadcast started");
	Log.info("Broadcast started");
	if (!thread.isAlive())
	    thread.start();
    }

    public void stop()
    {
	BolydeActivity.uiToast("Broadcast stopped");
	Log.info("Broadcast stopped");
	if (thread.isAlive())
	    thread.interrupt();
    }
}

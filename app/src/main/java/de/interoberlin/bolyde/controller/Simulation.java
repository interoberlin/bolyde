package de.interoberlin.bolyde.controller;

import android.app.Activity;

import java.util.Observable;
import java.util.Observer;

import de.interoberlin.bolyde.controller.accelerometer.AccelerationEvent;
import de.interoberlin.bolyde.controller.accelerometer.Accelerometer;
import de.interoberlin.bolyde.controller.http.BroadcastThread;
import de.interoberlin.bolyde.model.settings.Settings;
import de.interoberlin.mate.lib.model.Log;

public class Simulation implements Observer
{
    private static Simulation       instance;

    private static BolydeController controller;

    // Data values
    private static float	    dataX = Float.MAX_VALUE;
    private static float	    dataY = Float.MAX_VALUE;

    // Measured values + offset
    private static float	    rawX  = Integer.MAX_VALUE;
    private static float	    rawY  = Integer.MAX_VALUE;

    // Measured values + offset + rounded
    private static int	      x     = Integer.MAX_VALUE;
    private static int	      y     = Integer.MAX_VALUE;

    private Activity		activity;

    private Simulation(Activity activity)
    {
	this.activity = activity;
	controller = (BolydeController) activity.getApplicationContext();
    }

    public static Simulation getInstance(Activity activity)
    {
	if (instance == null)
	{
	    instance = new Simulation(activity);
	}

	return instance;
    }

    @Override
    public void update(Observable observable, Object data)
    {
	// Get acceleration data
	dataX = ((AccelerationEvent) data).getX();
	dataY = ((AccelerationEvent) data).getY();

	// Get sensibilities
	float sensibilityX = Settings.getSensitivityX();
	float sensibilityY = Settings.getSensitivityY();

	// Retrieve values from observed AccelerationEvent
	rawX = normalize(dataX - controller.getOffsetX(), sensibilityX);
	rawY = normalize(dataY - controller.getOffsetY(), sensibilityY);

	x = Math.round(rawX);
	y = Math.round(rawY);
    }

    public void start()
    {
	Log.info("Simulation started");
	Accelerometer.getInstance(activity).start();
	BroadcastThread.getInstance().start();
    }

    /**
     * Stops the simulation
     */
    public void stop()
    {
	Log.info("Simulation stopped");
	Accelerometer.getInstance(activity).stop();
	BroadcastThread.getInstance().stop();
    }

    private float normalize(float f, float sensibility)
    {
	float FACTOR = (((controller.getMAX_FACTOR() - controller.getMIN_FACTOR()) / 100) * sensibility) + controller.getMIN_FACTOR();

	if (f * FACTOR > controller.getMAX_VALUE())
	{
	    return controller.getMAX_VALUE();
	} else if (f * FACTOR < controller.getMIN_VALUE())
	{
	    return controller.getMIN_VALUE();
	} else
	{
	    return f * FACTOR;
	}
    }

    public static int getX()
    {
	return x;
    }

    public static void setX(int x)
    {
	Simulation.x = x;
    }

    public static int getY()
    {
	return y;
    }

    public static void setY(int y)
    {
	Simulation.y = y;
    }

    public static float getDataX()
    {
	return dataX;
    }

    public static float getDataY()
    {
	return dataY;
    }

    public static float getRawX()
    {
	return rawX;
    }

    public static float getRawY()
    {
	return rawY;
    }
}

package onlinefrontlines.profiler;

import java.util.TimerTask;

/**
 * Class that contains changes of a single value over time
 * 
 * @author jorrit
 * 
 * Copyright (C) 2009-2013 Jorrit Rouwe
 * 
 * This file is part of Online Frontlines.
 *
 * Online Frontlines is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Online Frontlines is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Online Frontlines.  If not, see <http://www.gnu.org/licenses/>.
 */
public final class TimeSeries extends TimerTask
{
	/**
	 * Rate at which we're sampling
	 */
	public final static long MS_PER_SAMPLE = 120 * 1000;
	
	/**
	 * How long we want to keep each sample
	 */
	public final static long TIME_TO_KEEP_SAMPLE = 24L * 60 * 60 * 1000;
	
	/**
	 * How many samples we must keep
	 */
	public final static int MAX_SAMPLES = (int)(TIME_TO_KEEP_SAMPLE / MS_PER_SAMPLE);
	
	/**
	 * Accumulation types
	 */
	public static enum Type
	{
		RATE,
		AVERAGE,
		VALUE,
		MIN,
		MAX		
	}
	
	/**
	 * Type for this series
	 */
	private Type type;
	
	/**
	 * Callback
	 */
	private TimeSeriesCallback callback;
	
	/**
	 * Start time of current sample
	 */
	private long startTime;
	
	/**
	 * Current accumulated value
	 */
	private double accumulatedValue;
	
	/**
	 * Amount of times accumulate was called
	 */
	private int accumulateCount;
	
	/**
	 * List of values over time
	 */
	private double[] values = new double[MAX_SAMPLES];
	
	/**
	 * How many values have been collected
	 */
	private int valueCount;
	
	/**
	 * Which value to overwrite next
	 */
	private int nextValue;
	
	/**
	 * Constructor
	 */
	public TimeSeries(Type type, TimeSeriesCallback callback)
	{
		this.type = type;
		this.callback = callback;
		
		resetAccumulated();
	}
	
	/**
	 * Reset time series
	 */
	public synchronized void reset()
	{
		valueCount = 0;
		nextValue = 0;

		resetAccumulated();
	}

	/**
	 * Accumulate value
	 * 
	 * @param value
	 */
	public synchronized void accumulate(double value)
	{
		if (accumulateCount == 0)
		{
			// First value is always stored
			accumulatedValue = value;
		}
		else
		{
			// Next values are accumulated
			switch (type)
			{
			case MIN:
				accumulatedValue = Math.min(accumulatedValue, value);
				break;
				
			case MAX:
				accumulatedValue = Math.max(accumulatedValue, value);
				break;
				
			default:
				accumulatedValue += value;
				break;
			}
		}
		
		++accumulateCount;
	}

	/**
	 * Reset accumulation system
	 */
	private void resetAccumulated()
	{
		accumulatedValue = 0;
		accumulateCount = 0;
		startTime = System.nanoTime();
	}
	
	/**
	 * Called by the timer 
	 */
	public synchronized void run()
	{
		if (callback != null)
			accumulate(callback.getValue());

		double next = 0;
		
		switch (type)
		{
		case RATE:
			long deltaTime = System.nanoTime() - startTime;
			next = 1.0e9 * accumulatedValue / deltaTime;
			break;
			
		case AVERAGE:
			next = accumulateCount > 0? accumulatedValue / accumulateCount : 0;
			break;

		case MIN:
		case MAX:
		case VALUE:
			next = accumulatedValue;
			break;
			
		default:
			assert(false);
			break;
		}
		
		values[nextValue] = next;
		nextValue = (nextValue + 1) % MAX_SAMPLES;
		valueCount = Math.min(valueCount + 1, MAX_SAMPLES);

		resetAccumulated();
	}	
	
	/**
	 * Get all values
	 */
	public synchronized double[] getValues()
	{
		double[] outValues = new double[valueCount];
		
		for (int out = valueCount - 1, in = nextValue; out >= 0; --out)
		{
			// Determine input value
			in--;
			if (in < 0) 
				in = MAX_SAMPLES - 1;
			
			// Write output value
			outValues[out] = values[in];
		}
			
		return outValues;
	}
}

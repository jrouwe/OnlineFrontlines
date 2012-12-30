package onlinefrontlines.profiler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Arrays;
import java.util.Timer;
import java.lang.management.ManagementFactory;

import onlinefrontlines.utils.Tools;

/**
 * The profiler class provides an interface to all profiling functionality
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
public final class Profiler 
{
	/**
	 * Predefined categories
	 */
	public static final String CATEGORY_HTTP_REQUEST = "HTTP Request";
	public static final String CATEGORY_SQL_QUERY = "SQL Query";
	public static final String CATEGORY_SQL_STORED_PROCEDURE = "SQL Stored Procedure";
	public static final String CATEGORY_GENERAL = "General";
	
	/**
	 * Predefined series
	 */
	public static final String SERIES_USED_MEMORY = "Used Memory (Mb)";
	public static final String SERIES_CPU_LOAD = "CPU Load";
	
	/**
	 * Post fixes for time series
	 */
	private static final String RATE = " (calls / s)";
	private static final String AVERAGE_TIME = " (average time in ms / call)";
	private static final String MIN_TIME = " (min time in ms / call)";
	private static final String MAX_TIME = " (max time in ms / call)";
	
	/**
	 * Singleton instance
	 */
	private static Profiler instance = new Profiler();
	
	/**
	 * Create timer to schedule updates
	 */
	private Timer timer;
	
	/**
	 * All time accumulators
	 */
	private ConcurrentHashMap<String, ConcurrentHashMap<String, TimeAccumulatorWithChildren>> allTimeAccumulators = new ConcurrentHashMap<String, ConcurrentHashMap<String, TimeAccumulatorWithChildren>>();
	
	/**
	 * All time series
	 */
	private ConcurrentHashMap<String, TimeSeries> allTimeSeries = new ConcurrentHashMap<String, TimeSeries>();
	
	/**
	 * Singleton instance
	 */
	static public Profiler getInstance()
	{
		return instance;
	}
	
	/**
	 * Time series of memory usage
	 */
	public static class MemoryTimeSeries implements TimeSeriesCallback
	{
		public double getValue()
		{
	    	Runtime r = Runtime.getRuntime();
	   		return 1e-6 * (r.totalMemory() - r.freeMemory());	   		
		}
	}
	
	/**
	 * Time series of CPU load
	 */
	public static class CPULoadTimeSeries implements TimeSeriesCallback
	{
		public double getValue()
		{
			try
			{
				return ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
			}
			catch (Exception e)
			{
				Tools.logException(e);
				return -1;
			}
		}
	}
	
	/**
	 * Init profiler
	 */
	public void init()
	{
		// Create timer
		assert(timer == null);		
		timer = new Timer(true);

		// Register default categories
		registerTimeAccumulatorCategory(CATEGORY_HTTP_REQUEST);
		registerTimeAccumulatorCategory(CATEGORY_SQL_QUERY);
		registerTimeAccumulatorCategory(CATEGORY_SQL_STORED_PROCEDURE);
		registerTimeAccumulatorCategory(CATEGORY_GENERAL);
		
		// Register default time series
		registerTimeSeries(SERIES_USED_MEMORY, new MemoryTimeSeries());
		registerTimeSeries(SERIES_CPU_LOAD, new CPULoadTimeSeries());
	}
	
	/**
	 * Exit profiler
	 */
	public void exit()
	{
		// Destroy timer
		timer.cancel();
		timer = null;
		
		// Remove all data
		allTimeAccumulators.clear();
		allTimeSeries.clear();
	}

	/**
	 * Register new time accumulator category
	 * 
	 * @param category
	 */
	public synchronized void registerTimeAccumulatorCategory(String category)
	{
		// Register category
		allTimeAccumulators.put(category, new ConcurrentHashMap<String, TimeAccumulatorWithChildren>());
		
		// Register corresponding time series
		registerTimeSeries(TimeSeries.Type.RATE, category + RATE);
		registerTimeSeries(TimeSeries.Type.AVERAGE, category + AVERAGE_TIME);
		registerTimeSeries(TimeSeries.Type.MIN, category + MIN_TIME);
		registerTimeSeries(TimeSeries.Type.MAX, category + MAX_TIME);
	}
	
	/**
	 * Register new time series
	 * 
	 * @param name
	 */
	public synchronized void registerTimeSeries(TimeSeries.Type type, String name)
	{
		TimeSeries ts = new TimeSeries(type, null);
		timer.scheduleAtFixedRate(ts, 0, TimeSeries.MS_PER_SAMPLE);
		allTimeSeries.put(name, ts);
	}

	/**
	 * Register time series with callback to get value at fixed intervals
	 * 
	 * @param name
	 * @param callback
	 */
	public synchronized void registerTimeSeries(String name, TimeSeriesCallback callback)
	{
		TimeSeries ts = new TimeSeries(TimeSeries.Type.VALUE, callback);
		timer.scheduleAtFixedRate(ts, 0, TimeSeries.MS_PER_SAMPLE);
		allTimeSeries.put(name, ts);
	}

	/**
	 * Get time accumulator
	 * 
	 * @param category
	 * @param subCategory
	 * @return
	 */
	public TimeAccumulatorWithChildren getTimeAccumulator(String category, String subCategory)
	{
		// Find sub categories
		ConcurrentHashMap<String, TimeAccumulatorWithChildren> sc = allTimeAccumulators.get(category);
		if (sc == null)
			return null;
		
		// Find stat
		TimeAccumulatorWithChildren a = sc.get(subCategory);
		if (a == null)
		{
			a = new TimeAccumulatorWithChildren();
			sc.put(subCategory, a);
		}
		
		return a;
	}
	
	/**
	 * Get time series
	 * 
	 * @param name
	 * @return
	 */
	public TimeSeries getTimeSeries(String name)
	{
		return allTimeSeries.get(name);
	}
	
	/**
	 * Reset profiler
	 */
	public synchronized void reset()
	{
		// Reset stats
		for (ConcurrentHashMap<String, TimeAccumulatorWithChildren> sc : allTimeAccumulators.values())
			for (TimeAccumulatorWithChildren a : sc.values())
				a.reset();
		
		// Reset time series
		for (TimeSeries s : allTimeSeries.values())
			s.reset();
	}

	/**
	 * Accumulate time for time series
	 * 
	 * @param category Category to accumulate for
	 * @param totalTime Time to accumulate in ms
	 */
	public void accumulateTime(String category, double totalTime)
	{
		accumulateTimeSeries(category + RATE, 1);
		accumulateTimeSeries(category + AVERAGE_TIME, totalTime);
		accumulateTimeSeries(category + MIN_TIME, totalTime);
		accumulateTimeSeries(category + MAX_TIME, totalTime);
	}
	
	/**
	 * Accumulate value on time series
	 * 
	 * @param name
	 * @param value
	 */
	public void accumulateTimeSeries(String name, double value)
	{
		TimeSeries s = getTimeSeries(name);
		if (s != null)
			s.accumulate(value);
	}
	
	/**
	 * Start sampling
	 * 
	 * @param category
	 * @param subCategory
	 * @return
	 */
	public Sampler startSampler(String category, String subCategory)
	{
		Sampler sampler = new Sampler(this, category, subCategory);
		sampler.start();
		return sampler;
	}
	
	/**
	 * Get all categories
	 */
	public synchronized String[] getTimeAccumulatorCategories()
	{
		String[] c = allTimeAccumulators.keySet().toArray(new String[0]);
		Arrays.sort(c);
		return c;
	}
	
	/**
	 * Get all sub categories for a category
	 */
	public synchronized String[] getTimeAccumulatorSubCategories(String category)
	{
		ConcurrentHashMap<String, TimeAccumulatorWithChildren> sc = allTimeAccumulators.get(category);
		if (sc == null)
			return null;
		
		return sc.keySet().toArray(new String[0]);
	}
	
	/**
	 * Get all time series names
	 */
	public synchronized String[] getTimeSeriesNames()
	{
		String[] n = allTimeSeries.keySet().toArray(new String[0]);
		Arrays.sort(n);
		return n;
	}
}

package onlinefrontlines.profiler;

/**
 * A sampler can be used to time a specific bit of code by using:
 * 
 * <pre>
 * Sampler sampler = Profiler.getInstance().getSampler("Category", "Sub Category");
 * try
 * {
 * 		...
 * }
 * finally
 * {
 * 		sampler.stop();
 * }
 * </pre>
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
public final class Sampler 
{
	/**
	 * Profiler that created us
	 */
	private Profiler profiler;
	
	/**
	 * Category that we were created with
	 */
	private String category;
	
	/**
	 * Sub category we were created with
	 */
	private String subCategory;
	
	/**
	 * Accumulator for category / sub category
	 */
	private TimeAccumulatorWithChildren accumulator;
	
	/**
	 * Start time in nanoseconds
	 */
	private long startTime;
	
	/**
	 * Current sampler for this thread
	 */
	private static ThreadLocal<Sampler> currentSampler = new ThreadLocal<Sampler>();
	
	/**
	 * Parent sampler
	 */
	private Sampler parentSampler;
	
	/**
	 * Constructor, used by Profiler
	 * 
	 * @param profiler
	 * @param category
	 * @param subCategory
	 */
	public Sampler(Profiler profiler, String category, String subCategory)
	{
		this.profiler = profiler;
		this.category = category;
		this.subCategory = subCategory;
		
		accumulator = profiler.getTimeAccumulator(category, subCategory);
	}
	
	/**
	 * Start sampling, used by Profiler
	 */
	public void start()
	{
		startTime = System.nanoTime();

		// Replace current sampler
		assert(currentSampler.get() != this);
		this.parentSampler = currentSampler.get();
		currentSampler.set(this);
	}
	
	/**
	 * Stop sampling and accumulate the stats
	 */
	public void stop()
	{
		double time = 1e-6 * (System.nanoTime() - startTime);
	
		// Accumulate child time for my parent
		if (parentSampler != null && parentSampler.accumulator != null)
			parentSampler.accumulator.accumulateChild(subCategory, time);
		
		// Accumulate time
		if (accumulator != null)
			accumulator.accumulate(time);
		
		// Accumulate time
		profiler.accumulateTime(category, time);
		
		// Restore current sampler
		assert(currentSampler.get() == this);
		currentSampler.set(parentSampler);
	}
}

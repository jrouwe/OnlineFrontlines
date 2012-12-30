package onlinefrontlines.profiler;

/**
 * Class that accumulates time, average time per call, min time and max time per call 
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
public class TimeAccumulator 
{
	/**
	 * Total count
	 */
	public long count;
	
	/**
	 * Total measured time in ms
	 */
	public double totalTime;
	
	/**
	 * Minimal measured time in ms
	 */
	public double minTime = 0;
	
	/**
	 * Maximal measured time in ms
	 */
	public double maxTime = 0;
	
	/**
	 * Reset accumulated stats
	 */
	public synchronized void reset()
	{
		count = 0;
		totalTime = 0;
		minTime = 0;
		maxTime = 0;
	}

	/**
	 * Accumulate totals
	 * 
	 * @param time Time to accumulate in ms
	 */
	public synchronized void accumulate(double time)
	{
		count++;
		this.totalTime += time;
		minTime = this.count == 1? time : Math.min(time, minTime);
		maxTime = Math.max(time, maxTime);
	}
	
	/**
	 * Get average time in ms
	 */
	public synchronized double getAverageTime()
	{
		return count > 0? totalTime / count : totalTime;
	}	
}

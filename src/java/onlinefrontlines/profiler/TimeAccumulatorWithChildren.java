package onlinefrontlines.profiler;

import java.util.HashMap;

/**
 * Class that accumulates time, average time per call, min time and max time per call and keeps track of children 
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
public final class TimeAccumulatorWithChildren extends TimeAccumulator
{
	/**
	 * Total time of children in ms
	 */
	public double totalChildTime;
	
	/**
	 * Time split per child in ms
	 */
	public HashMap<String, TimeAccumulator> children = new HashMap<String, TimeAccumulator>();
	
	/**
	 * Reset accumulated stats
	 */
	public synchronized void reset()
	{
		super.reset();
		
		totalChildTime = 0;
		children.clear();		
	}
	
	/**
	 * Accumulate child time
	 */
	public synchronized void accumulateChild(String subCategory, double time)
	{
		// Accumulate totals
		this.totalChildTime += time;
		
		// Keep list of children
		TimeAccumulator child = children.get(subCategory);
		if (child == null)
		{
			child = new TimeAccumulator();
			children.put(subCategory, child);
		}
		child.accumulate(time);			
	}
}

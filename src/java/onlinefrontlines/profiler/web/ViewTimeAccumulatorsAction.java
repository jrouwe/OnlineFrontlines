package onlinefrontlines.profiler.web;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Comparator;
import onlinefrontlines.profiler.Profiler;
import onlinefrontlines.profiler.TimeAccumulatorWithChildren;
import onlinefrontlines.profiler.TimeAccumulator;
import onlinefrontlines.web.*;

/**
 * View currently accumulated profile data
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
public class ViewTimeAccumulatorsAction extends WebAction
{
	/**
	 * Comparator for sorting stats
	 */
	private static class SortOnTime implements Comparator<String>
	{
		private final Profiler p = Profiler.getInstance(); 
		private final String category;
		
		public SortOnTime(String category)
		{
			this.category = category;
		}
		
		public int compare(String s1, String s2)
		{
			return (int)(Math.signum(p.getTimeAccumulator(category, s2).totalTime - p.getTimeAccumulator(category, s1).totalTime)); 
		}
	}

	/**
	 * Resulting html
	 */
	public String html;
	
	/**
	 * Execute action
	 * 
	 * @return The path to the JSP page to execute
	 */
	protected WebView execute() throws Exception
	{
		Profiler p = Profiler.getInstance();
		
		StringBuilder b = new StringBuilder();
		
    	DecimalFormat f = new DecimalFormat("#.#");

    	for (String c : p.getTimeAccumulatorCategories())
		{
			b.append("<h2>");
			b.append(c);
			b.append("</h2>");
			
			b.append("<table class=\"ptable\">");
			b.append("<tr><th>Name</th><th>Count</th><th>Total time (ms)</th><th>Child time (ms)</th><th>Average time (ms)</th><th>Min time (ms)</th><th>Max time (ms)</th></tr>");
			
			String[] subCategories = p.getTimeAccumulatorSubCategories(c); 			
	    	Arrays.sort(subCategories, new SortOnTime(c));			
			for (String sc : subCategories)
			{				
				TimeAccumulatorWithChildren a = p.getTimeAccumulator(c, sc);
				synchronized (a)
				{				
					// Create table row
					b.append("<tr><td>");
					b.append(sc);
					b.append("</td><td>");
					b.append(a.count);
					b.append("</td><td>");
					b.append(f.format(a.totalTime));
					b.append("</td><td>");
					
					// Create tooltip
					if (a.children.size() > 0)
					{
						b.append("<a href=\"\" onmouseover=\"Tip('");
						
						b.append("<table>");
						b.append("<tr><th>Name</th><th>Count</th><th>Total time (ms)</th><th>Average time (ms)</th><th>Min time (ms)</th><th>Max time (ms)</th></tr>");
						for (String ch : a.children.keySet())
						{
							b.append("<tr><td>");
							for (int i = 0; i < ch.length(); ++i)
							{
								char k = ch.charAt(i);
								if (k == '\'' || k == '\\')
									b.append('\\');
								b.append(k);
							}
							b.append("</td><td>");
							TimeAccumulator ct = a.children.get(ch);
							b.append(f.format(ct.count));
							b.append("</td><td>");
							b.append(f.format(ct.totalTime));
							b.append("</td><td>");
							b.append(f.format(ct.getAverageTime()));
							b.append("</td><td>");
							b.append(f.format(ct.minTime));
							b.append("</td><td>");
							b.append(f.format(ct.maxTime));
							b.append("</td></tr>");
						}
						b.append("</table>");
						
						b.append("');\" onmouseout=\"UnTip()\" onclick=\"return false;\">");
						b.append(f.format(a.totalChildTime));
						b.append("</a>");
					}
					else
					{
						b.append("-");
					}
					
					// Finish table row
					b.append("</td><td>");
					b.append(f.format(a.getAverageTime()));
					b.append("</td><td>");
					b.append(f.format(a.minTime));
					b.append("</td><td>");
					b.append(f.format(a.maxTime));
					b.append("</td></tr>");
				}
			}
			
			b.append("</table>");			
		}
		
		html = b.toString();
		
		return getSuccessView();
	}

}

package onlinefrontlines.profiler.web;

import onlinefrontlines.profiler.Profiler;
import onlinefrontlines.profiler.TimeSeries;
import onlinefrontlines.web.*;
import java.net.URLEncoder;
import java.text.DecimalFormat;

/**
 * View currently accumulated profiling time series
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
public class ViewTimeSeriesAction extends WebAction
{
	/**
	 * Resulting html
	 */
	public String html;
	
	/**
	 * Encoding table
	 */
	private static final char[] encodingTable = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-.".toCharArray();
	
	/**
	 * Encode value for google charts
	 */
	private char[] encode(double value, double minValue, double maxValue)
	{
		// Quantize value
		int quantized = (int)Math.round(4095.0 * (value - minValue) / (maxValue - minValue));
		if (quantized < 0)
			quantized = 0;
		if (quantized > 4095)
			quantized = 4095;
		
		// Encode value
		char[] encoded = new char[2];
		encoded[0] = encodingTable[quantized / 64];
		encoded[1] = encodingTable[quantized % 64];
		return encoded;		
	}
	
	/**
	 * Execute action
	 * 
	 * @return The path to the JSP page to execute
	 */
	protected WebView execute() throws Exception
	{
		Profiler a = Profiler.getInstance();
		
		StringBuilder b = new StringBuilder();
		
    	DecimalFormat f = new DecimalFormat("#.#");
		
		for (String n : a.getTimeSeriesNames())
		{
			// Get values
			double[] values = a.getTimeSeries(n).getValues();

			// Get min and max value
			double minValue = Double.MAX_VALUE;
			double maxValue = 0;
			for (Double v : values)
			{
				minValue = Math.min(v, minValue);
				maxValue = Math.max(v, maxValue);
			}
			if (minValue >= maxValue)
				maxValue = minValue + 1;

			// Start image
			b.append("<img style=\"margin: 5px 0px 0px 0px;\" src=\"");
			
			// Remember length
			int lengthAtUrlStart = b.length();

			// Add url
			b.append("http://chart.apis.google.com/chart?");
			
			// Size
			b.append("chs=600x300");
			
			// Data
			b.append("&amp;chd=e:");
			for (Double v : values)
				b.append(encode(v, minValue, maxValue));
			
			// Type
			b.append("&amp;cht=lc");
			
			// Title
			b.append("&amp;chtt=");
			b.append(URLEncoder.encode(n, "UTF-8"));
			
			// X and Y axis
			b.append("&amp;chxt=x,y");
			
			// X and Y axis range
			b.append("&amp;chxr=0,0,");
			b.append(values.length * TimeSeries.MS_PER_SAMPLE / (60 * 1000));
			b.append("|1,");
			b.append(f.format(minValue));
			b.append(",");
			b.append(f.format(maxValue));
			
			// Color
			b.append("&amp;chco=0000FF");			
			
			// URL cannot be bigger than 2048
			assert(b.length() - lengthAtUrlStart < 2048);

			// End image
			b.append("\" alt=\"chart\"/>");
		}
		
		html = b.toString();
		
		return getSuccessView();
	}
}

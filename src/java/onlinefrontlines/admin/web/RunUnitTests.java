package onlinefrontlines.admin.web;

import java.io.*;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import onlinefrontlines.web.*;
import onlinefrontlines.utils.*;
import org.junit.runner.*;
import org.junit.internal.TextListener;

/**
 * This action runs all unit tests
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
public class RunUnitTests extends WebAction 
{
	private static final Logger log = Logger.getLogger(RunUnitTests.class);
	
	/**
	 * Unit test result
	 */
	public String result;

	/**
	 * Collect all unit test classes 
	 */
	public void collectTests(File file, String className, ArrayList<Class<?>> classes) 
    {
        if (file.isDirectory()) 
        {
        	for (String child : file.list())
        		collectTests(new File(file, child), className + "." + child, classes);
        } 
        else 
        {
        	if (className.endsWith("Test.class"))
        	{
        		try
        		{
            		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        			className = className.substring(0, className.length() - 6);
	        		Class<?> c = classLoader.loadClass(className);
	        		classes.add(c);
        		}
        		catch (ClassNotFoundException e)
        		{
        			Tools.logException(e);
        		}
        	}
        }
    }

    /**
     * Execute the action
     */
    protected WebView execute() throws Exception 
    {
    	log.info("User '" + user.id + "' ran the unit tests");
    	
    	JUnitCore core = new JUnitCore();
    	
    	// Collect all tests to run
    	ArrayList<Class<?>> classes = new ArrayList<Class<?>>();    	
        collectTests(new File(servletContext.getRealPath("WEB-INF/classes/onlinefrontlines")), "onlinefrontlines", classes);
    	
        // Create output stream
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	TextListener listener = new TextListener(new PrintStream(out));
    	core.addListener(listener);
    	
    	// Run tests
    	core.run(classes.toArray(new Class[0]));
    	
    	// Store result
    	result = out.toString();
    	
        return getSuccessView();
    }
}

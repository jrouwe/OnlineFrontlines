package onlinefrontlines.countryedit.web;

import java.util.*;
import onlinefrontlines.web.*;
import onlinefrontlines.game.*;

/**
 * This action tests all countries
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
public class CountryListInvalidAction extends WebAction 
{
	public String output;
		
    /**
     * Execute the action
     */
    protected WebView execute() throws Exception 
    {
    	StringBuilder b = new StringBuilder();
    	
    	ArrayList<CountryConfigDAO.Summary> configs = CountryConfigDAO.list(0, Integer.MAX_VALUE, false, false, false, false);    	
    	for (CountryConfigDAO.Summary s : configs)
    	{
    		CountryConfig c = CountryConfigCache.getInstance().get(s.id);
    		
    		boolean addLink = false;
    		
    		try
    		{
	    		DeploymentHelper d = new DeploymentHelper(new Random());
	    		d.getDeployment(c);
	    		
	    		for (String w : d.getWarnings())
	    		{
	    			b.append("WARNING: ");
	    			b.append(w);
	    			b.append("<br/>");
	    			
	    			addLink = true;
	    		}
    		}
    		catch (DeploymentFailedException e)
    		{
    			b.append("ERROR: ");
    			b.append(e.getMessage());
        		b.append("<br/>");

        		addLink = true;
    		}
    		
    		if (addLink)
    		{
    			b.append("Edit [<a href=\"CountryEdit.do?countryConfigId=");
    			b.append(c.id);
    			b.append("\">Country</a>] [<a href=\"MapEdit.do?mapId=");
    			b.append(c.getMapId());
    			b.append("\">Map</a>] [<a href=\"DeploymentEdit.do?deploymentId=");
    			b.append(c.getDeploymentConfig(0).id);
    			b.append("\">Deployment Faction 1</a>] [<a href=\"DeploymentEdit.do?deploymentId=");
    			b.append(c.getDeploymentConfig(1).id);
    			b.append("\">Deployment Faction 2</a>]<br/><br/>");
    		}
    	}
    	
    	output = b.toString();
    	
    	return getSuccessView();
    }
}

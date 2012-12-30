package onlinefrontlines.taglib.ui;

import onlinefrontlines.Constants;
import onlinefrontlines.web.*;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.*;
import java.io.IOException;

/**
 * Displays the current error
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
public class ActionErrorTag extends BodyTagSupport 
{
	public static final long serialVersionUID = 0;

	/**
	 * Called when tag opened
	 */
	public int doStartTag() throws JspException
	{
		try 
		{
			// Get error list
			WebAction action = (WebAction)pageContext.getRequest().getAttribute(Constants.CURRENT_ACTION);
	        if (action.actionErrors != null && !action.actionErrors.isEmpty())
	        {
	        	// Render errors
	    		StringBuffer b = new StringBuffer();
	    		b.append("<div id=\"wwerr_" + getId() + "\" class=\"wwerr\">");
	    		for (String s : action.actionErrors)
	    		{
	    			b.append("<div class=\"errorMessage\">");
	    			b.append(s);
	    			b.append("</div>");
	    		}
	    		b.append("</div>");

	    		pageContext.getOut().print(b.toString());
	        }
		} 
		catch (IOException e) 
		{
			throw new JspTagException(e);
		}

		return SKIP_BODY;
	}
}

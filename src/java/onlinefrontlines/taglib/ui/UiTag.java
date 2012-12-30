package onlinefrontlines.taglib.ui;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.*;
import onlinefrontlines.Constants;
import onlinefrontlines.web.*;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Base class for all HTML UI tag elements
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
public abstract class UiTag extends BodyTagSupport 
{
	public static final long serialVersionUID = 0;

	/**
	 * Key attribute
	 */
	protected String key;
	
	public final void setKey(String key)
	{
		this.key = key;
	}
	
	/**
	 * Id attribute
	 */
	private String id;
	
	public final void setId(String id)
	{
		this.id = id;
	}
	
	public final String getId()
	{
		return id != null? id : key;
	}
	
	/**
	 * Name field
	 */
	private String name;
	
	public final void setName(String name)
	{
		this.name = name;
	}
	
	public final String getName()
	{
		return name != null? name : key;
	}
	
	/**
	 * Value attribute
	 */
	private String value;
	
	public final void setValue(String value)
	{
		this.value = value;
	}
	
	public final String getValue()
	{
		return value != null? value : "";
	}
	
	/**
	 * Get localized label
	 */
	protected final String getLabel()
	{
		return WebUtils.getText(key);
	}
	
	/**
	 * Render label
	 */
	protected final String getLabelDiv()
	{
		return "<div id=\"wwlbl_" + getId() + "\" class=\"wwlbl\">"
					+ "<label for=\"" + getId() + "\">" 
						+ getLabel() + ":"
					+ "</label>"
			   + "</div>";
	}
	
	/**
	 * Render control div
	 * 
	 * @param inner Inner HTML
	 */
	protected final String getControlDiv(String inner)
	{
		return "<div id=\"wwctrl_" + getId() + "\" class=\"wwctrl\">" + inner + "</div>";
	}
	
	/**
	 * Render field errors
	 */
	protected final String getFieldErrors()
	{
		// Get error list
		WebAction action = (WebAction)pageContext.getRequest().getAttribute(Constants.CURRENT_ACTION);
		if (action.fieldErrors != null)
		{
	        ArrayList<String> fieldErrors = action.fieldErrors.get(getName());
	        if (fieldErrors != null && !fieldErrors.isEmpty())
	        {
	        	// Render errors
	    		StringBuffer b = new StringBuffer();
	    		b.append("<div id=\"wwerr_" + getId() + "\" class=\"wwerr\">");
	    		for (String s : fieldErrors)
	    		{
	    			b.append("<div class=\"errorMessage\">");
	    			b.append(s);
	    			b.append("</div>");
	    		}
	    		b.append("</div>");
	    		return b.toString();
	        }
		}

		// No errors
    	return "";
	}

	/**
	 * Called when tag opened
	 */
	public int doStartTag() throws JspException
	{
		try 
		{
			pageContext.getOut().print(
				"<div id=\"wwgrp_" + getId() + "\" class=\"wwgrp\">" 
					+ getInnerHTML()
				+ "</div>");
		} 
		catch (IOException e) 
		{
			throw new JspTagException(e);
		}

		return SKIP_BODY;
	}
	
	/**
	 * Function to get inner HTML from the derived class
	 */
	protected abstract String getInnerHTML();
}

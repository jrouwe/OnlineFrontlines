package onlinefrontlines.taglib.ui;

import java.io.IOException;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import onlinefrontlines.utils.GlobalProperties;

/**
 * Outputs a form in HTML
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
public class FormTag extends BodyTagSupport 
{
	public static final long serialVersionUID = 0;

	/**
	 * Action to call on submit
	 */
	private String action;
	
	public void setAction(String action)
	{
		this.action = action;
	}
	
	/**
	 * Id of form
	 */
	private String id;
	
	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 * Post method
	 */
	private String method;
	
	public void setMethod(String method)
	{
		this.method = method;
	}
	
	/**
	 * Form enctype
	 */
	private String enctype;
	
	public void setEnctype(String enctype)
	{
		this.enctype = enctype;
	}
	
	/**
	 * Called when tag opened
	 */
	public int doStartTag() throws JspException
	{
		try 
		{
			pageContext.getOut().print("<form" + (id != null? " id=\"" + id + "\"" : "") + " action=\"" + GlobalProperties.getInstance().getString("app.url") + "/" + action + ".do\" method=\"" + (method != null? method : "post") + "\"" + (enctype != null? " enctype=\"" + enctype + "\"" : "") + ">");
		} 
		catch (IOException e) 
		{
			throw new JspTagException(e);
		}

		return EVAL_BODY_INCLUDE;
	}
	
	/**
	 * Called at end of body
	 */
	public int doAfterBody() throws JspTagException 
	{
		return SKIP_BODY;
	}
	
	/**
	 * Called when tag closed
	 */
	public int doEndTag() throws JspException
	{
		try 
		{
			pageContext.getOut().print("</form>");
		} 
		catch (IOException e) 
		{
			throw new JspTagException(e);
		}
		
		return EVAL_PAGE;
	}
}

package onlinefrontlines.taglib.ui;

import java.io.IOException;
import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

/**
 * Outputs a hidden input in HTML
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
public class HiddenTag extends BodyTagSupport 
{
	public static final long serialVersionUID = 0;

	/**
	 * Name field
	 */
	private String name;
	
	public final void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * Value attribute
	 */
	private String value;
	
	public final void setValue(String value)
	{
		this.value = value;
	}
	
	/**
	 * Called when tag opened
	 */
	public int doStartTag() throws JspException
	{
		try 
		{
			pageContext.getOut().print("<div><input type=\"hidden\" name=\"" + name + "\" value=\"" + value + "\"/></div>");
		} 
		catch (IOException e) 
		{
			throw new JspTagException(e);
		}

		return SKIP_BODY;
	}
}

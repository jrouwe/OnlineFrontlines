package onlinefrontlines.taglib.ui;

import java.io.IOException;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

/**
 * Outputs a box in HTML
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
public class BoxTag extends BodyTagSupport 
{
	public static final long serialVersionUID = 0;
	
	/**
	 * Title attribute
	 */
	private String title;
	
	public void setTitle(String title)
	{
		this.title = title;
	}
	
	/**
	 * Class attribute
	 */
	private String className;
	
	public void setClassName(String className)
	{
		this.className = className;
	}
	
	/**
	 * Style attribute
	 */
	private String style = "";
	
	public void setStyle(String style)
	{
		this.style = style;
	}
	
	/**
	 * Called when tag opened
	 */
	public int doStartTag() throws JspException
	{
		try 
		{
			// Write title
			if (title != null)
				pageContext.getOut().print("<div class=\"sub_title_" + className + "\">" + title + "</div>");
			
			// Write beginning of box
			pageContext.getOut().print("<div class=\"box_" + className + "\">"
										+ "<div class=\"box_" + className + "_top\"></div>"
										+ "<div class=\"box_" + className + "_body\" style=\"" + style + "\">");
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
			// Write end of box
			pageContext.getOut().print("</div>"
									+ "<div class=\"box_" + className + "_bottom\"></div>"
									+ "</div>");
		} 
		catch (IOException e) 
		{
			throw new JspTagException(e);
		}
		
		return EVAL_PAGE;
	}
}

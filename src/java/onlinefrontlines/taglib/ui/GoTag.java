package onlinefrontlines.taglib.ui;

import java.io.IOException;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

/**
 * Outputs a '-> GO' button in HTML
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
public class GoTag extends BodyTagSupport 
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
	 * Onclick attribute
	 */
	private String onclick;
	
	public void setOnclick(String onclick)
	{
		this.onclick = onclick;
	}

	/**
	 * Called when tag closed
	 */
	public int doEndTag() throws JspException
	{
		try 
		{
			getPreviousOut().print("<tr>"
				 + "<td class=\"go_action\"><a href=\"");
			getBodyContent().writeOut(getPreviousOut());
			if (onclick != null)
				getPreviousOut().print("\" onclick=\"" + onclick);
			getPreviousOut().print("\">" + title + "</a></td>"
				 + "<td class=\"go_arrow\"></td>"
				 + "<td class=\"go_button\"><a href=\"");
			getBodyContent().writeOut(getPreviousOut());
			if (onclick != null)
				getPreviousOut().print("\" onclick=\"" + onclick);
			getPreviousOut().print("\">GO</a></td>"
				 + "</tr>");
		} 
		catch (IOException e) 
		{
			throw new JspTagException(e);
		}		

		return EVAL_PAGE;
	}
}

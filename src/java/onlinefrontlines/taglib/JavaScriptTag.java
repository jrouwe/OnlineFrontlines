package onlinefrontlines.taglib;

import java.io.IOException;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

/**
 * Escape java script code in HTML
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
public class JavaScriptTag extends BodyTagSupport 
{
	public static final long serialVersionUID = 0;

	public int doAfterBody() throws JspTagException 
	{
		try 
		{
			// Enclose with javascript tags
			getPreviousOut().print("<script type=\"text/javascript\">/*<![CDATA[*/");
			getBodyContent().writeOut(getPreviousOut());
			getPreviousOut().print("/*]]>*/</script>");
		} 
		catch (IOException e) 
		{
			throw new JspTagException(e);
		}
		
		return SKIP_BODY;
	}
}

package onlinefrontlines.taglib;

import java.io.IOException;
import java.io.Reader;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

/**
 * Output HTML using javascript
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
public class JavaScriptDocumentWriteTag extends BodyTagSupport 
{
	public static final long serialVersionUID = 0;

	public int doAfterBody() throws JspTagException 
	{
		try 
		{
			// Enclose with document.write('...');
			getPreviousOut().print("document.write('");
			
			// Strip whitespace and escape characters
			Reader r = getBodyContent().getReader();
			JspWriter w = getPreviousOut();
			boolean wasWhiteSpace = true;
			for (int c = r.read(); c != -1; c = r.read())
			{
				boolean isWhiteSpace = Character.isWhitespace(c);
				if (isWhiteSpace)
				{
					if (!wasWhiteSpace)
						w.append(' ');
				}
				else
				{
					// Escape character
					if (c == '\\' || c == '\'')
						w.append('\\');
					
					w.append((char)c);
				}
				wasWhiteSpace = isWhiteSpace;				
			}
			
			getPreviousOut().print("');");
		} 
		catch (IOException e) 
		{
			throw new JspTagException(e);
		}
		
		return SKIP_BODY;
	}
}

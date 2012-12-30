package onlinefrontlines.taglib.ui;

import java.util.Map;
import onlinefrontlines.web.WebUtils;

/**
 * Outputs a select input in HTML
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
public class SelectTag extends UiTag 
{
	public static final long serialVersionUID = 0;

	/**
	 * List of objects to choose from
	 */
	private Map<Object, Object> list;
	
	public void setList(Map<Object, Object> list)
	{
		this.list = list;
	}
	
	/**
	 * Onchange javascript callback
	 */
	private String onchange;
	
	public void setOnchange(String onchange)
	{
		this.onchange = onchange;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getInnerHTML()
	{
		StringBuffer b = new StringBuffer();
		b.append("<select name=\"" + getName() + "\" id=\"" + getId() + "\"" + (onchange != null? " onchange=\"" + onchange + "\"" : "") + " class=\"input_select\">");
		String selectedKey = getValue();
		for (Map.Entry<Object, Object> e : list.entrySet())
		{
			String key = e.getKey().toString();
			String value = e.getValue().toString(); 
			b.append("<option value=\"" + key + "\"" + (key.equals(selectedKey)?  " selected=\"selected\"" : "") + ">" + WebUtils.htmlEncode(value) + "</option>");
		}
		b.append("</select>");

		return getLabelDiv() 
			+ getControlDiv(b.toString());
	}
}

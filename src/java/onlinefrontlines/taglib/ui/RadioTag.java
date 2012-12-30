package onlinefrontlines.taglib.ui;

import java.util.Map;

/**
 * Outputs a radio button in HTML
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
public class RadioTag extends UiTag 
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
	 * {@inheritDoc}
	 */
	public String getInnerHTML()
	{
		StringBuffer b = new StringBuffer();
		String selectedKey = getValue();
		for (Map.Entry<Object, Object> e : list.entrySet())
		{
			String key = e.getKey().toString();
			String value = e.getValue().toString(); 
			b.append("<input type=\"radio\" name=\"" + getName() + "\" id=\"" + getId() + key + "\" value=\"" + key + "\"" + (selectedKey.equals(key)? " checked=\"checked\"" : "") + "/><label for=\"" + getId() + key + "\">" + value + "</label><br/>");
		}

		return getLabelDiv() 
			+ getControlDiv(b.toString());
	}
}

package onlinefrontlines.taglib.ui;

import onlinefrontlines.web.WebUtils;

/**
 * Outputs a text area in HTML
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
public class TextAreaTag extends UiTag 
{
	public static final long serialVersionUID = 0;

	/**
	 * Number of columns
	 */
	private int cols;
	
	public void setCols(int cols)
	{
		this.cols = cols;
	}
	
	/**
	 * Number of rows
	 */
	private int rows;
	
	public void setRows(int rows)
	{
		this.rows = rows;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getInnerHTML()
	{
		return getFieldErrors()
			+ getLabelDiv() 
			+ getControlDiv("<textarea name=\"" + getName() + "\" cols=\"" + cols + "\" rows=\"" + rows + "\" id=\"" + getId() + "\" class=\"input_textarea\">" + WebUtils.htmlEncode(getValue()) + "</textarea>");	
	}
}

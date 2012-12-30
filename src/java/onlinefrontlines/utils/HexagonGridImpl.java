package onlinefrontlines.utils;

import java.util.*;
import java.awt.Point;

/**
 * Tile layout for a game with underlying storage for grid elements 
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
public class HexagonGridImpl<Element extends Object> extends HexagonGrid
{
	private ArrayList<Element> elements;
	
	/**
	 * Constructor
	 */
	public HexagonGridImpl(int sizeX, int sizeY, Element initialValue)
	{
		super(sizeX, sizeY);
		
		elements = new ArrayList<Element>(sizeX * sizeY);

		for (int i = sizeX * sizeY; i >= 0; --i)
			elements.add(initialValue);
	}
	
	/**
	 * Get value of element
	 */
	public Element get(int x, int y)
	{
		return elements.get(x + sizeX * y);
	}
	
	/**
	 * Get value of element
	 */
	public Element get(Point p)
	{
		return elements.get(p.x + sizeX * p.y);
	}
	
	/**
	 * Set value of element
	 */
	public void set(int x, int y, Element e)
	{
		elements.set(x + sizeX * y, e);
	}

	/**
	 * Set value of element
	 */
	public void set(Point p, Element e)
	{
		elements.set(p.x + sizeX * p.y, e);
	}
}

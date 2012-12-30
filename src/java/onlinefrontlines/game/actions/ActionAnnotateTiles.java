package onlinefrontlines.game.actions;

import java.text.DecimalFormat;
import java.util.*;
import java.awt.Point;
import onlinefrontlines.auth.User;
import onlinefrontlines.game.*;
import onlinefrontlines.utils.*;

/**
 * Action to move a unit, possibly in or out a container.
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
public class ActionAnnotateTiles extends Action
{
	@SuppressWarnings("serial")
	public static class Tile extends Point
	{
		public String value;
		
		public Tile(int x, int y, String value)
		{
			super(x, y);			
			this.value = value;
		}
		
		public Tile(Point p, String value)
		{
			super(p);
			this.value = value;
		}
		
		public Tile(int x, int y, double value)
		{
			super(x, y);			
	    	this.value = new DecimalFormat("#.#").format(value);
		}

		public Tile(Point p, double value)
		{
	    	super(p);
	    	this.value = new DecimalFormat("#.#").format(value);
		}
	}
	
	private String description;
	private ArrayList<Tile> tiles;
	
	/**
	 * Default constructor
	 */
	public ActionAnnotateTiles()
	{
	}
	
	/**
	 * Constructor to set properties
	 */
	public ActionAnnotateTiles(String description, ArrayList<Tile> tiles)
	{
		this.description = description;
		this.tiles = tiles;
	}
	
	/**
	 * Construct tiles from point list
	 */
	public static ArrayList<Tile> createTiles(Collection<Point> points, String value)
	{
		ArrayList<Tile> tiles = new ArrayList<Tile>();
		for (Point p : points)
			tiles.add(new Tile(p, value));
		return tiles;
	}
	
	/**
	 * Construct tiles from grid
	 */
	public static ArrayList<Tile> createTiles(HexagonGridImpl<Double> grid)
	{
		ArrayList<Tile> tiles = new ArrayList<Tile>();
		for (int y = 0; y < grid.sizeY; ++y)
			for (int x = 0; x < grid.sizeX; ++x)
			{
				double v = grid.get(x, y);
				if (v != 0)
					tiles.add(new Tile(x, y, v));
			}
					
		return tiles;
	}
		
	/**
	 * Apply the action
	 */
	public void doAction(boolean addToDb) throws IllegalRequestException
	{
		// Does nothing on the server
	}
	
	/**
	 * Convert action from a string
	 */
	public void fromString(String[] param, User initiatingUser) throws IllegalRequestException, IgnoreActionException
	{
		// Get description
		if (param.length < 1)
			throw new IllegalRequestException("Expected description.");		
		description = param[0];
		
		// Get tiles
		tiles = new ArrayList<Tile>();
		for (int c = 1; c < param.length; c += 3)
		{
			if (c + 3 > param.length)
				throw new IllegalRequestException("Expected 2 parameters for point");
			
			tiles.add(new Tile(Integer.parseInt(param[c]), Integer.parseInt(param[c + 1]), param[c + 2]));
		}
	}
	
	/**
	 * Convert action to a string
	 */
	public String toString(Faction remoteFaction)
	{
		String rv = "at," + description;
		
		for (Tile t : tiles)
			rv += "," + t.x + "," + t.y + "," + t.value;
		
		return rv;
	}
}

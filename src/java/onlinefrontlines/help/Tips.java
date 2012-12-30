package onlinefrontlines.help;

import java.util.ArrayList;

/**
 * This class contains all tips
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
public class Tips
{
	/**
	 * Singleton instance
	 */
	private static Tips instance = new Tips();
	
	/**
	 * Get instance
	 */
	public static Tips getInstance()
	{
		return instance;
	}

	/**
	 * Tip
	 */
	public static class Entry
	{
		/**
		 * Tip id
		 */
		public int id;
		
		/**
		 * Image (relative url to the application or null if none)
		 */
		public String image;
		
		/**
		 * Text
		 */
		public String text;
		
		/**
		 * If this tip should be shown in game
		 */
		public boolean showInGame;

		/**
		 * Image
		 */
		public String getImage()
		{
			return image;	
		}
		
		/**
		 * Text
		 */
		public String getText()
		{
			return text;
		}
	}
	
	/**
	 * All tips
	 */
	public ArrayList<Entry> tips = new ArrayList<Entry>();
}
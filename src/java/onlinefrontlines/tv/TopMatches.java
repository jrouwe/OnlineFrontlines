package onlinefrontlines.tv;

import java.util.ArrayList;
import onlinefrontlines.game.Faction;
import onlinefrontlines.userstats.UserRank;

/**
 * This class serves as a container for all top matches
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
public class TopMatches
{
	/**
	 * Summary of a top match
	 */
	public static class Match
	{
		public int gameId;
		public int mapId;
		public String countryConfigName;
		public String mapType;
		public int userId1;
		public int userId2;
		public String username1;
		public String username2;
		public int totalPoints1;
		public int totalPoints2;
		public Faction winningFaction;
		public int turnNumber;
		public boolean faction1IsRed;
		public int unitsDestroyed;
		
		/**
		 * Get game id
		 */
		public int getGameId()
		{
			return gameId;
		}
		
		/**
		 * Get map id
		 */
		public int getMapId()
		{
			return mapId;
		}
		
		/**
		 * Get country config name
		 */
		public String getCountryConfigName()
		{
			return countryConfigName;
		}
		
		/**
		 * Map type
		 */
		public String getMapType()
		{
			return mapType;
		}
		
		/**
		 * Get first user name
		 */
		public String getUsername1()
		{
			return username1;
		}
		
		/**
		 * Get second user name
		 */
		public String getUsername2()
		{
			return username2;
		}
		
		/**
		 * Points for user 1
		 */
		public int getTotalPoints1()
		{
			return totalPoints1;
		}
		
		/**
		 * Points for user 2
		 */
		public int getTotalPoints2()
		{
			return totalPoints2;
		}
		
		/**
		 * Get level for user 1
		 */
		public int getLevelUser1()
		{
			return UserRank.getLevelInternal(totalPoints1);
		}
		
		/**
		 * Get level for user 2
		 */
		public int getLevelUser2()
		{
			return UserRank.getLevelInternal(totalPoints2);
		}
		
		/**
		 * Turn number game ended
		 */
		public int getTurnNumber()
		{
			return turnNumber;
		}
		
		/**
		 * Check if faction 1 was red (returns true) or blue (returns false)
		 */
		public boolean getFaction1IsRed()
		{
			return faction1IsRed;
		}
		
		/**
		 * Number of units that were destroyed at end of game
		 */
		public int getUnitsDestroyed()
		{
			return unitsDestroyed;
		}
	}
	
	/**
	 * List of top matches
	 */
	public ArrayList<Match> matches = new ArrayList<Match>();
}

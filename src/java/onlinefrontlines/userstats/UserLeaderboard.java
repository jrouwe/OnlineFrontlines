package onlinefrontlines.userstats;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import onlinefrontlines.Army;

/**
 * This class contains a leaderboard sorted on one stat
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
public class UserLeaderboard
{
	/**
	 * Leaderboard entry
	 */
	public static class Entry
	{
		/**
		 * Position in leaderboard
		 */
		public int position;
		
		/**
		 * User id
		 */
		public int userId;
		
		/**
		 * User name
		 */
		public String username;
		
		/**
		 * Army faction
		 */
		public Army army;
		
		/**
		 * Value of stat (varies per leaderboard type) 
		 */
		public long statValue;
		
		/**
		 * Position in leaderboard
		 */
		public int getPosition()
		{
			return position;
		}
		
		/**
		 * User id
		 */
		public int getUserId()
		{
			return userId;
		}
		
		/**
		 * User name
		 */
		public String getUsername()
		{
			return username;
		}
		
		/**
		 * Army faction
		 */
		public Army getArmy()
		{
			return army;
		}
		
		/**
		 * Value of stat (varies per leaderboard type) 
		 */
		public long getStatValue()
		{
			return statValue;
		}

		/**
		 * Get user level
		 */
		public int getLevel()
		{
			return UserRank.getLevel(userId);
		}

		/**
		 * Get user rank
		 */
		public int getRank() 
		{
			return UserRank.getRank(userId);
		}
	}
	
	/**
	 * Top X entries
	 */
	private ArrayList<Entry> entries = new ArrayList<Entry>();	
	
	/**
	 * Map that maps user id to entry index
	 */
	private HashMap<Integer, Entry> userIdToEntry = new HashMap<Integer, Entry>();
	
	/**
	 * Add an entry
	 */
	public void addEntry(Entry e)
	{
		entries.add(e);		
		userIdToEntry.put(e.userId, e);
	}
	
	/**
	 * Get all entries
	 */
	public Collection<Entry> getEntries()
	{
		return entries;
	}

	/**
	 * Get entry by user id
	 * 
	 * @param userId Id of user
	 * @return Entry
	 */
	public Entry getEntry(int userId)
	{
		return userIdToEntry.get(userId);
	}
}

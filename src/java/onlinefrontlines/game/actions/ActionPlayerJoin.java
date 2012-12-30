package onlinefrontlines.game.actions;

import onlinefrontlines.utils.*;
import onlinefrontlines.auth.*;
import onlinefrontlines.game.*;
import onlinefrontlines.utils.IllegalRequestException;

/**
 * Action sent by the server to indicate that a new player joined.
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
public class ActionPlayerJoin extends Action
{
	private Faction faction;
	private int id;
	
	/**
	 * Constructor
	 */
	public ActionPlayerJoin()
	{
	}
	
	/**
	 * Constructor
	 */
	public ActionPlayerJoin(Faction faction, int id)
	{
		this.faction = faction;
		this.id = id;
	}
	
	/**
	 * Accessor to get the faction
	 */
	public Faction getFaction()
	{
		return faction;
	}

	/**
	 * Query if remote client is allowed to send this action
	 */
	public boolean remoteHasPermissionToSend()
	{
		return false;
	}
	
	/**
	 * Apply the action
	 */
	public void doAction(boolean addToDb) throws IllegalRequestException
	{
	}
	
	/**
	 * Convert action from a string
	 */
	public void fromString(String[] param, User initiatingUser) throws IllegalRequestException, IgnoreActionException
	{
		faction = Faction.fromInt(Integer.parseInt(param[1]));
		id = Integer.parseInt(param[2]);
	}
	
	/**
	 * Convert action to a string
	 */
	public String toString(Faction remoteFaction)
	{
		if (remoteFaction != null)
		{
			try
			{
				User user = UserCache.getInstance().get(id);
				
				return "j," 
					+ Faction.toInt(faction) + "," 
					+ id + "," 
					+ Tools.flashEscape(user.username);
			}
			catch (CacheException e)
			{
				Tools.logException(e);

				return "j," 
					+ Faction.toInt(faction) + "," 
					+ id + "," 
					+ "error";			
			}
		}
		
		return "j," + Faction.toInt(faction) + "," + id;
	}
}

package onlinefrontlines.lobby;

import java.util.Calendar;
import onlinefrontlines.Army;
import onlinefrontlines.game.CountryConfig;

/**
 * One country on a continent
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
public class Country 
{
	/**
	 * Location of the country
	 */
	public int locationX, locationY;
	
	/**
	 * Country config for the country
	 */
	public CountryConfig countryConfig;
	
	/**
	 * Army that has conquered the country
	 */
	public Army army;
	
	/**
	 * Person who last conquered this country
	 */
	public int ownerUserId = 0;
	
	/**
	 * While this time (in millis) is not reached the owner can still exclusively defend the country 
	 */
	public long ownerExclusiveTime = 0;
	
	/**
	 * Person who is currently defending the country
	 */
	public LobbyUser defender;
	
	/**
	 * Person who is currently attacking the country
	 */
	public LobbyUser attacker;
	
	/**
	 * Created game Id if game is playing
	 */
	public int currentGameId = 0;
	
	/**
	 * User id of user currently playing in this country
	 */
	public int currentGameUserId = 0;
	
	/**
	 * Counter that indicates when this object was last changed
	 */
	public int changeCount = 0;
	
	/**
	 * Constructor
	 */
	public Country(int locationX, int locationY, CountryConfig countryConfig, Army army)
	{
		this.locationX = locationX;
		this.locationY = locationY;
		this.countryConfig = countryConfig;
		this.army = army;
	}
	
	/**
	 * Get amount of milliseconds left before other user can start defending this country besides owner
	 */
	public long getOwnerExclusiveTimeLeft()
	{
		return Math.max(0, ownerExclusiveTime - Calendar.getInstance().getTime().getTime());
	}

	/**
	 * Get state string for communication with Flash application
	 */
	@Override
	public String toString()
	{
		return locationX + "," + locationY + "," + Army.toInt(army) + "," + ownerUserId + "," + getOwnerExclusiveTimeLeft() + "," + currentGameId + "," + currentGameUserId;
	}
}

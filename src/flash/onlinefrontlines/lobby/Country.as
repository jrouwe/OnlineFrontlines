package onlinefrontlines.lobby
{
	import flash.utils.*;

	/*
	 * State of a country
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
		public var locationX : int, locationY : int;
		
		/**
		 * Associated country config
		 */
		public var countryConfig : CountryConfig;
		
		/**
		 * Army that has conquered the country
		 */
		public var army : int;
		
		/**
		 * Person who last conquered this country
		 */
		public var ownerUserId : int = 0;
		
		/**
		 * While this time (in millis) is not reached the owner can still exclusively defend the country 
		 */
		public var ownerExclusiveTime : int = 0;
		
		/**
		 * Person who is currently defending the country
		 */
		public var defender : LobbyUser;
		
		/**
		 * Person who is currently attacking the country
		 */
		public var attacker : LobbyUser;
		
		/**
		 * Created game Id
		 */
		public var currentGameId : int = 0;
		
		/**
		 * Current user in game
		 */
		public var currentGameUserId : int = 0;
 
 		/**
		 * Constructor
		 */
		public function Country(locationX : int, locationY : int, countryConfig : CountryConfig)
		{
			this.locationX = locationX;
			this.locationY = locationY;
			this.countryConfig = countryConfig;
		}
		
		/**
		 * Get amount of milliseconds left before other user can start defending this country besides owner
		 */
		public function getOwnerExclusiveTimeLeft() : int
		{
			return Math.max(0, ownerExclusiveTime - getTimer());
		}
	}
}
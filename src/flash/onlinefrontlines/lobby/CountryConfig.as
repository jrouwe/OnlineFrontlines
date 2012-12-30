package onlinefrontlines.lobby
{
	/*
	 * Settings for a country
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
	public class CountryConfig 
	{
		/**
		 * Id
		 */
		public var id : int;
		
		/**
		 * Map id
		 */
		public var mapId : int;
		
		/**
		 * Name
		 */
		public var name : String;
		
		/**
		 * If this is a capture point
		 */
		public var isCapturePoint : Boolean;
		
		/**
		 * Type of the country
		 */
		public var countryType : String;
		
		/**
		 * Description for country type
		 */
		public var countryTypeDescription : String;
		
		/**
		 * Number of units in the map (in the format X vs Y)
		 */
		public var numUnits : String;
	}
}
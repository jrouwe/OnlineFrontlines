package onlinefrontlines.game
{
	import onlinefrontlines.utils.*;
	
	/*
	 * Properties of a hexagon terrain tile
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
	public class TerrainConfig
	{
		/**
		 * Id of the terrain type
		 */
		public var id : int;

		/**
		 * Name of the terrain type
		 */
		public var name : String;

		/**
		 * Number of victory points associated with this terrain
		 */
		public var victoryPoints : int;
		
		/**
		 * Image numbers of tiles corresponding to terrain type
		 */
		public var tileProperties : Array = new Array(); // of TerrainTileProperties

	    /**
	     * Strength modifier for the terrain (percent change relative to max damage)
	     */
		public var strengthModifier : int;
		
	    /**
	     * Static access to predefined terrain configs
	     */
		public static var airTerrain : TerrainConfig;
		public static var plainsTerrain : TerrainConfig;
		public static var waterTerrain : TerrainConfig;

		/**
		 * Static access to all terrain info
		 */
		public static var allTerrain : Array = new Array();
		
		/**
		 * Maps tileImageNumber to TerrainConfig
		 */
		public static var allTerrainMap : Object = new Object(); // Map<int, TerrainConfig>
		 
		/**
		 * Maps tileImageNumber to TerrainTileProperties
		 */
		public static var allTileMap : Object = new Object(); // Map<int, TerrainTileProperties> 
		
		/**
		 * Load all TerrainConfig objects from xml
		 */
		public static function loadAll(xml : XML) : void
		{
			for each (var terrainNode : XML in xml.ter)
			{
				// Load properties
				var t : TerrainConfig = new TerrainConfig();
				t.id = int(terrainNode.id);
				t.name = terrainNode.nam;
				t.victoryPoints = terrainNode.vcp;
				t.strengthModifier = terrainNode.stm;
				
				// Fill in airTerrain
				if (t.name.toLowerCase() == "air")
					airTerrain = t;

				// Fill in plainsTerrain
				if (t.name.toLowerCase() == "plains")
					plainsTerrain = t;

				// Fill in waterTerrain
				if (t.name.toLowerCase() == "water")
					waterTerrain = t;

				// Load tile properties
				for each (var imageNode : XML in terrainNode.img)
				{
					var elements : Array = imageNode.toString().split(",");
					var ttp : TerrainTileProperties = new TerrainTileProperties();
					ttp.tileImageNumber = int(elements[0]);
					ttp.edgeTerrainImageNumber = int(elements[1]);						
					ttp.openTerrainImageNumber = int(elements[2]);						
					t.tileProperties.push(ttp);
					allTerrainMap[ttp.tileImageNumber] = t;
					allTileMap[ttp.tileImageNumber] = ttp;
				}
				
				allTerrain.push(t);
			}
		}
	}
}
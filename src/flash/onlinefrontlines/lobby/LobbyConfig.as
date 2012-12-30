package onlinefrontlines.lobby
{
	import mx.controls.Image;
	import onlinefrontlines.utils.*;
	
	/*
	 * Configuration of all hexagons (countries) in a lobby
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
	public class LobbyConfig extends HexagonGrid
	{
		// Lobbies
		private var lobbies : Array = 
		[	
			"lobby_images/lobby_00.png",
			"lobby_images/lobby_01.png",
			"lobby_images/lobby_02.png",
			"lobby_images/lobby_03.png",
			"lobby_images/lobby_04.png",
			"lobby_images/lobby_05.png",
			"lobby_images/lobby_06.png",			
		];

		// Root objects
		private var background : Image;
		private var ownershipRoot : Image;
		private var stateRoot : Image;
		private var selectionRoot : Image;
		
		// Constructor
		public function LobbyConfig() : void
		{
			// Position of the tiles
			tilePosX = -9;
			tilePosY = -7;
			tileOffsetXOdd = 0;
			tileOffsetXEven = 9;
			tileDX = 18;
			tileDY = 14;
			
			// Size of the lobby
			sizeX = 37;
			sizeY = 43;
		
			// Set size
			width = 650;
			height = 595;
			
			// Create background
			background = new Image();
			background.opaqueBackground = true;
			addChild(background);
			
			// Create sub layers and make sure they're big enough to be clipped
			ownershipRoot = new Image();
			ownershipRoot.width = width + 1;
			ownershipRoot.cacheAsBitmap = true;
			addChild(ownershipRoot);

			stateRoot = new Image();
			stateRoot.width = width + 1;
			stateRoot.cacheAsBitmap = true;
			addChild(stateRoot);
			
			selectionRoot = new Image();
			selectionRoot.width = width + 1;
			addChild(selectionRoot);
		}
		
		// Create new tile
		protected override function createTile(locationX : int, locationY : int, imageX : int, imageY : int) : HexagonTile
		{
			return new LobbyTile(locationX, locationY, imageX, imageY, ownershipRoot, stateRoot, selectionRoot);
		}

		// Get tile at location X, Y
		public function getTile(x : int, y : int) : LobbyTile
		{
			return tiles[x][y];
		}
		
		// Get country config id
		public function getCountryConfigId(x : int, y : int) : int
		{
			return getTile(x, y).getCountryConfigId();
		}
		
		// Load data from XML node
		public function loadFromNode(node : XMLList) : void
		{		
			// Convert country config ids
			var elements : Array = node.gcid.split(",");
			var currentElement : int = 0;
			for (var y : int = 0; y < sizeY; ++y)
				for (var x : int = 0; x < sizeX; ++x)
				{
					getTile(x, y).setCountryConfigId(int(elements[currentElement]));
					currentElement++;
				}

			// Convert background image
			setBackgroundImageNumber(int(node.bimg));
		}
		
		// Convert country config ids to a string to store in the database
		public function countryConfigIdsToString() : String
		{
			var str : String = "";
			for (var y : int = 0; y < sizeY; ++y)
			{
				for (var x : int = 0; x < sizeX; ++x)
				{
					if (str != "")
						str += ",";
	
					str += getTile(x, y).getCountryConfigId();
				}
			}
			return str;
		}
		
		// Set background
		public function setBackgroundImageNumber(image : int) : void
		{
			background.source = Tools.getAssetsURL(lobbies[image]);
			background.load();
		}
		
		// Hide or show all tiles at once
		public function setAllTilesVisible(v : Boolean) : void
		{
			for (var y : int = 0; y < sizeY; ++y)
				for (var x : int = 0; x < sizeX; ++x)
					getTile(x, y).setVisible(v);
		}
	}
}
package onlinefrontlines.utils
{
	import mx.containers.Canvas;
	import mx.controls.Image;
	import flash.events.MouseEvent;
	import onlinefrontlines.utils.Tools;
	
	/*
	 * Hexagon grid base class (used by game and lobby)
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
	public class HexagonGrid extends Canvas
	{
		// Position of the tiles
		protected var tilePosX : int;
		protected var tilePosY : int;
		protected var tileOffsetXOdd : int;
		protected var tileOffsetXEven : int;
		protected var tileDX : int;
		protected var tileDY : int;
		
		// Size of the tile array
		public var sizeX : int;
		public var sizeY : int;
		
		// Tiles
		protected var tiles : Array;
		
		// Location of neighbouring tiles
		private static var neighbourTable : Array = [
									 [ // Odd rows
									  { x : 0, y : -1 },
									  { x : 1, y : -1 },
									  { x : 1, y : 0 },
									  { x : 1, y : 1 },
									  { x : 0, y : 1 },
									  { x : -1, y : 0 }
									 ],
									 [ // Even rows
									  { x : -1, y : -1 },
									  { x : 0, y : -1 },
									  { x : 1, y : 0 },
									  { x : 0, y : 1 },
									  { x : -1, y : 1 },
									  { x : -1, y : 0 }
									 ]
									];
		
		// Constructor
		public function HexagonGrid() : void
		{
			// Make sure no images leave this canvas
			clipContent = true;
			horizontalScrollPolicy = "off";
			verticalScrollPolicy = "off";
			
			// No special effect when disabled
			setStyle("disabledOverlayAlpha", 0);
		}

		// Function to create the tiles
		public function createTiles(tilePressed : Function, tileReleased : Function, tileRollOver : Function, tileRollOut : Function, enableOnlyPlayableArea : Boolean) : void
		{	
			tiles = Tools.createArray(sizeX, sizeY, null);

			for (var y : int = 0; y < sizeY; ++y)
				for (var x : int = 0; x < sizeX; ++x)
				{
					// Get location
					var loc : Object = getTileLocation(x, y);
					
					// Create tile
					var t : HexagonTile = createTile(x, y, loc.x, loc.y);
					
					// Add callbacks
					if (!enableOnlyPlayableArea || isTileInPlayableArea(x, y))
					{
						t.tilePressed = tilePressed;
						t.tileReleased = tileReleased;
						t.tileRollOver = tileRollOver;
						t.tileRollOut = tileRollOut;
					}
											
					tiles[x][y] = t;	
				}
		}
		
		// Create new tile
		protected function createTile(locationX : int, locationY : int, imageX : int, imageY : int) : HexagonTile
		{
			return new HexagonTile(locationX, locationY, imageX, imageY);
		}

		// Calculate position of tile at X, Y
		public function getTileLocation(x : int, y : int) : Object
		{
			return { x : tilePosX + x * tileDX + ((y & 1) == 0? tileOffsetXEven : tileOffsetXOdd), y : tilePosY + y * tileDY };
		}
		
		// Get DX per tile
		public function getTileDX() : int
		{
			return tileDX;
		}
		
		// Get DY per tile
		public function getTileDY() : int
		{
			return tileDY;
		}
		
		// Check if tile is in range
		public function isTileInRange(x : int, y : int) : Boolean
		{
			return x >= 0 && x < sizeX && y >= 0 && y < sizeY;
		}
		
		// Check if tile is in playable area
		public function isTileInPlayableArea(x : int, y : int) : Boolean
		{
			if (y <= 0 || y >= sizeY - 1) 
				return false;
				
			if ((y & 1) == 0)
				return x >= 0 && x < sizeX - 1;
			else
				return x >= 1 && x < sizeX;
		}
		
		// Get x and y location of neighbouring tiles
		public function getNeighbours(x : int, y : int) : Array
		{
			var r : Array = new Array();
			
			var n : Array = neighbourTable[y & 1];
			
			for (var i : int = 0; i < n.length; ++i)
			{
				var xr : int = x + n[i].x;
				var yr : int = y + n[i].y;
				
				if (isTileInRange(xr, yr))
					r.push({ x : xr, y : yr });
			}		
			
			return r;
		}

		// Get shortest distance from one tile to the next
		public static function getDistance(x1 : int, y1 : int, x2 : int, y2 : int) : int
		{
			// Trivial calculation if on same row
			if (y1 == y2)
				return Math.abs(x2 - x1);
				
			// Otherwise start looking for neighbours
			var bestX : int = x1;
			var bestY : int = y1;
				
			var n : Array = neighbourTable[y1 & 1];
	
			for (var i : int = 0; i < n.length; ++i)
			{
				// Try out this neighbour
				var xr : int = x1 + n[i].x;
				var yr : int = y1 + n[i].y;
				
				// Check if it is the closest result so far
				if (Math.abs(x2 - xr) <= Math.abs(x2 - bestX) // Find the best match in X direction
					&& Math.abs(y2 - yr) < Math.abs(y2 - y1)) // Must be in the row closer to our destination
				{
					// Store it
					bestX = xr;
					bestY = yr;
				}
			}
				
			// Recurse to best candidate
			return getDistance(bestX, bestY, x2, y2) + 1;
		}		
	}
}
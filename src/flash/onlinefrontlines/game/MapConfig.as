package onlinefrontlines.game
{
	import mx.controls.Image;
	import flash.events.Event;
	import flash.events.TimerEvent;
	import flash.utils.Timer;
	import flash.display.BitmapData;
	import flash.geom.Matrix;
	import flash.geom.Rectangle;
	import flash.geom.Point;
	import flash.filters.BlurFilter;
	import onlinefrontlines.utils.*;
	
	/*
	 * Hexagon game grid
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
	public class MapConfig extends HexagonGrid
	{
		// Root objects
		public var terrainRoot : Image;
		private var overlayRoot : Image;
		public var ownerIconRoot : Image;
		public var unitRoot : Image;
		public var unitTextRoot : Image;
		private var selectionRoot : Image;
		public var effectsRoot : Image;
		
		// Current animation frame
		private var frame : int = 0;
		private static var numFrames : int = 3;
		private var playDirection : int = 1;
		
		// Constructor
		public function MapConfig() : void
		{
			// Position of the tiles
			tilePosX = -16;
			tilePosY = -15;
			tileOffsetXOdd = 0;
			tileOffsetXEven = 17;
			tileDX = 34;
			tileDY = 19;
			
			// Size of the map
			sizeX = 26;
			sizeY = 32;
		
			// Set size
			width = 870;
			height = 595;

			// Create sub layers and make sure they're big enough to be clipped
			terrainRoot = new Image();			
			terrainRoot.width = width + 1;
			terrainRoot.opaqueBackground = true;
			terrainRoot.cacheAsBitmap = true;
			addChild(terrainRoot);
			
			overlayRoot = new Image();
			overlayRoot.width = width + 1;
			overlayRoot.alpha = 0.4;			
			overlayRoot.cacheAsBitmap = true;
			addChild(overlayRoot);
			
			ownerIconRoot = new Image();
			ownerIconRoot.width = width + 1;
			ownerIconRoot.visible = false;
			ownerIconRoot.cacheAsBitmap = true;
			addChild(ownerIconRoot);

			unitRoot = new Image();
			unitRoot.width = width + 1;
			addChild(unitRoot);
			
			unitTextRoot = new Image();
			unitTextRoot.width = width + 1;
			addChild(unitTextRoot);
			
			selectionRoot = new Image();
			selectionRoot.width = width + 1;
			addChild(selectionRoot);
			
			effectsRoot = new Image();
			effectsRoot.width = width + 1;
			effectsRoot.mouseEnabled = false;
			addChild(effectsRoot);
			
			// Optimisation: Animation is handled here as otherwise every tile would register its own animation timer
			var timer : Timer = new Timer(1000, 0);
			timer.addEventListener(TimerEvent.TIMER, onTimerTick);
			timer.start();
		}
		
		// Create new tile
		protected override function createTile(locationX : int, locationY : int, imageX : int, imageY : int) : HexagonTile
		{
			return new MapTile(locationX, locationY, imageX, imageY, terrainRoot, overlayRoot, ownerIconRoot, selectionRoot);
		}

		// Get tile at location X, Y
		public function getTile(x : int, y : int) : MapTile
		{
			return tiles[x][y];
		}

		// Load data from XML node
		public function loadFromNode(node : XMLList) : void
		{		
			// Convert tile image numbers
			var elements : Array = node.tile.split(",");
			var currentElement : int = 0;
			for (var y : int = 0; y < sizeY; ++y)
				for (var x : int = 0; x < sizeX; ++x)
				{
					getTile(x, y).setTerrainImage(int(elements[currentElement]));
					currentElement++;
				}
	
			// Convert owners
			elements = node.own.split(",");
			currentElement = 0;
			for (var y2 : int = 0; y2 < sizeY; ++y2)
				for (var x2 : int = 0; x2 < sizeX; ++x2)
				{
					getTile(x2, y2).setOwnerFaction(int(elements[currentElement]));
					currentElement++;
				}
		}
		
		// Convert tile images to a string to store in the database
		public function tileImageNumbersToString() : String
		{
			// Create string containing map data
			var str : String = "";
			for (var y : int = 0; y < sizeY; ++y)
			{
				for (var x : int = 0; x < sizeX; ++x)
				{
					if (str != "")
						str += ",";
	
					str += getTile(x, y).getTerrainImage();
				}
			}
			return str;
		}
		
		// Convert tile images to a string to store in the database
		public function tileOwnersToString() : String
		{
			// Create string containing owner data
			var str : String = "";
			for (var y : int = 0; y < sizeY; ++y)
			{
				for (var x : int = 0; x < sizeX; ++x)
				{
					if (str != "")
						str += ",";
	
					str += getTile(x, y).getOwnerFaction();
				}
			}
			return str;
		}
		
		// Timer handler
		public function onTimerTick(e : Event) : void
		{
			// Determine frame
			frame += playDirection;
			if (frame < 0 || frame >= numFrames)
			{
				if (frame < 0)
				{
					frame = Math.min(1, numFrames - 1);
					playDirection = 1;
				}
				else
				{
					frame = Math.max(numFrames - 2, 0);
					playDirection = -1;
				}
			} 

			// Update all animated bitmaps
			if (tiles != null)
				for (var y : int = 0; y < sizeY; ++y)
					for (var x : int = 0; x < sizeX; ++x)
					{
						var t : MapTile = getTile(x, y);
						if (t.isTerrainImageAnimated())
							t.setTerrainImageFrame(frame);
					}
		}
		
		// Get bitmap of terrain tiles
		public function getTerrainBitmap() : BitmapData
		{
			const scale : Number = 0.2;
			
			var m : Matrix = new Matrix();

			// Create image with the tiles
			var b : BitmapData = new BitmapData(width, height);			
			for (var y : int = 0; y < sizeY; ++y)
				for (var x : int = 0; x < sizeX; ++x)
				{
					var tl : Object = getTileLocation(x, y);
					m.identity();
					m.translate(tl.x, tl.y);
					b.draw(TerrainImage.getAnimatedBitmapData(getTile(x, y).getTerrainImage()).getFrame(0), m);					
				}
				
			// Blur the image as flash uses point sampling to downscale
			var fb : BitmapData = new BitmapData(width, height);
			fb.applyFilter(b, new Rectangle(0, 0, width, height), new Point(0, 0), new BlurFilter(1 / scale, 1 / scale));
			
			// Downscale image
			var sb : BitmapData = new BitmapData(scale * width, scale * height);
			m.identity();
			m.scale(scale, scale);
			sb.draw(fb, m);			

			return sb;
		}		
	}
}
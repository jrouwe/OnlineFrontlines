package onlinefrontlines.game
{
	import mx.controls.Image;
	import onlinefrontlines.utils.*;
	import onlinefrontlines.game.*;

	/*
	 * Arrow that indicates path a unit is going to take
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
	public class MovementArrow
	{
		// Movement arrows bitmap
		[Embed(source='../../assets/unit_images/movement_arrows.png')]
		private static var movementArrowClass : Class;		
		private static var movementArrow : AnimatedBitmapData = new AnimatedBitmapData(movementArrowClass, 8, 2);
		
		// Mapping that maps direction 0-6 to frame number in arrows bitmap
		private static var directionToFrameMapping : Array = 
		[
			// from: top right
			[ 
				-1,		// to: top right
				-1,		// to: right
				2,		// to: bottom right
				1,		// to: bottom left
				14,		// to: left
				-1,		// to: top left
				7		// to: none
			],
			// from: right
			[ 
				-1,		// to: top right
				-1,		// to: right
				-1,		// to: bottom right
				11,		// to: bottom left
				9,		// to: left
				13,		// to: top left
				8		// to: none
			],
			// from: bottom right
			[
				2,		// to: top right
				-1,		// to: right
				-1,		// to: bottom right
				-1,		// to: bottom left
				12,		// to: left
				0,		// to: top left
				4		// to: none
			],
			// from: bottom left
			[
				1,		// to: top right
				11,		// to: right
				-1,		// to: bottom right
				-1,		// to: bottom left
				-1,		// to: left
				3,		// to: top left
				5		// to: none
			],
			// from: left
			[
				14,		// to: top right
				9,		// to: right
				12,		// to: bottom right
				-1,		// to: bottom left
				-1,		// to: left
				-1,		// to: top left
				10		// to: none
			],
			// from: top left
			[
				-1,		// to: top right
				13,		// to: right
				0,		// to: bottom right
				3,		// to: bottom left
				-1,		// to: left
				-1,		// to: top left
				6		// to: none
			],
			// from: none
			[
				-1,		// to: top right
				-1,		// to: right
				-1,		// to: bottom right
				-1,		// to: bottom left
				-1,		// to: left
				-1,		// to: top left
				-1		// to: none
			]
		];

		private var path : Array;				
		private var mapConfig : MapConfig;
		private var bitmapList : Array = new Array();
		private var previousSelectionState : Array = new Array();
				
		// Constructor
		public function MovementArrow(path : Array, mapConfig : MapConfig)
		{
			this.path = path;
			this.mapConfig = mapConfig;

			var locations : Array = new Array();	
					
			for each (var p : Object in path)
			{
				// Convert path into tile locations			
				locations.push(mapConfig.getTileLocation(p.x, p.y));
				
				// Replace tileSelectionMove with tileSelecionNone
				var t : MapTile = mapConfig.getTile(p.x, p.y);
				if (t.getSelectionImage() == MapTile.tileSelectionMove)
				{
					t.setSelectionImage(MapTile.tileSelectionNone);
					previousSelectionState.push(MapTile.tileSelectionMove);
				}
				else
				{
					previousSelectionState.push(-1);
				}
			}
				
			for (var i : int = 0; i < locations.length; ++i)
			{
				// Determine incoming direction
				var previous : int = 6;
				if (i > 0)
					previous = getDirection(locations[i], locations[i - 1]);
					
				// Determine outgoing direction
				var next : int = 6;
				if (i + 1 < locations.length)
					next = getDirection(locations[i], locations[i + 1]);
				
				// Determine frame to select
				var f : int = directionToFrameMapping[previous][next];
					
				if (f >= 0)
				{					
					// Create bitmap
					var b : AnimatedBitmap = new AnimatedBitmap(movementArrow);
					b.setFrame(f);
					b.x = locations[i].x;
					b.y = locations[i].y;
					mapConfig.effectsRoot.addChild(b);
					bitmapList.push(b);
				}
			}
		}
		
		// Remove the arrow again
		public function destroy() : void
		{
			// Remove bitmaps
			while (bitmapList.length > 0)
				mapConfig.effectsRoot.removeChild(bitmapList.pop());

			// Restore tile selection state
			for (var i : int = 0; i < path.length; ++i)
				if (previousSelectionState[i] == MapTile.tileSelectionMove)
				{
					var t : MapTile = mapConfig.getTile(path[i].x, path[i].y);
					t.setSelectionImage(previousSelectionState[i]);
				}
		}
		
		// Determine direction between two tiles, 0 = top right, 5 = top left (clockwise), 6 = none
		private function getDirection(current : Object, next : Object) : int
		{
			if (next.y < current.y)
			{
				if (next.x < current.x)
					return 5;
				if (next.x > current.x)
					return 0;
			}
			if (next.y == current.y)
			{
				if (next.x < current.x)
					return 4;
				if (next.x > current.x)
					return 1;
			}
			if (next.y > current.y)
			{
				if (next.x < current.x)
					return 3;
				if (next.x > current.x)
					return 2;
			}
			
			return -1;
		}
	}
}
package onlinefrontlines.utils
{
	import flash.events.MouseEvent;
	import flash.display.Sprite;
	import onlinefrontlines.utils.*;
	
	/*
	 * A tile on the hexagon grid
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
	public class HexagonTile
	{
		// Image location
		protected var imageX : int;
		protected var imageY : int;
		
		// Tile location
		public var locationX : int;
		public var locationY : int;
		
		// Callback functions
		public var tilePressed : Function;
		public var tileReleased : Function;
		public var tileRollOver : Function;
		public var tileRollOut : Function;
		
		// Constructor
		public function HexagonTile(locationX : int, locationY : int, imageX : int, imageY : int) : void
		{
			// Store tile location
			this.locationX = locationX;
			this.locationY = locationY;
		
			// Store image location
			this.imageX = imageX;
			this.imageY = imageY;
		}
		
		// Add listeners to tile
		protected function addListeners(sprite : Sprite) : void
		{			
			// Alias this
			var parentObject : HexagonTile = this;
			
			// Add listeners
			sprite.addEventListener(MouseEvent.MOUSE_OVER, 
				function(e : MouseEvent) : void
				{
					// Highlight tile
					if (tileRollOver != null || tileRollOut != null || tilePressed != null || tileReleased != null)
						setHighlighted(true);
					
					// Do callback
					if (tileRollOver != null)
						tileRollOver(parentObject);
				});

			sprite.addEventListener(MouseEvent.MOUSE_OUT,
				function(e : MouseEvent) : void
				{
					// Do callback
					if (tileRollOut != null)
						tileRollOut(parentObject);
				
					// Remove highlight
					if (tileRollOver != null || tileRollOut != null || tilePressed != null || tileReleased != null)
						setHighlighted(false);
				});

			sprite.addEventListener(MouseEvent.MOUSE_DOWN, 
				function(e : MouseEvent) : void
				{
					if (tilePressed != null)
						tilePressed(parentObject)
				});

			sprite.addEventListener(MouseEvent.MOUSE_UP, 
				function(e : MouseEvent) : void
				{
					if (tileReleased != null)
						tileReleased(parentObject)
				});
		}
		
		// Highlight on / off
		public function setHighlighted(highlighted : Boolean) : void
		{
		}
	}
}
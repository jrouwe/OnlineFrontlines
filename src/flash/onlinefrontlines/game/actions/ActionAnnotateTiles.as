package onlinefrontlines.game.actions
{
	import mx.core.Application;
	import mx.containers.Canvas;
	import mx.controls.Label;
	import flash.display.DisplayObject;
	import onlinefrontlines.utils.*;
	import onlinefrontlines.game.*;
	import onlinefrontlines.game.uistates.*;
	
	/*
	 * Annotates tile with string for debugging
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
	public class ActionAnnotateTiles extends Action 
	{
		private var description : String;
		private var tiles : Array;
		private var prevChildren : Array;
		
		// Apply the action
		public override function doAction() : void
		{
			var c : Canvas = Application.application.annotation;			
			prevChildren = c.getChildren();			
			c.removeAllChildren();
			
			// Add description
			var d : Label = new Label();
			d.x = 10;
			d.y = 10;
			d.text = description;
			d.setStyle("color", "#ffff00");
			d.selectable = false;
			c.addChild(d);			
			
			for each (var t : Object in tiles)
			{
				// Get tile location
				var l : Object = gameState.mapConfig.getTileLocation(t.x, t.y);
				
				// Add tile
				d = new Label();
				d.x = l.x + 10;
				d.y = l.y + 10;
				d.text = t.value;
				d.setStyle("color", "#ffff00");
				d.selectable = false;
				c.addChild(d);				
			}
		}
		
		// Undo the action
		public override function undoAction() : void
		{
			var c : Canvas = Application.application.annotation;			
			c.removeAllChildren();
			for each (var ch : DisplayObject in prevChildren)
				c.addChild(ch);			
		}	

		// Convert action from a string
		public override function fromString(param : Array) : void
		{
			// Get selected unit
			description = param[1];
			
			// Get path
			tiles = new Array();
			for (var c : int = 2; c < param.length; c += 3)
			{
				var t : Object = { x : int(param[c]), y : int(param[c + 1]), value : param[c + 2] };
				
				tiles.push(t);
			}
		}
		
		/**
		 * Convert action to a string
		 */
		public override function toString() : String
		{
			var rv : String = "at," + description + ",";
			
			for each (var t : Object in tiles)
				rv += "," + t.x + "," + t.y + "," + t.value;
			
			return rv;
		}
	}
}

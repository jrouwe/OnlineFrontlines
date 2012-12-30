package onlinefrontlines.utils
{
    import flash.display.*;
	import flash.events.MouseEvent;
	import flash.text.*;
	import mx.controls.Image;
	import mx.controls.Text;
	import mx.events.FlexEvent;
	import mx.core.Application;
	
	/*
	 * Balloon class that displays help
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
	public class HelpBalloon extends Image
	{
		// Sizes		
		private static var arrowSize : int = 10;
		private static var arrowSizeCorner : int = 5;
		private static var arrowBaseSize : int = 5;
		private static var edgeSize : int = 5;
		private static var cornerSize : int = 5;
		private static var maxTextSize : int = 300;
		
		private static var openBalloons : int = 0;
		private static var queuedBalloons : Array = new Array(); // of Object
		private static var shownBalloons : Object = new Object(); // Set of balloons already shown

		// Constructor
		public function HelpBalloon(x : int, y : int, text : String, parent : DisplayObjectContainer, closeCallback : Function) : void
		{
			// Take the full width of the application
			this.x = 0;
			this.y = 0;
			this.width = Application.application.width;
			this.height = Application.application.height;
					
			// Create semi transparent rectangle
			addChild(Tools.createRectangle(width, height, 0x333333, 0.5));

			// Add background
			var balloon : MovieClip = new MovieClip();
			addChild(balloon);

			// Add text
			var textField : TextField = new TextField();
			textField.width = maxTextSize;
			textField.height = 0;
			textField.autoSize = TextFieldAutoSize.LEFT;
			textField.selectable = false;
			textField.multiline = true;
			textField.wordWrap = true;
			textField.text = text;
			addChild(textField);

			// Get size of text
			var w : int = Math.min(textField.width, textField.textWidth + 5);
			var h : int = textField.height;
						
			// Determine arrow corner
			var corner : int;
			if (x < width / 2)
			{
				if (y < height / 2)
					corner = 0; // Top left
				else if (y > parent.height - height / 2)
					corner = 6; // Bottom left
				else
					corner = 7; // Left
			}
			else if (x > parent.width - width / 2)
			{
				if (y < height / 2)
					corner = 2; // Top right
				else if (y > parent.height - height / 2)
					corner = 4; // Right
				else
					corner = 3; // Bottom right
			}
			else
			{
				if (y < parent.height / 2)
					corner = 1; // Top
				else
					corner = 5; // Bottom
			}

			// Will be point where balloon ends			
			var anchorX : int, anchorY : int;
			
			// Draw background
			var g : Graphics = balloon.graphics;

			var xl : int = arrowSize;
			var xr : int = arrowSize + w + 2 * edgeSize;
			var xm : int = (xl + xr) / 2;			
			var yt : int = arrowSize;  
			var yb : int = arrowSize + h + 2 * edgeSize;
			var ym : int = (yt + yb) / 2;
			
			g.beginFill(0xFFFFFF);
			g.lineStyle(1, 0x000000);
			if (corner == 0)
			{
				anchorX = xl - arrowSizeCorner, anchorY = yt - arrowSizeCorner;
				g.moveTo(xl, yt + arrowBaseSize);
				g.lineTo(anchorX, anchorY);
				g.lineTo(xl + arrowBaseSize, yt);
			}
			else
			{
				g.moveTo(xl, yt + cornerSize);
				g.curveTo(xl, yt, xl + cornerSize, yt);
			}
			if (corner == 1)
			{
				anchorX = xm, anchorY = yt - arrowSize;
				g.lineTo(xm - arrowBaseSize, yt);
				g.lineTo(anchorX, anchorY);
				g.lineTo(xm + arrowBaseSize, yt);
			}
			if (corner == 2)
			{
				anchorX = xr + arrowSizeCorner, anchorY = yt - arrowSizeCorner;
				g.lineTo(xr - arrowBaseSize, yt);
				g.lineTo(anchorX, anchorY);
				g.lineTo(xr, yt + arrowBaseSize);
			}
			else
			{
				g.lineTo(xr - cornerSize, yt);
				g.curveTo(xr, yt, xr, yt + cornerSize);
			}
			if (corner == 3)
			{
				anchorX = xr + arrowSize, anchorY = ym;
				g.lineTo(xr, ym - arrowBaseSize);
				g.lineTo(anchorX, anchorY);
				g.lineTo(xr, ym + arrowBaseSize);
			}
			if (corner == 4)
			{
				anchorX = xr + arrowSizeCorner + arrowSizeCorner, anchorY = yb + arrowSizeCorner;
				g.lineTo(xr, yb - arrowBaseSize);
				g.lineTo(anchorX, anchorY);
				g.lineTo(xr - arrowBaseSize, yb);
			}
			else
			{
				g.lineTo(xr, yb - cornerSize);
				g.curveTo(xr, yb, xr - cornerSize, yb);
			}
			if (corner == 5)
			{
				anchorX = xm, anchorY = yb + arrowSize;
				g.lineTo(xm + arrowBaseSize, yb);
				g.lineTo(anchorX, anchorY);
				g.lineTo(xm - arrowBaseSize, yb);
			}
			if (corner == 6)
			{
				anchorX = xl - arrowSizeCorner, anchorY = yb + arrowSizeCorner;
				g.lineTo(xl + arrowBaseSize, yb);
				g.lineTo(anchorX, anchorY);
				g.lineTo(xl, yb - arrowBaseSize);
			}
			else
			{
				g.lineTo(xl + cornerSize, yb);
				g.curveTo(xl, yb, xl, yb - cornerSize);
			} 
			if (corner == 7)
			{
				anchorX = xl - arrowSize, anchorY = ym;
				g.lineTo(xl, ym + arrowBaseSize);
				g.lineTo(anchorX, anchorY);
				g.lineTo(xl, ym - arrowBaseSize);
			}
			g.endFill();
						
			// Position balloon and text so that the balloon starts at the anchor point
			balloon.x = x - anchorX;
			balloon.y = y - anchorY;
			textField.x = x + arrowSize + edgeSize - anchorX;
			textField.y = y + arrowSize + edgeSize - anchorY;
			
			// Close on click
			var image : Image = this;
			addEventListener(MouseEvent.CLICK, 
				function(e : MouseEvent) : void 
				{ 	
					// Decrement open balloons
					--openBalloons;

					// Remove from parent
					parent.removeChild(image);

					// Cancel event
					e.stopPropagation();

					// Send callback
					if (closeCallback != null)
						closeCallback();
				});

			// Add to parent
			parent.addChild(this);
			
			// Increment open balloons
			openBalloons++
		}
		
		/**
		 * Show the next balloon
		 */
		private static function showNext() : void
		{
			if (queuedBalloons.length > 0)
			{
				var balloon : Object = queuedBalloons[0];
				queuedBalloons.splice(0, 1);
				
				show(balloon.x, balloon.y, balloon.text, balloon.showOnce, showNext);
			}
		}
		
		/**
		 * Queue a balloon and show it when the last one is clicked away
		 */
		public static function queue(x : int, y : int, text : String, showOnce : Boolean) : void
		{
			queuedBalloons.push({ x : x, y : y, text : text, showOnce : showOnce });

			if (openBalloons == 0)
				showNext();
		}
		
		/**
		 * Same as above but centered on a display object
		 */
		public static function queueCenterOnDisplayObject(d : DisplayObject, text : String, showOnce : Boolean) : void
		{
			var x : int = d.x + d.width / 2;
			var y : int = d.y + d.height / 2;
			
			while (d.parent != null)
			{
				d = d.parent;
				
				x += d.x;
				y += d.y;
			}
			
			queue(x, y, text, showOnce);
		}			
		
		/**
		 * Same as above but centered on a display object
		 */
		public static function queueCenterOnHexagonGrid(g : HexagonGrid, x : int, y : int, text : String, showOnce : Boolean) : void
		{
			var p : Object = g.getTileLocation(x, y);
			
			var px : int = g.x + p.x + g.getTileDX() / 2;
			var py : int = g.y + p.y + g.getTileDY() / 2;

			var d : DisplayObject = g;			
			while (d.parent != null)
			{
				d = d.parent;
				
				px += d.x;
				py += d.y;
			}
			
			queue(px, py, text, showOnce);
		}			

		/**
		 * Show a balloon
		 */
		public static function show(x : int, y : int, text : String, showOnce : Boolean, closeCallback : Function) : void
		{
			// Check global override
			if (Application.application.parameters.showHelpBalloons != null
				&& int(Application.application.parameters.showHelpBalloons) == 0)
			{
				closeCallback();
				return;
			}
				
			// Check show once
			if (showOnce)
			{
				if (shownBalloons[text] != null)
				{
					closeCallback();
					return;
				}
				else
					shownBalloons[text] = 1;
			}
			
			// Show balloon	
			new HelpBalloon(x, y, text, Application.application.balloons, closeCallback)
		}				
	}
}

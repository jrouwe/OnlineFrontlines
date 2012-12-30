package onlinefrontlines.utils
{
	import mx.controls.Image;
	import onlinefrontlines.utils.*;

	/*
	 * Simple bitmap text class for fixed sized fonts
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
	public class BitmapText
	{
		// Font
		[Embed(source='../../assets/fonts/unit_font.png')]
		private static var unitFontClass : Class;		
		private static var unitFont : AnimatedBitmapData = new AnimatedBitmapData(unitFontClass, 10, 3);
		[Embed(source='../../assets/fonts/menu_font.png')]
		private static var menuFontClass : Class;		
		private static var menuFont : AnimatedBitmapData = new AnimatedBitmapData(menuFontClass, 12, 1);
		[Embed(source='../../assets/fonts/damage_font.png')]
		private static var damageFontClass : Class;		
		private static var damageFont : AnimatedBitmapData = new AnimatedBitmapData(damageFontClass, 11, 1);

		// Types
		public static var typeUnit : int = 0;
		public static var typeMenu : int = 1;
		public static var typeDamage : int = 2;

		// Colors		
		public static var colorWhite : int = 0;
		public static var colorGray : int = 1;
		public static var colorRed : int = 2;

		// Graphics
		private var font : AnimatedBitmapData;
		private var parentImage : Image;
		private var sign : AnimatedBitmap; 
		private var dot : AnimatedBitmap;
		private var fontDigits : Array = new Array();

		// State		
		private var value : int = -1;
		private var dotLocation : int = 0;
		private var color : int = colorWhite;
		private var visible : Boolean = true;
		
		// Constructor
		public function BitmapText(parentImage : Image, numDigits : int, dotLocation : int, fontType : int) : void
		{
			this.parentImage = parentImage;
			this.dotLocation = dotLocation;
			
			var hasSign : Boolean = false;
			var hasDot : Boolean = false;
			switch (fontType)
			{
			case 0:
				font = unitFont;
				break;
				
			case 1:
				font = menuFont;
				hasDot = dotLocation > 0;
				break;
				
			case 2:
				font = damageFont;
				hasSign = true;
				break;
			} 

			for (var i : int = 0; i < numDigits; ++i)
			{
				var b : AnimatedBitmap = new AnimatedBitmap(font);
				fontDigits.push(b);
				parentImage.addChild(b);
			}
			
			if (hasSign)
			{
				sign = new AnimatedBitmap(font);
				sign.setFrame(10);
				parentImage.addChild(sign);
			}
			
			if (hasDot)
			{
				dot = new AnimatedBitmap(font);
				dot.setFrame(11);
				parentImage.addChild(dot);
			}
			
			setValue(0);
		}
		
		// Destroy the text
		public function destroy() : void
		{
			if (sign != null)
				parentImage.removeChild(sign);
				
			for each (var b : AnimatedBitmap in fontDigits)
				parentImage.removeChild(b);
		}

		// Set text visible / invisible
		public function setVisible(visible : Boolean) : void
		{
			if (this.visible == visible)
				return;
			this.visible = visible;
		
			var v : int = value;
			
			if (sign != null)
			{
				sign.visible = visible && v < 0;
				v = Math.abs(v);
			}
			
			if (dot != null)
				dot.visible = visible;
			
			for (var i : int = fontDigits.length - 1; i >= 0; --i)
			{
				fontDigits[i].visible = ((i == fontDigits.length - 1 || v > 0) && visible);
				v /= 10;
			}
		}
		
		// Set alpha value
		public function setAlpha(alpha : Number) : void
		{
			if (sign != null)
				sign.alpha = alpha;
			for (var i : int = fontDigits.length - 1; i >= 0; --i)
				fontDigits[i].alpha = alpha;
		}

		// Set value
		public function setValue(value : int) : void
		{
			if (this.value == value)
				return;
			this.value = value;
			
			updateText();
		}		
		
		// Set position
		public function setPosition(x : int, y : int) : void
		{
			if (sign != null)
			{
				sign.x = x;
				sign.y = y;
				
				x += sign.width;
			}
		
			var count : int = fontDigits.length - dotLocation;
			for each (var b : AnimatedBitmap in fontDigits)
			{
				b.x = x;
				b.y = y;
				
				x += b.width;
				
				// Place dot
				--count;
				if (dot != null && count == 0)
				{
					dot.x = x;
					dot.y = y;
					
					x += dot.width;
				}
			}
		}		

		// Set color
		public function setColor(color : int) : void
		{
			if (this.color == color)
				return;
			this.color = color;

			updateText();				
		}
		
		// Get color
		public function getColor() : int
		{
			return color;
		}
		
		// Internal function to render the text
		private function updateText() : void
		{
			var v : int = value;
			
			if (sign != null)
			{
				sign.visible = v < 0;
				v = Math.abs(v);
			}
			
			for (var i : int = fontDigits.length - 1; i >= 0; --i)
			{
				fontDigits[i].setFrame(v % 10 + 10 * color);
				fontDigits[i].visible = ((i >= fontDigits.length - dotLocation - 1 || v > 0) && visible);
				v /= 10;
			}
		}
	}
}
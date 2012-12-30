package onlinefrontlines.utils
{
	import mx.effects.effectClasses.AnimatePropertyInstance;
	
	/*
	 * Animating color instance
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
	public class AnimateColorInstance extends AnimatePropertyInstance
	{
		/** The start color values for each of the r, g, and b channels */
		protected var startValues : Object;
		
		/** The change in color value for each of the r, g, and b channels. */
		protected var delta : Object;
		
		/**
		 * Constructor
		 *
		 * @param target The Object to animate with this effect.
		 */
		public function AnimateColorInstance(target : Object)
		{
			super(target);
		}
		
		/**
		 * Converts an integer (hex) value to an object with separate r, g, 
		 * and b elements.
		 */
		private static function intToRgb(color : int) : Object
		{
			var r : int = (color & 0xFF0000) >> 16;
			var g : int = (color & 0x00FF00) >> 8;
			var b : int = color & 0x0000FF;
			return { r : r, g : g, b : b };
		}
	
		/**
		 * @private
		 */
		override public function play() : void
		{
			// We need to call play first so that the fromValue is
			// correctly set, but this has the side effect of calling
			// onTweenUpdate before startValues or delta can be set,
			// so we need to check for that in onTweenUpdate to avoid
			// run time errors.
			super.play();
			
			// Calculate the delta for each of the color values
			startValues = intToRgb(fromValue);
			var stopValues : Object = intToRgb(toValue);
			delta = {
						r : (startValues.r - stopValues.r) / duration,
						g : (startValues.g - stopValues.g) / duration,
						b : (startValues.b - stopValues.b) / duration
					};
			
		}
		
		/**
		 * @private
		 */
		override public function onTweenUpdate(value : Object) : void
		{
			// Bail out if delta hasn't been set yet
			if (delta == null)
			{
				return;
			}
			
			// Catch the situation in which the playheadTime is actually more
			// than duration, which causes incorrect colors to appear at the 
			// end of the animation.
			var playheadTime : int = this.playheadTime;
			if (playheadTime > duration)
			{
				// Fix the local playhead time to avoid going past the end color
				playheadTime = duration;
			}
			
			// Calculate the new color value based on the elapased time and the change
			// in color values
			var colorValue : int = ((startValues.r - playheadTime * delta.r) << 16)
								+ ((startValues.g - playheadTime * delta.g) << 8)
								+ (startValues.b - playheadTime * delta.b);
			
			// Either set the property directly, or set it as a style
			if (!isStyle)
			{
				target[property] = colorValue;
			}
			else
			{
				target.setStyle(property, colorValue);
			}
		}
	}
}
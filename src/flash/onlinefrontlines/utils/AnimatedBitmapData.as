package onlinefrontlines.utils
{
	import flash.geom.Rectangle;
	import flash.geom.Point;
	import flash.display.Bitmap;
	import flash.display.BitmapData;
	import flash.events.Event;

	/*
	 * Animated bitmap shared data
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
	public class AnimatedBitmapData
	{
		private var bitmapData : Array = new Array();
		
		// Constructor 
		public function AnimatedBitmapData(bitmapClass : Class, framesX : int, framesY : int = 1)
		{
			var bitmap : Bitmap = new bitmapClass();
			
			var w : int = bitmap.width / framesX;
			var h : int = bitmap.height / framesY;
			
			for (var y : int = 0; y < framesY; ++y)
				for (var x : int = 0; x < framesX; ++x)
				{
					var f : BitmapData = new BitmapData(w, h);
					f.copyPixels(bitmap.bitmapData, new Rectangle(x * w, y * h, w, h), new Point(0, 0));
					bitmapData.push(f);
				}
		}
		
		// Get a frame
		public function getFrame(frame : int) : BitmapData
		{
			return bitmapData[frame];
		}
		
		// Get total number of frames
		public function getNumFrames() : int
		{
			return bitmapData.length;
		}
	}
}
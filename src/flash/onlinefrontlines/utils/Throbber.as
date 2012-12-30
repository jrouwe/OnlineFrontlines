package onlinefrontlines.utils
{
	import mx.core.Application;
	import mx.controls.Image;
	import mx.core.BitmapAsset; 
	import flash.events.Event;
	import flash.utils.*;
	
	/*
	 * Spinning wheel to indicate progress
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
	public class Throbber extends Image
	{
		// Images
		[Embed(source='../../assets/throbber/throbber.png')]
		private static var throbberClass : Class;
		[Embed(source='../../assets/throbber/throbber_left.png')]
		private static var throbberLeftClass : Class;
		[Embed(source='../../assets/throbber/throbber_right.png')]
		private static var throbberRightClass : Class;
		
		// Images
		private var left : BitmapAsset;
		private var right : BitmapAsset;
		
		// State
		private var startTime : int;
		private var direction : int;
		
		// Constants
		private static var leftToRightTime : int = 1000;
		
		// Constructor
		public function Throbber()
		{
			// Set size
			width = 65;
			height = 10;
			
			// Properties
			mouseEnabled = false;
			mouseChildren = false;

			// Create Images
			addChild(new throbberClass());
			
			left = new throbberLeftClass();
			left.y = 2;
			addChild(left);
			
			right = new throbberRightClass();
			right.y = 2;
			addChild(right);

			// Initially not visible
			visible = false;
		}
		
		// Show the throbber
		public function show() : void
		{
			if (visible)
				return;
			visible = true;

			// Init state
			startTime = getTimer();
			direction = 1;
			left.visible = false;
			right.visible = true;
			right.x = 10;

			// Add listener			
			Application.application.addEventListener(Event.ENTER_FRAME, onEnterFrame);
		}
		
		// Hide the throbber
		public function hide() : void
		{
			if (!visible)
				return;
			visible = false;
				
			// Remove listener
			Application.application.removeEventListener(Event.ENTER_FRAME, onEnterFrame);
		}
		
		// Enter frame handler
		private function onEnterFrame(e : Event) : void
		{
			// Get time 
			var t : int = getTimer();
			var dt : int = t - startTime;
			
			// Check turn around time
			if (dt > leftToRightTime)
			{
				startTime = t;
				dt = 0;
				direction = -direction;
			}
			
			// Determine position of throbber
			if (direction == -1)
			{
				left.visible = true;
				left.x = 43 - int(dt * 11 / leftToRightTime) * 3;
				right.visible = false;
			}
			else
			{
				left.visible = false;
				right.visible = true;
				right.x = 10 + int(dt * 11 / leftToRightTime) * 3;
			}
		}
	}
}
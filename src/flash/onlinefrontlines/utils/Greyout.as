package onlinefrontlines.utils
{
	import mx.core.Application;
	import mx.controls.Image;
	import flash.display.Shape;
	import flash.display.Graphics;

	/*
	 * Tranclucent black overlay (for dimming screen on popup)
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
	public class Greyout extends Image
	{
		public function Greyout() : void
		{
			// Take the full width of the application
			x = 0;
			y = 0;
			width = Application.application.width;
			height = Application.application.height;
					
			// Create semi transparent rectangle
			source = Tools.createRectangle(width, height, 0x333333, 0.5);
			
			// Hide image
			visible = false;			
		}
	}
}
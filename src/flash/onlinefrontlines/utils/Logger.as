package onlinefrontlines.utils
{
	import mx.core.Application;
	import mx.controls.TextArea;
	import mx.events.FlexEvent;
	import flash.ui.Keyboard;
	import flash.events.KeyboardEvent;
	
	/*
	 * Simple window to show the log
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
	public class Logger extends ToolWindow
	{
		/**
		 * Logger instance
		 */
		private static var instance : Logger;
		
		/**
		 * Text area 
		 */
		private var textArea : TextArea;
		
		/**
		 * Static initialization
		 */
		public static function init() : void
		{
			instance = new Logger();
		}
		
		/**
		 * Log a text string
		 */
		public static function log(text : String) : void
		{
			if (instance != null)
				instance.logInternal(text);
		}
			
		/**
		 * Constructor
		 */
		public function Logger() : void
		{
			// Set size
			super(400, 200);

			// Position
			x = 10;
			y = 10;			
			
			// Add text area
			textArea = new TextArea();
			textArea.x = 0;
			textArea.y = 0;
			textArea.width = 400;
			textArea.height = 200;
			clientCanvas.addChild(textArea);
			
			// Fix scrolling
			textArea.addEventListener(FlexEvent.UPDATE_COMPLETE,
				function (e : FlexEvent) : void
				{
					textArea.verticalScrollPosition = textArea.maxVerticalScrollPosition;
				});				
				
			// Add to tree
			Application.application.canvas.addChild(this);

			// Initially hide
			visible = false;			
		}	
		
		/**
		 * Log a text string
		 */
		private function logInternal(text : String) : void
		{
			trace(text);
			
			textArea.text = textArea.text.slice(-65536) + text + "\n";
		}	
		
		/**
		 * Show the window
		 */
		public static function show() : void
		{
			instance.visible = true;
		}
		
		/**
		 * Close callback
		 */
		public override function onClosed() : void
		{
			// Hide
			visible = false;
		}
	}
}

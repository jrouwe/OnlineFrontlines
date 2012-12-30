package onlinefrontlines.utils
{
	import mx.core.Application;
	import mx.containers.Canvas;
	import mx.controls.Button;
	import mx.controls.Image;
	import mx.controls.TextInput;
	import mx.events.FlexEvent; 
	import flash.events.MouseEvent;
	import flash.ui.Keyboard;
	import flash.events.KeyboardEvent;
	import onlinefrontlines.utils.*;
	
	/*
	 * Base class window
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
	public class ToolWindow extends Canvas
	{
		// Title bar image
		[Embed(source='../../assets/tool_window/title_bar.png')]
		private static var titleBarClass : Class;
		
		// Close button		
		[Embed(source='../../assets/tool_window/close_up.png')]
		private static var closeUpClass : Class;
		[Embed(source='../../assets/tool_window/close_down.png')]
		private static var closeDownClass : Class;
		[Embed(source='../../assets/tool_window/close_over.png')]
		private static var closeOverClass : Class;
		[Embed(source='../../assets/tool_window/close_disabled.png')]
		private static var closeDisabledClass : Class;
		
		// Client canvas
		public var clientCanvas : Canvas;
		
		// Dragging support
		private var titleBarImage : Image;		
		private var isDragging : Boolean = false;

		// Keyboard support
		private static var toolWindows : Array = new Array();
		public static var keyboardFocus : TextInput;
		
		// Constructor
		public function ToolWindow(w : int, h : int)
		{
			// Set size
			width = w;
			height = h + 11;

			// Make sure no images leave this canvas
			clipContent = true;
			horizontalScrollPolicy = "off";
			verticalScrollPolicy = "off";

			// Create background
			titleBarImage = new Image();
			titleBarImage.source = titleBarClass;
			addChild(titleBarImage);
			
			// Create client canvas
			clientCanvas = new Canvas();
			clientCanvas.x = 0;
			clientCanvas.y = 11;
			clientCanvas.width = w;
			clientCanvas.height = h;
			addChild(clientCanvas);

			// Create close button
			var button : Button = new Button();
			button.x = width - 10;
			button.y = 1;
			button.width = 9;
			button.height = 9;
			button.setStyle("upSkin", closeUpClass);
			button.setStyle("overSkin", closeOverClass);
			button.setStyle("downSkin", closeDownClass);
			button.setStyle("disabledSkin", closeDisabledClass);
			addChild(button);
			button.addEventListener(MouseEvent.CLICK, onClosePressed);

			// Add listeners
			addEventListener(FlexEvent.SHOW, onShow);
			addEventListener(FlexEvent.HIDE, onHide);
			addEventListener(MouseEvent.MOUSE_DOWN, onMouseDown); 
			addEventListener(MouseEvent.MOUSE_UP, onMouseUp);
		}		

		// Init tool window system		
		public static function init() : void
		{
			// Make sure we can receive keyboard input by creating an invisible text field
			keyboardFocus = new TextInput();
			keyboardFocus.x = -10;
			keyboardFocus.y = -10;
			keyboardFocus.width = 0;
			keyboardFocus.height = 0;
			Application.application.addChild(keyboardFocus);
			keyboardFocus.setFocus();
			
			// Register handler that catches all keys
			Application.application.addEventListener(KeyboardEvent.KEY_DOWN, onKeyDown); 		
		}
		
		// Key down handler
		private static function onKeyDown(e : KeyboardEvent) : void
		{
			if (e.keyCode == Keyboard.ESCAPE)
			{
				// Close topmost window
				if (toolWindows.length > 0)
					toolWindows[toolWindows.length - 1].onClosed();
			}
		}		
		
		// Show handler
		private function onShow(event : FlexEvent) : void
		{
			// Add to list of tool windows				
			toolWindows.push(this);
		}

		// Hide handler
		private function onHide(event : FlexEvent) : void
		{				
			// Remove from list of tool windows
			var idx : int = toolWindows.indexOf(this);
			if (idx >= 0)
				toolWindows.splice(idx, 1);
		}
		
		// Mouse down handler
		private function onMouseDown(e : MouseEvent) : void 
		{ 
			if (e.target == titleBarImage && !isDragging)
			{
				startDrag();
				isDragging = true;
			}
		}

		// Mouse up handler
		private function onMouseUp(e : MouseEvent) : void 
		{ 
			if (isDragging)
			{
				stopDrag();
				onStateChanged();
				isDragging = false;
			}
		}
		
		// Close button pressed
		private function onClosePressed(event : MouseEvent) : void
		{
			onClosed();
		}

		// Close callback
		public function onClosed() : void
		{
		}
		
		// Callback that state changed and state might need to be saved
		public function onStateChanged() : void
		{
		}
	}
}
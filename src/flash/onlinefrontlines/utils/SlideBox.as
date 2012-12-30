package onlinefrontlines.utils
{
	import mx.core.Application;
	import mx.controls.Image;
	import mx.controls.Text;
	import mx.containers.Canvas;
	import mx.events.FlexEvent;
	import flash.events.Event;
	import flash.utils.*;
	
	/*
	 * Box with text that slides on screen, stays for a while and then slides away
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
	public class SlideBox extends Canvas
	{
		// Application defined enable / disable UI function
		public static var enableUI : Function;
		
		// Background image
		[Embed(source='../../assets/slide_box/background.png')]
		private static var backgroundClass : Class;
		
		// States
		private static var stateClosed : int = 0;
		private static var stateOpening : int = 1;
		private static var stateOpen : int = 2;
		private static var stateClosing : int = 3;
		
		// Timing
		private static var closeDelay : int = 500;
		private static var openCloseTime : int = 500;
		private static var openDelay : int = 3000;
		
		// All messages to show
		private var messages : Array = new Array();
		
		// Current state
		private var state : int = stateClosed;
		private var stateStartTime : int = getTimer();
		private var textField : Text;

		// Constructor
		public function SlideBox() : void
		{
			// Set dimensions
			width = 694;
			height = 45;
			 
			// No scroll bars please
			horizontalScrollPolicy = "off";
			verticalScrollPolicy = "off";
			
			// Add background
			var background : Image = new Image();
			background.source = backgroundClass;
			addChild(background);	

			// Create text
			textField = new Text();
			textField.x = 10;
			textField.width = width - 20;
			textField.styleName = "messageBox";
			textField.selectable = false;
			textField.addEventListener(FlexEvent.UPDATE_COMPLETE, 
				function (e : FlexEvent) : void 
				{
					// Vertically center 
					textField.y = (height - textField.height) / 2 - 5; 
				});
			addChild(textField);
			
			// Initially hide
			visible = false;

			// Add listener
			Application.application.addEventListener(Event.ENTER_FRAME, enterFrame);
		}
		
		// Show message box with specified text
		public function show(text : String) : void
		{
			messages.push(text);
		}

		// Enter frame handler
		private function enterFrame(e : Event) : void
		{
			var timeInState : int = getTimer() - stateStartTime;
			
			switch (state)
			{
			case stateClosed:
				visible = false;
				if (timeInState > closeDelay && messages.length > 0)
				{
					textField.text = messages[0];
					messages.splice(0, 1);
					switchState(stateOpening);
				}
				break;
				
			case stateOpening:
				visible = true;
				y = Math.min(height * timeInState / openCloseTime - height, 0);
				if (y == 0)
					switchState(stateOpen);
				break;
				
			case stateOpen:
				if (timeInState > openDelay)
					switchState(stateClosing);
				break;
				
			case stateClosing:
				y = -height * timeInState / openCloseTime;
				if (y <= -height)
					switchState(stateClosed);
				break;			
			}
		}
		
		// Switch to a new state
		private function switchState(state : int) : void
		{		
			this.state = state;
			stateStartTime = getTimer();
		}
		
		// Close all active and pending slide boxes
		public function closeAll() : void
		{
			visible = false;
			switchState(stateClosed);
			messages = new Array();
		}
	}
}

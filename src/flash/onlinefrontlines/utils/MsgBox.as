package onlinefrontlines.utils
{
	import mx.core.Application;
	import flash.display.Shape;
	import flash.display.Graphics;
	import mx.controls.Image;
	import mx.controls.Text;
	import mx.controls.Button;
	import mx.controls.TextInput;
	import mx.containers.Canvas;
	import mx.events.FlexEvent;
	import flash.events.MouseEvent;
	
	/*
	 * Basic message box
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
	public class MsgBox extends Canvas
	{
		// Application defined enable / disable UI function
		public static var enableUI : Function;
		
		// Background image
		[Embed(source='../../assets/msg_box/background.png')]
		private static var backgroundClass : Class;

		// Constructor
		public function MsgBox() : void
		{
			// No scroll bars please
			horizontalScrollPolicy = "off";
			verticalScrollPolicy = "off";
			
			// Initially hide
			visible = false;
		}
		
		// Close current message box
		public function close() : void
		{
			if (visible)
			{
				enableUI(true);
				
				removeAllChildren();
				
				visible = false;
			}
		}
				
		// Show message box with specified text
		public function show(text : String) : void
		{		
			// Close previous box
			close();	
			
			// Disable other user interface elements
			enableUI(false);
							
			// Add background
			var background : Image = new Image();
			background.source = backgroundClass;
			addChild(background);	

			// Create text
			var textField : Text = new Text();
			textField.x = 10;
			textField.width = width - 20;
			textField.text = text;
			textField.styleName = "messageBox";
			textField.selectable = false;
			textField.addEventListener(FlexEvent.UPDATE_COMPLETE, 
				function (e : FlexEvent) : void 
				{
					// Vertically center 
					textField.y = 60 - textField.height / 2; 
				});
			addChild(textField);
	
			// Show
			visible = true;
		}
		
		// Create a button
		protected function createButton(x : int, y : int, label : String, callback : Function) : void
		{
			var b : Button = new Button();
			b.x = x;
			b.y = y;
			b.width = 96;
			b.height = 17;
			b.styleName = "messageBoxButton";
			b.label = label;
			b.addEventListener(MouseEvent.CLICK, 
				function(e : MouseEvent) : void 
				{ 	
					close();
					
					if (callback != null)
						callback(); 
				});
			addChild(b);
		}
		
		// Show message box with specified text and close it when the user presses ok
		public function showOk(text : String, okCallback : Function) : void
		{
			show(text);
			
			createButton((width - 96) / 2, 117, "OK", okCallback);
		}
		
		// Show message box with specified text and add yes / no button
		public function showYesNo(text : String, yesCallback : Function, noCallback : Function) : void
		{
			show(text);
			
			createButton(width / 2 - 106, 117, "Yes", yesCallback);
			createButton(width / 2 + 10, 117, "No", noCallback);
		}		

		// Show message box with specified text and add accept / decline button
		public function showAcceptDecline(text : String, acceptCallback : Function, declineCallback : Function) : void
		{
			show(text);
			
			createButton(width / 2 - 106, 117, "Accept", acceptCallback);
			createButton(width / 2 + 10, 117, "Decline", declineCallback);
		}		
	}
}

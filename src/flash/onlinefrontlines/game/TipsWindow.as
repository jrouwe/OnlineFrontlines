package onlinefrontlines.game
{
	import onlinefrontlines.utils.*;
	import mx.controls.Image;
	import mx.controls.Text;
	import mx.controls.Button;
	import mx.controls.CheckBox;
	import flash.events.MouseEvent;
	
	/*
	 * Window that shows tips on startup
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
	public class TipsWindow extends ToolWindow
	{
		// Background image
		[Embed(source='../../assets/msg_box/background.png')]
		private static var backgroundClass : Class;

		// Gameplay tips
		private static var tips : Array = new Array();
		
		// Currently displayed tip
		private var currentTip : int = -1;
		
		// Callback function when window closes
		private var closeCallback : Function;
		
		// Tips contents
		private var text : Text;
		private var image : Image;
		
		// Constructor
		public function TipsWindow() : void
		{
			super(280, 139);
			
			// Add background
			var background : Image = new Image();
			background.source = backgroundClass;
			clientCanvas.addChild(background);
			
			// Create tip text area
			text = new Text();
			text.x = 10;
			text.y = 10;
			text.width = width - 20;
			text.height = height - 75;
			text.styleName = "tipsWindow";
			text.selectable = false;
			clientCanvas.addChild(text);
			
			// Create tip image
			image = new Image();
			image.x = 10;
			image.y = 10;
			clientCanvas.addChild(image);
			
			// Create do not show again checkbox
			var c : CheckBox = new CheckBox();
			c.x = 15;
			c.y = height - 65;
			c.styleName = "tipsWindow";
			c.label = "Do not show again";
			c.selected = hasUserDisabledTips();
			clientCanvas.addChild(c);

			// Create next tip button
			var b : Button = new Button();
			b.width = 96;
			b.height = 17;
			b.x = width / 2 - 10 - b.width;
			b.y = 117;
			b.styleName = "messageBoxButton";
			b.label = "Next Tip";
			b.addEventListener(MouseEvent.CLICK, 
				function(e : MouseEvent) : void 
				{ 	
					selectTip();
				});
			clientCanvas.addChild(b);

			// Create close button
			b = new Button();
			b.width = 96;
			b.height = 17;
			b.x = width / 2 + 10;
			b.y = 117;
			b.styleName = "messageBoxButton";
			b.label = "Close";
			b.addEventListener(MouseEvent.CLICK, 
				function(e : MouseEvent) : void 
				{ 	
					// Check if do not show again is checked
					LocalSettings.getInstance().suppressTips = c.selected;
					
					// Close window
					onClosed();
				});
			clientCanvas.addChild(b);

			// Initially hide
			visible = false;
		}
		
		// If the user ticked the do not show again box
		public function hasUserDisabledTips() : Boolean
		{
			return LocalSettings.getInstance().suppressTips;
		}
		
		// Select the next tip
		public function selectTip() : void
		{
			// First select random tip, then just the next
			if (currentTip < 0)
				currentTip = Tools.randRange(0, tips.length - 1);
			else
				currentTip = (currentTip + 1) % tips.length;				

			// Set text
			text.text = tips[currentTip].text;	
			var imageSource : String = tips[currentTip].image; 
			image.source = imageSource != null && imageSource != ""? Tools.getAssetsURL(imageSource) : null;

			// Put text in correct place			
			if (image.source != null)
				text.x = 50;				
			else
				text.x = 10;
			text.width = width - text.x - 10;	
		}
				
		// Show gameplay tips window
		public function show(closeCallback : Function) : void
		{
			// Store callback		
			this.closeCallback = closeCallback;
			
			// Select a tip
			selectTip();

			// Show
			visible = true;
		}
		
		// Close callback
		public override function onClosed() : void
		{
			// Do callback
			if (closeCallback != null)
				closeCallback();
						
			// Hide
			visible = false;
		}
		
		// Load all tips
		public static function loadAll(xml : XML) : void
		{
			for each (var tipNode : XML in xml.tip)
				tips.push({ text : tipNode.txt, image : tipNode.img });
		}
	}
}

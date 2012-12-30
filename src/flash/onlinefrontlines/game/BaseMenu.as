package onlinefrontlines.game
{
	import mx.core.Application;
	import mx.controls.Button;
	import mx.controls.Image;
	import mx.containers.Canvas;
	import flash.events.MouseEvent;
	import onlinefrontlines.utils.*;
	import onlinefrontlines.game.*;
	
	/*
	 * Menu for base camps
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
	public class BaseMenu extends ToolWindow
	{
		// Application defined enable / disable UI function
		public static var enableUI : Function;
		
		// Background image
		[Embed(source='../../assets/base_menu/base_menu.png')]
		private static var backgroundClass : Class;

		// Unit button
		[Embed(source='../../assets/base_menu/unit_up.png')]
		private static var unitUpClass : Class;
		[Embed(source='../../assets/base_menu/unit_down.png')]
		private static var unitDownClass : Class;
		[Embed(source='../../assets/base_menu/unit_over.png')]
		private static var unitOverClass : Class;
		[Embed(source='../../assets/base_menu/unit_disabled.png')]
		private static var unitDisabledClass : Class;

		// Members
		private var maxUnits : int = 3;
		private var exitCallback : Function;

		// Constructor
		public function BaseMenu() : void
		{
			// Construct base class
			super(105, 34);
			
			// Initially hide
			visible = false;
		}
	
		// Close previous view
		public function close() : void
		{
			if (visible)
			{
				enableUI(true);
				
				clientCanvas.removeAllChildren();
				
				visible = false;
			}
		}
		
		// Close callback
		public override function onClosed() : void
		{
			close();
			exitCallback();
		}

		// Show menu
		public function show(x : int, y : int, base : UnitState, exitCallback : Function, deployCallback : Function, checkActionPoints : Boolean) : void
		{
			// Close previous menu
			close();	
			
			// Disable user interface
			enableUI(false);
			
			// Store callback
			this.exitCallback = exitCallback;
			
			// Set position	
			this.x = x;
			this.y = y;
			
			// Make sure the menu is on the screen
			if (x + width > Application.application.width)
				this.x = Application.application.width - width;
			if (y + height > Application.application.height)
				this.y = Application.application.height - height;
				
			// Add background
			var background : Image = new Image();
			background.source = backgroundClass;
			clientCanvas.addChild(background);
			
			// Create unit image
			var unitImage : Image = new Image();
			unitImage.mouseEnabled = false;

			// Create unit text image
			var unitTextImage : Image = new Image();
			unitTextImage.mouseEnabled = false;
			
			// Add contained units
			for (var i : int = 0; i < maxUnits && i < base.containedUnits.length; ++i)
			{
				var u : UnitState = base.containedUnits[i];
				
				// Create graphics
				var unitGraphics : UnitGraphics = new UnitGraphics(u, unitImage, unitTextImage, true, null);
				unitGraphics.moveTo(35 * i, 2);

				// Create deploy button
				var deployButton : Button = new Button();
				deployButton.x = 1 + 35 * i;
				deployButton.y = 0;
				deployButton.data = u;
				deployButton.setStyle("upSkin", unitUpClass);
				deployButton.setStyle("overSkin", unitOverClass);
				deployButton.setStyle("downSkin", unitDownClass);
				deployButton.setStyle("disabledSkin", unitDisabledClass);
				if (deployCallback != null && (!checkActionPoints || u.actionsLeft > 0))
					deployButton.addEventListener(MouseEvent.CLICK,
						function (e : MouseEvent) : void
						{
							close();
							deployCallback(e.target.data);
						});
				else
					deployButton.enabled = false;				
				clientCanvas.addChild(deployButton);
			}

			// Overlay units over buttons
			clientCanvas.addChild(unitImage);
			clientCanvas.addChild(unitTextImage);

			// Show
			visible = true;
		}
	}
}
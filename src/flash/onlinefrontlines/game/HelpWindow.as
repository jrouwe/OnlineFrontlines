package onlinefrontlines.game
{
	import mx.core.Application;
	import mx.core.BitmapAsset;
	import mx.controls.Label;
	import mx.controls.Text;
	import mx.controls.Image;
	import mx.containers.Canvas;
	import onlinefrontlines.utils.*;
	import onlinefrontlines.game.*;
	
	/*
	 * Window that displays unit help
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
	public class HelpWindow extends ToolWindow
	{
		// Images
		[Embed(source='../../assets/help_window/background.png')]
		private static var backgroundClass : Class;
		[Embed(source='../../assets/help_window/star.png')]
		private static var starClass : Class;
		
		// Constructor
		public function HelpWindow()
		{
			// Construct base class
			super(575 + 15, 8 * 58);

			// Initially hide
			visible = false;
		}
				
		// Show menu
		public function show() : void
		{
			// Close previous menu
			close();	
			
			// Create image that scrolls in this canvas
			var image : Image = new Image();
			image.height = UnitConfig.allUnits.length * 58;
			clientCanvas.addChild(image);

			// Add units
			for (var i : int = 0; i < UnitConfig.allUnits.length; ++i)
			{
				var y : int = i * 58;
				var config : UnitConfig = UnitConfig.allUnits[i];
				
				// Add tile
				var unitBackground : BitmapAsset = new backgroundClass();
				unitBackground.y = y;
				image.addChild(unitBackground);			
				
				// Add unit name
				var unitName : Label = new Label();
				unitName.x = 36;
				unitName.y = y + 2;
				unitName.width = 68;
				unitName.styleName = "helpWindowUnitName";
				unitName.selectable = false;
				unitName.text = config.name;
				clientCanvas.addChild(unitName);

				// Add unit images
				var imgf1 : BitmapAsset = new (UnitGraphics.getImageClass(Faction.f1, config.imageNumber, false))();
				imgf1.x = 10;
				imgf1.y = y + 18;
				image.addChild(imgf1);

				var imgf2 : BitmapAsset = new (UnitGraphics.getImageClass(Faction.f2, config.imageNumber, true))();
				imgf2.x = 63;
				imgf2.y = y + 18;
				image.addChild(imgf2);
				
				// Add attack strength
				addStars(image, 141, y + 23, getNumAttackStars(config.getStrengthProperties(UnitClass.air).getStrength(true)));
				addStars(image, 141, y + 33, getNumAttackStars(config.getStrengthProperties(UnitClass.land).getStrength(true)));
				addStars(image, 141, y + 43, getNumAttackStars(config.getStrengthProperties(UnitClass.water).getStrength(true)));

				// Add armour
				addStars(image, 195, y + 33, getNumArmourStars(config.maxArmour));
				
				// Add attack range
				addStars(image, 279, y + 23, config.getStrengthProperties(UnitClass.air).attackRange);
				addStars(image, 279, y + 33, config.getStrengthProperties(UnitClass.land).attackRange);
				addStars(image, 279, y + 43, config.getStrengthProperties(UnitClass.water).attackRange);
				
				// Add add movement
				addStars(image, 330, y + 33, config.getMaxMovement());

				// Add description
				var description : Text = new Text();
				description.x = 374;
				description.y = y + 21;
				description.width = 137;
				description.height = 30;
				description.styleName = "helpWindowDescription";
				description.selectable = false;
				description.text = config.description;
				clientCanvas.addChild(description);

				// Add points
				var points : Label = new Label();
				points.x = 528;
				points.y = y + 22;
				points.width = 36;
				points.styleName = "helpWindowPoints";
				points.setStyle("textAlign", "center");
				points.selectable = false;
				points.text = config.victoryPoints == 0? "?" : config.victoryPoints.toString();
				clientCanvas.addChild(points);
			}
				
			// Show
			visible = true;
		}
		
		// Get number of attack stars to display
		private function getNumAttackStars(strength : int) : int
		{
			if (strength == 0)
				return 0;
				
			return Math.max(strength / 5, 1)
		}
		
		// Get number of armour stars to display
		private function getNumArmourStars(maxArmour : int) : int
		{
			if (maxArmour <= 1)
				return 0;

			return Math.max(maxArmour / 10, 1);
		}
		
		// Add stars images
		private function addStars(image : Image, x : int, y : int, num : int) : void
		{
			for (var i : int = 0; i < num && i < 5; ++i)
			{
				var star : BitmapAsset = new starClass();
				star.x = x + i * 7;
				star.y = y;
				image.addChild(star);
			}
		}
		
		// Close the window
		public function close() : void
		{
			if (visible)
			{
				// Remove all UI elements				
				clientCanvas.removeAllChildren();
				
				// Hide
				visible = false;
			}
		}

		// Close callback
		public override function onClosed() : void
		{
			Application.application.showHelp.selected = false;
		}
	}
}
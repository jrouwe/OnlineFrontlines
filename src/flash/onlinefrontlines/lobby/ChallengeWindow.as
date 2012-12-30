package onlinefrontlines.lobby
{
	import mx.core.Application;
	import flash.display.Shape;
	import flash.display.Graphics;
	import mx.controls.Image;
	import mx.controls.Label;
	import mx.controls.Button;
	import mx.controls.TextInput;
	import mx.containers.Canvas;
	import flash.events.MouseEvent;
	import flash.net.*;
	import onlinefrontlines.utils.*;
	
	/*
	 * Window to accept / decline a challenge
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
	public class ChallengeWindow extends Canvas
	{
		// Application defined enable / disable UI function
		public static var enableUI : Function;
		
		// Background image
		[Embed(source='../../assets/lobby/challenge_window.png')]
		private static var backgroundClass : Class;

		// Constructor
		public function ChallengeWindow() : void
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
				
		// Create a button
		private function createButton(x : int, label : String, callback : Function) : Button
		{
			var b : Button = new Button();
			b.x = x;
			b.y = 346;
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
			return b;
		}
		
		/**
		 * Add a text string
		 */
		private function addText(x : int, y : int, text : String) : Label
		{
			var label : Label = new Label();
			label.x = x;
			label.y = y;
			label.text = text;
			label.styleName = "default";
			label.selectable = false;
			addChild(label);
			return label;
		}

		/**
		 * Add an image
		 */
		private function addImage(x : int, y : int, source : Class) : Image
		{
			var image : Image = new Image();
			image.x = x;
			image.y = y;
			image.source = source;
			addChild(image);
			return image;
		}

		// Show message box with specified text
		public function show(text : String, country : Country, user : LobbyUser, yesCallback : Function, noCallback : Function) : void
		{		
			// Close previous box
			close();	
			
			// Disable other user interface elements
			enableUI(false);
							
			// Add background
			var background : Image = new Image();
			background.source = backgroundClass;
			addChild(background);	

			// Create title
			var title : Label = new Label();
			title.x = 0;
			title.y = 0;
			title.text = text;
			title.width = width;
			title.styleName = "challengeWindowTitle";
			title.selectable = false;
			addChild(title);
			
			// Add map image
			var mi : Image = addImage(41, 32, null);
			mi.source = Tools.getImagesURL("map_" + country.countryConfig.mapId + ".png");

			// Fill dialog
			addText(83, 170, country.countryConfig.name);
			var ct : Label = addText(83, 188, country.countryConfig.countryType);
			ct.toolTip = country.countryConfig.countryTypeDescription;
			addText(83, 206, country.countryConfig.numUnits);
			if (user != null)
			{ 
				addImage(83, 246 + 5, Army.getSmallIcon(user.army));
				var un : Label = addText(93, 246 + 0, user.username);
				un.width = 75;
				addText(83, 246 + 18, user.level.toString());
				addImage(134, 246 + 24, RankImage.getImageClass(user.rank));
				addText(83, 246 + 36, user.getLeaderboardPosition());
				addText(83, 246 + 54, user.feedbackScore.toString());
				addText(83, 246 + 72, user.gamesWon.toString());
				addText(134, 246 + 72, user.gamesLost.toString());
				
				// User view button
				var b : Button = new Button();
				b.x = 200;
				b.y = 246 + 3;
				b.width = 41;
				b.height = 12;
				b.styleName = "viewButton";
				b.addEventListener(MouseEvent.CLICK, 
					function(e : MouseEvent) : void 
					{ 	
						Tools.navigateTo("UserStats.do", "userId=" + user.userId, "_blank");							
					});
				addChild(b);				

				// Feedback view button
				b = new Button();
				b.x = 200;
				b.y = 228 + 75;
				b.width = 41;
				b.height = 12;
				b.styleName = "viewButton";
				b.addEventListener(MouseEvent.CLICK, 
					function(e : MouseEvent) : void 
					{ 	
						Tools.navigateTo("FeedbackList.do", "userId=" + user.userId, "_blank");							
					});
				addChild(b);				
			}

			// Create buttons
			createButton(width / 2 - 106, "Yes", yesCallback);
			createButton(width / 2 + 10, "No", noCallback);

			// Show
			visible = true;
		}		
	}
}

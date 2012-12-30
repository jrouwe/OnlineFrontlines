package onlinefrontlines.game
{
	import flash.display.DisplayObjectContainer;
	import mx.controls.Image;
	import mx.controls.Label;
	import flash.net.URLRequest;
	import flash.net.URLLoader;
	import flash.net.URLLoaderDataFormat;
	import flash.display.Bitmap;
	import flash.display.BitmapData;	
	import flash.events.Event;
	import org.gif.player.GIFPlayer;
	import onlinefrontlines.utils.BMPDecoder;

	/*
	 * Side panel that shows user information
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
	public class UserPanel
	{
		// Background image
		[Embed(source='../../assets/score_window/user_panel.png')]
		private static var backgroundClass : Class;

		public static function create(parent : DisplayObjectContainer, x : int, y : int, player : Player) : void
		{
			// Add background
			var background : Image = new Image();
			background.x = x;
			background.y = y;
			background.source = backgroundClass;
			parent.addChild(background);	
			
			if (player != null)
			{
				// Add avatar image
				var avatar : Image = new Image();
				avatar.x = x + 10;
				avatar.y = y + 10;
				parent.addChild(avatar);				
				if (player.avatarURL != null && player.avatarURL.indexOf(".gif") >= 0)
				{	
					// Gif			
					var gifPlayer : GIFPlayer = new GIFPlayer();
					avatar.addChild(gifPlayer);
					gifPlayer.load(new URLRequest(player.avatarURL));
				}
				else if (player.avatarURL != null && player.avatarURL.indexOf(".bmp") >= 0)
				{
					// Bmp
					var loader : URLLoader = new URLLoader();
					loader.dataFormat = URLLoaderDataFormat.BINARY;
					loader.addEventListener(Event.COMPLETE, function (e : Event) : void {
							try
							{ 
								var decoder : BMPDecoder = new BMPDecoder();
								var bd : BitmapData = decoder.decode(loader.data);
								var bm : Bitmap = new Bitmap(bd);
								avatar.addChild(bm);
							} 
							catch (e : VerifyError)
							{
								 // Silently ignore bad bitmaps
							}
						});
					loader.load(new URLRequest(player.avatarURL));
				}
				else   
				{
					// Non gif
					avatar.source = player.avatarURL;
					avatar.load();
				}
				
				// Add player name						
				var label : Label = new Label();
				label.x = x + 65;
				label.y = y + 10;
				label.width = 75;
				label.styleName = "scoreWindowPlayerName";
				label.text = player.name;
				label.selectable = false;
				parent.addChild(label);

				// Add score
				label = new Label();
				label.x = x + 65;
				label.y = y + 27;
				label.width = 75;
				label.styleName = "scoreWindowStat";
				label.text = "Points: " + player.totalPoints;
				label.selectable = false;
				parent.addChild(label);

				// Add score
				label = new Label();
				label.x = x + 65;
				label.y = y + 37;
				label.width = 75;
				label.styleName = "scoreWindowStat";
				label.text = "Level: " + player.level;
				label.selectable = false;
				parent.addChild(label);
			}		
		}	
	}
}
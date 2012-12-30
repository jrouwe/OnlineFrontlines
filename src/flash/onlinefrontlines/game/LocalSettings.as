package onlinefrontlines.game
{
	import flash.net.SharedObject;
	import mx.core.Application;
	
	/*
	 * Class that stores all local settings
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
	public class LocalSettings
	{
		// Shared object that contains our settings
		private var so : SharedObject;

		// Singleton instance
		private static var instance : LocalSettings;
		
		// Get the singleton instance
		public static function getInstance() : LocalSettings
		{
			if (instance == null)
			{
				instance = new LocalSettings();
				instance.load();
			}
			
			return instance;
		}
		
		// Load settings
		private function load() : void
		{
			// Load shared object
			so = SharedObject.getLocal("OnlineFrontlinesLocalSettings");
		}
		
		// Set default values based on player experience
		public function setDefaults(playerLevel : int) : void
		{
			if (so.size == 0)
			{
				so.data.suppressTips = playerLevel >= 10;
				so.data.showInfo = playerLevel < 10;
			}
		}
		
		// Suppress tips property
		public function get suppressTips() : Boolean
		{
			return so.data.suppressTips == 1;
		}
		
		public function set suppressTips(value : Boolean) : void
		{
			so.data.suppressTips = value? 1 : 0;
			so.flush();
		}		
		
		// Chat window properties
		public function saveChatWindowState() : void
		{
			so.data.chatWindowX = Application.application.chatWindow.x;
			so.data.chatWindowY = Application.application.chatWindow.y;
			so.flush();
		}
		
		public function restoreChatWindowState() : void
		{
			if (so.data.chatWindowX != null)
			{
				Application.application.chatWindow.x = so.data.chatWindowX;
				Application.application.chatWindow.y = so.data.chatWindowY;
			} 
		}

		// Score window properties
		public function saveScoreWindowState() : void
		{
			so.data.scoreWindowX = Application.application.scoreWindow.x;
			so.data.scoreWindowY = Application.application.scoreWindow.y;
			so.flush();
		}
		
		public function restoreScoreWindowState() : void
		{
			if (so.data.scoreWindowX != null)
			{
				Application.application.scoreWindow.x = so.data.scoreWindowX;
				Application.application.scoreWindow.y = so.data.scoreWindowY;
			} 
		}

		// Graveyard properties
		public function saveGraveyardState() : void
		{
			so.data.graveyardX = Application.application.graveyardWindow.x;
			so.data.graveyardY = Application.application.graveyardWindow.y;
			so.flush();
		}
		
		public function restoreGraveyardState() : void
		{
			if (so.data.graveyardX != null)
			{
				Application.application.graveyardWindow.x = so.data.graveyardX;
				Application.application.graveyardWindow.y = so.data.graveyardY;
			} 
		}
		
		// Owner state
		public function saveOwnerState() : void
		{
			so.data.ownerVisible = Application.application.showOwner.selected; 
			so.flush();
		}
		
		public function restoreOwnerState() : void
		{
			if (so.data.ownerVisible != null)
				Application.application.showOwner.selected = so.data.ownerVisible; 
		}

		// Sound state
		public function saveSoundState() : void
		{
			so.data.soundEnabled = Application.application.enableSound.selected; 
			so.flush();
		}
		
		public function restoreSoundState() : void
		{
			if (so.data.soundEnabled != null)
				Application.application.enableSound.selected = so.data.soundEnabled; 
		}

		// Show info state
		public function saveShowInfoState() : void
		{
			so.data.showInfo = Application.application.showInfo.selected; 
			so.flush();
		}
		
		public function restoreShowInfoState() : void
		{
			if (so.data.showInfo != null)
				Application.application.showInfo.selected = so.data.showInfo; 
		}
	}
}

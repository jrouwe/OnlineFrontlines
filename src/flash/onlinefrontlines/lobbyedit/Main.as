package onlinefrontlines.lobbyedit
{
	import mx.core.Application;
	import mx.collections.ArrayCollection;
	import mx.events.ListEvent;
	import flash.net.URLLoader;
	import flash.net.URLRequest;
	import flash.events.Event;
	import flash.events.MouseEvent;
	import flash.events.IOErrorEvent;
	import onlinefrontlines.utils.*;
	import onlinefrontlines.lobby.*;
	import onlinefrontlines.lobbyedit.*;
	
	/*
	 * Main class for lobby editor
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
	public class Main
	{
		// The lobby with all tiles
		private static var lobbyConfig : LobbyConfig;
		
		// Selected country config id
		private static var selectedCountryConfigId : int = 0;
		
		// Enable counter
		private static var disableCount : int = 0;

		// Enable / disable user interface
		public static function enable(enable : Boolean) : void
		{
			// Filter out multiple enables / disables
			if (enable)
				disableCount--;
			else
				disableCount++;
			enable = disableCount == 0;	

			Application.application.greyOut.visible = !enable;
			Application.application.lobbyConfig.enabled = enable;
			Application.application.saveButton.enabled = enable;
			Application.application.brush1.enabled = enable;
			Application.application.brush7.enabled = enable;
			Application.application.brushFill.enabled = enable;
		}
	
		// Function to call when a tile is clicked
		private static function tilePressed(tile : LobbyTile) : void
		{
			// Setup easy accessor functions
			var getValue : Function = 
				function(x : int, y : int) : int 
				{ 
					return lobbyConfig.getTile(x, y).getCountryConfigId(); 
				};
			var	setValue : Function = 
				function(x : int, y : int) : void 
				{
					var t : LobbyTile = lobbyConfig.getTile(x, y);
					
					t.setCountryConfigId(selectedCountryConfigId);
		
					if (selectedCountryConfigId == 0)
						t.setState(LobbyTile.stateEditorEmpty);
					else
						t.setState(LobbyTile.stateEditorSameConfig);
				};
				
			// Do draw action
			if (Application.application.brush1.selected)
			{
				// Change 1 tile
				setValue(tile.locationX, tile.locationY);
			}
			else if (Application.application.brush7.selected)
			{
				// Change 7 tiles
				setValue(tile.locationX, tile.locationY);
				var n : Array = lobbyConfig.getNeighbours(tile.locationX, tile.locationY);
				for (var i : int = 0; i < n.length; ++i)
					setValue(n[i].x, n[i].y);
			}
			else
			{
				// Fill everything that has this image
				var prevValue : int = getValue(tile.locationX, tile.locationY);
				if (prevValue != selectedCountryConfigId)
				{					
					// Start with current tile
					var list : Array = new Array();
					list.push({ x : tile.locationX, y : tile.locationY });
					
					// Loop until the list is empty
					while (list.length > 0)
					{
						// Get element
						var elem : Object = list.pop();
						
						// Fill all tiles of the same type
						if (getValue(elem.x, elem.y) == prevValue)
						{
							// Set value
							setValue(elem.x, elem.y);
		
							// Add neighbours
							list = lobbyConfig.getNeighbours(elem.x, elem.y).concat(list);
						}
					}
				}
			}
		}
		
		// Called after finished saving the map data		
		private static function saveComplete(xmlRes : XML) : void
		{
			// Check success
			if (xmlRes.code != 0)
				Application.application.msgBox.showOk("Error saving lobby!", null);
			else
				Application.application.msgBox.showOk("Lobby saved successfully!", null);
		}
		
		// Called after saving finished with a failure
		private static function saveFailed() : void 
		{
			Application.application.msgBox.showOk("Could not reach server!", null);
		}

		// Update lobby config colors
		private static function updateColors() : void
		{
			for (var y : int = 0; y < lobbyConfig.sizeY; ++y)
				for (var x : int = 0; x < lobbyConfig.sizeX; ++x)
				{
					var tile : LobbyTile = lobbyConfig.getTile(x, y);
										
					tile.setVisible(true);
					
					if (tile.getCountryConfigId() == 0)
						tile.setState(LobbyTile.stateEditorEmpty);
					else if (tile.getCountryConfigId() == selectedCountryConfigId)
						tile.setState(LobbyTile.stateEditorSameConfig);
					else
						tile.setState(LobbyTile.stateEditorOtherConfig);
				}					
		}

		// Called after finished loading the lobby data
		private static function loadComplete(xmlRes : XML) : void 
		{
			// Check success
			if (xmlRes.code != 0)
			{
				Application.application.msgBox.show("Error loading lobby!");
				return;
			}
			
			// Get data
			lobbyConfig.loadFromNode(xmlRes.lobby);
			
			// Fill in country config list
			var collection : ArrayCollection = new ArrayCollection();
			collection.addItem({ label : "<none>", data : 0 });
			for each (var ccNode : XML in xmlRes.ccfg)
			{
				collection.addItem({ label: ccNode.name, data : ccNode.id });
			}
			Application.application.countryConfigs.dataProvider = collection;
			Application.application.countryConfigs.addEventListener(ListEvent.CHANGE,
				function(e : ListEvent) : void
				{					
					selectedCountryConfigId = Application.application.countryConfigs.selectedItem.data;
					updateColors();
				});
			
			// Link up save button
			Application.application.saveButton.addEventListener(MouseEvent.CLICK, 
				function(e : MouseEvent) : void
				{
					// Create request					
					var data : String = "lobbyId=" + Application.application.parameters.lobbyId
										+ "&tileCountryConfigIds=" + lobbyConfig.countryConfigIdsToString();

					// Process request
					Application.application.msgBox.show("Saving lobby...");
					Tools.processRequest("LobbyEditSave.do", data, saveComplete, saveFailed);
				});
				
			// Initialize editor
			Application.application.countryConfigs.selectedIndex = 0;			
			Application.application.brush1.selected = true;
			updateColors();
			
			// Remove loading popup
			Application.application.msgBox.close();
		}

		// Called after loading finished with a failure
		private static function loadFailed() : void 
		{
			Application.application.msgBox.show("Could not reach server!");
		}

		// Main loop for the editor
		public static function run() : void
		{
			// Setup enableUI function
			MsgBox.enableUI = enable;
			
			// Configure lobby object
			lobbyConfig = Application.application.lobbyConfig;
			lobbyConfig.createTiles(tilePressed, null, null, null, false);
			lobbyConfig.setAllTilesVisible(false);

			// Create request				
			var data : String; 
			if (Application.application.parameters.lobbyId != null)
				data = "lobbyId=" + Application.application.parameters.lobbyId;

			// Send load request
			Application.application.msgBox.show("Loading lobby...");
			Tools.processRequest("LobbyEditLoad.do", data, loadComplete, loadFailed);
		}
	}
}
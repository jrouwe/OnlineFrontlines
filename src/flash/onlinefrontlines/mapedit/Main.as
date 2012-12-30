package onlinefrontlines.mapedit
{
	import mx.core.Application;
	import flash.net.URLLoader;
	import flash.net.URLRequest;
	import flash.events.Event;
	import flash.events.MouseEvent;
	import flash.events.IOErrorEvent;
	import flash.display.BitmapData;
	import flash.utils.ByteArray;
	import com.adobe.images.PNGEncoder;
	import onlinefrontlines.utils.*;
	import onlinefrontlines.game.*;
	import onlinefrontlines.mapedit.*;
	
	/*
	 * Main class for map editor
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
		// The map with all tiles
		private static var mapConfig : MapConfig;
		
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
			Application.application.mapConfig.enabled = enable;
			Application.application.tileSelectionTool.enabled = enable;
			Application.application.ownerSelectionTool.enabled = enable;
			Application.application.saveButton.enabled = enable;
			Application.application.modeTiles.enabled = enable;
			Application.application.modeOwner.enabled = enable;
			Application.application.brush1.enabled = enable;
			Application.application.brush7.enabled = enable;
			Application.application.brushFill.enabled = enable;
		}
	
		// Function to call when a tile is clicked
		private static function tilePressed(tile : MapTile) : void
		{
			// Reset current selection
			tile.setSelectionImage(MapTile.tileSelectionNone);

			// Set up functions to get / set values and determine the new value
			var getValue : Function, setValue : Function, newValue : int;
			if (Application.application.modeTiles.selected)
			{
				getValue = function(x : int, y : int) : int { return mapConfig.getTile(x, y).getTerrainImage(); };
				setValue = function(x : int, y : int, v : int) : void { mapConfig.getTile(x, y).setTerrainImage(v); };
				newValue = Application.application.tileSelectionTool.selectedTile;
			}
			else
			{
				getValue = function(x : int, y : int) : int { return mapConfig.getTile(x, y).getOwnerFaction(); };
				setValue = function(x : int, y : int, v : int) : void { mapConfig.getTile(x, y).setOwnerFaction(v); };
				newValue = Application.application.ownerSelectionTool.selectedOwner;
			}
				
			// Do draw action
			if (Application.application.brush1.selected)
			{
				// Change 1 tile
				setValue(tile.locationX, tile.locationY, newValue);
			}
			else if (Application.application.brush7.selected)
			{
				// Change 7 tiles
				setValue(tile.locationX, tile.locationY, newValue);
				var n : Array = mapConfig.getNeighbours(tile.locationX, tile.locationY);
				for (var i : int = 0; i < n.length; ++i)
					setValue(n[i].x, n[i].y, newValue);
			}
			else
			{
				// Fill everything that has this image
				var prevValue : int = getValue(tile.locationX, tile.locationY);
				if (prevValue != newValue)
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
							setValue(elem.x, elem.y, newValue);
		
							// Add neighbours
							list = mapConfig.getNeighbours(elem.x, elem.y).concat(list);
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
				Application.application.msgBox.showOk("Error saving map!", null);
			else
				Application.application.msgBox.showOk("Map saved successfully!", null);
		}

		// Called after saving finished with a failure
		private static function saveFailed() : void 
		{
			Application.application.msgBox.showOk("Could not reach server!", null);
		}

		// Called after finished loading the map data
		private static function loadComplete(xmlRes : XML) : void 
		{
			// Check success
			if (xmlRes.code != 0)
			{
				var errorMap : Object = new Object();
				errorMap[-1] = "Communication error!";
				errorMap[-2] = "Map does not exist!";
				errorMap[-3] = "You don't have permission to edit this map!";
				errorMap[-4] = "Map already published!";
				Application.application.msgBox.show(errorMap[int(xmlRes.code)]);
				return;
			}
			
			// Load map data
			mapConfig.loadFromNode(xmlRes.map);

			// Link up mode button
			Application.application.modeTiles.addEventListener(MouseEvent.CLICK, 
				function(e : MouseEvent) : void
				{
					Application.application.mapConfig.ownerIconRoot.visible = false;
					Application.application.tileSelectionTool.visible = true;
					Application.application.ownerSelectionTool.visible = false;
				});
			
			Application.application.modeOwner.addEventListener(MouseEvent.CLICK, 
				function(e : MouseEvent) : void
				{	
					Application.application.mapConfig.ownerIconRoot.visible = true;
					Application.application.tileSelectionTool.visible = false;
					Application.application.ownerSelectionTool.visible = true;
				});
			
			// Link up save button
			Application.application.saveButton.addEventListener(MouseEvent.CLICK, 
				function(e : MouseEvent) : void
				{
					// Show message box
					Application.application.msgBox.show("Saving map...");

					// Create image of terrain
					var bitmapData : BitmapData = mapConfig.getTerrainBitmap();
					
    				// Encode to PNG
				    var imageData : ByteArray = PNGEncoder.encode(bitmapData);
				    
				    // Encode to hex
					var encodedData : String = Tools.byteArrayToHexString(imageData);
			    
					// Create request					
					var data : String = "mapId=" + Application.application.parameters.mapId
										+ "&tileImageNumbers=" + escape(mapConfig.tileImageNumbersToString())
										+ "&tileOwners=" + escape(mapConfig.tileOwnersToString())
										+ "&mapImage=" + encodedData;

					// Process request
					Tools.processRequest("MapSave.do", data, saveComplete, saveFailed);
				});
			
			// Remove loading popup
			Application.application.msgBox.close();

			// Show help
			HelpBalloon.queueCenterOnDisplayObject(Application.application.tileSelectionTool, "Select a tile to paint here", true);
			HelpBalloon.queueCenterOnDisplayObject(Application.application.mapConfig, "Click here to paint the tile", true);
			HelpBalloon.queueCenterOnDisplayObject(Application.application.modeOwner, "Select this mode to paint initial tile ownership (this determines the areas where you can deploy)", true);
			HelpBalloon.queueCenterOnDisplayObject(Application.application.brush7, "These determine the amount of tiles you paint at the same time.", true);
			HelpBalloon.queueCenterOnDisplayObject(Application.application.saveButton, "Click here to save your map", true);
		}

		// Called after loading finished with a failure
		private static function loadFailed() : void 
		{
			Application.application.msgBox.show("Could not reach server!");
		}

		// Main loop for the editor
		public static function run() : void
		{
			// Setup edit mode
			Application.application.modeTiles.selected = true;
			Application.application.ownerSelectionTool.visible = false;
			Application.application.brush1.selected = true;
			
			// Setup enableUI function
			MsgBox.enableUI = enable;
			
			// Configure map object
			mapConfig = Application.application.mapConfig;
			mapConfig.createTiles(tilePressed, null, null, null, false);

			// Create request				
			var data : String; 
			if (Application.application.parameters.mapId != null)
				data = "mapId=" + Application.application.parameters.mapId;

			// Send load request
			Application.application.msgBox.show("Loading map...");
			Tools.processRequest("MapLoad.do", data, loadComplete, loadFailed);
		}
	}
}
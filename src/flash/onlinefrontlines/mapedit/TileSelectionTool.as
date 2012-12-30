package onlinefrontlines.mapedit
{
	import mx.core.Application;
	import mx.controls.Image;
	import mx.containers.Canvas;
	import flash.events.MouseEvent;
	import onlinefrontlines.utils.*;
	import onlinefrontlines.game.*;
	
	/*
	 * Tool to paint tiles
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
	public class TileSelectionTool extends Canvas
	{
		// Delta between tiles
		private static var toolTileDY : int = 30;
		
		// Currently selected tile
		public var selectedTile : int = 1;
		
		// The tiles
		private var tiles : Array = new Array();		
		
		// Create tool window
		public function TileSelectionTool() : void
		{
			// No special effect when disabled
			setStyle("disabledOverlayAlpha", 0);

			var terrainRoot : Image = new Image();
			terrainRoot.height = TerrainImage.getNumTiles() * toolTileDY; // Force scroll bar
			addChild(terrainRoot);
			var ownerRoot : Image = new Image();
			ownerRoot.alpha = 0.4;
			addChild(ownerRoot);
			var ownerIconRoot : Image = new Image();
			ownerIconRoot.visible = false;
			addChild(ownerIconRoot);
			var selectionRoot : Image = new Image();
			addChild(selectionRoot);

			// Add tiles	
			for (var i : int = 1; i <= TerrainImage.getNumTiles(); ++i)
			{					
				// Create tile
				var t : MapTile = new MapTile(0, i, 0, (i - 1) * toolTileDY, terrainRoot, ownerRoot, ownerIconRoot, selectionRoot);
	
				// Select correct image
				t.setTerrainImage(i);
				if (i == selectedTile)
					t.setSelectionImage(MapTile.tileSelectionSelected);
				else
					t.setSelectionImage(MapTile.tileSelectionNone);
	
				// Set mouse handler
				t.tilePressed = 
					function(tile : MapTile) : void
					{
						// Select new tile
						tiles[selectedTile].setSelectionImage(MapTile.tileSelectionNone);
						tile.setSelectionImage(MapTile.tileSelectionSelected);
						selectedTile = tile.locationY;
					};
					
				tiles[i] = t;
			}
		}
		
		// Enable / disable the tool
		public function enable(enable : Boolean) : void
		{
			for (var i : int = 1; i <= TerrainImage.getNumTiles(); ++i)
				tiles[i].sel.enabled = enable;
		}		
	}
}

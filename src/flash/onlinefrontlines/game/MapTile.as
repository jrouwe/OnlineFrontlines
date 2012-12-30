package onlinefrontlines.game
{
	import mx.core.BitmapAsset;
	import mx.controls.Image;
	import flash.display.Sprite;
	import flash.display.Shape;
	import flash.display.Graphics;
	import onlinefrontlines.utils.*;
	
	/*
	 * A tile on the hexagon game grid
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
	public class MapTile extends HexagonTile
	{
		// Selection tile states
		public static var tileSelectionNone : int = 1;
		public static var tileSelectionSelected : int = 2;
		public static var tileSelectionAttack : int = 3;
		public static var tileSelectionMove : int = 4;
		public static var tileSelectionEnterBase : int = 5;
		public static var tileSelectionMenu : int = 6;
		public static var tileSelectionLand : int = 7;
		public static var tileSelectionBoat : int = 8;
		public static var tileSelectionForbidden : int = 9;
		public static var tileSelectionEnterBaseConfirm : int = 10;
		
		// Selection images
		[Embed(source='../../assets/tile_selection/tile_selected.png')]
		private static var tileSelectionSelectedClass : Class;
		[Embed(source='../../assets/tile_selection/tile_attack.png')]
		private static var tileSelectionAttackClass : Class;
		[Embed(source='../../assets/tile_selection/tile_move.png')]
		private static var tileSelectionMoveClass : Class;
		[Embed(source='../../assets/tile_selection/tile_enter_base.png')]
		private static var tileSelectionEnterBaseClass : Class;
		[Embed(source='../../assets/tile_selection/tile_menu.png')]
		private static var tileSelectionMenuClass : Class;
		[Embed(source='../../assets/tile_selection/tile_land.png')]
		private static var tileSelectionLandClass : Class;
		[Embed(source='../../assets/tile_selection/tile_boat.png')]
		private static var tileSelectionBoatClass : Class;
		[Embed(source='../../assets/tile_selection/tile_enter_base_confirm.png')]
		private static var tileSelectionEnterBaseConfirmClass : Class;

		[Embed(source='../../assets/tile_selection/tile_none_hi.png')]
		private static var tileSelectionNoneHiClass : Class;
		[Embed(source='../../assets/tile_selection/tile_selected_hi.png')]
		private static var tileSelectionSelectedHiClass : Class;
		[Embed(source='../../assets/tile_selection/tile_attack_hi.png')]
		private static var tileSelectionAttackHiClass : Class;
		[Embed(source='../../assets/tile_selection/tile_move_hi.png')]
		private static var tileSelectionMoveHiClass : Class;
		[Embed(source='../../assets/tile_selection/tile_enter_base_hi.png')]
		private static var tileSelectionEnterBaseHiClass : Class;
		[Embed(source='../../assets/tile_selection/tile_menu_hi.png')]
		private static var tileSelectionMenuHiClass : Class;
		[Embed(source='../../assets/tile_selection/tile_land_hi.png')]
		private static var tileSelectionLandHiClass : Class;
		[Embed(source='../../assets/tile_selection/tile_boat_hi.png')]
		private static var tileSelectionBoatHiClass : Class;
		[Embed(source='../../assets/tile_selection/tile_forbidden_hi.png')]
		private static var tileSelectionForbiddenHiClass : Class;
		[Embed(source='../../assets/tile_selection/tile_enter_base_confirm_hi.png')]
		private static var tileSelectionEnterBaseConfirmHiClass : Class;

		// Selection array
		private static var tileSelectionClasses : Array = 
		[
			[
				null,
				tileSelectionSelectedClass,
				tileSelectionAttackClass,
				tileSelectionMoveClass,
				tileSelectionEnterBaseClass,
				tileSelectionMenuClass,
				tileSelectionLandClass,
				tileSelectionBoatClass,
				null,
				tileSelectionEnterBaseConfirmClass,
			],
			[
				tileSelectionNoneHiClass,
				tileSelectionSelectedHiClass,
				tileSelectionAttackHiClass,
				tileSelectionMoveHiClass,
				tileSelectionEnterBaseHiClass,
				tileSelectionMenuHiClass,
				tileSelectionLandHiClass,
				tileSelectionBoatHiClass,
				tileSelectionForbiddenHiClass,
				tileSelectionEnterBaseConfirmHiClass,
			]
		];

		// Root assets
		private var terrainRoot : Image;
		private var overlayRoot : Image;
		private var ownerIconRoot : Image;
		private var selectionRoot : Image;
		
		// Current tile
		private var terrainAsset : AnimatedBitmap;
		private var terrainNumber : int;

		// Owner states
		[Embed(source='../../assets/tile_overlay/tile_overlay_red.png')]
		private static var overlayRedClass : Class;
		[Embed(source='../../assets/tile_overlay/tile_overlay_blue.png')]
		private static var overlayBlueClass : Class;
		[Embed(source='../../assets/tile_overlay/tile_overlay_red_icon.png')]
		private static var overlayRedIconClass : Class;
		[Embed(source='../../assets/tile_overlay/tile_overlay_blue_icon.png')]
		private static var overlayBlueIconClass : Class;
		[Embed(source='../../assets/tile_overlay/tile_overlay_red_fogged.png')]
		private static var overlayRedFoggedClass : Class;
		[Embed(source='../../assets/tile_overlay/tile_overlay_blue_fogged.png')]
		private static var overlayBlueFoggedClass : Class;
		[Embed(source='../../assets/tile_overlay/tile_overlay_none_fogged.png')]
		private static var overlayNoneFoggedClass : Class;

		// Colored overlay states
		private static var overlayClasses : Array = 
		[
			[	// Not fogged
				null,
				overlayRedClass,
				overlayBlueClass
			],
			[	// Fogged
				overlayNoneFoggedClass,
				overlayRedFoggedClass,
				overlayBlueFoggedClass
			]
		];
		
		private static var ownerIconClasses : Array =
		[
			null,
			overlayRedIconClass,
			overlayBlueIconClass
		];

		// Overlay
		private var overlayAsset : BitmapAsset;
		private var ownerIconAsset : BitmapAsset;
		private var ownerFaction : int = Faction.none;
		private var showFog : Boolean = false;
		
		// Selection
		private var selectionSprite : Sprite;
		private var hitAreaSprite : Sprite;
		private var selectionAsset : BitmapAsset;
		private var selectionNumber : int;
		private var highlighted : Boolean;
			
		// Constructor
		public function MapTile(locationX : int, locationY : int, imageX : int, imageY : int, terrainRoot : Image, overlayRoot : Image, ownerIconRoot : Image, selectionRoot : Image) : void
		{
			// Construct base class
			super(locationX, locationY, imageX, imageY);
			
			// Store roots
			this.terrainRoot = terrainRoot;
			this.overlayRoot = overlayRoot;
			this.ownerIconRoot = ownerIconRoot;
			this.selectionRoot = selectionRoot;
			
			// Create hit area			
			hitAreaSprite = new Sprite();
			hitAreaSprite.x = imageX;
			hitAreaSprite.y = imageY;
			hitAreaSprite.visible = false;
			hitAreaSprite.addChild(createTileShape(0xFFFFFF));
			selectionRoot.addChild(hitAreaSprite);
			
			// Create selection
			selectionSprite = new Sprite();
			selectionSprite.x = imageX;
			selectionSprite.y = imageY;
			selectionSprite.hitArea = hitAreaSprite;
			selectionRoot.addChild(selectionSprite);
			addListeners(selectionSprite);
			
			// Set initial state
			setTerrainImage(1);
			setOwnerFaction(Faction.none);
			setShowFog(false);
			setSelectionImage(tileSelectionNone);
			setHighlighted(false);
		}
		
		// Get / set the terrain image
		public function setTerrainImage(number : int) : void
		{
			terrainNumber = number;
			
			var idx : int = -1;
			if (terrainAsset != null)
			{
				idx = terrainRoot.getChildIndex(terrainAsset);
				terrainRoot.removeChild(terrainAsset);
				terrainAsset = null;
			}
				
			terrainAsset = TerrainImage.createStaticBitmap(number);
			terrainAsset.x = imageX;
			terrainAsset.y = imageY;
			if (idx >= 0)
				terrainRoot.addChildAt(terrainAsset, idx);
			else
				terrainRoot.addChild(terrainAsset);
		}			
		
		public function getTerrainImage() : int
		{
			return terrainNumber;
		}
		
		public function isTerrainImageAnimated() : Boolean
		{
			return terrainAsset.getNumFrames() > 1;
		}
		
		public function setTerrainImageFrame(frame : int) : void
		{
			terrainAsset.setFrame(frame);
		}

		// Get / set the owner image
		public function setOwnerFaction(faction : int) : void
		{
			if (ownerFaction == faction)
				return;
			ownerFaction = faction;
			
			updateColoredOverlay();
			updateOwnerIcon();
		}			
		
		public function getOwnerFaction() : int
		{
			return ownerFaction;
		}
		
		// Get set show fog
		public function setShowFog(show : Boolean) : void
		{
			if (showFog == show)
				return;
			showFog = show;
			
			updateColoredOverlay();
		}
		
		public function getShowFog() : Boolean
		{
			return showFog;
		}

		// Update colored overlay image
		private function updateColoredOverlay() : void
		{
			if (overlayAsset != null)
			{
				overlayRoot.removeChild(overlayAsset);
				overlayAsset = null;
			}
			
			var imageClass : Class = overlayClasses[showFog? 1 : 0][Faction.toArmy(ownerFaction) + 1];
			if (imageClass != null)
			{
				overlayAsset = new imageClass();
				overlayAsset.x = imageX;
				overlayAsset.y = imageY;
				overlayRoot.addChild(overlayAsset);
			}
		}
		
		// Update owner icon image
		private function updateOwnerIcon() : void
		{			
			if (ownerIconAsset != null)
			{
				ownerIconRoot.removeChild(ownerIconAsset);
				ownerIconAsset = null;
			}
			
			var imageClass : Class = ownerIconClasses[Faction.toArmy(ownerFaction) + 1];
			if (imageClass != null)
			{
				ownerIconAsset = new imageClass();
				ownerIconAsset.x = imageX;
				ownerIconAsset.y = imageY;
				ownerIconRoot.addChild(ownerIconAsset);
			}				
		}
		
		// Get / set the selection image
		public function setSelectionImage(number : int) : void
		{
			if (selectionNumber == number)
				return;
			selectionNumber = number;

			updateSelectionImage();
		}
		
		public function getSelectionImage() : int
		{
			return selectionNumber;
		}
		
		// Set highlight
		public override function setHighlighted(highlighted : Boolean) : void
		{
			if (this.highlighted == highlighted)
				return;
			this.highlighted = highlighted;
			
			updateSelectionImage();
		}		
		
		private function updateSelectionImage() : void
		{			
			if (selectionAsset != null)
			{
				selectionSprite.removeChild(selectionAsset);
				selectionAsset = null;
			}
				
			var imageClass : Class = tileSelectionClasses[highlighted? 1 : 0][selectionNumber - 1];
			if (imageClass != null)
			{
				selectionAsset = new imageClass();
				selectionSprite.addChild(selectionAsset);
			}
		}			
		
		// Create a tile shaped graphic
		public static function createTileShape(color : int) : Shape
		{
			var s : Shape = new Shape();
			var g : Graphics = s.graphics;
			g.beginFill(color, 1);
			g.moveTo(16, 4);
			g.moveTo(19, 4);
			g.lineTo(34, 11);
			g.lineTo(34, 24);
			g.lineTo(19, 31);
			g.lineTo(16, 31);
			g.lineTo(0, 23);
			g.lineTo(0, 11);
			g.lineTo(16, 4);
			g.endFill();
			return s;
		}
	}
}
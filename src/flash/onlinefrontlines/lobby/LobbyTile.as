package onlinefrontlines.lobby
{
	import mx.core.BitmapAsset;
	import mx.controls.Image;
	import flash.display.Sprite;
	import flash.display.Shape;
	import flash.display.Graphics;
	import onlinefrontlines.utils.*;
	
	/*
	 * One country on the grid
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
	public class LobbyTile extends HexagonTile
	{
		// States
		public static var stateRed : int = 0;
		public static var stateBlue : int = 1;
		public static var stateGameInProgressRed : int = 2;
		public static var stateGameInProgressBlue : int = 3;
		public static var stateDefendableRed : int = 4;
		public static var stateDefendableBlue : int = 5;
		public static var stateAttackableRed : int = 6;
		public static var stateAttackableBlue : int = 7;
		public static var stateIAttackedWaitingForDefenderRed : int = 8;
		public static var stateIAttackedWaitingForDefenderBlue : int = 9;
		public static var stateOtherAttackedICanDefendRed : int = 10;
		public static var stateOtherAttackedICanDefendBlue : int = 11;
		public static var stateDefendedByMeRed : int = 12;
		public static var stateDefendedByMeBlue : int = 13;
		public static var stateUnusableRed : int = 14;
		public static var stateUnusableBlue : int = 15;
		public static var stateCapturePointRed : int = 16;
		public static var stateCapturePointBlue : int = 17;
		public static var stateDefendedByOtherRed : int = 18;
		public static var stateDefendedByOtherBlue : int = 19;
		public static var stateEditorEmpty : int = 20;
		public static var stateEditorSameConfig : int = 21;
		public static var stateEditorOtherConfig : int = 22; 
		public static var stateDefendableFriendlyGameRed : int = 23;
		public static var stateDefendableFriendlyGameBlue : int = 24;
		
		public static var highlightOffset : int = 25;
		
		// Ownership
		public static var ownershipNone : int = 0;
		public static var ownershipMe : int = 1;
		public static var ownershipFriend : int = 2;
		public static var ownershipOther : int = 3;
		
		// Hexagon state
		[Embed(source='../../assets/lobby/hexagon_state.png')]
		private static var hexagonStateClass : Class;
		private static var hexagonStateData : AnimatedBitmapData = new AnimatedBitmapData(hexagonStateClass, highlightOffset, 2);
		
		// Hexagon ownership
		[Embed(source='../../assets/lobby/hexagon_ownership.png')]
		private static var hexagonOwnershipClass : Class;
		private static var hexagonOwnershipData : AnimatedBitmapData = new AnimatedBitmapData(hexagonOwnershipClass, 4, 1);

		// Root assets
		private var ownershipRoot : Image;
		private var stateRoot : Image;
		private var selectionRoot : Image;
		
		// Current state
		private var stateBitmap : AnimatedBitmap;
		private var currentState : int;
		private var currentHighlighted : Boolean;

		// Ownership		
		private var ownershipBitmap : AnimatedBitmap;

		// Selection
		private var selectionSprite : Sprite;
		private var hitAreaSprite : Sprite;
		private var highlighted : Boolean;
		
		// Associated country config id
		private var countryConfigId : int;
			
		// Constructor
		public function LobbyTile(locationX : int, locationY : int, imageX : int, imageY : int, ownershipRoot : Image, stateRoot : Image, selectionRoot : Image) : void
		{
			// Construct base class
			super(locationX, locationY, imageX, imageY);
			
			// Store roots
			this.ownershipRoot = ownershipRoot;
			this.stateRoot = stateRoot;
			this.selectionRoot = selectionRoot;
			
			// Create ownership
			ownershipBitmap = new AnimatedBitmap(hexagonOwnershipData);
			ownershipBitmap.x = imageX;
			ownershipBitmap.y = imageY;
			ownershipRoot.addChild(ownershipBitmap);

			// Create state
			stateBitmap = new AnimatedBitmap(hexagonStateData);
			stateBitmap.x = imageX;
			stateBitmap.y = imageY;
			stateRoot.addChild(stateBitmap);

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
		}
		
		// Get / set country config id
		public function setCountryConfigId(id : int) : void
		{
			countryConfigId = id;
		}
		
		public function getCountryConfigId() : int
		{
			return countryConfigId;
		}
		
		// Set visible
		public function setVisible(visible : Boolean) : void
		{
			stateBitmap.visible = visible;
			ownershipBitmap.visible = visible;
			selectionSprite.visible = visible;
		}
		
		// Get / set state
		public function setState(state : int) : void
		{
			this.currentState = state;
			stateBitmap.setFrame(currentState + (currentHighlighted? highlightOffset : 0));
		}
		
		public function getState() : int
		{
			return currentState;
		}
		
		// Get / set ownership
		public function setOwnership(ownership : int) : void
		{
			ownershipBitmap.setFrame(ownership);
		}
		
		public function getOwnership() : int
		{
			return ownershipBitmap.getFrame();
		}
		
		// Set highlight
		public override function setHighlighted(highlighted : Boolean) : void
		{
			this.currentHighlighted = highlighted;
			stateBitmap.setFrame(currentState + (currentHighlighted? highlightOffset : 0));
			
			// Make sure this image is on top when it is highlighted
			if (highlighted)
			{
				stateRoot.removeChild(stateBitmap);
				stateRoot.addChild(stateBitmap);
			}			
		}		
		
		// Create a tile shaped graphic
		public static function createTileShape(color : int) : Shape
		{
			var s : Shape = new Shape();
			var g : Graphics = s.graphics;
			g.beginFill(color, 1);
			g.moveTo(0, 5);
			g.moveTo(7, 0);
			g.lineTo(12, 0);
			g.lineTo(19, 5);
			g.lineTo(19, 14);
			g.lineTo(12, 19);
			g.lineTo(7, 19);
			g.lineTo(0, 14);
			g.lineTo(0, 5);
			g.endFill();
			return s;
		}
		
		// Get hexagon state
		public static function getHexagonStateBitmap(x : int, y : int, frame : int) : AnimatedBitmap
		{
			var b : AnimatedBitmap = new AnimatedBitmap(hexagonStateData);
			b.setFrame(frame);
			b.x = x;
			b.y = y;
			return b;
		}
	}
}
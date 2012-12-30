package onlinefrontlines.game
{
	import mx.core.Application;
	import mx.core.BitmapAsset;
	import mx.controls.Label;
	import mx.controls.Text;
	import mx.controls.Image;
	import mx.containers.Canvas;
	import flash.events.Event;
	import flash.geom.Rectangle;
	import flash.display.Bitmap;
	import flash.utils.*;
	import onlinefrontlines.utils.*;
	import onlinefrontlines.game.*;
	
	/*
	 * Window that displays an attack
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
	public class BattleView extends ToolWindow
	{
		// Application defined enable / disable UI function
		public static var enableUI : Function;

		// Background image
		[Embed(source='../../assets/battle_view/battle_view.png')]
		private static var backgroundClass : Class;

		// Shadows
		[Embed(source='../../assets/battle_view/shadow_planes.png')]
		private static var shadowPlanes : Class;		

		// Offset to place shadow relative to plane
		private static var unitShadowOffset : int = 20;

		// Explosion animation
		[Embed(source='../../assets/battle_view/short_explosion.png')]
		private static var explosionAnimationClass : Class;		
		private static var explosionAnimationData : AnimatedBitmapData = new AnimatedBitmapData(explosionAnimationClass, 3);

		// Explosion sound
		[Embed(source="../../assets/sounds/short_explosion.mp3")]
		private static var explosionSoundClass : Class;
		
		// Base parts
		[Embed(source='../../assets/battle_view/base.png')]
		private static var basePartsClass : Class;
		private static var basePartsData : Array = 
		[
			{ x : -11, y : -8, ok : Tools.subBitmap(basePartsClass, 7, 12, 56, 19), destroyed : Tools.subBitmap(basePartsClass, 7, 43, 56, 19) },
			{ x : -11, y : 24, ok : Tools.subBitmap(basePartsClass, 77, 4, 56, 19), destroyed : Tools.subBitmap(basePartsClass, 77, 35, 56, 19) },
			{ x : -11, y : 7, ok : Tools.subBitmap(basePartsClass, 164, 7, 11, 21), destroyed : Tools.subBitmap(basePartsClass, 164, 38, 11, 21) },
			{ x : 34, y : 7, ok : Tools.subBitmap(basePartsClass, 175, 7, 11, 21), destroyed : Tools.subBitmap(basePartsClass, 175, 38, 11, 21) }
		];		

		// States
		private static var stateEntering : int = 1;
		private static var stateLeftFiring : int = 2;
		private static var stateLeftBombing : int = 3;
		private static var stateRightFiring : int = 4;
		private static var stateRightBombing : int = 5;
		private static var stateDone : int = 6;
		
		// Battle view arena properties
		private static var arenaX : int = 9;
		private static var arenaY : int = 7;
		private static var arenaSizeX : int = 169;
		private static var arenaSizeY : int = 115;
		
		// State entering properties
		private static var unitEnteringDistance : int = 100;
		private static var unitEnteringTime : int = 500;
		private static var unitEnteringDelay : int = 500;
		
		// State taking damage properties
		private static var takingDamageTimePerArmourPoint : int = 75;
		
		// State bombing properties
		private static var unitBombingTime : int = 1000;
		private static var unitBombingOffScreenTime : int = 500;
		
		// Positions to place the units
		private static var unitPositions : Array =
		[
			[
				{ x : 1, y : 2 },
				{ x : 0, y : 1 },
				{ x : 0, y : 3 },
				{ x : 1, y : 1 },
				{ x : 1, y : 3 }
			],
			[
				{ x : 4, y : 2 },
				{ x : 4, y : 1 },
				{ x : 4, y : 3 },
				{ x : 3, y : 1 },
				{ x : 3, y : 3 }
			]
		];
		
		// UI components
		private var armourText : Array = [ null, null ]; // of Label
		private var leftModifier : Label;
		private var rightModifier : Label;
		private var exitCallback : Function;
		private var terrainMask : Image;
		private var terrainImage : Image;
		private var unitMask : Array = [ null, null ]; // of Image
		private var unitImage : Array = [ null, null ]; // of Image
		private var unitBitmaps : Array = [ new Array(), new Array() ]; // Array of Array of BitmapAsset
		private var unitShadows : Array = [ new Array(), new Array() ]; // Array of Array of BitmapAsset
		private var baseParts : Array = [ new Array(), new Array() ]; // Array of Array of BitmapAsset
		private var muzzleFlashes : Array = new Array(); // of BitmapAsset
		private var activeExplosions : Array = new Array(); // of AnimatedBitmap
		
		// Current state
		private var state : int;
		private var stateStartTime : int;
		private var takingDamageStartTime : int = -1;
		private var oldUnitState : Array = [ null, null ]; // of UnitState
		private var unitState : Array = [ null, null ]; // of UnitState
		
		// Constructor
		public function BattleView()
		{
			// Construct base class
			super(185, 152);
			
			// Make sure no images leave this canvas
			clientCanvas.clipContent = true;
			clientCanvas.horizontalScrollPolicy = "off";
			clientCanvas.verticalScrollPolicy = "off";

			// Add background
			var background : Image = new Image();
			background.source = backgroundClass;
			clientCanvas.addChild(background);	

			// Add text fields
			armourText[0] = new Label();
			armourText[0].x = 10;
			armourText[0].y = 8;
			armourText[0].width = 70;
			armourText[0].styleName = "battleViewArmour";
			armourText[0].setStyle("textAlign", "center");
			armourText[0].selectable = false;
			clientCanvas.addChild(armourText[0]);
			
			armourText[1] = new Label();
			armourText[1].x = 108;
			armourText[1].y = 8;
			armourText[1].width = 70;
			armourText[1].styleName = "battleViewArmour";
			armourText[1].setStyle("textAlign", "center");
			armourText[1].selectable = false;
			clientCanvas.addChild(armourText[1]);
			
			leftModifier = new Label();
			leftModifier.x = 10;
			leftModifier.y = 128;
			leftModifier.width = 80;
			leftModifier.styleName = "battleViewModifier";
			leftModifier.setStyle("textAlign", "left");
			leftModifier.selectable = false;
			clientCanvas.addChild(leftModifier);
			
			rightModifier = new Label();
			rightModifier.x = 95;
			rightModifier.y = 128;
			rightModifier.width = 80;
			rightModifier.styleName = "battleViewModifier";
			rightModifier.setStyle("textAlign", "right");
			rightModifier.selectable = false;
			clientCanvas.addChild(rightModifier);

			// Masks
			terrainMask = createMask();
			unitMask[0] = createMask();
			unitMask[1] = createMask();
			
			// Initially hide
			visible = false;
		}
		
		// Show menu
		public function show(gameState : GameState, oldStateSelectedUnit : UnitState, selectedUnit : UnitState, oldStateTargettedUnit : UnitState, targettedUnit : UnitState, exitCallback : Function) : void
		{
			// Close previous menu
			close();	
			
			// Disable user interface
			enableUI(false);

			// Store parameters
			this.oldUnitState[0] = oldStateSelectedUnit;
			this.oldUnitState[1] = oldStateTargettedUnit;
			this.unitState[0] = selectedUnit;
			this.unitState[1] = targettedUnit;
			this.exitCallback = exitCallback;

			// Place the window away from the action
			var s : Object = gameState.mapConfig.getTileLocation(oldStateSelectedUnit.locationX, oldStateSelectedUnit.locationY);			
			var t : Object = gameState.mapConfig.getTileLocation(oldStateTargettedUnit.locationX, oldStateTargettedUnit.locationY);					
			var sr : Rectangle = new Rectangle(s.x, s.y, 35, 31);
			var tr : Rectangle = new Rectangle(t.x, t.y, 35, 31);
			var ur : Rectangle = sr.union(tr);
			if (ur.x > Application.application.width - ur.x - ur.width)
				x = ur.x - width;
			else
				x = ur.x + ur.width;
			if (ur.y > Application.application.height - ur.y - ur.height)
				y = ur.y - height;
			else
				y = ur.y + ur.height;

			// Get terrain types	
			var selectedUnitTerrain : TerrainConfig = gameState.getAttackTerrain(oldStateSelectedUnit);
			var targettedUnitTerrain : TerrainConfig = gameState.getAttackTerrain(oldStateTargettedUnit);
			
			// Create terrain
			var leftTile : int = gameState.mapConfig.getTile(oldStateSelectedUnit.locationX, oldStateSelectedUnit.locationY).getTerrainImage();
			var rightTile : int = gameState.mapConfig.getTile(oldStateTargettedUnit.locationX, oldStateTargettedUnit.locationY).getTerrainImage();
			createTerrain(leftTile, rightTile);
			
			// Create units
			createUnit(0);
			createUnit(1);
	
			// Fill in texts
			armourText[0].text = oldStateSelectedUnit.armour;
			armourText[1].text = oldStateTargettedUnit.armour;
			this.leftModifier.text = selectedUnitTerrain.name + ": " + Tools.modifierToString(selectedUnitTerrain.strengthModifier) + "%";
			this.rightModifier.text = targettedUnitTerrain.name + ": " + Tools.modifierToString(targettedUnitTerrain.strengthModifier) + "%";
			
			// Add listeners
			Application.application.addEventListener(Event.ENTER_FRAME, onEnterFrame);
			
			// Initial state
			switchState(stateEntering);
				
			// Show
			visible = true;
		}
		
		// Close callback
		public override function onClosed() : void
		{
			close();
			exitCallback();
		}

		// Close the window
		public function close() : void
		{
			if (visible)
			{
				// Get rid of stuff
				clientCanvas.removeChild(terrainImage);
				for (var i : int = 0; i < 2; ++i)
				{
					clientCanvas.removeChild(unitImage[i]);
					unitBitmaps[i] = new Array();
					unitShadows[i] = new Array();
					baseParts[i] = new Array();
				}
				activeExplosions = new Array();
				muzzleFlashes = new Array();
		
				// Remove listeners		
				Application.application.removeEventListener(Event.ENTER_FRAME, onEnterFrame);
	
				enableUI(true);
				
				visible = false;
			}
		}

		// Get tile location
		private function getTileLocation(x : int, y : int) : Object
		{
			return { x : -34 + x * 34 + ((y & 1) == 0? 17 : 34), y : 27 + y * 19 };
		}
		
		// Move asset to tile location
		private function moveTo(asset : Bitmap, x : int, y : int) : void
		{
			var l : Object = getTileLocation(x, y);
			asset.x = l.x;
			asset.y = l.y;
		}
		
		// Create mask for image in battle view
		private function createMask() : Image
		{
			var mask : Image = new Image();
			mask.source = Tools.createRectangle(arenaSizeX, arenaSizeY);
			mask.visible = false;
			mask.x = arenaX;
			mask.y = arenaY;
			clientCanvas.addChild(mask);
			
			return mask;
		}
		
		// Create terrain background
		private function createTerrain(leftTile : int, rightTile : int) : void
		{
			// Get corresponding terrain tiles
			var leftEdgeTerrainTile : int = TerrainConfig.allTileMap[leftTile].edgeTerrainImageNumber;
			var rightEdgeTerrainTile : int = TerrainConfig.allTileMap[rightTile].edgeTerrainImageNumber;
			var leftOpenTerrainTile : int = TerrainConfig.allTileMap[leftTile].openTerrainImageNumber;
			var rightOpenTerrainTile : int = TerrainConfig.allTileMap[rightTile].openTerrainImageNumber;

			// New terrain image
			terrainImage = new Image();
			terrainImage.x = arenaX;
			terrainImage.y = arenaX;
			terrainImage.width = arenaSizeX;
			terrainImage.height = arenaSizeY;
			terrainImage.mask = terrainMask;
			clientCanvas.addChild(terrainImage);		
			
			// Create tiles
			for (var y : int = 0; y < 5; ++y)
				for (var x : int = 0; x < 6; ++x)
				{
					// Determine tile image
					var image : int;
					if (y == 0 || y == 4 || (y == 2 && (x == 0 || x == 5)))
						image = x < 3? leftEdgeTerrainTile : rightEdgeTerrainTile;
					else
						image = x < 3? leftOpenTerrainTile : rightOpenTerrainTile;
					
					// Create tile
					var a : AnimatedBitmap = TerrainImage.createAnimatedBitmap(image);
					moveTo(a, x, y);
					terrainImage.addChild(a);
				}
		}
		
		// Create image for a unit group
		private function createUnit(index : int) : void
		{
			var unitStateBefore : UnitState = oldUnitState[index];
			var unitStateAfter : UnitState = unitState[index];
			var props : Object = UnitGraphics.getUnitProperties(unitStateBefore);

			// Create image
			var image : Image = new Image();
			unitImage[index] = image;
			image.x = arenaX;
			image.y = arenaY;
			image.width = arenaSizeX;
			image.height = arenaSizeY;
			image.mask = unitMask[index];
			clientCanvas.addChild(image);
			
			var i : int;
			var b : BitmapAsset;

			// Add shadows
			var numBefore : int = Math.ceil((unitStateBefore.armour * props.battleViewMaxUnits) / unitStateBefore.unitConfig.maxArmour);
			var positions : Array = unitPositions[index];
			if (unitStateBefore.unitConfig.unitClass == UnitClass.air)
			{
				for (i = 0; i < numBefore; ++i)
				{
					b = new shadowPlanes();
					moveTo(b, positions[i].x, positions[i].y);
					b.x += 8;
					b.y += 11 + unitShadowOffset;
					image.addChild(b);
					unitShadows[index].push(b);
				}
				
				// Move image up so that shadows are on the ground
				image.y -= unitShadowOffset;
			}

			// Add units
			var imageClass : Class = UnitGraphics.getImageClass(unitStateBefore.faction, unitStateBefore.unitConfig.imageNumber, index == 1);
			for (i = 0; i < numBefore; ++i)
			{
				b = new imageClass();
				moveTo(b, positions[i].x, positions[i].y);
				image.addChild(b);
				unitBitmaps[index].push(b);
			}
			
			// Create base parts
			if (unitStateBefore.unitConfig.isBase)
			{
				var numBasePartsBefore : int = Math.ceil((unitStateBefore.armour * basePartsData.length) / unitStateBefore.unitConfig.maxArmour);
				for (i = 0; i < basePartsData.length; ++i)
				{
					b = new BitmapAsset();
					moveTo(b, positions[0].x, positions[0].y);
					b.x += basePartsData[i].x;
					b.y += basePartsData[i].y;
					b.bitmapData = i >= basePartsData.length - numBasePartsBefore? basePartsData[i].ok : basePartsData[i].destroyed;
					image.addChild(b);
					baseParts[index].push(b);
				}					
			}
		}
		
		// Enter frame event handler
		private function onEnterFrame(e : Event) : void
		{
			var done1 : Boolean, done2 : Boolean;
			
			switch (state)
			{
			case stateEntering:
				if (!updateEntering())
					startLeftAttacking();
				break;
				
			case stateLeftFiring:
				done1 = !updateFiring(0);
				done2 = !updateTakingDamage(1); 
				if (done1 && done2)
					startRightAttacking();
				break;
				
			case stateLeftBombing:
				done1 = !updateBombing(0);
				done2 = !updateTakingDamage(1); 
				if (done1 && done2)
					startRightAttacking();
				break;
								
			case stateRightFiring:
				done1 = !updateFiring(1);
				done2 = !updateTakingDamage(0); 
				if (done1 && done2)
					switchState(stateDone);
				break;

			case stateRightBombing:
				done1 = !updateBombing(1);
				done2 = !updateTakingDamage(0); 
				if (done1 && done2)
					switchState(stateDone);
				break;
				
			case stateDone:
				if (!updateDone())
					onClosed();
				break;
			}
		}
		
		// Switch to a new state
		private function switchState(newState : int) : void
		{
			state = newState;
			stateStartTime = getTimer();
			
			switch (state)
			{			
			case stateEntering:
				prepareEntering();				
				break;
				
			case stateLeftFiring:
				prepareFiring(0);
				break;
				
			case stateLeftBombing:
				prepareBombing(0);
				break;
				
			case stateRightFiring:
				prepareFiring(1);
				break;

			case stateLeftBombing:
				prepareBombing(1);
				break;
			}				
		}
		
		// Prepare entering
		private function prepareEntering() : void
		{
			// Start outside screen
			unitImage[0].x = arenaX - unitEnteringDistance;
			unitImage[1].x = arenaX + unitEnteringDistance;
		}
		
		// Update entering
		private function updateEntering() : Boolean
		{
			var elapsedTime : int = getTimer() - stateStartTime;
			if (elapsedTime > unitEnteringTime)
			{
				// Done entering
				unitImage[0].x = arenaX;
				unitImage[1].x = arenaX;
				
				// Wait for a small amount of extra time before going to the next state
				return elapsedTime < unitEnteringTime + unitEnteringDelay;
			}
			else
			{	
				// Moving onto screen
				var distance : int = (unitEnteringDistance * (unitEnteringTime - elapsedTime)) / unitEnteringTime;
				unitImage[0].x = arenaX - distance;
				unitImage[1].x = arenaX + distance;
				return true;
			}
		}		
		
		// Go to correct state for left attacking
		private function startLeftAttacking() : void
		{
			takingDamageStartTime = -1;
		
			var props : Object = UnitGraphics.getUnitProperties(unitState[0]);			
			var enemyOnGround : Boolean = unitState[1].unitConfig.unitClass != UnitClass.air;
			
			// Play attack sound
			SoundSystem.play(props.attackSound[enemyOnGround? 0 : 1]);

			// Play animation
			if (props.canBomb
				&& oldUnitState[0].ammo > 0
				&& enemyOnGround)
				switchState(stateLeftBombing);
			else
				switchState(stateLeftFiring);
		}
		
		// Go to correct state for right attacking
		private function startRightAttacking() : void
		{
			takingDamageStartTime = -1;

			if (!unitState[1].canCounterAttack(oldUnitState[0]))
				switchState(stateDone);
			else
			{
				var props : Object = UnitGraphics.getUnitProperties(unitState[1]);			
				var enemyOnGround : Boolean = unitState[0].unitConfig.unitClass != UnitClass.air;
				
				// Play attack sound
				SoundSystem.play(props.attackSound[enemyOnGround? 0 : 1]);
	
				// Play animation
				if (props.canBomb
					&& oldUnitState[1].ammo > 0
					&& enemyOnGround)
					switchState(stateRightBombing);
				else
					switchState(stateRightFiring);
			}
		}

		// Prepare firing
		private function prepareFiring(index : int) : void
		{
			// Create all muzzle flashes
			var unitStateBefore : UnitState = oldUnitState[index];
			var unitStateAfter : UnitState = unitState[index];
			var props : Object = UnitGraphics.getUnitProperties(unitStateBefore);
			var pos : Array = UnitGraphics.unitMuzzlePositions[index == 0? 1 : 0][unitStateBefore.faction == Faction.f1? 0 : 1][unitStateBefore.unitConfig.imageNumber];
			var enemyOnGround : Boolean = unitState[index == 0? 1 : 0].unitConfig.unitClass != UnitClass.air;
			if (pos.length > 0 && pos[0] is Array)
				pos = pos[enemyOnGround? 0 : 1];
											
			var image : Image = unitImage[index];
			for each (var a : BitmapAsset in unitBitmaps[index])
				if (a.visible)
					for each (var o : Object in pos)
					{
						// Create image
						var anchor : Object = UnitGraphics.muzzleFlashesAnchor[props.flashType[enemyOnGround? 0 : 1]][o.d];
						var f : BitmapAsset = new (UnitGraphics.muzzleFlashesClass[props.flashType[enemyOnGround? 0 : 1]][o.d])();
						f.x = a.x + o.x - anchor.x;
						f.y = a.y + o.y - anchor.y;
						f.visible = false;
						image.addChild(f);
						muzzleFlashes.splice(Tools.randRange(0, muzzleFlashes.length), 0, f);
					}
		}
		
		// Update firing
		private function updateFiring(index : int) : Boolean
		{
			if (muzzleFlashes.length > 0)
			{
				var last : BitmapAsset = muzzleFlashes[muzzleFlashes.length - 1];
				if (last.visible)
				{
					// Remove muzzle flash
					last.visible = false;
					muzzleFlashes.pop();

					// Create impact
					createImpact(index);
					
					// Trigger damage
					startTakingDamage();
				}
				else
				{
					// Show next muzzle flash
					last.visible = true;
				}
			}
		
			return muzzleFlashes.length > 0;			
		}
		
		// Prepare bombing
		private function prepareBombing(index : int) : void
		{
			// Make sure this unit image is on top of the other one
			clientCanvas.removeChild(unitImage[index]);
			clientCanvas.addChild(unitImage[index]);
		}
		
		// Update bombing
		private function updateBombing(index : int) : Boolean
		{
			var elapsedTime : int = getTimer() - stateStartTime;
			var image : Image = unitImage[index];		
			var sign : int = index == 0? 1 : -1;	
			
			if (elapsedTime < unitBombingTime)
			{
				// Flying towards target
				image.x = arenaX + sign * ((elapsedTime * width) / unitBombingTime);				
				return true;
			}
			else if (elapsedTime < unitBombingTime + unitBombingOffScreenTime)
			{
				// Bombs dropping
				image.x = arenaX + sign * width;
				createImpact(index);
				
				// Trigger damage
				startTakingDamage();
				return true; 
			}
			else if (elapsedTime < unitBombingTime + unitBombingOffScreenTime + unitEnteringTime)
			{
				// Reentering screen
				image.x = arenaX - sign * (unitEnteringDistance * (unitEnteringTime - (elapsedTime - unitBombingTime - unitBombingOffScreenTime))) / unitEnteringTime;
				return true;
			}
			else
			{
				// Done
				image.x = arenaX;
				return false;
			}
		}
		
		// Create impact animation
		private function createImpact(index : int) : void
		{
			var selectedUnit : UnitState = unitState[index];
			var targettedUnit : UnitState = unitState[index ^ 1];
			var props : Object = UnitGraphics.getUnitProperties(selectedUnit);
			var enemyOnGround : Boolean = unitState[index ^ 1].unitConfig.unitClass != UnitClass.air;

			var a : AnimatedBitmap = new AnimatedBitmap(props.impactType[enemyOnGround? 0 : 1]);
			a.x = index == 0? Tools.randRange(110, 160) : Tools.randRange(10, 60);
			a.y = Tools.randRange(50, 100);
			a.play(100, AnimatedBitmap.LOOP_OFF, true);
			
			unitImage[index ^ 1].addChild(a);
		}

		// Update done state		
		private function updateDone() : Boolean
		{
			var elapsedTime : int = getTimer() - stateStartTime;
			return elapsedTime <= 1000;
		}		
		
		// Start taking damage
		private function startTakingDamage() : void
		{
			if (takingDamageStartTime == -1)
				takingDamageStartTime = getTimer();
		}		
		
		// Update taking damage
		private function updateTakingDamage(index : int) : Boolean
		{
			if (takingDamageStartTime == -1)
				return false;
				
			var i : int;
			var animation : AnimatedBitmap;
			
			var unitStateBefore : UnitState = oldUnitState[index];
			var unitStateAfter : UnitState = unitState[index];
			var props : Object = UnitGraphics.getUnitProperties(unitStateBefore);
			var elapsedTime : int = getTimer() - takingDamageStartTime;
			var maxArmourLost : int = unitStateBefore.faction == unitStateAfter.faction? unitStateBefore.armour - unitStateAfter.armour : unitStateBefore.armour;
			var maxTime : int = maxArmourLost * takingDamageTimePerArmourPoint;

			// Update armour text
			var armourLost : int = Math.min(elapsedTime * maxArmourLost / maxTime, maxArmourLost);
			armourText[index].text = unitStateBefore.armour - armourLost;

			if (!unitStateBefore.unitConfig.isBase)
			{
				// Create explosions for destroyed units
				var numBefore : int = Math.ceil((unitStateBefore.armour * props.battleViewMaxUnits) / unitStateBefore.unitConfig.maxArmour);
				var numAfter : int = Math.ceil(((unitStateBefore.armour - maxArmourLost) * props.battleViewMaxUnits) / unitStateBefore.unitConfig.maxArmour);
				var maxDestroyed : int = numBefore - numAfter;
				var numDestroyed : int = Math.min(elapsedTime * maxDestroyed / maxTime, maxDestroyed);
				var bitmaps : Array = unitBitmaps[index];
				var shadows : Array = unitShadows[index];
				for (i = 0; i < numDestroyed; ++i)
					if (bitmaps[i].visible)
					{
						// Hide unit
						bitmaps[i].visible = false;
						if (shadows.length > 0)
							shadows[i].visible = false;
	
						// Start animation
						animation = new AnimatedBitmap(explosionAnimationData);
						animation.x = bitmaps[i].x + 6;
						animation.y = bitmaps[i].y + 2;
						animation.play(50, AnimatedBitmap.LOOP_OFF, true);
						unitImage[index].addChild(animation);
						activeExplosions.push(animation);
						
						// Start sound
						SoundSystem.play(explosionSoundClass);
					}					
			}
			else
			{
				// Create explosions for destroyed base parts
				var maxBasePartsDestroyed : int = basePartsData.length - Math.ceil(((unitStateBefore.armour - maxArmourLost) * basePartsData.length) / unitStateBefore.unitConfig.maxArmour);
				var numPartsDestroyed : int = Math.min(elapsedTime * maxBasePartsDestroyed / maxTime, maxBasePartsDestroyed);
				var parts : Array = baseParts[index];
				for (i = 0; i < numPartsDestroyed; ++i)
					if (parts[i].bitmapData == basePartsData[i].ok)
					{
						// Change into destroyed part
						parts[i].bitmapData = basePartsData[i].destroyed;
	
						// Start animation
						animation = new AnimatedBitmap(explosionAnimationData);
						animation.x = parts[i].x + 6;
						animation.y = parts[i].y + 2;
						animation.play(50, AnimatedBitmap.LOOP_OFF, true);
						unitImage[index].addChild(animation);
						activeExplosions.push(animation);
						
						// Start sound
						SoundSystem.play(explosionSoundClass);
					}					
			}

			// Clean up finished explosions
			for (i = activeExplosions.length - 1; i >= 0; --i)
				if (!activeExplosions[i].isPlaying())
					activeExplosions.splice(i, 1);
			
			// Check if still working		
			return armourLost < maxArmourLost || activeExplosions.length > 0;
		}
	}
}
package onlinefrontlines.game.uistates
{
	import mx.core.Application;
	import flash.utils.*;
	import onlinefrontlines.utils.*;
	import onlinefrontlines.game.*;
	import onlinefrontlines.game.actions.*;
	
	/*
	 * Attacking animations
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
	public class UIStateUnitAttacking extends UIState
	{
		// Explosion animation
		[Embed(source='../../../assets/unit_images/explosion.png')]
		private static var explosionAnimationClass : Class;		
		private static var explosionAnimationData : AnimatedBitmapData = new AnimatedBitmapData(explosionAnimationClass, 7);

		// Explosion sound
		[Embed(source="../../../assets/sounds/explosion.mp3")]
		private static var explosionSoundClass : Class;

		private var oldStateSelectedUnit : UnitState;
		private var selectedUnit : UnitState;
		private var oldStateTargettedUnit : UnitState;
		private var targettedUnit : UnitState;
		
		private static var stateBattleView : int = 1;
		private static var stateDamageAmount : int = 2;
		private static var stateExploding : int  = 3;
		private static var stateDone : int  = 4;
		
		private var currentState : int  = stateBattleView;		
		private var stateStartTime : int;
		
		private var selectedUnitDamageAmount : BitmapText;
		private var targettedUnitDamageAmount : BitmapText;
		
		private var selectedUnitExplosion : AnimatedBitmap;
		private var targettedUnitExplosion : AnimatedBitmap;
		
		private static var damageEffectTime : int = 1500;
		private static var damageEffectStaticTime : int = 1000;		

		// Constructor
		public function UIStateUnitAttacking(oldStateSelectedUnit : UnitState, selectedUnit : UnitState, oldStateTargettedUnit : UnitState, targettedUnit : UnitState) : void
		{
			this.oldStateSelectedUnit = oldStateSelectedUnit;
			this.selectedUnit = selectedUnit;
			this.oldStateTargettedUnit = oldStateTargettedUnit;
			this.targettedUnit = targettedUnit;
		}

		// If this state is playing an animation
		public override function isPlaying() : Boolean
		{ 
			return true;
		}
		
		// Called when entering this state
		public override function onEnterState() : void
		{
			// Update graphics for unit
			selectedUnit.graphics.updateGraphics();
			targettedUnit.graphics.updateGraphics();

			// Highlight units
			gameState.mapConfig.getTile(oldStateSelectedUnit.locationX, oldStateSelectedUnit.locationY).setSelectionImage(MapTile.tileSelectionSelected);
			gameState.mapConfig.getTile(oldStateTargettedUnit.locationX, oldStateTargettedUnit.locationY).setSelectionImage(MapTile.tileSelectionAttack);

			// Determine facing direction
			var s : Object = gameState.mapConfig.getTileLocation(oldStateSelectedUnit.locationX, oldStateSelectedUnit.locationY);			
			var t : Object = gameState.mapConfig.getTileLocation(oldStateTargettedUnit.locationX, oldStateTargettedUnit.locationY);					
			selectedUnit.graphics.setFacingLeft(s.x > t.x);
			targettedUnit.graphics.setFacingLeft(t.x > s.x);

			// Display the battle
			if (Application.application.showInfo.selected)
				Application.application.battleView.show(gameState, oldStateSelectedUnit, selectedUnit, oldStateTargettedUnit, targettedUnit, onBattleViewClosed);
			else
				onBattleViewClosed();
		}
		
		// Called when leaving the state
		public override function onLeaveState() : void
		{
			// Close battle view
			Application.application.battleView.close();

			// Clear tiles
			gameState.clearTileSelection();
			
			// Destroy numbers
			stopDamageAmount();
			
			// Update unit state
			if (selectedUnit.armour <= 0)
				selectedUnit.graphics.setVisible(false);
			
			if (targettedUnit.armour <= 0)
				targettedUnit.graphics.setVisible(false);
		}				
		
		public override function onMousePressed(tile : MapTile) : void
		{
			var unit : UnitState = gameState.getUnitOnTile(tile);
			if (unit != null 
				&& unit.faction == gameState.currentPlayer
				&& gameState.canUnitPerformAction(unit))
			{
				// Treat as normal selection click
				gameState.toState(new UIStateSelectingUnit());
				gameState.tilePressed(tile);
			}
		}

		// Called on update
		public override function onUpdate() : void
		{
			if (currentState == stateDamageAmount)
			{
				// Check if effect is done
				if (getTimer() - stateStartTime > damageEffectTime)
				{
					stopDamageAmount();

					// Switch to next state					
					currentState = stateDone;
					
					if (selectedUnit.armour <= 0)
					{
						selectedUnitExplosion = startExplosion(selectedUnit);
						currentState = stateExploding;
					}
						
					if (targettedUnit.armour <= 0)
					{
						targettedUnitExplosion = startExplosion(targettedUnit);
						currentState = stateExploding;
					}
				}	
				else
				{
					positionDamageAmount(selectedUnitDamageAmount, selectedUnit);
					positionDamageAmount(targettedUnitDamageAmount, targettedUnit);
				}					
			}
			
			if (currentState == stateExploding)
			{
				// If both units have finished exploding go to next state
				if ((selectedUnitExplosion == null || !selectedUnitExplosion.isPlaying()) 
					&& (targettedUnitExplosion == null || !targettedUnitExplosion.isPlaying()))
				{
					currentState = stateDone;
				}
			}
			
			if (currentState == stateDone)
			{
				// Select unit
				gameState.toState(new UIStateUnitSelected(selectedUnit));
			}
		}
		
		// Callback when battle view was closed
		private function onBattleViewClosed() : void
		{			
			currentState = stateDamageAmount;
			
			stateStartTime = getTimer();
			selectedUnitDamageAmount = startDamageAmount(oldStateSelectedUnit, selectedUnit);
			targettedUnitDamageAmount = startDamageAmount(oldStateTargettedUnit, targettedUnit);
		}
		
		// Start numbers effect for a unit
		private function startDamageAmount(oldUnit : UnitState, unit : UnitState) : BitmapText
		{
			var t : BitmapText = new BitmapText(Application.application.mapConfig.effectsRoot, 2, 0, BitmapText.typeDamage);
			t.setValue(oldUnit.armour >= unit.armour? unit.armour - oldUnit.armour : -oldUnit.armour);
			positionDamageAmount(t, unit);
			return t;
		}

		// Start numbers effect for a unit
		private function positionDamageAmount(t : BitmapText, unit : UnitState) : BitmapText
		{
			var dt : int = getTimer() - stateStartTime;
			var fraction : Number = dt < damageEffectStaticTime? 1 : Math.min(1, Number(damageEffectTime - dt) / Number(damageEffectTime - damageEffectStaticTime));
			var tileLocation : Object = gameState.mapConfig.getTileLocation(unit.locationX, unit.locationY);			
			t.setPosition(tileLocation.x + 3, tileLocation.y + 11 - (1.0 - fraction) * 10);
			t.setAlpha(fraction);
			return t;
		}

		// Destroy numbers effect
		private function stopDamageAmount() : void
		{
			if (selectedUnitDamageAmount != null)
			{
				selectedUnitDamageAmount.destroy();
				selectedUnitDamageAmount = null;
			}
			
			if (targettedUnitDamageAmount != null)
			{
				targettedUnitDamageAmount.destroy();
				targettedUnitDamageAmount = null;
			}
		}
		
		// Start explosion effect for a unit
		private function startExplosion(unit : UnitState) : AnimatedBitmap
		{
			unit.graphics.setVisible(false);

			// Start animation
			var tileLocation : Object = gameState.mapConfig.getTileLocation(unit.locationX, unit.locationY);			
			var animation : AnimatedBitmap = new AnimatedBitmap(explosionAnimationData);
			animation.x = tileLocation.x + 6;
			animation.y = tileLocation.y + 2;
			animation.play(100, AnimatedBitmap.LOOP_OFF, true);
			Application.application.mapConfig.effectsRoot.addChild(animation);
			
			// Start sound
			SoundSystem.play(explosionSoundClass);
			
			return animation;
		}
	}
}

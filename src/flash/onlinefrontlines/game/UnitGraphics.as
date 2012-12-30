package onlinefrontlines.game
{
	import mx.containers.Canvas;
	import mx.core.BitmapAsset;
	import mx.controls.Label;
	import mx.controls.Image;
	import flash.display.Sprite;
	import flash.display.DisplayObject;
	import flash.display.DisplayObjectContainer;
	import onlinefrontlines.utils.*;
	import onlinefrontlines.game.*;

	/*
	 * Images for all units
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
	public class UnitGraphics
	{
		// Unit images
		[Embed(source='../../../../web/assets/unit_images/rd_unit_00_l.png')]
		private static var redUnit00L : Class;		
		[Embed(source='../../../../web/assets/unit_images/rd_unit_01_l.png')]
		private static var redUnit01L : Class;
		[Embed(source='../../../../web/assets/unit_images/rd_unit_02_l.png')]
		private static var redUnit02L : Class;
		[Embed(source='../../../../web/assets/unit_images/rd_unit_03_l.png')]
		private static var redUnit03L : Class;
		[Embed(source='../../../../web/assets/unit_images/rd_unit_04_l.png')]
		private static var redUnit04L : Class;
		[Embed(source='../../../../web/assets/unit_images/rd_unit_05_l.png')]
		private static var redUnit05L : Class;
		[Embed(source='../../../../web/assets/unit_images/rd_unit_06_l.png')]
		private static var redUnit06L : Class;
		[Embed(source='../../../../web/assets/unit_images/rd_unit_07_l.png')]
		private static var redUnit07L : Class;
		[Embed(source='../../../../web/assets/unit_images/rd_unit_08_l.png')]
		private static var redUnit08L : Class;
		[Embed(source='../../../../web/assets/unit_images/rd_unit_09_l.png')]
		private static var redUnit09L : Class;
		[Embed(source='../../../../web/assets/unit_images/rd_unit_10_l.png')]
		private static var redUnit10L : Class;
		[Embed(source='../../../../web/assets/unit_images/rd_unit_11_l.png')]
		private static var redUnit11L : Class;
		[Embed(source='../../../../web/assets/unit_images/rd_unit_12_l.png')]
		private static var redUnit12L : Class;
		[Embed(source='../../../../web/assets/unit_images/rd_unit_13_l.png')]
		private static var redUnit13L : Class;
		[Embed(source='../../../../web/assets/unit_images/rd_unit_14_l.png')]
		private static var redUnit14L : Class;
		[Embed(source='../../../../web/assets/unit_images/rd_unit_15_l.png')]
		private static var redUnit15L : Class;
		[Embed(source='../../../../web/assets/unit_images/rd_unit_16_l.png')]
		private static var redUnit16L : Class;
		[Embed(source='../../../../web/assets/unit_images/rd_unit_17_l.png')]
		private static var redUnit17L : Class;
		[Embed(source='../../../../web/assets/unit_images/rd_unit_18_l.png')]
		private static var redUnit18L : Class;
		[Embed(source='../../../../web/assets/unit_images/rd_unit_19_l.png')]
		private static var redUnit19L : Class;
		[Embed(source='../../../../web/assets/unit_images/rd_unit_20_l.png')]
		private static var redUnit20L : Class;
		[Embed(source='../../../../web/assets/unit_images/rd_unit_21_l.png')]
		private static var redUnit21L : Class;
		[Embed(source='../../../../web/assets/unit_images/rd_unit_22_l.png')]
		private static var redUnit22L : Class;
		
		private static var redUnitsL : Array = 
		[
			redUnit00L,
			redUnit01L,
			redUnit02L,
			redUnit03L,
			redUnit04L,
			redUnit05L,
			redUnit06L,
			redUnit07L,
			redUnit08L,
			redUnit09L,
			redUnit10L,
			redUnit11L,
			redUnit12L,
			redUnit13L,
			redUnit14L,
			redUnit15L,
			redUnit16L,
			redUnit17L,
			redUnit18L,
			redUnit19L,
			redUnit20L,
			redUnit21L,
			redUnit22L,
		];

		[Embed(source='../../../../web/assets/unit_images/rd_unit_01_r.png')]
		private static var redUnit01R : Class;
		[Embed(source='../../../../web/assets/unit_images/rd_unit_02_r.png')]
		private static var redUnit02R : Class;
		[Embed(source='../../../../web/assets/unit_images/rd_unit_03_r.png')]
		private static var redUnit03R : Class;
		[Embed(source='../../../../web/assets/unit_images/rd_unit_04_r.png')]
		private static var redUnit04R : Class;
		[Embed(source='../../../../web/assets/unit_images/rd_unit_05_r.png')]
		private static var redUnit05R : Class;
		[Embed(source='../../../../web/assets/unit_images/rd_unit_06_r.png')]
		private static var redUnit06R : Class;
		[Embed(source='../../../../web/assets/unit_images/rd_unit_07_r.png')]
		private static var redUnit07R : Class;
		[Embed(source='../../../../web/assets/unit_images/rd_unit_08_r.png')]
		private static var redUnit08R : Class;
		[Embed(source='../../../../web/assets/unit_images/rd_unit_09_r.png')]
		private static var redUnit09R : Class;
		[Embed(source='../../../../web/assets/unit_images/rd_unit_10_r.png')]
		private static var redUnit10R : Class;
		[Embed(source='../../../../web/assets/unit_images/rd_unit_11_r.png')]
		private static var redUnit11R : Class;
		[Embed(source='../../../../web/assets/unit_images/rd_unit_12_r.png')]
		private static var redUnit12R : Class;
		[Embed(source='../../../../web/assets/unit_images/rd_unit_13_r.png')]
		private static var redUnit13R : Class;
		[Embed(source='../../../../web/assets/unit_images/rd_unit_14_r.png')]
		private static var redUnit14R : Class;
		[Embed(source='../../../../web/assets/unit_images/rd_unit_18_r.png')]
		private static var redUnit18R : Class;
		[Embed(source='../../../../web/assets/unit_images/rd_unit_19_r.png')]
		private static var redUnit19R : Class;
		[Embed(source='../../../../web/assets/unit_images/rd_unit_20_r.png')]
		private static var redUnit20R : Class;
		[Embed(source='../../../../web/assets/unit_images/rd_unit_22_r.png')]
		private static var redUnit22R : Class;
		
		private static var redUnitsR : Array = 
		[
			redUnit00L,
			redUnit01R,
			redUnit02R,
			redUnit03R,
			redUnit04R,
			redUnit05R,
			redUnit06R,
			redUnit07R,
			redUnit08R,
			redUnit09R,
			redUnit10R,
			redUnit11R,
			redUnit12R,
			redUnit13R,
			redUnit14R,
			redUnit15L,
			redUnit16L,
			redUnit17L,
			redUnit18R,
			redUnit19R,
			redUnit20R,
			redUnit21L,
			redUnit22R
		];

		[Embed(source='../../../../web/assets/unit_images/bl_unit_00_l.png')]
		private static var blueUnit00L : Class;		
		[Embed(source='../../../../web/assets/unit_images/bl_unit_01_l.png')]
		private static var blueUnit01L : Class;
		[Embed(source='../../../../web/assets/unit_images/bl_unit_02_l.png')]
		private static var blueUnit02L : Class;
		[Embed(source='../../../../web/assets/unit_images/bl_unit_03_l.png')]
		private static var blueUnit03L : Class;
		[Embed(source='../../../../web/assets/unit_images/bl_unit_04_l.png')]
		private static var blueUnit04L : Class;
		[Embed(source='../../../../web/assets/unit_images/bl_unit_05_l.png')]
		private static var blueUnit05L : Class;
		[Embed(source='../../../../web/assets/unit_images/bl_unit_06_l.png')]
		private static var blueUnit06L : Class;
		[Embed(source='../../../../web/assets/unit_images/bl_unit_07_l.png')]
		private static var blueUnit07L : Class;
		[Embed(source='../../../../web/assets/unit_images/bl_unit_08_l.png')]
		private static var blueUnit08L : Class;
		[Embed(source='../../../../web/assets/unit_images/bl_unit_09_l.png')]
		private static var blueUnit09L : Class;
		[Embed(source='../../../../web/assets/unit_images/bl_unit_10_l.png')]
		private static var blueUnit10L : Class;
		[Embed(source='../../../../web/assets/unit_images/bl_unit_11_l.png')]
		private static var blueUnit11L : Class;
		[Embed(source='../../../../web/assets/unit_images/bl_unit_12_l.png')]
		private static var blueUnit12L : Class;
		[Embed(source='../../../../web/assets/unit_images/bl_unit_13_l.png')]
		private static var blueUnit13L : Class;
		[Embed(source='../../../../web/assets/unit_images/bl_unit_14_l.png')]
		private static var blueUnit14L : Class;
		[Embed(source='../../../../web/assets/unit_images/bl_unit_15_l.png')]
		private static var blueUnit15L : Class;
		[Embed(source='../../../../web/assets/unit_images/bl_unit_16_l.png')]
		private static var blueUnit16L : Class;
		[Embed(source='../../../../web/assets/unit_images/bl_unit_17_l.png')]
		private static var blueUnit17L : Class;
		[Embed(source='../../../../web/assets/unit_images/bl_unit_18_l.png')]
		private static var blueUnit18L : Class;
		[Embed(source='../../../../web/assets/unit_images/bl_unit_19_l.png')]
		private static var blueUnit19L : Class;
		[Embed(source='../../../../web/assets/unit_images/bl_unit_20_l.png')]
		private static var blueUnit20L : Class;
		[Embed(source='../../../../web/assets/unit_images/bl_unit_21_l.png')]
		private static var blueUnit21L : Class;
		[Embed(source='../../../../web/assets/unit_images/bl_unit_22_l.png')]
		private static var blueUnit22L : Class;
		
		private static var blueUnitsL : Array = 
		[
			blueUnit00L,
			blueUnit01L,
			blueUnit02L,
			blueUnit03L,
			blueUnit04L,
			blueUnit05L,
			blueUnit06L,
			blueUnit07L,
			blueUnit08L,
			blueUnit09L,
			blueUnit10L,
			blueUnit11L,
			blueUnit12L,
			blueUnit13L,
			blueUnit14L,
			blueUnit15L,
			blueUnit16L,
			blueUnit17L,
			blueUnit18L,
			blueUnit19L,
			blueUnit20L,
			blueUnit21L,
			blueUnit22L
		];

		[Embed(source='../../../../web/assets/unit_images/bl_unit_01_r.png')]
		private static var blueUnit01R : Class;
		[Embed(source='../../../../web/assets/unit_images/bl_unit_02_r.png')]
		private static var blueUnit02R : Class;
		[Embed(source='../../../../web/assets/unit_images/bl_unit_03_r.png')]
		private static var blueUnit03R : Class;
		[Embed(source='../../../../web/assets/unit_images/bl_unit_04_r.png')]
		private static var blueUnit04R : Class;
		[Embed(source='../../../../web/assets/unit_images/bl_unit_05_r.png')]
		private static var blueUnit05R : Class;
		[Embed(source='../../../../web/assets/unit_images/bl_unit_06_r.png')]
		private static var blueUnit06R : Class;
		[Embed(source='../../../../web/assets/unit_images/bl_unit_07_r.png')]
		private static var blueUnit07R : Class;
		[Embed(source='../../../../web/assets/unit_images/bl_unit_08_r.png')]
		private static var blueUnit08R : Class;
		[Embed(source='../../../../web/assets/unit_images/bl_unit_09_r.png')]
		private static var blueUnit09R : Class;
		[Embed(source='../../../../web/assets/unit_images/bl_unit_10_r.png')]
		private static var blueUnit10R : Class;
		[Embed(source='../../../../web/assets/unit_images/bl_unit_11_r.png')]
		private static var blueUnit11R : Class;
		[Embed(source='../../../../web/assets/unit_images/bl_unit_12_r.png')]
		private static var blueUnit12R : Class;
		[Embed(source='../../../../web/assets/unit_images/bl_unit_13_r.png')]
		private static var blueUnit13R : Class;
		[Embed(source='../../../../web/assets/unit_images/bl_unit_14_r.png')]
		private static var blueUnit14R : Class;
		[Embed(source='../../../../web/assets/unit_images/bl_unit_18_r.png')]
		private static var blueUnit18R : Class;
		[Embed(source='../../../../web/assets/unit_images/bl_unit_19_r.png')]
		private static var blueUnit19R : Class;
		[Embed(source='../../../../web/assets/unit_images/bl_unit_20_r.png')]
		private static var blueUnit20R : Class;
		[Embed(source='../../../../web/assets/unit_images/bl_unit_22_r.png')]
		private static var blueUnit22R : Class;
		
		private static var blueUnitsR : Array = 
		[
			blueUnit00L,
			blueUnit01R,
			blueUnit02R,
			blueUnit03R,
			blueUnit04R,
			blueUnit05R,
			blueUnit06R,
			blueUnit07R,
			blueUnit08R,
			blueUnit09R,
			blueUnit10R,
			blueUnit11R,
			blueUnit12R,
			blueUnit13R,
			blueUnit14R,
			blueUnit15L,
			blueUnit16L,
			blueUnit17L,
			blueUnit18R,
			blueUnit19R,
			blueUnit20R,
			blueUnit21L,
			blueUnit22R
		];

		// Unit container icons
		[Embed(source='../../assets/unit_images/fx_unit_10_container_indicator.png')]
		private static var unit10ContainerIndicator : Class;
		[Embed(source='../../assets/unit_images/fx_unit_12_container_indicator.png')]
		private static var unit12ContainerIndicator : Class;
		[Embed(source='../../assets/unit_images/fx_unit_15_container_indicator.png')]
		private static var unit15ContainerIndicator : Class;
		[Embed(source='../../assets/unit_images/fx_unit_16_container_indicator.png')]
		private static var unit16ContainerIndicator : Class;
		[Embed(source='../../assets/unit_images/fx_unit_17_container_indicator.png')]
		private static var unit17ContainerIndicator : Class;
		[Embed(source='../../assets/unit_images/fx_unit_19_container_indicator.png')]
		private static var unit19ContainerIndicator : Class;

		private static var containerIndicators : Array = 
		[
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			unit10ContainerIndicator,
			null,
			unit12ContainerIndicator,
			null,
			null,
			unit15ContainerIndicator,
			unit16ContainerIndicator,
			unit17ContainerIndicator,
			null,
			unit19ContainerIndicator,
			null,
			null,
			null
		];
		
		private static var containerIndicatorLocations : Array = 
		[
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			[ { x : 14, y : 16 } ],
			null,
			[ { x : 4, y : 15 }, { x : 12, y : 15 }, { x : 20, y : 15 } ],
			null,
			null,
			[ { x : 5, y : 16 }, { x : 14, y : 16 }, { x : 23, y : 16 } ],
			[ { x : 3, y : 15 }, { x : 12, y : 15 }, { x : 21, y : 15 } ],
			[ { x : 4, y : 15 }, { x : 15, y : 15 }, { x : 26, y : 15 } ],
			null,
			[ { x : 14, y : 12 } ],
			null,
			null,
			null
		];

		// Muzzle flashes
		[Embed(source='../../assets/battle_view/muzzle1_l.png')]
		private static var muzzle1LClass : Class;
		[Embed(source='../../assets/battle_view/muzzle1_r.png')]
		private static var muzzle1RClass : Class;
		[Embed(source='../../assets/battle_view/muzzle2_l.png')]
		private static var muzzle2LClass : Class;
		[Embed(source='../../assets/battle_view/muzzle2_r.png')]
		private static var muzzle2RClass : Class;
		[Embed(source='../../assets/battle_view/muzzle3_l.png')]
		private static var muzzle3LClass : Class;
		[Embed(source='../../assets/battle_view/muzzle3_r.png')]
		private static var muzzle3RClass : Class;
		[Embed(source='../../assets/battle_view/muzzle4_l.png')]
		private static var muzzle4LClass : Class;
		[Embed(source='../../assets/battle_view/muzzle4_r.png')]
		private static var muzzle4RClass : Class;
		
		public static var muzzleFlashesClass : Array = 
		[
			null,
			[ muzzle1LClass, muzzle1RClass ],
			[ muzzle2LClass, muzzle2RClass ],
			[ muzzle3LClass, muzzle3RClass ],
			[ muzzle4LClass, muzzle4RClass ]
		];
		
		public static var muzzleFlashesAnchor : Array =
		[
			null,
			[ { x : 12, y : 4 }, { x : 0, y : 4 } ],
			[ { x : 9, y : 6 }, { x : 2, y : 7 } ],
			[ { x : 5, y : 3 }, { x : 0, y : 3 } ],
			[ { x : 5, y : 5 }, { x : 2, y : 6 } ]
		];
		
		// Impacts
		[Embed(source='../../assets/battle_view/impact_small.png')]
		private static var impactSmallClass : Class;		
		private static var impactSmallData : AnimatedBitmapData = new AnimatedBitmapData(impactSmallClass, 3);		
		[Embed(source='../../assets/battle_view/impact_large.png')]
		private static var impactLargeClass : Class;		
		private static var impactLargeData : AnimatedBitmapData = new AnimatedBitmapData(impactLargeClass, 3);
		[Embed(source='../../assets/battle_view/impact_air.png')]
		private static var impactAirClass : Class;		
		public static var impactAirData : AnimatedBitmapData = new AnimatedBitmapData(impactAirClass, 3);

		// Movement sounds
		[Embed(source="../../assets/sounds/move_unknown.mp3")]
		public static var moveUnknownClass : Class;
		[Embed(source="../../assets/sounds/move_foot.mp3")]
		private static var moveFootClass : Class;
		[Embed(source="../../assets/sounds/move_tank.mp3")]
		private static var moveTankClass : Class;
		[Embed(source="../../assets/sounds/move_boat.mp3")]
		private static var moveBoatClass : Class;
		[Embed(source="../../assets/sounds/move_jet.mp3")]
		private static var moveJetClass : Class;
		[Embed(source="../../assets/sounds/move_prop.mp3")]
		private static var movePropClass : Class;

		// Attack sounds
		[Embed(source="../../assets/sounds/attack_aa_gun.mp3")]
		public static var attackAAGun : Class;
		[Embed(source="../../assets/sounds/attack_cannon.mp3")]
		public static var attackCannon : Class;
		[Embed(source="../../assets/sounds/attack_mg.mp3")]
		public static var attackMG : Class;
		[Embed(source="../../assets/sounds/attack_shot_gun.mp3")]
		public static var attackShotGun : Class;
		[Embed(source="../../assets/sounds/attack_bomb.mp3")]
		public static var attackBomb : Class;

		// General unit properties
		private static var unitProperties : Array = 
		[
			{ battleViewMaxUnits : 0, 	flashType : [ 0, 0 ],	impactType : [ impactSmallData, impactAirData ],	canBomb : false, 	movementSound : moveUnknownClass,	attackSound : [ null, 			null 			] }, // Unknown
			{ battleViewMaxUnits : 4, 	flashType : [ 1, 1 ],	impactType : [ impactLargeData, impactAirData ],	canBomb : false, 	movementSound : moveTankClass,		attackSound : [ attackCannon, 	attackCannon 	] }, // Heavy Tank
			{ battleViewMaxUnits : 4, 	flashType : [ 4, 4 ],	impactType : [ impactSmallData, impactAirData ],	canBomb : false, 	movementSound : moveTankClass,		attackSound : [ attackAAGun, 	attackAAGun 	] }, // AA Tank
			{ battleViewMaxUnits : 4, 	flashType : [ 2, 2 ],	impactType : [ impactLargeData, impactAirData ],	canBomb : false, 	movementSound : moveTankClass,		attackSound : [ attackCannon, 	attackCannon 	] }, // Artillery
			{ battleViewMaxUnits : 5, 	flashType : [ 1, 1 ],	impactType : [ impactSmallData, impactAirData ],	canBomb : false, 	movementSound : moveFootClass,		attackSound : [ attackMG, 		attackMG	 	] }, // Infantry
			{ battleViewMaxUnits : 5, 	flashType : [ 3, 3 ],	impactType : [ impactSmallData, impactAirData ],	canBomb : false, 	movementSound : moveJetClass,		attackSound : [ attackMG, 		attackMG 		] }, // Fighter
			{ battleViewMaxUnits : 4, 	flashType : [ 3, 3 ],	impactType : [ impactLargeData, impactAirData ],	canBomb : true, 	movementSound : movePropClass,		attackSound : [ attackBomb, 	attackMG 		] }, // Dive bomber
			{ battleViewMaxUnits : 3, 	flashType : [ 3, 3 ],	impactType : [ impactLargeData, impactAirData ],	canBomb : true, 	movementSound : movePropClass,		attackSound : [ attackBomb, 	attackMG 		] }, // Bomber
			{ battleViewMaxUnits : 3, 	flashType : [ 3, 3 ],	impactType : [ impactSmallData, impactAirData ],	canBomb : false, 	movementSound : null,				attackSound : [ attackMG, 		attackMG 		] }, // Glider
			{ battleViewMaxUnits : 5, 	flashType : [ 3, 3 ],	impactType : [ impactSmallData, impactAirData ],	canBomb : false, 	movementSound : moveFootClass,		attackSound : [ attackMG, 		attackMG 		] }, // Airborne
			{ battleViewMaxUnits : 4, 	flashType : [ 4, 4 ],	impactType : [ impactSmallData, impactAirData ],	canBomb : false, 	movementSound : moveBoatClass,		attackSound : [ attackCannon, 	attackCannon 	] }, // Attack boat
			{ battleViewMaxUnits : 2, 	flashType : [ 4, 4 ],	impactType : [ impactSmallData, impactAirData ],	canBomb : false, 	movementSound : moveBoatClass,		attackSound : [ attackCannon, 	attackCannon 	] }, // Battle ship
			{ battleViewMaxUnits : 1, 	flashType : [ 4, 4 ],	impactType : [ impactSmallData, impactAirData ],	canBomb : false, 	movementSound : moveBoatClass,		attackSound : [ attackCannon, 	attackCannon 	] }, // Aircraft carrier
			{ battleViewMaxUnits : 3, 	flashType : [ 4, 4 ],	impactType : [ impactSmallData, impactAirData ],	canBomb : false, 	movementSound : moveBoatClass,		attackSound : [ attackShotGun,	attackShotGun	] }, // Marine Lander
			{ battleViewMaxUnits : 5, 	flashType : [ 3, 3 ],	impactType : [ impactSmallData, impactAirData ],	canBomb : false, 	movementSound : moveFootClass,		attackSound : [ attackShotGun,	attackShotGun	] }, // Marine
			{ battleViewMaxUnits : 1, 	flashType : [ 3, 3 ],	impactType : [ impactSmallData, impactAirData ],	canBomb : false, 	movementSound : null,				attackSound : [ attackMG, 		attackMG 		] }, // Army base
			{ battleViewMaxUnits : 1, 	flashType : [ 3, 3 ],	impactType : [ impactSmallData, impactAirData ],	canBomb : false, 	movementSound : null,				attackSound : [ attackMG, 		attackMG 		] }, // Air field
			{ battleViewMaxUnits : 1, 	flashType : [ 3, 3 ],	impactType : [ impactSmallData, impactAirData ],	canBomb : false, 	movementSound : null,				attackSound : [ attackMG, 		attackMG 		] }, // Harbour
			{ battleViewMaxUnits : 1, 	flashType : [ 4, 4 ],	impactType : [ impactSmallData, impactAirData ],	canBomb : false, 	movementSound : null,				attackSound : [ attackMG, 		attackMG 		] }, // Bunker
			{ battleViewMaxUnits : 1, 	flashType : [ 1, 4 ], 	impactType : [ impactLargeData, impactAirData ],	canBomb : false, 	movementSound : moveBoatClass, 		attackSound : [ attackCannon, 	attackMG 		] }, // Sub marine
			{ battleViewMaxUnits : 1, 	flashType : [ 3, 3 ],	impactType : [ impactSmallData, impactAirData ],	canBomb : false, 	movementSound : null,				attackSound : [ attackMG, 		attackMG 		] }, // Radar
			{ battleViewMaxUnits : 1, 	flashType : [ 3, 3 ],	impactType : [ impactSmallData, impactAirData ],	canBomb : false, 	movementSound : null,				attackSound : [ attackMG, 		attackMG 		] }, // Flag
			{ battleViewMaxUnits : 3, 	flashType : [ 3, 3 ],	impactType : [ impactLargeData, impactAirData ],	canBomb : true, 	movementSound : moveJetClass,		attackSound : [ attackBomb, 	attackMG 		] }, // Stealth Bomber
		];
		
		// Muzzle flash properties
		public static var unitMuzzlePositions : Array = 
		[
			[ // Left facing
				[ // Faction 1
					[ ], // Unknown
					[ { x : 5, y : 15, d : 0 } ], // Heavy Tank
					[ { x : 6, y : 9, d : 0 }, { x : 6, y : 12, d : 0 } ], // AA Tank
					[ { x : 8, y : 9, d : 0 } ], // Artillery
					[ { x : 8, y : 15, d : 0 } ], // Infantry
					[ { x : 11, y : 23, d : 0 }, { x : 10, y : 21, d : 0 }, { x : 11, y : 14, d : 0 } ], // Fighter
					[ { x : 10, y : 22, d : 0 }, { x : 10, y : 14, d : 0 } ], // Dive bomber
					[ { x : 3, y : 19, d : 0 } ], // Bomber
					[ ], // Glider
					[ { x : 6, y : 16, d : 0 }, { x : 6, y : 16, d : 0 } ], // Airborne
					[ { x : 6, y : 14, d : 0 }, { x : 5, y : 15, d : 0 } ], // Attack boat
					[ { x : 2, y : 16, d : 0 }, { x : 27, y : 14, d : 1 }, { x : 32, y : 16, d : 1 } ], // Battle ship				
					[ { x : 11, y : 13, d : 0 } ], // Aircraft carrier
					[ { x : 12, y : 13, d : 0 } ], // Marine Lander
					[ { x : 5, y : 14, d : 0 } ], // Marine
					[ { x : -10, y : -1, d : 0 }, { x : -10, y : 15, d : 0 }, { x : -10, y : 35, d : 0 }, { x : 43, y : -1, d : 1 }, { x : 43, y : 15, d : 1 }, { x : 43, y : 35, d : 1 } ], // Army base
					[ { x : -10, y : -1, d : 0 }, { x : -10, y : 15, d : 0 }, { x : -10, y : 35, d : 0 }, { x : 43, y : -1, d : 1 }, { x : 43, y : 15, d : 1 }, { x : 43, y : 35, d : 1 } ], // Air field
					[ { x : -10, y : -1, d : 0 }, { x : -10, y : 15, d : 0 }, { x : -10, y : 35, d : 0 }, { x : 43, y : -1, d : 1 }, { x : 43, y : 15, d : 1 }, { x : 43, y : 35, d : 1 } ], // Harbour
					[ { x : 4, y : 10, d : 0 }, { x : 4, y : 12, d : 0 } ], // Bunker
					[ [ { x : 3, y : 18, d : 0 } ], [ { x : 4, y : 12, d : 0 }, { x : 4, y : 12, d : 0 } ] ], // Sub marine
					[ ], // Radar
					[ ], // Flag
					[ ], // Stealth Bomber
				],
				[ // Faction 2
					[ ], // Unknown
					[ { x : 5, y : 14, d : 0 } ], // Heavy Tank
					[ { x : 5, y : 9, d : 0 }, { x : 5, y : 12, d : 0 } ], // AA Tank
					[ { x : 8, y : 8, d : 0 } ], // Artillery
					[ { x : 6, y : 15, d : 0 } ], // Infantry
					[ { x : 12, y : 22, d : 0 }, { x : 13, y : 23, d : 0 }, { x : 13, y : 13, d : 0 } ], // Fighter
					[ { x : 8, y : 22, d : 0 }, { x : 9, y : 13, d : 0 } ], // Dive bomber
					[ { x : 3, y : 19, d : 0 } ], // Bomber
					[ ], // Glider
					[ { x : 7, y : 17, d : 0 }, { x : 7, y : 17, d : 0 } ], // Airborne
					[ { x : 4, y : 15, d : 0 }, { x : 6, y : 15, d : 0 } ], // Attack boat
					[ { x : 4, y : 14, d : 0 }, { x : 26, y : 12, d : 1 }, { x : 31, y : 14, d : 1 } ], // Battle ship
					[ { x : 19, y : 13, d : 0 } ], // Aircraft carrier
					[ { x : 12, y : 13, d : 0 } ], // Marine Lander
					[ { x : 6, y : 14, d : 0 } ], // Marine
					[ { x : -10, y : -1, d : 0 }, { x : -10, y : 15, d : 0 }, { x : -10, y : 35, d : 0 }, { x : 43, y : -1, d : 1 }, { x : 43, y : 15, d : 1 }, { x : 43, y : 35, d : 1 } ], // Army base
					[ { x : -10, y : -1, d : 0 }, { x : -10, y : 15, d : 0 }, { x : -10, y : 35, d : 0 }, { x : 43, y : -1, d : 1 }, { x : 43, y : 15, d : 1 }, { x : 43, y : 35, d : 1 } ], // Air field
					[ { x : -10, y : -1, d : 0 }, { x : -10, y : 15, d : 0 }, { x : -10, y : 35, d : 0 }, { x : 43, y : -1, d : 1 }, { x : 43, y : 15, d : 1 }, { x : 43, y : 35, d : 1 } ], // Harbour
					[ { x : 4, y : 10, d : 0 }, { x : 4, y : 12, d : 0 } ], // Bunker
					[ [ { x : 3, y : 18, d : 0 } ], [ { x : 4, y : 12, d : 0 }, { x : 4, y : 12, d : 0 } ] ], // Sub marine
					[ ], // Radar
					[ ], // Flag
					[ ], // Stealth Bomber
				]
			],
			[ // Right facing
				[ // Faction 1
					[ ], // Unknown
					[ { x : 31, y : 15, d : 1 } ], // Heavy Tank
					[ { x : 27, y : 9, d : 1 }, { x : 27, y : 12, d : 1 } ], // AA Tank
					[ { x : 29, y : 9, d : 1 } ], // Artillery
					[ { x : 28, y : 15, d : 1 } ], // Infantry
					[ { x : 22, y : 23, d : 1 }, { x : 23, y : 21, d : 1 }, { x : 22, y : 14, d : 1 } ], // Fighter
					[ { x : 24, y : 22, d : 1 }, { x : 24, y : 14, d : 1 } ], // Dive bomber
					[ { x : 31, y : 19, d : 1 } ], // Bomber
					[ ], // Glider
					[ { x : 27, y : 17, d : 1 }, { x : 27, y : 17, d : 1 } ], // Airborne
					[ { x : 28, y : 14, d : 1 }, { x : 29, y : 15, d : 1 } ], // Attack boat
					[ { x : 1, y : 16, d : 0 }, { x : 6, y : 14, d : 0 }, { x : 32, y : 16, d : 1 } ], // Battle ship				
					[ { x : 22, y : 13, d : 1 } ], // Aircraft carrier
					[ { x : 22, y : 13, d : 1 } ], // Marine Lander
					[ { x : 29, y : 14, d : 1 } ], // Marine
					[ { x : -10, y : -1, d : 0 }, { x : -10, y : 15, d : 0 }, { x : -10, y : 35, d : 0 }, { x : 43, y : -1, d : 1 }, { x : 43, y : 15, d : 1 }, { x : 43, y : 35, d : 1 } ], // Army base
					[ { x : -10, y : -1, d : 0 }, { x : -10, y : 15, d : 0 }, { x : -10, y : 35, d : 0 }, { x : 43, y : -1, d : 1 }, { x : 43, y : 15, d : 1 }, { x : 43, y : 35, d : 1 } ], // Air field
					[ { x : -10, y : -1, d : 0 }, { x : -10, y : 15, d : 0 }, { x : -10, y : 35, d : 0 }, { x : 43, y : -1, d : 1 }, { x : 43, y : 15, d : 1 }, { x : 43, y : 35, d : 1 } ], // Harbour
					[ { x : 31, y : 10, d : 0 }, { x : 31, y : 12, d : 0 } ], // Bunker
					[ [ { x : 31, y : 18, d : 1 } ], [ { x : 30, y : 12, d : 1 }, { x : 30, y : 12, d : 1 } ] ], // Sub marine
					[ ], // Radar
					[ ], // Flag
					[ ], // Stealth Bomber
				],
				[ // Faction 2
					[ ], // Unknown
					[ { x : 29, y : 14, d : 1 } ], // Heavy Tank
					[ { x : 28, y : 9, d : 1 }, { x : 28, y : 12, d : 1 } ], // AA Tank
					[ { x : 26, y : 9, d : 1 } ], // Artillery
					[ { x : 28, y : 15, d : 1 } ], // Infantry
					[ { x : 21, y : 22, d : 1 }, { x : 20, y : 23, d : 1 }, { x : 21, y : 14, d : 1 } ], // Fighter
					[ { x : 26, y : 22, d : 1 }, { x : 25, y : 13, d : 1 } ], // Dive bomber
					[ { x : 31, y : 19, d : 1 } ], // Bomber
					[ ], // Glider
					[ { x : 27, y : 17, d : 1 }, { x : 27, y : 17, d : 1 } ], // Airborne
					[ { x : 28, y : 15, d : 1 }, { x : 30, y : 15, d : 1 } ], // Attack boat
					[ { x : 4, y : 15, d : 0 }, { x : 10, y : 12, d : 0 }, { x : 31, y : 14, d : 1 } ], // Battle ship
					[ { x : 16, y : 13, d : 1 } ], // Aircraft carrier
					[ { x : 20, y : 13, d : 1 } ], // Marine Lander
					[ { x : 28, y : 14, d : 1 } ], // Marine
					[ { x : -10, y : -1, d : 0 }, { x : -10, y : 15, d : 0 }, { x : -10, y : 35, d : 0 }, { x : 43, y : -1, d : 1 }, { x : 43, y : 15, d : 1 }, { x : 43, y : 35, d : 1 } ], // Army base
					[ { x : -10, y : -1, d : 0 }, { x : -10, y : 15, d : 0 }, { x : -10, y : 35, d : 0 }, { x : 43, y : -1, d : 1 }, { x : 43, y : 15, d : 1 }, { x : 43, y : 35, d : 1 } ], // Air field
					[ { x : -10, y : -1, d : 0 }, { x : -10, y : 15, d : 0 }, { x : -10, y : 35, d : 0 }, { x : 43, y : -1, d : 1 }, { x : 43, y : 15, d : 1 }, { x : 43, y : 35, d : 1 } ], // Harbour
					[ { x : 31, y : 10, d : 0 }, { x : 31, y : 12, d : 0 } ], // Bunker
					[ [ { x : 31, y : 18, d : 1 } ], [ { x : 30, y : 12, d : 1 }, { x : 30, y : 12, d : 1 } ] ], // Sub marine
					[ ], // Radar
					[ ], // Flag
					[ ], // Stealth Bomber
				]
			]
		];

		// Ammo icons
		[Embed(source='../../assets/unit_images/ammo_back.png')]
		private static var ammoBackClass : Class;
		private static var ammoBackData : AnimatedBitmapData = new AnimatedBitmapData(ammoBackClass, 2);
		[Embed(source='../../assets/unit_images/ammo_icon.png')]
		private static var ammoIconClass : Class;
		
		// Placement image
		[Embed(source='../../assets/unit_images/base_placement.png')]
		private static var basePlacementClass : Class;

		// Unit this graphic belongs to
		private var unit : UnitState;
		
		// Current state
		private var stateUnknown : Boolean = false;
		private var facingLeft : Boolean;
		private var unitNumber : int;

		// Parents for the images
		private var unitImage : Image;
		private var unitTextImage : Image;
		
		// Properties
		private var showIcons : Boolean;
		private var mapConfig : MapConfig;
		
		// Visual components
		private var unitSprite : Sprite;
		private var unitSpriteChildren : Array = new Array();
		private var unitPlacementSprite : BitmapAsset;
		private var unitText : BitmapText;
		
		// Constructor		
		public function UnitGraphics(unit : UnitState, unitImage : Image, unitTextImage : Image, showIcons : Boolean, mapConfig : MapConfig) : void
		{
			// Store parameters
			this.unit = unit;
			this.unitImage = unitImage;
			this.unitTextImage = unitTextImage;
			this.showIcons = showIcons;
			this.mapConfig = mapConfig;
			
			// Determine initial orientation
			facingLeft = unit.faction == Faction.f2;
			
			// Unit image
			unitSprite = new Sprite();
			unitSprite.mouseEnabled = false;
			unitImage.addChild(unitSprite);
			
			// Text
			if (unitTextImage != null)
				unitText = new BitmapText(unitTextImage, 2, 0, BitmapText.typeUnit);
			
			// Update
			updateGraphics();
			setTextColor(BitmapText.colorWhite);
		}

		// Destroy all resources		
		public function destroy() : void
		{
			destroyChildren();

			unitImage.removeChild(unitSprite);

			if (unitText != null)
				unitText.destroy();
		}
		
		// Display the whole unit or not
		public function setVisible(v : Boolean) : void
		{
			unitSprite.visible = v && (unit.armour > 0 || getStateUnknown());
			
			setTextVisible(v);
		}		
		
		// Display the text or not
		public function setTextVisible(v : Boolean) : void
		{
			if (unitText != null)
			{
				v = v && !getStateUnknown() && unit.container == null && (unit.armour > 0 || getStateUnknown());
			
				unitText.setVisible(v);
			}
		}		

		// Set unit text color
		public function setTextColor(color : int) : void
		{
			if (unitText != null)
				unitText.setColor(color);
		}
		
		// Get unit text color
		public function getTextColor() : int
		{
			if (unitText != null)
				return unitText.getColor();
			else
				return BitmapText.colorWhite;
		}
		
		// Update the graphic for the unit
		public function updateGraphics() : void
		{
			// Update state
			unitNumber = getStateUnknown()? UnitConfig.unknownUnit.imageNumber : unit.unitConfig.imageNumber;
			
			// Update graphics
			if (unitText != null)
				unitText.setValue(unit.armour);
			updateSprites();
		}
		
		// Destroy child display objects
		private function destroyChildren() : void
		{
			for each (var c : DisplayObject in unitSpriteChildren)
			{
				if (c is AnimatedBitmap)
					(c as AnimatedBitmap).stop();
				unitSprite.removeChild(c);
			}
			unitSpriteChildren = new Array();
		}
		
		// Internal function to update the sprites
		private function updateSprites() : void
		{
			// Remove old images
			destroyChildren();

			// Create new image
			var unitAsset : BitmapAsset = new (getImageClass(unit.faction, unitNumber, facingLeft))();
			unitSprite.addChild(unitAsset);
			unitSpriteChildren.push(unitAsset);

			if (!getStateUnknown() && showIcons)
			{
				// Create container indicators
				var containerIndicatorClass : Class = containerIndicators[unit.unitConfig.imageNumber];
				if (containerIndicatorClass != null)
					for (var i : int = 0; i < unit.containedUnits.length; ++i)
					{
						var position : Object = containerIndicatorLocations[unit.unitConfig.imageNumber][i];
						var indicator : BitmapAsset = new containerIndicatorClass();
						indicator.x = position.x;
						indicator.y = position.y;
						unitSprite.addChild(indicator);
						unitSpriteChildren.push(indicator);
					}					
				
				if (unit.unitConfig.maxAmmo > 0)
				{
					// Ammo background
					var ammoBackAsset : AnimatedBitmap = new AnimatedBitmap(ammoBackData);
					ammoBackAsset.x = 0;
					ammoBackAsset.y = 12;
					if (unit.ammo > 0) 
						ammoBackAsset.setFrame(0);
					else
						ammoBackAsset.play(500, AnimatedBitmap.LOOP_ON, false);
					unitSprite.addChild(ammoBackAsset);
					unitSpriteChildren.push(ammoBackAsset);
			
					// Create ammo indicators
					for (var a : int = 0; a < unit.ammo; ++a)
					{				
						var ammoIconAsset : BitmapAsset = new ammoIconClass();
						ammoIconAsset.x = 1;
						ammoIconAsset.y = 21 - a * 2;
						unitSprite.addChild(ammoIconAsset);
						unitSpriteChildren.push(ammoIconAsset);
					}				
				}
			}
		}
	
		// Position unit using coordinates
		public function moveTo(x : int, y : int) : void
		{
			// Move graphic
			unitSprite.x = x;
			unitSprite.y = y;

			// Move text
			if (unitText != null)
				unitText.setPosition(x + 12, y);
		}
		
		// Position unit on tile
		public function moveToCurrentTile() : void
		{
			var tileLocation : Object = mapConfig.getTileLocation(unit.locationX, unit.locationY);			
			
			moveTo(tileLocation.x, tileLocation.y);
		}

		// Call before starting the move animation		
		public function doMoveStart() : void
		{
			moveToTop();
			
			unitSprite.visible = true;
		
			setTextVisible(false);
		}
		
		// Call after move animation has completed
		public function doMoveStop() : void
		{
			moveToCurrentTile();

			unitSprite.visible = unit.container == null;
			
			setTextVisible(true);
		}
				
		// Call to initiate dragging
		public function doStartDrag() : void
		{
			moveToTop();
		
			unitSprite.startDrag();
			
			setTextVisible(false);
			
			if (unit.unitConfig.isBase)
			{
				unitPlacementSprite = new basePlacementClass();
				unitPlacementSprite.alpha = 0.5;
				unitPlacementSprite.x = -68;
				unitPlacementSprite.y = -35;
				unitSprite.addChild(unitPlacementSprite);
			}
		}
		
		// Call to stop dragging
		public function doStopDrag() : void
		{
			unitSprite.stopDrag();	
			
			moveTo(unitSprite.x, unitSprite.y);
			
			setTextVisible(true);

			if (unitPlacementSprite != null)
			{
				unitSprite.removeChild(unitPlacementSprite);
				unitPlacementSprite = null;
			}
		}
		
		// Function to change icon of unit into the question mark icon (fog of war)
		public function setStateUnknown(unknown : Boolean) : void
		{
			if (unknown == stateUnknown)
				return;
			stateUnknown = unknown;
				
			updateGraphics();
			setTextVisible(!unknown);
		}
		
		// Check if unit state is unknown
		public function getStateUnknown() : Boolean
		{
			return stateUnknown || unit.unitConfig == UnitConfig.unknownUnit;
		}
		
		// Set detection status
		public function setStateDetected(detected : Boolean) : void
		{
			unitSprite.alpha = detected? 1.0 : 0.5;
		}
		
		// Set last unit direction
		public function setFacingLeft(left : Boolean) : void
		{
			if (facingLeft == left)
				return;
			facingLeft = left;
			
			updateSprites();
		}
		
		// Move graphic to the top
		private function moveToTop() : void
		{
			var p : DisplayObjectContainer = unitSprite.parent;			
			p.swapChildren(p.getChildAt(p.numChildren - 1), unitSprite);
		}
		
		// Get unit image
		public static function getImageClass(faction : int, unitNumber : int, facingLeft : Boolean) : Class
		{
			if (Faction.toArmy(faction) == Army.red)
				return facingLeft? redUnitsL[unitNumber] : redUnitsR[unitNumber];
			else
				return facingLeft? blueUnitsL[unitNumber] : blueUnitsR[unitNumber];
		}
		
		// Get unit properties
		public static function getUnitProperties(unit : UnitState) : Object
		{
			return unitProperties[unit.unitConfig.imageNumber];
		}
	}
}
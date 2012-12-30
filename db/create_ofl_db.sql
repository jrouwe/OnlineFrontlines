DROP DATABASE IF EXISTS `onlinefrontlines`;

SET storage_engine=InnoDB;

CREATE DATABASE `onlinefrontlines` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

USE `onlinefrontlines`;

GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON onlinefrontlines.* TO 'onlinefrontlines'@'localhost' IDENTIFIED BY 'onlinefrontlines';
GRANT SELECT ON mysql.proc TO 'onlinefrontlines'@'localhost';

CREATE TABLE `users` (                      
	`id` bigint(20) NOT NULL auto_increment,  
	`facebookId` char(255),
	`username` char(32) NOT NULL,             
	`realname` char(255),
	`email` char(255),
	`emailIsPublic` tinyint(1) NOT NULL default '0',
	`country` char(255),
	`city` char(255),
	`website` char(255),
	`isAdmin` tinyint(1) NOT NULL default '0',
	`army` int(11) NOT NULL,  
	`creationTime` bigint(20) NOT NULL default '0',
	`receiveGameEventsByMail` tinyint(1) NOT NULL default '1',
	`autoDefendOwnedCountry` tinyint(1) NOT NULL default '0',
	`showHelpBalloons` tinyint(1) NOT NULL default '1',
	`autoDeclineFriendlyDefender` tinyint(1) NOT NULL default '0',
	PRIMARY KEY (`id`),                      
	UNIQUE KEY `facebookId` (`facebookId`)
);

CREATE TABLE `user_stats` (
	`userId` bigint(20) NOT NULL,
	`gamesPlayed` int(11) NOT NULL default '0',
	`gamesWon` int(11) NOT NULL default '0',
	`gamesLost` int(11) NOT NULL default '0',
	`totalPoints` int(11) NOT NULL default '0',
	`currentVictoryStreak` int(11) NOT NULL default '0',
	`maxVictoryStreak` int(11) NOT NULL default '0',
	`creationTime` bigint(20) NOT NULL default '0',
	PRIMARY KEY (`userId`),
	FOREIGN KEY (`userId`) REFERENCES `users` (`id`) ON DELETE CASCADE
);

CREATE TABLE `maps` (                       
	`id` bigint(20) NOT NULL auto_increment,  
	`name` char(32) NOT NULL,                 
	`sizeX` int(11) NOT NULL,                 
	`sizeY` int(11) NOT NULL,                 
	`tileImageNumbers` text NOT NULL,   
	`tileOwners` text NOT NULL,   
	`creatorUserId` bigint(20) DEFAULT '3',
	PRIMARY KEY (`id`),
	UNIQUE KEY `name` (`name`),       
	FOREIGN KEY (`creatorUserId`) REFERENCES `users` (`id`) ON DELETE CASCADE
);
        
CREATE TABLE `terrain` (                
	`id` bigint(20) NOT NULL auto_increment,             
	`name` char(32) NOT NULL,             
	`victoryPoints` int(11) NOT NULL,
	`strengthModifier` int(11) NOT NULL DEFAULT 0,                                                                                
	PRIMARY KEY (`id`),                   
	UNIQUE KEY `name` (`name`)
);        
        
CREATE TABLE `terrain_tile_properties` (     
	`terrainId` bigint(20) NOT NULL,             
	`tileImageNumber` int(11) NOT NULL,           
	`edgeTerrainImageNumber` int(11) NOT NULL,           
	`openTerrainImageNumber` int(11) NOT NULL,           
	PRIMARY KEY (`terrainId`,`tileImageNumber`),
	FOREIGN KEY (`terrainId`) REFERENCES `terrain` (`id`) ON DELETE CASCADE
);

CREATE TABLE `units` (                      
	`id` bigint(20) NOT NULL auto_increment,  
	`name` char(32) NOT NULL,                 
	`imageNumber` int(11) NOT NULL,           
	`unitClass` int(11) NOT NULL,             
	`maxArmour` int(11) NOT NULL,             
	`maxAmmo` int(11) NOT NULL,               
	`visionRange` int(11) NOT NULL,           
	`movementPoints` int(11) NOT NULL,        
	`actions` int(11) NOT NULL,        
	`containerMaxUnits` int(11) NOT NULL,        
	`containerArmourPercentagePerTurn` int(11) NOT NULL,        
	`containerAmmoPercentagePerTurn` int(11) NOT NULL,        
	`transformableToUnitId` int(11) NOT NULL,        
	`transformableType` int(11) NOT NULL,
	`victoryPoints` int(11) NOT NULL,
	`description` text,
	`isBase` tinyint(1) NOT NULL,
	`victoryCategory` int(11) NOT NULL,
	`beDetectedRange` int(11) NOT NULL,
	PRIMARY KEY (`id`),                       
	UNIQUE KEY `name` (`name`)        
);
    
CREATE TABLE `units_set_up_on` (
	`unitId` bigint(20) NOT NULL,
	`terrainId` bigint(20) NOT NULL,
	PRIMARY KEY (`unitId`,`terrainId`),
	FOREIGN KEY (`unitId`) REFERENCES `units` (`id`) ON DELETE CASCADE,
	FOREIGN KEY (`terrainId`) REFERENCES `terrain` (`id`) ON DELETE CASCADE  
);

CREATE TABLE `units_set_up_next_to` (
	`unitId` bigint(20) NOT NULL,
	`terrainId` bigint(20) NOT NULL,
	PRIMARY KEY (`unitId`,`terrainId`),
	FOREIGN KEY (`unitId`) REFERENCES `units` (`id`) ON DELETE CASCADE,
	FOREIGN KEY (`terrainId`) REFERENCES `terrain` (`id`) ON DELETE CASCADE  
);

CREATE TABLE `units_movement_cost` (                                                                      
	`unitId` bigint(20) NOT NULL,                                                                           
	`terrainId` bigint(20) NOT NULL,                                                                        
	`movementCost` int(11) NOT NULL,                                                                        
	PRIMARY KEY (`unitId`,`terrainId`),                                                                    
	FOREIGN KEY (`unitId`) REFERENCES `units` (`id`) ON DELETE CASCADE,
	FOREIGN KEY (`terrainId`) REFERENCES `terrain` (`id`) ON DELETE CASCADE  
);
     
CREATE TABLE `units_strength_properties` (                                                                      
	`unitId` bigint(20) NOT NULL,                                                                                 
	`enemyUnitClass` int(11) NOT NULL,                                                                            
	`strengthWithAmmo` int(11) NOT NULL,                                                                          
	`strengthWithoutAmmo` int(11) NOT NULL,                                                                       
	`attackRange` int(11) NOT NULL,           
	PRIMARY KEY (`unitId`,`enemyUnitClass`),                                                                     
	FOREIGN KEY (`unitId`) REFERENCES `units` (`id`) ON DELETE CASCADE  
);

CREATE TABLE `units_container` (
	`containerUnitId` bigint(20) NOT NULL,
	`containedUnitId` bigint(20) NOT NULL,
	PRIMARY KEY (`containerUnitId`,`containedUnitId`),
	FOREIGN KEY (`containerUnitId`) REFERENCES `units` (`id`) ON DELETE CASCADE,
	FOREIGN KEY (`containedUnitId`) REFERENCES `units` (`id`) ON DELETE CASCADE
);

CREATE TABLE `deployment` (
	`id` bigint(20) NOT NULL auto_increment,
	`name` char(32) NOT NULL,
	`creatorUserId` bigint(20) DEFAULT '3',
	PRIMARY KEY (`id`),
	UNIQUE KEY `name` (`name`),        
	FOREIGN KEY (`creatorUserId`) REFERENCES `users` (`id`) ON DELETE CASCADE
);

CREATE TABLE `deployment_amount` (
	`deploymentId` bigint(20) NOT NULL auto_increment,
	`unitId` bigint(20) NOT NULL,
	`amount` int(11) NOT NULL,
	PRIMARY KEY (`deploymentId`,`unitId`),
	FOREIGN KEY (`deploymentId`) REFERENCES `deployment` (`id`) ON DELETE CASCADE,
	FOREIGN KEY (`unitId`) REFERENCES `units` (`id`) ON DELETE CASCADE
);

CREATE TABLE `country_types` (                       
	`id` bigint(20) NOT NULL auto_increment,  
	`name` char(32) NOT NULL,
	`description` char(255) default '',
	PRIMARY KEY (`id`)
);                 

CREATE TABLE `country_configs` (                       
	`id` bigint(20) NOT NULL auto_increment,  
	`name` char(32) NOT NULL,                 
	`mapId` bigint(20) NOT NULL,
	`deploymentConfigId1` bigint(20) NOT NULL,
	`deploymentConfigId2` bigint(20) NOT NULL,
	`scoreLimit` int(11) NOT NULL,   
	`fogOfWarEnabled` int(1) NOT NULL,   
	`isCapturePoint` int(1) NOT NULL,
	`countryTypeId` bigint(20),
	`requiredLevel` int(11) DEFAULT '0',
	`creatorUserId` bigint(20) DEFAULT '3',
	`publishState` int(11) DEFAULT '2',
	`suitableForAI` int(1) DEFAULT '0',
	PRIMARY KEY (`id`),
	UNIQUE KEY `name` (`name`),
	FOREIGN KEY (`mapId`) REFERENCES `maps` (`id`) ON DELETE CASCADE,
	FOREIGN KEY (`countryTypeId`) REFERENCES `country_types` (`id`) ON DELETE CASCADE,
	FOREIGN KEY (`deploymentConfigId1`) REFERENCES `deployment` (`id`) ON DELETE CASCADE,
	FOREIGN KEY (`deploymentConfigId2`) REFERENCES `deployment` (`id`) ON DELETE CASCADE,
	FOREIGN KEY (`creatorUserId`) REFERENCES `users` (`id`) ON DELETE CASCADE
);
        
CREATE TABLE `lobbies` (                       
	`id` bigint(20) NOT NULL auto_increment,  
	`name` char(32) NOT NULL,                 
	`backgroundImageNumber` int(11) NOT NULL,                 
	`sizeX` int(11) NOT NULL,                 
	`sizeY` int(11) NOT NULL,    
	`tileCountryConfigIds` text NOT NULL,   
	`worldMapEnterButtonX` int(11) NOT NULL,
	`worldMapEnterButtonY` int(11) NOT NULL,
	`minRequiredLevel` int(11) NOT NULL DEFAULT 0,
	`maxLevel` int(11) NOT NULL DEFAULT -1,
	`maxUsers` int(11) NOT NULL DEFAULT 100,
	PRIMARY KEY (`id`),
	UNIQUE KEY `name` (`name`)        
);

CREATE TABLE `user_stats_captures` (
	`userId` bigint(20) NOT NULL,
	`lobbyId` bigint(20) NOT NULL,
	`count` int(11) NOT NULL,
	FOREIGN KEY (`userId`) REFERENCES `users` (`id`) ON DELETE CASCADE,
	FOREIGN KEY (`lobbyId`) REFERENCES `lobbies` (`id`) ON DELETE CASCADE,
	PRIMARY KEY (`userId`, `lobbyId`)
);
        
CREATE TABLE `user_stats_units` (
	`userId` bigint(20) NOT NULL,
	`unitId` bigint(20) NOT NULL,
	`numAttacks` int(11) NOT NULL default '0',
	`numDefends` int(11) NOT NULL default '0',
	`damageDealt` int(11) NOT NULL default '0',
	`damageReceived` int(11) NOT NULL default '0',
	`kills` int(11) NOT NULL default '0',
	`deaths` int(11) NOT NULL default '0',
	INDEX (`userId`, `unitId`),
	FOREIGN KEY (`userId`) REFERENCES `users` (`id`) ON DELETE CASCADE,
	FOREIGN KEY (`unitId`) REFERENCES `units` (`id`) ON DELETE CASCADE
);

CREATE TABLE `lobby_country_state` (
	`lobbyId` bigint(20) NOT NULL,
	`locationX` int(11) NOT NULL,
	`locationY` int(11) NOT NULL,
	`ownerUserId` bigint(20),
	`ownerExclusiveTime` bigint(20),
	`army` int(11) NOT NULL,
	UNIQUE KEY `UK_lobby_country_state` (`lobbyId`, `locationX`, `locationY`),
	FOREIGN KEY (`lobbyId`) REFERENCES `lobbies` (`id`) ON DELETE CASCADE,
	FOREIGN KEY (`ownerUserId`) REFERENCES `users` (`id`) ON DELETE CASCADE
);
        
CREATE TABLE `lobby_users` (
	`lobbyId` bigint(20) NOT NULL,
	`userId` bigint(20) NOT NULL,
	`defendedCountryX` int(11) NOT NULL,
	`defendedCountryY` int(11) NOT NULL,
	`attackedCountryX` int(11) NOT NULL,
	`attackedCountryY` int(11) NOT NULL,
	`hasAcceptedChallenge` int(1),
	`challengeValidUntil` bigint(20),
	`army` int(11) NOT NULL,
	UNIQUE KEY `UK_lobby_user_state` (`lobbyId`, `userId`),
	FOREIGN KEY (`lobbyId`) REFERENCES `lobbies` (`id`) ON DELETE CASCADE,
	FOREIGN KEY (`userId`) REFERENCES `users` (`id`) ON DELETE CASCADE
);
        
CREATE TABLE `lobby_chat` (
	`id` bigint(20) NOT NULL auto_increment,  
	`lobbyId` bigint(20) NOT NULL,
	`userId` bigint(20) NOT NULL,
	`message` text NOT NULL,
	PRIMARY KEY (`id`),
	INDEX (`lobbyId`),
	FOREIGN KEY (`lobbyId`) REFERENCES `lobbies` (`id`) ON DELETE CASCADE,
	FOREIGN KEY (`userId`) REFERENCES `users` (`id`) ON DELETE CASCADE
);

CREATE TABLE `games` (
	`id` bigint(20) NOT NULL auto_increment,
	`userId1` bigint(20),
	`userId2` bigint(20),
	`countryConfigId` bigint(20),
	`faction1IsRed` int(1),
	`faction1Starts` int(1),
	`creationTime` bigint(20),
	`turnEndTime` bigint(20),
	`turnNumber` int(11) NOT NULL DEFAULT '0',
	`currentPlayer` int(11) NOT NULL DEFAULT '0',
	`winningFaction` int(11) NOT NULL,
	`winningScoreFaction1` int(11) NOT NULL DEFAULT '0',
	`winningScoreFaction2` int(11) NOT NULL DEFAULT '0',
	`winningNumberUnitsDestroyedFaction1` int(11) NOT NULL DEFAULT '0',
	`winningNumberUnitsDestroyedFaction2` int(11) NOT NULL DEFAULT '0',	
	`winningTime` bigint(20) NOT NULL DEFAULT '0',	
	`lobbyId` bigint(20),
	`attackedCountryX` int(11),
	`attackedCountryY` int(11),   
	`defendedCountryX` int(11),
	`defendedCountryY` int(11),   
	`playByMail` int(1),
	`corrupt` int(1) DEFAULT '0',
	`actions` text NOT NULL,
	`lobbyProcessedGame` int(1) DEFAULT '0',
	PRIMARY KEY (`id`),
	FOREIGN KEY (`userId1`) REFERENCES `users` (`id`) ON DELETE CASCADE,
	FOREIGN KEY (`userId2`) REFERENCES `users` (`id`) ON DELETE CASCADE,
	FOREIGN KEY (`countryConfigId`) REFERENCES `country_configs` (`id`) ON DELETE CASCADE
);

CREATE TABLE `feedback` (
	`id` bigint(20) NOT NULL auto_increment,
	`reporterUserId` bigint(20),
	`opponentUserId` bigint(20),
	`gameId` bigint(20) NOT NULL,
	`score` int(11) NOT NULL,
	`comments` text,
	`reply` text,
	`creationTime` bigint(20) NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE KEY `UK_feedback` (`reporterUserId`, `opponentUserId`, `gameId`),
	FOREIGN KEY (`reporterUserId`) REFERENCES `users` (`id`) ON DELETE CASCADE,
	FOREIGN KEY (`opponentUserId`) REFERENCES `users` (`id`) ON DELETE CASCADE,
	FOREIGN KEY (`gameId`) REFERENCES `games` (`id`) ON DELETE CASCADE
);

CREATE TABLE `feedback_score_per_pair` (
	`id` bigint(20) NOT NULL auto_increment,
	`reporterUserId` bigint(20),
	`opponentUserId` bigint(20),
	`score` int(11) NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE KEY `UK_feedback_score_per_pair` (`reporterUserId`, `opponentUserId`),
	FOREIGN KEY (`reporterUserId`) REFERENCES `users` (`id`) ON DELETE CASCADE,
	FOREIGN KEY (`opponentUserId`) REFERENCES `users` (`id`) ON DELETE CASCADE
);

CREATE TABLE `tips` (
	`id` bigint(20) NOT NULL auto_increment,
	`image` varchar(256),
	`text` text NOT NULL,
	`showInGame` int(1) DEFAULT 1,
	PRIMARY KEY (`id`)
);

CREATE VIEW feedback_score AS SELECT opponentUserId AS userId, SUM(score) AS score FROM feedback_score_per_pair GROUP BY userId;

CREATE VIEW user_stats_total_points AS (SELECT userId, users.username, users.army, totalPoints FROM user_stats JOIN users ON users.id=userId) ORDER BY totalPoints DESC LIMIT 1000;
CREATE VIEW user_stats_units_destroyed AS (SELECT userId, users.username, users.army, SUM(kills) AS totalUnitsDestroyed FROM user_stats_units JOIN users ON users.id=userId GROUP BY userId) ORDER BY totalUnitsDestroyed DESC LIMIT 10;
CREATE VIEW user_stats_win_percentage AS (SELECT userId, users.username, users.army, FLOOR(gamesWon*100/gamesPlayed) AS winPercentage FROM user_stats JOIN users ON users.id=userId WHERE gamesPlayed>10) ORDER BY winPercentage DESC LIMIT 10;
CREATE VIEW user_stats_total_countries AS (SELECT ownerUserId AS userId, users.username, users.army, COUNT(ownerUserId) AS totalCountries FROM lobby_country_state JOIN users ON users.id=ownerUserId WHERE ownerUserId IS NOT NULL GROUP BY ownerUserId) ORDER BY totalCountries DESC LIMIT 10;
CREATE VIEW user_stats_total_captures AS (SELECT userId, users.username, users.army, SUM(count) AS totalCaptures FROM user_stats_captures JOIN users ON users.id=userId GROUP BY userId) ORDER BY totalCaptures DESC;

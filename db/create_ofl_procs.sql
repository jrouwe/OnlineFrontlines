USE `onlinefrontlines`$$

/*
	Get list of top matches
*/
CREATE PROCEDURE getTopMatches(inMidnightTime LONG, inFaction1 INT, inFaction2 INT, inMinScore INT, inMinTurns INT, inComputerID INT)
BEGIN
	DECLARE day INT DEFAULT 24 * 60 * 60 * 1000;
	
	SELECT 
		games.id AS id,
		country_configs.mapId AS mapId,
		country_configs.name AS countryConfigName,
		country_types.name AS mapType,
		user1.id AS id1,
		user2.id AS id2,
		user1.username AS name1,
		user2.username AS name2,
		stats1.totalPoints AS points1,
		stats2.totalPoints AS points2,
		winningFaction, 
		turnNumber,
		faction1IsRed,
		winningNumberUnitsDestroyedFaction1 + winningNumberUnitsDestroyedFaction2 AS unitsDestroyed,
		(stats1.totalPoints + stats2.totalPoints)
			* ((ABS(winningScoreFaction1 - winningScoreFaction2) + 1) / (winningScoreFaction1 + winningScoreFaction2))
			* IF(inMidnightTime - winningTime < day, 1, day / (inMidnightTime - winningTime)) AS importance
		
		FROM games 

		JOIN users AS user1 ON user1.id=userId1
		JOIN users AS user2 ON user2.id=userId2
		JOIN user_stats AS stats1 ON stats1.userId=userId1
		JOIN user_stats AS stats2 ON stats2.userId=userId2
		JOIN country_configs ON countryConfigId=country_configs.id
		JOIN country_types ON country_configs.countryTypeId=country_types.id

		WHERE 
			(userId1 <> inComputerID)
			AND (userId2 <> inComputerID)
			AND (winningFaction = inFaction1 OR winningFaction = inFaction2) 
			AND (turnNumber > inMinTurns)
			AND (winningScoreFaction1 > inMinScore OR winningScoreFaction2 > inMinScore) 
			AND (winningTime < inMidnightTime)
			AND (NOT corrupt)

		ORDER BY importance 
			
		DESC LIMIT 10;
END$$

/*
	Update game state
*/					
CREATE PROCEDURE updateGame(inActions TEXT, inTurnEndTime LONG, inTurnNumber INT, inCurrentPlayer INT, inWinningFaction INT, inWinningScoreFaction1 INT, inWinningScoreFaction2 INT, inWinningNumberUnitsDestroyedFaction1 INT, inWinningNumberUnitsDestroyedFaction2 INT, inWinningTime LONG, inGameId INT)
proc: BEGIN
	UPDATE games SET actions=inActions, turnEndTime=inTurnEndTime, turnNumber=inTurnNumber, currentPlayer=inCurrentPlayer, winningFaction=inWinningFaction, winningScoreFaction1=inWinningScoreFaction1, winningScoreFaction2=inWinningScoreFaction2, winningNumberUnitsDestroyedFaction1=inWinningNumberUnitsDestroyedFaction1, winningNumberUnitsDestroyedFaction2=inWinningNumberUnitsDestroyedFaction2, winningTime=inWinningTime WHERE id=inGameId;
END$$

/*
 	Create or update lobby user
 */
CREATE PROCEDURE createOrUpdateLobbyUser(inLobbyId INT, inUserId INT, inDefendedCountryX INT, inDefendedCountryY INT, inAttackedCountryX INT, inAttackedCountryY INT, inHasAcceptedChallenge INT, inChallengeValidUntil LONG, inArmy INT)
proc: BEGIN
	INSERT INTO lobby_users (lobbyId, userId, defendedCountryX, defendedCountryY, attackedCountryX, attackedCountryY, hasAcceptedChallenge, challengeValidUntil, army) VALUES (inLobbyId, inUserId, inDefendedCountryX, inDefendedCountryY, inAttackedCountryX, inAttackedCountryY, inHasAcceptedChallenge, inChallengeValidUntil, inArmy) ON DUPLICATE KEY UPDATE defendedCountryX=inDefendedCountryX, defendedCountryY=inDefendedCountryY, attackedCountryX=inAttackedCountryX, attackedCountryY=inAttackedCountryY, hasAcceptedChallenge=inHasAcceptedChallenge, challengeValidUntil=inChallengeValidUntil, army=inArmy;
END$$

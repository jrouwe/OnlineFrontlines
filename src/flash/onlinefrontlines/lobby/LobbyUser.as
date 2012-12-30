package onlinefrontlines.lobby
{
	/*
	 * User that has participated / is participating in this lobby
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
	public class LobbyUser 
	{
		/**
		 * Lobby we belong to
		 */
		public var lobbyState : LobbyState;
		
		/**
		 * User id
		 */
		public var userId : int;
		
		/**
		 * User name
		 */
		public var username : String;
		
		/**
		 * Army of user (can change for non registered users)
		 */
		public var army : int;
		
		/**
		 * If user is still connected
		 */
		public var isConnected : Boolean;
	
		/**
		 * Country that the user is defending
		 */
		private var defendedCountry : Country;
		
		/**
		 * Country that the user is attacking
		 */
		private var attackedCountry : Country;
		
		/**
		 * Indicates when the user is linked to an attacked / defended country 
		 * if he still needs to accept the challenge or not 
		 */
		public var hasAcceptedChallenge : Boolean;
		
		/**
		 * Current rank
		 */
		public var rank : int = 0;
		
		/**
		 * Current level
		 */
		public var level : int = 1;
		
		/**
		 * Leaderboard position
		 */
		public var leaderboardPosition : int = 1;
		
		/**
		 * Player feedback score
		 */
		public var feedbackScore : int = 0;
		
		/**
		 * Games won
		 */
		public var gamesWon : int = 0;
		
		/**
		 * Games lost
		 */
		public var gamesLost : int = 0;
		
		/**
		 * If user automatically defends his countries
		 */
		public var autoDefendOwnedCountry : Boolean = false;
		
		/**
    	 * If user automatically declines friendly defenders
    	 */
		public var autoDeclineFriendlyDefender : Boolean = false;
		
		/**
		 * Constructor
		 */	
		public function LobbyUser(lobbyState : LobbyState, userId : int, username : String)
		{
			this.lobbyState = lobbyState;
			this.userId = userId;
			this.username = username;
		}

		/**
		 * Set country to defend and attack
		 */
		public function setCountries(defendedCountry : Country, attackedCountry : Country) : void
		{
			// Check NOP
			if (defendedCountry == this.defendedCountry
				&& attackedCountry == this.attackedCountry)
				return;
			
			// Unlink from countries
			if (this.defendedCountry != null)
			{
				this.defendedCountry.defender = null;
			}
			if (this.attackedCountry != null)
			{
				this.attackedCountry.attacker = null;
			}
			
			// Link to new countries
			if (defendedCountry != null)
			{
				defendedCountry.defender = this;
			}
			if (attackedCountry != null)
			{
				attackedCountry.attacker = this;
			}
		
			// Set pointers
			this.defendedCountry = defendedCountry;
			this.attackedCountry = attackedCountry;
		}
	
		/**
		 * Access to the currently defended country
		 */
		public function getDefendedCountry() : Country
		{
			return defendedCountry;
		}
	
		/**
		 * Access to the currently attacked country
		 */
		public function getAttackedCountry() : Country
		{
			return attackedCountry;
		}
		
		/**
		 * Get leaderboard position as text string
		 */
		public function getLeaderboardPosition() : String
		{
			if (leaderboardPosition > 0)
				return leaderboardPosition.toString();
			else
				return "Not ranked";
		}
	}
}
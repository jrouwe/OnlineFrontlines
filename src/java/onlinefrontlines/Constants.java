package onlinefrontlines;

/**
 * Application constants
 * 
 * @author jrouwe
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
public class Constants
{
	/**
	 * Authentication parameters
	 */
	public final static String AUTH_STRING = "auth";
	
	/**
	 * Default time an authentication string remains valid
	 */
	public final static long AUTH_TIME_DEFAULT = 4L * 60L * 60L * 1000L;

	/**
	 * Salt for all passwords
	 */
	public final static String PASSWORD_SALT = "onlinefrontlines";
	
	/**
	 * Key that contains the current action
	 */
	public final static String CURRENT_ACTION = "onlinefrontlines.currentAction";
	
	/**
	 * User ID for AI opponent 
	 */
	public final static int USER_ID_AI = 1;
	
	/**
	 * Timing properties for a normal game
	 */
	public final static int GAME_SETUP_TIME = 240 * 1000;
	public final static int GAME_MIN_TIME_PER_TURN = 60 * 1000;
	public final static int GAME_TIME_EXTRA_PER_UNIT = 20 * 1000;
	
	/**
	 * Time per turn in play by mail mode
	 */
	public final static long PLAY_BY_MAIL_TURN_TIME = 5L * 24L * 60L * 60L * 1000L;

	/**
	 * Time per turn when playing in a lobby
	 */
	public final static long PLAY_LOBBY_TURN_TIME = 3L * 24L * 60L * 60L * 1000L;
	
	/**
	 * Time per turn when playing in a lobby for a high ranked user
	 */
	public final static int HIGH_RANKED_USER_LEVEL = 10;
	public final static long PLAY_LOBBY_TURN_TIME_HIGH_RANKED_USER = 21L * 24L * 60L * 60L * 1000L;

	/**
	 * Time a challenge stays valid in the lobby
	 */
	public final static long LOBBY_CHALLENGE_TIME = 3L * 24L * 60L * 60L * 1000L;

	/**
	 * Time delay between polling from the client in milliseconds
	 */
	public final static long MIN_DELAY_BETWEEN_CLIENT_POLLS = 3000;
	public final static long MAX_DELAY_BETWEEN_CLIENT_POLLS = 50000;
	public final static long CLIENT_POLL_TIMEOUT = 60000;	
	
	/**
	 * Time for which a user can uniquely defend a country after he conqueres it
	 */
	public final static long EXCLUSIVE_TIME_AFTER_CONQUERED = 24L * 60L * 60L * 1000L;
	
	/**
	 * Time after which a lobby gets reset when all tiles have been conquered
	 */
	public final static long TIME_BEFORE_RESET_AFTER_ALL_CONQUERED = 10 * 60 * 1000;
}
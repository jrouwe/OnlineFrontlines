package onlinefrontlines.admin.web;

import org.apache.log4j.Logger;
import java.util.ArrayList;
import java.util.Random;
import java.sql.SQLException;
import onlinefrontlines.Army;
import onlinefrontlines.auth.*;
import onlinefrontlines.web.*;
import onlinefrontlines.feedback.*;
import onlinefrontlines.game.*;
import onlinefrontlines.userstats.*;

/**
 * This action generates dummy data in the database for testing purposes
 * 
 * @author jorrit
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
public class GenerateDummyData extends WebAction 
{	
	private static final Logger log = Logger.getLogger(GenerateDummyData.class);
	
	private static final int maxUsers = 50;
	private static final int maxGames = 50;
	private static final int maxFeedback = 50;
	private static final int maxStats = 100;
	
	private ArrayList<User> createdUsers = new ArrayList<User>();

	private static class Game
	{
		public GameState game;
		public User user1;
		public User user2;
	};
	private ArrayList<Game> createdGames = new ArrayList<Game>();
	
	/**
     * Execute the action
     */
    protected WebView execute() throws Exception 
    {
    	log.info("User '" + user.id + "' generated dummy data");
    	
		createUsers();
		createGames();
		createFeedback();
		createStats();
   		
        return getSuccessView();
    }
        
    /**
     * Create dummy users
     */
    private void createUsers()
    {
    	Random r = new Random();
    	
    	for (int i = 0; i < maxUsers; ++i)
    	{
    		try
    		{
    			// Create user
        		User u = new User();
        		u.username = "user" + Math.abs(r.nextInt(99999));
        		switch (r.nextInt(3))
        		{
        		case 0:
        			u.army = Army.red;
        			break;
        		case 1:
        			u.army = Army.blue;
        			break;
        		}        		
    			UserDAO.create(u);    			
    			createdUsers.add(u);
    		}
    		catch (Exception e)
    		{    			
    		}
    	}
    }
    
    /**
     * Create dummy games
     */
    private void createGames() throws Exception
    {
    	Random r = new Random();
    	
    	CountryConfig c = CountryConfigCache.getInstance().get(1);

    	for (int i = 0; i < maxGames; ++i)
    	{
    		try
    		{    			
    			Game g = new Game();
    			g.game = c.createGameState(true, true, 0, -1, -1, -1, -1, false, -1);
    			GameStateDAO.create(g.game);
    			g.user1 = createdUsers.get(r.nextInt(createdUsers.size()));
    			g.game.joinGame(Faction.f1, g.user1);
    			g.user2 = createdUsers.get(r.nextInt(createdUsers.size()));
    			g.game.joinGame(Faction.f2, g.user2);
    			createdGames.add(g);
    		}
    		catch (SQLException e)
    		{    			
    		}
    	}
    }

    /**
     * Create random feedback
     */
    private void createFeedback()
    {
    	Random r = new Random();
    	
    	for (int i = 0; i < maxFeedback; ++i)
    	{
    		try
    		{    			
    			Game g = createdGames.get(r.nextInt(createdGames.size()));
    			Feedback f = new Feedback();
    			f.gameId = g.game.id;
    			f.reporterUserId = g.user1.id;
    			f.opponentUserId = g.user2.id;
    			f.comments = "feedback-" + Math.abs(r.nextLong());
    			f.score = r.nextInt(3) - 1;
    			FeedbackDAO.create(f);
    		}
    		catch (SQLException e)
    		{    			
    		}
    	}
    }
    
    /**
     * Create random stats
     */
    private void createStats()
    {
    	Random r = new Random();
    	
    	for (int i = 0; i < maxStats; ++i)
    	{
    		try
    		{    			
    			// Accumulate user stats
    			User u = createdUsers.get(r.nextInt(createdUsers.size()));
    			UserStats s = new UserStats(u.id);
    			int mul = 1 + r.nextInt(10);
    			s.gamesPlayed = mul;
    			switch (r.nextInt(3))
    			{
    			case 0:
    				s.gamesWon = mul;
    				break;
    				
    			case 1:
    				s.gamesLost = mul;
    				break;
    			}
    			s.totalPoints = r.nextInt(300000);
    			UserStatsDAO.accumulateStats(s);
    		}
    		catch (SQLException e)
    		{    			
    		}
    	}
    }
}

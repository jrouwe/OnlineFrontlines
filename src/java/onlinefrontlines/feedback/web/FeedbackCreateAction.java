package onlinefrontlines.feedback.web;

import java.util.Map;
import java.util.LinkedHashMap;
import java.sql.SQLException;
import onlinefrontlines.web.*;
import onlinefrontlines.feedback.Feedback;
import onlinefrontlines.feedback.FeedbackDAO;
import onlinefrontlines.game.*;

/**
 * This adds feedback to the database
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
public class FeedbackCreateAction extends WebAction 
{
	/**
	 * Id of the game
	 */
	public int gameId;
	
	/**
	 * User to give feedback to
	 */
	public String opponentName;
	
	/**
	 * His current score
	 */
	public int opponentScore;
	
	/**
	 * Score
	 */
	public int score;
	
	/**
	 * User supplied comments
	 */
	public String comments;
	
	/**
	 * Get possible score values
	 */
	public Map<Integer, String> getScoreValues()
	{
		LinkedHashMap<Integer, String> m = new LinkedHashMap<Integer, String>();
		m.put(1, "Positive");
		m.put(0, "Neutral");
		m.put(-1, "Negative");
		return m;
	}

	/**
	 * Input action
	 */
	protected WebView input() throws Exception
	{
        // Get game summary
        GameStateDAO.Summary s = GameStateDAO.getGameSummary(gameId);        
        if (s == null)
        {
        	addActionError(getText("gameDoesNotExist"));
        	return getErrorView();
        }

        // Check players
    	if (s.player1Id == 0 || s.player2Id == 0)
		{
			addActionError(getText("gameDoesNotHave2Players"));
			return getErrorView();
		}
		
    	// Get opponent
		if (s.player1Id == user.id)
		{
			opponentName = s.player2Name;
			opponentScore = FeedbackDAO.getScore(s.player2Id);
		}
		else if (s.player2Id == user.id)
		{
			opponentName = s.player1Name;
			opponentScore = FeedbackDAO.getScore(s.player1Id);
		}
		else
		{
			addActionError(getText("youDoNotParticipate"));
			return getErrorView();
		}

    	return getInputView();
	}

    /**
     * Execute the action
     */
    protected WebView execute() throws Exception 
    {
        // Get game summary
        GameStateDAO.Summary s = GameStateDAO.getGameSummary(gameId);        
        if (s == null)
        {
        	addActionError(getText("gameDoesNotExist"));
        	return getErrorView();
        }

        // Check players
    	if (s.player1Id == 0 || s.player2Id == 0)
		{
			addActionError(getText("gameDoesNotHave2Players"));
			return getErrorView();
		}
		
    	// Get opponent
		if (s.player1Id == user.id)
		{
			opponentName = s.player2Name;
			opponentScore = FeedbackDAO.getScore(s.player2Id);
		}
		else if (s.player2Id == user.id)
		{
			opponentName = s.player1Name;
			opponentScore = FeedbackDAO.getScore(s.player1Id);
		}
		else
		{
			addActionError(getText("youDoNotParticipate"));
			return getErrorView();
		}

    	// Validate score
    	if (score < -1 || score > 1)
    	{
    		addFieldError("score", getText("invalidScore"));
    		return getInputView();
    	}
    	
    	// Validate comments    	
    	if (comments == null || comments.length() == 0)
    	{
    		addFieldError("comments", getText("commentsRequired"));
    		return getInputView();
    	}

    	// Add feedback to db
    	try
    	{
        	Feedback feedback = new Feedback();
    		if (s.player1Id == user.id)
    		{
    			feedback.reporterUserId = s.player1Id;
    			feedback.opponentUserId = s.player2Id;
    		}
    		else if (s.player2Id == user.id)
    		{
    			feedback.reporterUserId = s.player2Id;
    			feedback.opponentUserId = s.player1Id;
    		}
    		else
    			assert(false);
        	feedback.gameId = gameId;
        	feedback.score = score;
        	feedback.comments = comments;
        	FeedbackDAO.create(feedback);
    	}
    	catch (SQLException e)
    	{
    		addActionError(getText("feedbackAlreadyGiven"));
    		return getErrorView();
    	}
        
        return getSuccessView();
    }
}

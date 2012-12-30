package onlinefrontlines.feedback.web;

import onlinefrontlines.web.*;
import onlinefrontlines.feedback.Feedback;
import onlinefrontlines.feedback.FeedbackDAO;

/**
 * This adds reply to feedback to the database
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
public class FeedbackReplyAction extends WebAction 
{
	/**
	 * Feedback id
	 */
	public int feedbackId;
	
	/**
	 * Reply to the feedback
	 */
	public String reply;
	
	/**
	 * Output userId value for redirect
	 */
	public int userId;
	
    /**
     * Execute the action
     */
    protected WebView execute() throws Exception 
    {
    	// Validate reply
    	if (reply == null || reply.isEmpty())
    	{
    		addFieldError("reply", getText("replyRequired"));
    		return getInputView();
    	}
        
    	// Store user id for redirect to feedback page
        userId = user.id;

        // Get feedback
    	Feedback feedback = FeedbackDAO.find(feedbackId);
    	if (feedback == null)
    	{
    		addActionError(getText("feedbackDoesNotExist"));
    		return getErrorView();
    	}

    	// Check user
    	if (user.id != feedback.opponentUserId)
    	{
    		addActionError(getText("cannotReplyToFeedback"));
    		return getErrorView();
    	}
    	
    	// Check if there is already a reply
    	if (feedback.reply != null)
    	{
    		addActionError(getText("alreadyRepliedToFeedback"));
    		return getErrorView();
    	}
    	
    	// Update feedback
    	feedback.reply = reply;
    	FeedbackDAO.update(feedback);
        
        return new WebViewRedirect("FeedbackList.do?userId=" + userId);
    }
}

package onlinefrontlines.facebook.web;

import onlinefrontlines.web.*;
import onlinefrontlines.utils.*;

/**
 * This action displays the home page when activated in facebook
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
public class FacebookCanvas extends WebAction
{
	// Suppress warnings about facebook parameters
	public String app_request_type;
	public String notif_t;
	public String request_ids;
	public String ref;
	public String signed_request;
	public String fb_xd_fragment;
	public String fb_source;
	public String fb_bmpos;
	public String count;
	public String type;
	
	// Output url & bool
	public String targetUrl;
	public boolean targetIsInvitation;

	/**
	 * Execute the action
	 */
	protected WebView execute() throws Exception
	{
		if (request_ids != null && request_ids.length() > 0)
		{
			targetUrl = GlobalProperties.getInstance().getString("app.url") + "/PBMShowInvitation.do?requestIds=" + request_ids;
			targetIsInvitation = true;
		}
		else
		{
			targetUrl = GlobalProperties.getInstance().getString("app.url") + "/Home.do";
			targetIsInvitation = false;
		}
		
		return getSuccessView();
	}
}
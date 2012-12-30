package onlinefrontlines.web;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import onlinefrontlines.utils.GlobalProperties;

/**
 * Redirects the browser to a new page
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
public class WebViewRedirect implements WebView 
{
	/**
	 * Url to redirect to
	 */
	private String url;
	
	/**
	 * Constructor
	 */
	public WebViewRedirect(String url)
	{
		// Make full path
		if (!url.startsWith("http://") && !url.startsWith("https://"))
			this.url = GlobalProperties.getInstance().getString("app.url") + "/" + url;
		else
			this.url = url;
	}
	
	/**
	 * Redirect to new URL
	 */
	public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		// Don't cache this
		WebUtils.setNoCacheHeaders(response);
		
		// Redirect to url
		response.sendRedirect(url);
	}
}

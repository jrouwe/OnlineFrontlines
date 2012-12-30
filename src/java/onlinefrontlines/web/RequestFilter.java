package onlinefrontlines.web;

import onlinefrontlines.Constants;
import onlinefrontlines.auth.AutoAuth;
import onlinefrontlines.profiler.Profiler;
import onlinefrontlines.profiler.Sampler;
import onlinefrontlines.utils.Tools;
import java.util.HashMap;
import java.util.Enumeration;
import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

/**
 * Filters all incoming action requests
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
public class RequestFilter implements Filter
{
	/**
	 * List of registered actions
	 */
	private HashMap<String, WebActionConfig> registeredActions = new HashMap<String, WebActionConfig>();
	
	/**
	 * Stored servlet context
	 */
	private ServletContext servletContext;

	/**
	 * Wrapper for the request
	 */
	private static class RequestWrapper extends HttpServletRequestWrapper
	{
		private WebAction action;
		
		/**
		 * Constructor
		 */
		RequestWrapper(HttpServletRequest request, WebAction action)
		{
			super(request);

			this.action = action;
		}

		/**
		 * Override get attribute to search in actions fields
		 */
		@Override
		public Object getAttribute(String name)
		{
			// Try as attribute
			Object attribute = super.getAttribute(name);
			if (attribute != null)
				return attribute;
			
			// Ignore system attributes
			if (name.startsWith("javax.servlet.") || name.startsWith("org.apache."))
				return null;
			
			// Try as field
			attribute = action.config.getField(action, name);
			super.setAttribute(name, attribute);
			return attribute;
		}
	}

	/**
	 * Fill in action config object
	 * 
	 * @param action Action node from actions.xml
	 * @param actionConfig Action config object to fill in
	 */
	public void getActionConfig(Node action, WebActionConfig actionConfig) throws Exception
	{
		// Get class loader
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		
		// Get attributes
		NamedNodeMap actionAttrs = action.getAttributes();
		
    	// Get action class
    	Node actionClassNode = actionAttrs.getNamedItem("class");
    	if (actionClassNode != null)
    		actionConfig.action = classLoader.loadClass(actionClassNode.getNodeValue());
    	
    	// Get method
    	Node methodNode = actionAttrs.getNamedItem("method");
    	if (methodNode != null)
    	{
    		String method = methodNode.getNodeValue().toLowerCase();
    		if (method.equals("input"))
    			actionConfig.method = WebActionConfig.ActionMethod.INPUT;
    		else if (method.equals("execute"))
    			actionConfig.method = WebActionConfig.ActionMethod.EXECUTE;
    		else
    			throw new Exception("Invalid method " + method);
    	}    	
    	
    	NodeList actionChildren = action.getChildNodes();
    	for (int j = 0; j < actionChildren.getLength(); ++j)
    	{
    		Node child = actionChildren.item(j);            		
    		String childName = child.getNodeName().toLowerCase();            		
    		if (childName.equals("interceptor"))
    		{
    			// Create interceptor
    			String interceptorClass = child.getFirstChild().getNodeValue();
   				actionConfig.interceptor = (WebInterceptor)classLoader.loadClass(interceptorClass).newInstance();
    		}
    		else if (childName.equals("result"))
    		{
    			// Get type
    			Node resultTypeNode = child.getAttributes().getNamedItem("type");
    			String resultType = resultTypeNode != null? resultTypeNode.getNodeValue().toLowerCase() : "jsp";
    			
    			// Get name
    			Node resultNameNode = child.getAttributes().getNamedItem("name");
    			String resultName = resultNameNode != null? resultNameNode.getNodeValue().toLowerCase() : "success";
    			
    			// Get url
    			String resultUrl = child.getFirstChild().getNodeValue();
    			
    			// Create view
    			WebView view;
    			if (resultType.equals("jsp"))
    				view = new WebViewJsp(resultUrl);
    			else if (resultType.equals("redirect"))
    				view = new WebViewRedirect(resultUrl);
    			else
    				throw new Exception("Invalid view type " + resultType);
    				
    			// Fill in correct entry
    			if (resultName.equals("input"))
    				actionConfig.inputView = view;
    			else if (resultName.equals("success"))
    				actionConfig.successView = view;
    			else if (resultName.equals("error"))
    				actionConfig.errorView = view;
    			else if (resultName.equals("redirect"))
    				actionConfig.redirectView = view;
    			else
    				throw new Exception("Invalid name " + resultName);
    		}
    	}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void init(FilterConfig config)
	{
		// Store servletcontext
		servletContext = config.getServletContext();

	    try 
	    {
	    	// Open actions.xml config file
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(servletContext.getResourceAsStream("/WEB-INF/classes/actions.xml"));
            if (doc == null)
            	throw new Exception("Config file 'actions.xml' not found.");

            // Normalize text representation
            doc.getDocumentElement().normalize();
            
            // Load default action
            NodeList defaultAction = doc.getElementsByTagName("default-action");
            if (defaultAction == null || defaultAction.getLength() != 1)
            	throw new Exception("Need a default action");
            WebActionConfig defaultActionConfig = new WebActionConfig();
            getActionConfig(defaultAction.item(0), defaultActionConfig);
            
            // Load actions
            NodeList actions = doc.getElementsByTagName("action");
            if (actions == null)
            	throw new Exception("Need at least one action");
            for (int i = 0; i < actions.getLength(); ++i)
            {
            	// Get action node
            	Node action = actions.item(i);
            	
            	// Get name 
            	String actionName = action.getAttributes().getNamedItem("name").getNodeValue();

            	// Get config
            	WebActionConfig actionConfig = new WebActionConfig(defaultActionConfig); 
            	getActionConfig(action, actionConfig);
            	
            	// Register action
            	registeredActions.put("/OnlineFrontlines/" + actionName + ".do", actionConfig);
            }
        }
	    catch (Exception e)
	    {
			// Log error
			Tools.logException(e);
	    }
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void destroy()
	{		
	}
	
	/**
	 * Override doFilter method
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		// Get request
		String requestURI = ((HttpServletRequest)request).getRequestURI();
		
		Sampler sampler = Profiler.getInstance().startSampler(Profiler.CATEGORY_HTTP_REQUEST, requestURI);
		try
		{
			// Set encoding
			request.setCharacterEncoding("UTF-8");
			
	        // Don't cache this
			WebUtils.setNoCacheHeaders((HttpServletResponse)response);
			
			// Find action
			WebActionConfig actionConfig = registeredActions.get(requestURI);
			if (actionConfig == null)
			{
				WebView view = new WebViewJsp("/WEB-INF/jsp/NotFound.jsp");
				view.execute((HttpServletRequest)request, (HttpServletResponse)response);
				return;
			}
			
			// Create action
			WebAction action = null;
			try
			{
				action = (WebAction)actionConfig.action.newInstance();
			}
			catch (Exception e)
			{			
				// Log exceptions
				Tools.logException(e);
				
				// Send error
				WebView view = actionConfig.errorView;
				view.execute((HttpServletRequest)request, (HttpServletResponse)response);
				return;
			}
	
			// Create wrappers
			RequestWrapper req = new RequestWrapper((HttpServletRequest)request, action);
			
			// Set constants on action
			action.config = actionConfig;
			action.servletContext = servletContext;
			action.request = req;
			action.response = (HttpServletResponse)response;
	
			// Set constants on request
			req.setAttribute(Constants.CURRENT_ACTION, action);
			
			// Set user on request
			AutoAuth.AuthResult result = AutoAuth.getAuthenticatedUser(action.request, action.response);
			if (result != null)
			{
				action.user = result.user;
				action.facebookAccessToken = result.facebookAccessToken;
			}			
	
			// Set parameters on action
			Enumeration<?> params = request.getParameterNames();
			while (params.hasMoreElements()) 
			{
				String name = (String)params.nextElement();
				String value = request.getParameter(name);			
				action.config.setField(action, name, value);
			}		
			
			// Execute action
			WebView view;
			try
			{
				view = actionConfig.interceptor.intercept(action);
			}
			catch (Exception e)
			{
				// Log exceptions
				Tools.logException(e);
				
				// Send error
				action.addActionError(action.getText("unexpectedError"));
				view = action.getErrorView();
			}
	
			try
			{
				// Execute view
				view.execute(req, (HttpServletResponse)response);
			}
			catch (Exception e)
			{
				// Log exception
				Tools.logException(e);
				
				// Send internal server error
				((HttpServletResponse)response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		}
		finally
		{
			sampler.stop();
		}
	}
}

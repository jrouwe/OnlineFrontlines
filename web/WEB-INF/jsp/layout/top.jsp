<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ page import="onlinefrontlines.*" %>
<%@ page import="onlinefrontlines.auth.*" %>
<%@ page import="onlinefrontlines.facebook.*" %>
<%@ page import="onlinefrontlines.web.*" %>
<%@ page import="onlinefrontlines.userstats.*" %>
<%@ page import="onlinefrontlines.utils.*" %>
<%@ page import="onlinefrontlines.game.*" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<meta property="og:title" content="${param.title}"/>
		<meta property="og:type" content="game"/>
		<meta property="og:image" content="http://www.onlinefrontlines.com/OnlineFrontlines/assets/icon.png"/>
		<meta property="og:site_name" content="Online Frontlines"/>
		<meta property="fb:admins" content="${applicationScope.fbAdminsUid}"/>
		<meta name="keywords" content="free, turn based, strategy, game, play by mail, online, multiplayer, online frontlines"/>		
		<title>
			Online Frontlines - ${param.title} 
		</title>
		<link rel="stylesheet" type="text/css" href="styles.jsp"/>
		${param.headerContent}
	</head>
	<body ${param.bodyTagContent}>
		<div id="fb-root"></div>
	    <ofl:JavaScript>
			window.fbAsyncInit = function() {
				FB.init({
					appId      : '${applicationScope.fbApiKey}',
	            	status     : true, 
	            	cookie     : true,
	            	xfbml      : true,
	            	oauth      : true,
	         	});
				FB.Event.subscribe("auth.login", 
					function(response) { 
						FB.api('/me', function(response2) {
  							window.location.reload();
						});					 
					});
	        };
	        (function(d){
	        	var js, id = 'facebook-jssdk'; if (d.getElementById(id)) {return;}
				js = d.createElement('script'); js.id = id; js.async = true;
				js.src = "//connect.facebook.net/en_US/all.js";
				d.getElementsByTagName('head')[0].appendChild(js);
	         }(document));
	    </ofl:JavaScript>
		<ofl:Cache key="top" group="user" cachePerUser="true" timeToLiveSeconds="120">
			<% 
				WebAction action = ((WebAction)request.getAttribute(Constants.CURRENT_ACTION));
			%>										
			<ofl:JavaScript>
				var menuItems = [
					{ 
						title: 'HOME', 
						action: '${applicationScope.appUrl}/Home.do', 
						subItems:
						[
						],
						subItemsPadding: 0
					},
					{ 
						title: 'PLAY NOW', 
						action: '${applicationScope.appUrl}/PlayNow.do',
						subItems:
						[
							{ 
								title: 'CONTINUE GAME',
								action: '${applicationScope.appUrl}/GameContinue.do'
							},
							{ 
								title: 'PLAY VERSUS COMPUTER',
								action: '${applicationScope.appUrl}/GameCreate.do?ai=true'
							},
							{ 
								title: 'INVITE FRIEND',
								action: '${applicationScope.appUrl}/PBMCreateInvitation.do'
							},
							{ 
								title: 'SHOW INVITATIONS',
								action: '${applicationScope.appUrl}/PBMShowInvitation.do'
							},
							{ 
								title: 'WORLD MAP',
								action: '${applicationScope.appUrl}/WorldMap.do'
							},
							{ 
								title: 'CREATE OWN COUNTRY',
								action: '${applicationScope.appUrl}/CountryCreateHome.do'
							},
							{ 
								title: 'PLAY OWN COUNTRY',
								action: '<c:url value="${applicationScope.appUrl}/PBMCreateInvitation.do"><c:param name="custom" value="true"/></c:url>'
							},
						],
						subItemsPadding: 70
					},
					{ 
						title: 'PROFILE', 
						action: '${applicationScope.appUrl}/MyStats.do',
						subItems:
						[
							{ 
								title: 'PROFILE',
								action: '${applicationScope.appUrl}/MyStats.do'
							},
							{ 
								title: 'EDIT PROFILE',
								action: '${applicationScope.appUrl}/EditProfile.do'
							},
							{ 
								title: 'RECEIVED FEEDBACK',
								action: '${applicationScope.appUrl}/MyFeedbackList.do'
							},
							{ 
								title: 'GIVE FEEDBACK',
								action: '${applicationScope.appUrl}/FeedbackRequiredList.do'
							}						
						],
						subItemsPadding: 120
					},
					{ 
						title: 'RANKINGS', 
						action: '${applicationScope.appUrl}/UserStatsList.do',
						subItems:
						[
						],
						subItemsPadding: 420
					},
					{ 
						title: 'MOTD', 
						action: '${applicationScope.appUrl}/TV.do',
						subItems:
						[
						],
						subItemsPadding: 0
					},
					{ 	
						title: 'HELP', 
						action: '${applicationScope.appUrl}/HelpGeneral.do',
						subItems:
						[
							{ 
								title: 'GENERAL',
								action: '${applicationScope.appUrl}/HelpGeneral.do'
							},
							{ 
								title: 'RULES',
								action: '${applicationScope.appUrl}/HelpRules.do'
							},
							{ 
								title: 'LAND UNITS',
								action: '<c:url value="${applicationScope.appUrl}/HelpUnit.do"><c:param name="unitClass" value="1"/></c:url>'
							},
							{ 
								title: 'SEA UNITS',
								action: '<c:url value="${applicationScope.appUrl}/HelpUnit.do"><c:param name="unitClass" value="2"/></c:url>'
							},
							{ 
								title: 'AIR UNITS',
								action: '<c:url value="${applicationScope.appUrl}/HelpUnit.do"><c:param name="unitClass" value="3"/></c:url>'
							},
							{ 
								title: 'TERRAIN',
								action: '${applicationScope.appUrl}/HelpTerrain.do'
							},
							{ 
								title: 'SCREENSHOTS',
								action: '${applicationScope.appUrl}/HelpScreens.do'
							}
						],
						subItemsPadding: 180
					},
					{ 
						title: 'CONTACT', 
						action: '${applicationScope.facebookUrl}',
						subItems:
						[
						],
						subItemsPadding: 0
					}
				];
			</ofl:JavaScript>
			<div id="main">
				<ofl:Cache key="top_MemberCount" timeToLiveSeconds="600">
					<div id="banner" onclick="location.href='${applicationScope.appUrl}/Home.do'">
						MEMBERS: <%= UserDAO.getNumMembers() %>
					</div>
				</ofl:Cache>
				<div id="user_info">
					<c:choose>
						<c:when test="${user != null}">
							<div id="userinfo_left">
								<div id="userinfo_image">
									<% 
										String userImage;
										try
										{
											userImage = action.user.getProfileImageURL();
										}
										catch (Exception e)
										{
											userImage = "";
										}
									%>						
									<img src="<%= userImage %>" alt="Avatar"/>
								</div>
							</div>
							<div id="userinfo_right">
								<div id="userinfo_name"><c:out value="${user.username}"/></div>
								<%
									UserStats stats;
									try
									{
										stats = UserStatsCache.getInstance().get(action.user.id);
									}
									catch (Exception e)
									{
										stats = new UserStats(0);
									}
								%>													
								<div id="userinfo_loggedin_info">
									Points: <%= stats.totalPoints %><br/>
									Level: <%= stats.getLevel() %><br/>
									Rank: <%= stats.getRankName() %><br/>
								</div>
								<div id="userinfo_logout_button">
									<a href="#" onclick="
										FB.getLoginStatus(function(response) {
  											if (response.status === 'connected')
  												FB.logout(function(response2) { window.location='${applicationScope.appUrl}/Logout.do'; });
  											else
  												window.location='${applicationScope.appUrl}/Logout.do';
  										});
  										return true;
  									">LOG OUT</a>									
								</div>
							</div>
						</c:when>
						<c:otherwise>
							<div id="userinfo_notloggedin_info">
								<div class="fb-login-button" data-show-faces="false" data-width="200" data-max-rows="1" scope="email"></div>
						    </div>
						</c:otherwise>
					</c:choose>
				</div>
				<noscript>
					<div class="errorMessage">JavaScript is turned off in your browser! Please turn it on and refresh the page.</div>
				</noscript>
				<% 
					if (action.user != null)
					{
						int pendingInvitationCount;
						int gamesRequiringAttentionCount;
						try
						{
							pendingInvitationCount = action.facebookAccessToken != null? Facebook.getPendingRequestCount(action.facebookAccessToken) : 0;
							gamesRequiringAttentionCount = GameStateDAO.getGamesRequiringAttentionCount(action.user.id);
						}
						catch (Exception e)
						{
							pendingInvitationCount = 0;
							gamesRequiringAttentionCount = 0;
						}
					
						if (pendingInvitationCount > 0) 
						{ 
				%>
					<div id="member_line">
						<a href="${applicationScope.appUrl}/PBMShowInvitation.do">You have <%= pendingInvitationCount %> pending invitations</a>.
					</div> 
				<%
						}
						else if (gamesRequiringAttentionCount > 0) 
						{ 
				%>
					<div id="member_line">
						<a href="${applicationScope.appUrl}/GameContinue.do">You have <%= gamesRequiringAttentionCount %> games that require attention</a>.
					</div> 
				<%
						}
						else if (!action.user.getHasArmy()) 
						{ 
				%>
					<div id="member_line">
						You have not selected an army yet. Click <a href="${applicationScope.appUrl}/EditArmy.do">here</a> to select one!
					</div> 
				<%
						}
					} 
				%>
			</ofl:Cache>
			<div id="menu">
				<ofl:JavaScript>
					var selectedMenuItem = null;
					for (var i = 0; i < menuItems.length; ++i)
					{
						if (menuItems[i].title == '${param.mainMenuSelection}')
							selectedMenuItem = menuItems[i];
												
						document.write("<div class=\"menu_item" + (menuItems[i].title == '${param.mainMenuSelection}'? '_selected' : '') + "\"/><a href=\"" + menuItems[i].action + "\">" + menuItems[i].title + "</a></div>");
					}
				</ofl:JavaScript>
			</div>
			<div id="sub_menu">
				<ofl:JavaScript>
					if (selectedMenuItem != null)
						for (var j = 0; j < selectedMenuItem.subItems.length; ++j)
							document.write("<div class=\"sub_menu_item" + (selectedMenuItem.subItems[j].title == '${param.subMenuSelection}'? '_selected' : '') + "\"/><a href=\"" + selectedMenuItem.subItems[j].action + "\">" + selectedMenuItem.subItems[j].title + "</a></div>");
				</ofl:JavaScript>
			</div>
			<ofl:JavaScript>
				if (selectedMenuItem != null)
				{
					var sub_menu_style = document.getElementById("sub_menu").style; 
					sub_menu_style.paddingLeft = selectedMenuItem.subItemsPadding + "px";
					sub_menu_style.width = (990 - selectedMenuItem.subItemsPadding) + "px";
				}
			</ofl:JavaScript>
			<div id="content">
			
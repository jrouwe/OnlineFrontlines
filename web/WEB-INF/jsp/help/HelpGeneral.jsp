<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="General"/>
	<jsp:param name="mainMenuSelection" value="HELP"/>
	<jsp:param name="subMenuSelection" value="GENERAL"/>
</jsp:include>    

<ofl:Cache key="HelpGeneral" keyExp="${what}" timeToLiveSeconds="3600">
	<div id="content_left">
		<ofl:Box title="GENERAL" className="260" style="height: 481px">
			<c:set var="counter" value="0"/>
			<c:forTokens items="Game Modes;Friendly Game;World Map;Ranks;Feedback" delims=";" var="category">
				<c:if test="${counter != 0}">
					<div class="help_menu_divider">
					</div>
				</c:if>
				<div class="help_menu_line">
					<c:choose>
						<c:when test="${what == category || (what == '' && counter == 0)}">
							<div class="help_menu_item_selected" style="width: 225px;">
								${category}
							</div>
						</c:when>
						<c:otherwise>
							<div class="help_menu_item" style="width: 225px;">
								<a href="<c:url value="${applicationScope.appUrl}/HelpGeneral.do"><c:param name="what" value="${category}"/></c:url>">${category}</a>
							</div>
						</c:otherwise>
					</c:choose>  
				</div>
				<c:set var="counter" value="${counter + 1}"/>
			</c:forTokens>
		</ofl:Box>				
	</div>

	<div class="vertical_divider">
	</div>

	<div id="content_center">
		<c:choose>
			<c:when test="${what == 'Game Modes' || what == ''}">
				<div class="sub_title_466">
					GAME MODES
				</div>
			
				<ofl:Box className="466" style="height: 482px; overflow: auto">
					<div class="help_content">
						<p>Online Frontlines is a turn based strategy game. The game plays on a single screen and during each round 
						you can move all your units. Each turn can take up to 5 days. The game will send you an e-mail to tell you 
						when it is your turn.</p>
						
						<p>There are 2 modes:</p>
						
						<ul>
							<li>The first mode is a friendly game. You send a Facebook invitation to a friend and if he accepts a game is created.</li>
							<li>The second mode is played on the continents of the world map. You can attack a country or defend it.</li>
						</ul>

						<p>For both game modes you will receive points for every unit you destroy and tiles you conquer. For every
						1000 points you earn, you'll level up. Your level determines the maps, units and continents you can play with
						and it also determines your rank.</p>
					</div>
				</ofl:Box>
			</c:when>		
			<c:when test="${what == 'Friendly Game'}">
				<div class="sub_title_466">
					FRIENDLY GAME
				</div>
			
				<ofl:Box className="466" style="height: 482px; overflow: auto">
					<div class="help_content">
						<p>You select someone from your Facebook friends list and select a map. The challenge will then appear
						to your friend as a Facebook notification. If he accepts, a game will be created and you each 
						take turns to play. At the end of each turn a mail will be sent out to the other player to notify 
						him of the move. You'll have 5 days per turn, so you can for example spend 10 minutes per day to make your move.</p>
						
						<p>Click <a href="${applicationScope.appUrl}/PBMCreateInvitation.do">here</a> to start a game.</p>					
					</div>
				</ofl:Box>
			</c:when>		
			<c:when test="${what == 'World Map'}">
				<div class="sub_title_466">
					WORLD MAP
				</div>
			
				<ofl:Box className="466" style="height: 482px; overflow: auto">
					<div class="help_content">
						<p>The world map contains continents. Not all continents will be available at the beginning, but they will
						gradually unlock as you get more points. Africa is the first continent you'll be playing in.</p>

						<p>A continent consists of red <img src="${applicationScope.assetsUrl}/help/hexagon_red.png" alt="red"/> 
						and blue <img src="${applicationScope.assetsUrl}/help/hexagon_blue.png" alt="blue"/> countries.</p>
						
						<p>You can see which color you are by looking at the colored icon next to 
						your name in the player list on the continent map or in your profile.</p>
						
						<p>Countries that you cannot select will be grayed out like this 
						<img src="${applicationScope.assetsUrl}/help/hexagon_unusable.png" alt="unusable"/>.</p>
						
						<p>To start a game you first select a country to defend <img src="${applicationScope.assetsUrl}/help/hexagon_red.png" alt="defend"/>
						and then a country to attack <img src="${applicationScope.assetsUrl}/help/hexagon_attackable.png" alt="attack"/>. 
						The defended country state will change to <img src="${applicationScope.assetsUrl}/help/hexagon_you.png" alt="you"/> and the
						attacked country to <img src="${applicationScope.assetsUrl}/help/hexagon_attacked.png" alt="attacked"/>.												
						The attack will remain valid for 3 days (even if you log out). 
						
						<p>Enemy players will see the attacked country as <img src="${applicationScope.assetsUrl}/help/hexagon_defendable.png" alt="defendable"/>, 
						friendly players will see the attacked country as <img src="${applicationScope.assetsUrl}/help/hexagon_defendable_friendly.png" alt="defendable friendly"/>. 
						Both types of players can click on the country to start a game (friendly players clicking on a country will result in a friendly game
						where no countries can be conquered).</p>
						
						<p>During the game, you'll have 5 days to make your move. The opponent will be notified by mail 
						that it is his turn and will have 5 days to respond. If you fail to make a move in 5 days, you'll lose the game.
						Visit the <a href="${applicationScope.appUrl}/HelpRules.do">rules section</a> to learn about the game rules.</p>
						
						<p>You can cancel an attack or defend at any time by clicking on another country.</p>
						
						<p>If you win the game, the country that you attacked changes into your armies color, and it will be marked with a green 
						background to indicate that you have last conquered it <img src="${applicationScope.assetsUrl}/help/hexagon_owned_by_you.png" alt="owned by you"/>. 
						You then have 1 day exclusive access to that country, no one can attack to or defend from it during that time. 
						The country will show up with a red background to other users to indicate this
						<img src="${applicationScope.assetsUrl}/help/hexagon_exclusive.png" alt="exclusive for enemy"/>. 
						Lose the game and the country that you defended will change into the enemies color.</p>
						
						<p>When a friend has conquered a country its background will be dark green <img src="${applicationScope.assetsUrl}/help/hexagon_owned_by_friend.png" alt="owned by friend"/>.</p>
						
						<p>On the continent map there are some capture points <img src="${applicationScope.assetsUrl}/help/hexagon_capture_point.png" alt="capture point"/>,
						if all of the countries neighboring it are captured by one army, the capture point changes color. All players that happen to own a country
						neighboring that capture point will earn a 'capture'. Earning a capture will earn you a medal, and there is a leaderboard that lists the people
						with the most captures.</p>
						
						<p>Other icons that are used on the continent map are for games in progress <img src="${applicationScope.assetsUrl}/help/hexagon_game_in_progress.png" alt="game in progress"/>,
						players that you cannot interact with <img src="${applicationScope.assetsUrl}/help/hexagon_other_player.png" alt="other players"/>,
						and enemy players that you can interact with <img src="${applicationScope.assetsUrl}/help/hexagon_enemy_player.png" alt="enemy players"/>.</p>
						
						<p>If you automatically want to defend countries that you have conquered before, check the 
						<a href="${applicationScope.appUrl}/EditProfile.do">'Auto Defend Conquered Countries on World Map' checkbox in your profile</a>.</p> 
						
						<p>Click <a href="${applicationScope.appUrl}/WorldMap.do">here</a> to go to the world map.</p>					
					</div>
				</ofl:Box>
			</c:when>
			<c:when test="${what == 'Ranks'}">
				<div class="sub_title_466">
					RANKS
				</div>

				<ofl:Box className="466" style="height: 482px; overflow: auto">
					<div class="help_content">						
						<table>
							<tr>
								<th>Image</th>
								<th>Name</th>
								<th>Requirements</th>
							</tr>
							<tr>
								<td><img src="${applicationScope.assetsUrl}/ranks_big/rank01.gif" alt="rank"/></td>
								<td>Private</td>
								<td>You start at this rank</td>
							</tr>
							<tr>
								<td><img src="${applicationScope.assetsUrl}/ranks_big/rank02.gif" alt="rank"/></td>
								<td>Private First Class</td>
								<td>Requires level 5</td>
							</tr>
							<tr>
								<td><img src="${applicationScope.assetsUrl}/ranks_big/rank03.gif" alt="rank"/></td>
								<td>Corporal</td>
								<td>Requires level 10</td>
							</tr>
							<tr>
								<td><img src="${applicationScope.assetsUrl}/ranks_big/rank04.gif" alt="rank"/></td>
								<td>Sergeant</td>
								<td>Requires level 20</td>
							</tr>
							<tr>
								<td><img src="${applicationScope.assetsUrl}/ranks_big/rank05.gif" alt="rank"/></td>
								<td>Staff Sergeant</td>
								<td>Requires level 30</td>
							</tr>
							<tr>
								<td><img src="${applicationScope.assetsUrl}/ranks_big/rank06.gif" alt="rank"/></td>
								<td>Sergeant First Class</td>
								<td>Requires level 50</td>
							</tr>
							<tr>
								<td><img src="${applicationScope.assetsUrl}/ranks_big/rank07.gif" alt="rank"/></td>
								<td>Master Sergeant</td>
								<td>Requires level 75</td>
							</tr>
							<tr>
								<td><img src="${applicationScope.assetsUrl}/ranks_big/rank08.gif" alt="rank"/></td>
								<td>Second Lieutenant</td>
								<td>Requires level 100</td>
							</tr>
							<tr>
								<td><img src="${applicationScope.assetsUrl}/ranks_big/rank09.gif" alt="rank"/></td>
								<td>First Lieutenant</td>
								<td>Requires level 125</td>
							</tr>
							<tr>
								<td><img src="${applicationScope.assetsUrl}/ranks_big/rank10.gif" alt="rank"/></td>
								<td>Captain</td>
								<td>Requires level 150</td>
							</tr>
							<tr>
								<td><img src="${applicationScope.assetsUrl}/ranks_big/rank11.gif" alt="rank"/></td>
								<td>Major</td>
								<td>Requires level 200</td>
							</tr>
							<tr>
								<td><img src="${applicationScope.assetsUrl}/ranks_big/rank12.gif" alt="rank"/></td>
								<td>Lieutenant Colonel</td>
								<td>Requires level 250</td>
							</tr>
							<tr>
								<td><img src="${applicationScope.assetsUrl}/ranks_big/rank13.gif" alt="rank"/></td>
								<td>Colonel</td>
								<td>Requires level 300</td>
							</tr>
							<tr>
								<td><img src="${applicationScope.assetsUrl}/ranks_big/rank14.gif" alt="rank"/></td>
								<td>Brigadier General</td>
								<td>Requires position 20 in the leaderboard</td>
							</tr>
							<tr>
								<td><img src="${applicationScope.assetsUrl}/ranks_big/rank15.gif" alt="rank"/></td>
								<td>Major General</td>
								<td>Requires position 15 in the leaderboard</td>
							</tr>
							<tr>
								<td><img src="${applicationScope.assetsUrl}/ranks_big/rank16.gif" alt="rank"/></td>
								<td>Lieutenant General</td>
								<td>Requires position 10 in the leaderboard</td>
							</tr>
							<tr>
								<td><img src="${applicationScope.assetsUrl}/ranks_big/rank17.gif" alt="rank"/></td>
								<td>General</td>
								<td>Requires position 5 in the leaderboard</td>
							</tr>
							<tr>
								<td><img src="${applicationScope.assetsUrl}/ranks_big/rank18.gif" alt="rank"/></td>
								<td>General of the Army</td>
								<td>Requires position 1 in the leaderboard</td>
							</tr>
						</table>
					</div>
				</ofl:Box>
			</c:when>				  
			<c:when test="${what == 'Feedback'}">
				<div class="sub_title_466">
					FEEDBACK
				</div>
			
				<ofl:Box className="466" style="height: 482px; overflow: auto">
					<div class="help_content">
						<p>For each game you play, you can leave feedback for your opponent. You can give
						him positive, neutral or negative feedback and you can leave him a comment. 
						Each player can contribute to +1, 0 or -1 point of the feedback score of another person. 
						The last feedback given determines this value.</p>
						
						<p>You can review a players feedback in his player profile. The world map and leaderboards
						link to this information.</p>  						
					</div>
				</ofl:Box>
			</c:when>				  
		</c:choose>
	</div>

	<div class="vertical_divider">
	</div>
</ofl:Cache>						

<div id="content_right">
	<jsp:include page="/WEB-INF/jsp/help/Tips.jsp"/>
	
	<jsp:include page="/WEB-INF/jsp/home/Social.jsp"/>
</div>

<%@include file="/WEB-INF/jsp/layout/bottom.jsp"%>

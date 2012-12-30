<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="Rules"/>
	<jsp:param name="mainMenuSelection" value="HELP"/>
	<jsp:param name="subMenuSelection" value="RULES"/>
</jsp:include>    

<ofl:Cache key="HelpRules" keyExp="${what}" timeToLiveSeconds="3600">
	<div id="content_left">
		<ofl:Box title="RULES" className="260" style="height: 481px">
			<c:set var="counter" value="0"/>
			<c:forTokens items="Setup Phase;Actions;Movement;Winning;Attacking;Armour;Ammo;Deploying Units;Transporting Units;Airborne / Glider;Marine / Lander;Time Controls" delims=";" var="category">
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
								<a href="<c:url value="${applicationScope.appUrl}/HelpRules.do"><c:param name="what" value="${category}"/></c:url>">${category}</a>
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
			<c:when test="${what == 'Setup Phase' || what == ''}">
				<div class="sub_title_466">
					SETUP PHASE
				</div>
			
				<ofl:Box className="466" style="height: 482px; overflow: auto">
					<div class="help_content">
						<p>When a game starts, both players can drag and drop their units to form their 
						initial setup.</p>
						
						<p>It is possible to drag units into bases and, by clicking on the bases, units can 
						be removed from them again.</p>
						
						<p>During this setup phase, neither of the players can see the other players units.</p>
						
						<p>Once both players have pressed 'Start Game' the game will start and the 
						opponents units will be displayed.</p>
					</div>
				</ofl:Box>
			</c:when>
		
			<c:when test="${what == 'Actions'}">
				<div class="sub_title_466">
					ACTIONS
				</div>
			
				<ofl:Box className="466" style="height: 482px; overflow: auto">
					<div class="help_content">
						<p>Players take turns in the game. Each turn all units of a player can perform 2 actions.</p>
						
						<p>The following actions are possible:</p>
						
						<ul style="text-align: left;">
							<li><b>Move:</b> The unit can move a max amount of tiles. The amount of tiles you can move is fixed per turn so you can't move twice.</li>
							<li><b>Attack:</b> The unit can attack another unit.</li>
							<li><b>Enter a Base, Carrier or Attack Boat.</b></li>
							<li><b>Exit a Base, Carrier or Attack Boat.</b></li>
							<li><b>Transform:</b> Units can transform into another unit.</li>
						</ul>
						
						<p>When you enter a Base, Carrier or Attack Boat, no further actions will be possible.</p>
						
						<p>The most commonly used combinations are listed below:</p>
						
						<table>
							<tr><td><img src="${applicationScope.assetsUrl}/help/action_move.png" alt="Move"/></td></tr>
							<tr><td><img src="${applicationScope.assetsUrl}/help/action_move.png" alt="Move"/></td><td>+</td><td><img src="${applicationScope.assetsUrl}/help/action_attack.png" alt="Attack"/></td></tr>
							<tr><td><img src="${applicationScope.assetsUrl}/help/action_attack.png" alt="Attack"/></td><td>+</td><td><img src="${applicationScope.assetsUrl}/help/action_move.png" alt="Move"/></td></tr>
							<tr><td><img src="${applicationScope.assetsUrl}/help/action_attack.png" alt="Attack"/></td><td>+</td><td><img src="${applicationScope.assetsUrl}/help/action_attack.png" alt="Attack"/></td></tr>
							<tr><td><img src="${applicationScope.assetsUrl}/help/action_move.png" alt="Move"/></td><td>+</td><td><img src="${applicationScope.assetsUrl}/help/action_enter_base.png" alt="Enter Base"/></td></tr>
							<tr><td><img src="${applicationScope.assetsUrl}/help/action_attack.png" alt="Attack"/></td><td>+</td><td><img src="${applicationScope.assetsUrl}/help/action_enter_base.png" alt="Enter Base"/></td></tr>
							<tr><td><img src="${applicationScope.assetsUrl}/help/action_exit_base.png" alt="Exit Base"/></td><td>+</td><td><img src="${applicationScope.assetsUrl}/help/action_move.png" alt="Move"/></td></tr>
							<tr><td><img src="${applicationScope.assetsUrl}/help/action_exit_base.png" alt="Exit Base"/></td><td>+</td><td><img src="${applicationScope.assetsUrl}/help/action_attack.png" alt="Attack"/></td></tr>
							<tr><td><img src="${applicationScope.assetsUrl}/help/action_move.png" alt="Move"/></td><td>+</td><td><img src="${applicationScope.assetsUrl}/help/action_transform.png" alt="Transform"/></td></tr>
							<tr><td><img src="${applicationScope.assetsUrl}/help/action_transform.png" alt="Transform"/></td><td>+</td><td><img src="${applicationScope.assetsUrl}/help/action_move.png" alt="Move"/></td></tr>
						</table>
		
						<p>The color of the number above the unit determines a units current status. 
						A grey number means that you cannot perform any action with this unit 
						(moving other units might make it possible for the unit to perform actions
						again), a white number means you can select this unit and use it, a red number means 
						that the currently selected unit can attack this unit in the current turn 
						(by moving and attacking).</p>  
						
						<table>
							<tr>
								<td><img src="${applicationScope.assetsUrl}/help/action_white.png" alt="White"/></td>
								<td><img src="${applicationScope.assetsUrl}/help/action_grey.png" alt="Grey"/></td>
								<td><img src="${applicationScope.assetsUrl}/help/action_red.png" alt="Red"/></td>
							</tr>
						</table>	
					</div>
				</ofl:Box>
			</c:when>
		
			<c:when test="${what == 'Movement'}">
				<div class="sub_title_466">
					MOVEMENT
				</div>
			
				<div class="help_banner">
					<img src="${applicationScope.assetsUrl}/help/move.gif" alt="Movement"/>
				</div>
				
				<ofl:Box className="466" style="height: 382px; overflow: auto">
					<div class="help_content">
						<p>Click on a unit to see how far it can move. Click on a tile to make the unit move to that location.</p>
						
						<p>How far a unit can move depends on the unit type and the terrain.</p>
						
						<p>Go to <a href="${applicationScope.appUrl}/HelpUnit.do">unit help</a> to see how far units can move.<br/>
						Go to <a href="${applicationScope.appUrl}/HelpTerrain.do">terrain help</a> to see how movement is affected by terrain.</p>
					</div>
				</ofl:Box>
			</c:when>
		
			<c:when test="${what == 'Winning'}">
				<div class="sub_title_466">
					WINNING
				</div>
		
				<ofl:Box className="466" style="height: 482px; overflow: auto">
					<div class="help_content">
						<p>The game is won when the first player reaches the point limit.</p>
						
						<p>You can see the point limit and your current score by pressing the <img src="${applicationScope.assetsUrl}/help/stats_button.png" alt="stats button"/> button in game.</p>
						
						<p>This will popup the status window:</p>
						
						<img src="${applicationScope.assetsUrl}/help/stats_window.png" alt="status window"/>
						
						<p>Points can be gained by destroying enemy units and capturing tiles with your land units.</p>
						
						<p>The color of the tile indicates who controls the tile at the moment. If you click on 
						the <img src="${applicationScope.assetsUrl}/help/owner_button.png" alt="owner button"/> button in game, a colored icon 
						is displayed on each tile.</p>
						
						<p>Go to <a href="${applicationScope.appUrl}/HelpUnit.do">unit help</a> to see how many points a unit is worth.<br/></p>
		
						<p>Next to the point limit, you can also win the game by:</p>
						
						<ul>
							<li>Destroying all enemy units</li>
							<li>Capturing all enemy bases</li>
							<li>Destroying all enemy flags</li>
						</ul>
						
						<p>During the course of the game, the draw and surrender buttons will become active. 
						If you press the 'Surrender' button you lose. The game is a draw when both players 
						press the 'Draw' button in the same turn.</p>
					</div>
				</ofl:Box>
			</c:when>
		
			<c:when test="${what == 'Attacking'}">
				<div class="sub_title_466">
					ATTACKING
				</div>
			
				<div class="help_banner">
					<img src="${applicationScope.assetsUrl}/help/attack.gif" alt="Attack"/>
				</div>
				 
				<ofl:Box className="466" style="height: 382px; overflow: auto">
					<div class="help_content">
						<p>When you select a unit a crosshair indicates what targets can be attacked.
						If the unit is out of range and you can move the unit so it can attack, 
						the number above the unit will turn red.</p>
						
						<p>Click on the crosshair to attack your target. Not all units can attack each other.
						Some units can only fire at ground targets or attack planes. 
						At what distance you can attack depends on the unit type.</p> 
						
						<p>The unit that attacks deals damage first, if the defending unit can counter it will attack back (with his reduced hit points so the attack will be weaker).<p>
						
						<p>As an example: A Marine on a Mountain attacks a Heavy Tank in the Wetlands.<p>
						
						<p>First the Marine attacks: It has an Attack Power of 20 against land units. Mountain gives a 20% bonus and Wetlands give a -15% penalty. The maximum amount of damage that the Marine can do is 20 + (20% - -15%) = 27 points. The damage that the Marine does will therefore be between 0 and 27 and on average be 13.5. Lets say the damage dealt was 10.<p>
						
						<p>Next the Heavy Tank attacks back: It has an Attack Power of 24 against land units but its armour is now 30 instead of 40, this means the Attack Power is now reduced to 24 * 30 / 40 = 18. Accounting for the terrain bonuses the amount of damage that the Heavy Tank can do is 18 + (-15% - 20%) = 11.7. Again the damage will be between 0 and 11.7 and on average 5.9.</p>
						
						<p>Air units are considered to be on terrain type 'Air' which gives 0% bonus.</p>
						
						<p>Units that have ammo have two numbers for their Attack Power, e.g. 22/1.
						This means that with ammo the unit will have an Attack Power of 22 and without 
						it will have an Attack Power of 1.</p> 
						
						<p>Go to <a href="${applicationScope.appUrl}/HelpUnit.do">unit help</a> for more info.</p>
					</div>
				</ofl:Box>
			</c:when>
		
			<c:when test="${what == 'Armour'}">
				<div class="sub_title_466">
					ARMOUR
				</div>
			
				<div class="help_banner">
					<img src="${applicationScope.assetsUrl}/help/repair.gif" alt="Armour"/>
				</div>
				
				<ofl:Box className="466" style="height: 382px; overflow: auto">
					<div class="help_content">
						<p>The number above each unit indicates the amount of armour it has left.</p>
						
						<p>When your unit is damaged the armor drops. When the number reaches zero, the unit 
						is destroyed. When the unit is destroyed it is moved to the graveyard, you can view 
						the graveyard by pressing the <img src="${applicationScope.assetsUrl}/help/graveyard_button.png" alt="graveyard"/> 
						button.</p>
						
						<p>You can repair a damaged unit if you return it to his base.
						Planes can be repaired at airfields and carriers.
						Ground unit can be repaired at army bases and attack boats.
						Ships can be repaired in a harbor. Every turn a unit is in a base
						34% of its health is restored.</p>
						
						<p>Go to <a href="${applicationScope.appUrl}/HelpUnit.do">unit help</a> to see the armour for each 
						unit type.</p>
					</div>
				</ofl:Box>
			</c:when>
		
			<c:when test="${what == 'Ammo'}">
				<div class="sub_title_466">
					AMMO
				</div>
			
				<div class="help_banner">
					<img src="${applicationScope.assetsUrl}/help/rearm.gif" alt="Ammo"/>
				</div>
				
				<ofl:Box className="466" style="height: 382px; overflow: auto">
					<div class="help_content">
						<p>Some units have a limited ammo supply.
						The ammo is displayed on the left side of the unit and in the right menu bar.
						When the ammo is depleted the unit can re-arm at the appropriate base.
						Planes can re-arm at airfields and carriers.
						Ground unit can re-arm at army bases and attack boats.
						Ships can re-arm in a harbor. Every turn a unit is in a base
						50% of its ammo is replenished.</p>
						
						<p>Units that have ammo have two numbers for their Attack Power, e.g. 22/1.
						This means that with ammo the unit will have an Attack Power of 22 and without 
						it will have an Attack Power of 1.</p> 
						
						<p>Go to <a href="${applicationScope.appUrl}/HelpUnit.do">unit help</a> for more info.</p>
					</div>
				</ofl:Box>
			</c:when>
		
			<c:when test="${what == 'Deploying Units'}">
				<div class="sub_title_466">
					DEPLOYING UNITS
				</div>
			
				<div class="help_banner">
					<img src="${applicationScope.assetsUrl}/help/deploy.gif" alt="Ammo"/>
				</div>
				
				<ofl:Box className="466" style="height: 382px; overflow: auto">
					<div class="help_content">
						<p>When a unit has entered a base and if he has an action point left the unit can be 
						deployed again. This can by done by clicking on the base, clicking on the unit
						in the popup that you want to deploy and finally clicking on the location where you
						want to deploy the unit.</p>
					</div>
				</ofl:Box>
			</c:when>
		
			<c:when test="${what == 'Transporting Units'}">
				<div class="sub_title_466">
					TRANSPORTING UNITS
				</div>
			
				<div class="help_banner">
					<img src="${applicationScope.assetsUrl}/help/transport.gif" alt="Ammo"/>
				</div>
					
				<ofl:Box className="466" style="height: 382px; overflow: auto">
					<div class="help_content">
						<p>Some units can move when another unit is inside. These units work exactly
						as bases, except they have movement points.</p>
					</div>
				</ofl:Box>
			</c:when>
		
			<c:when test="${what == 'Airborne / Glider'}">
				<div class="sub_title_466">
					AIRBORNE / GLIDER
				</div>
			
				<div class="help_banner">
					<img src="${applicationScope.assetsUrl}/help/glider.gif" alt="Ammo"/>
				</div>
					
				<ofl:Box className="466" style="height: 382px; overflow: auto">
					<div class="help_content">
						<p>The Airborne unit can transform into a Glider in a base. The Glider can move very 
						fast but cannot attack. To attack, the Glider needs to land and become an Airborne again. 
						Click on the Glider to show the Land buton, click on the Land button to make the Glider
						land.</p>
					</div>
				</ofl:Box>
			</c:when>
		
			<c:when test="${what == 'Marine / Lander'}">
				<div class="sub_title_466">
					MARINE / LANDER
				</div>
			
				<div class="help_banner">
					<img src="${applicationScope.assetsUrl}/help/lander.gif" alt="Ammo"/>
				</div>
					
				<ofl:Box className="466" style="height: 382px; overflow: auto">
					<div class="help_content">
						<p>The Marine unit can transform into a Lander unit next to water. 
						The Lander unit can cross water and transform back to a Marine next to land. Move
						the Marine next to water to make the Boat button appear, click on the button and
						then on the location you want to deploy the boat to.</p>
					</div>
				</ofl:Box>
			</c:when>
		
			<c:when test="${what == 'Time Controls'}">
				<div class="sub_title_466">
					TIME CONTROLS
				</div>
			
				<ofl:Box className="466" style="height: 482px; overflow: auto">
					<div class="help_content">
						<p>At any time during the game you can view all moves that were done before. 
						This feature is especially handy if you are playing by mail and want to review
						your opponents move, but it can also be used when looking at the match of the day.</p>
					
						<ul>
						<li>The <img src="${applicationScope.assetsUrl}/time_control/play_home_up.png" alt="home"/> button takes you to the first turn in the game.</li>
						<li>The <img src="${applicationScope.assetsUrl}/time_control/play_begin_up.png" alt="previous"/> button takes you to the previous turn.</li>
						<li>The <img src="${applicationScope.assetsUrl}/time_control/play_back_up.png" alt="back"/> button takes you one move back.</li>
						<li>The <img src="${applicationScope.assetsUrl}/time_control/play_next_up.png" alt="forward"/> button takes to the next move.</li>
						<li>The <img src="${applicationScope.assetsUrl}/time_control/play_last_up.png" alt="next"/> button takes to the next turn.</li>
						<li>The <img src="${applicationScope.assetsUrl}/time_control/play_end_up.png" alt="end"/> button takes to the last move.</li>
						<li>The <img src="${applicationScope.assetsUrl}/time_control/play_play_up.png" alt="play"/> button starts replaying the game from the current move.</li>
						<li>The <img src="${applicationScope.assetsUrl}/time_control/play_pause_up.png" alt="pause"/> button stops the replay.</li>
						</ul>
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

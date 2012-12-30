<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="Create Your Own Country"/>
	<jsp:param name="mainMenuSelection" value="PLAY NOW"/>
	<jsp:param name="subMenuSelection" value="CREATE OWN COUNTRY"/>
</jsp:include>    
	
<div id="content_center_single_col">
	<ofl:Box title="CREATE YOUR OWN COUNTRIES" className="466">
		<div class="help_content">
			<p>Creating your own country allows you to play a game with your own map and units,  
			to create a country perform the following steps:</p>
			
			<ul>
			<li>Choose or create a map. A map consists of the actual tiles and the initial ownership of tiles. You can choose from existing maps or you can create your own using the map editor.</li>
			<li>Choose a deployment. A deployment is a collection of units. Each side of the game can have a different deployment. You can choose from existing deployments or you can choose to create 1 or 2 of your own deployments.</li>
			<li>Create a country. A country combines the deployments, map and some other properties.</li>
			<li>Invite a friend to test your new country. Please don't change the country while you're playing. If you do your game might become corrupted and unplayable.</li>
			<li>Publish your map. Select Publish in the country list. This will change your country, map and its deployments to read only and trigger a message to the admins. When approved, the country becomes available for everyone to play.</li>   
			</ul>
		</div>
	</ofl:Box>

	<ofl:Box title="ACTIONS" className="466">
		<table class="gtable">
			<ofl:Go title="List Maps">${applicationScope.appUrl}/MapList.do</ofl:Go>
			<ofl:Go title="Create New Map">${applicationScope.appUrl}/MapCreate.do</ofl:Go>
			<ofl:Go title="List Deployments">${applicationScope.appUrl}/DeploymentList.do</ofl:Go>
			<ofl:Go title="Create New Deployment">${applicationScope.appUrl}/DeploymentCreate.do</ofl:Go>
			<ofl:Go title="List Countries">${applicationScope.appUrl}/CountryList.do</ofl:Go>
			<ofl:Go title="Create New Country">${applicationScope.appUrl}/CountryCreate.do</ofl:Go>
		</table>
	</ofl:Box>
</div>
	
<%@include file="/WEB-INF/jsp/layout/bottom.jsp"%>

<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix='fmt' uri='http://java.sun.com/jsp/jstl/fmt' %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="Terrain Help"/>
	<jsp:param name="mainMenuSelection" value="HELP"/>
	<jsp:param name="subMenuSelection" value="TERRAIN"/>
</jsp:include>    

<ofl:Cache key="HelpTerrain" keyExp="${selectedTerrain}" timeToLiveSeconds="3600">
	<div id="content_left">
		<ofl:Box title="TERRAIN" className="260" style="height: 481px">
			<c:set var="first" value="1"/>
			<c:forEach items="${terrain}" var="t">
				<c:if test="${t.name != 'Air'}">
					<c:if test="${first == 0}">
						<div class="help_menu_divider">
						</div>
					</c:if>
					<div class="help_menu_line">
						<div class="help_menu_tile_image"><img src="${applicationScope.assetsUrl}/terrain_images/tile_<fmt:formatNumber minIntegerDigits="2" value="${t.firstTileImageNumber}"/>.png" alt="unit"/></div>
						<c:choose>
							<c:when test="${selectedTerrain.id == t.id}">
								<div class="help_menu_item_selected" style="width: 190px;">
									<c:out value="${t.name}"/>
								</div>
							</c:when>
							<c:otherwise>
								<div class="help_menu_item" style="width: 190px;">
									<a href="<c:url value="${applicationScope.appUrl}/HelpTerrain.do"><c:param name="terrainId" value="${t.id}"/></c:url>"><c:out value="${t.name}"/></a>
								</div>
							</c:otherwise>
						</c:choose>  
					</div>
					<c:set var="first" value="0"/>
				</c:if>
			</c:forEach>
		</ofl:Box>
	</div>

	<div class="vertical_divider">
	</div>

	<div id="content_center">
		<div class="sub_title_466">
			<c:out value="${selectedTerrain.name}"/>
		</div>
		
		<div class="help_banner">
			<img src="${applicationScope.assetsUrl}/help/terrain_banner_<fmt:formatNumber minIntegerDigits="2" value="${selectedTerrain.id}"/>.png" alt="Terrain banner"/>
		</div>
	
		<ofl:Box className="466">
			<p>
				<c:out value="${selectedTerrainDescription}"/>
			</p>			
		</ofl:Box>
	
		<ofl:Box title="TERRAIN INFO" className="466">
			<table class="help_stats_table">
				<tr>
					<td></td>
					<td class="help_stats_divider" rowspan="3"></td>
					<td class="help_stats_header">Value</td>
					<td class="help_stats_divider" rowspan="3"></td>
				</tr>					
				<tr>
					<td class="help_stats_label">Damage Modifier</td>
					<td class="help_stats_value">${selectedTerrain.strengthModifier}%</td>
				</tr>
				<tr>
					<td class="help_stats_label">Victory Points</td>
					<td class="help_stats_value">${selectedTerrain.victoryPoints}</td>
				</tr>
			</table>
		</ofl:Box>
	
		<ofl:Box title="UNIT MOVEMENT" className="466">
			<table class="help_stats_table">
				<tr>
					<td></td>
					<td class="help_stats_divider" rowspan="30"></td>
					<td class="help_stats_header">Moves</td>
					<td class="help_stats_divider" rowspan="30"></td>
				</tr>					
				<c:forEach items="${unitMovement}" var="m">
					<tr>
						<td class="help_stats_label"><c:out value="${m.name}"/></td>
						<td class="help_stats_value">${m.maxMovement}</td>
					</tr>
				</c:forEach>
			</table>
		</ofl:Box>
	</div>
	
	<div class="vertical_divider">
	</div>
</ofl:Cache>

<div id="content_right">
	<jsp:include page="/WEB-INF/jsp/help/Tips.jsp"/>
	
	<jsp:include page="/WEB-INF/jsp/home/Social.jsp"/>
</div>

<%@include file="/WEB-INF/jsp/layout/bottom.jsp"%>

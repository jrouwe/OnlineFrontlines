<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix='fmt' uri='http://java.sun.com/jsp/jstl/fmt' %>

<c:choose>
	<c:when test="${unitClass == 1}">
		<c:set var="unitClassName" value="Land Units"/>
		<c:set var="unitClassNameUpper" value="LAND UNITS"/>
	</c:when>
	<c:when test="${unitClass == 2}">
		<c:set var="unitClassName" value="Sea Units"/>
		<c:set var="unitClassNameUpper" value="SEA UNITS"/>
	</c:when>
	<c:when test="${unitClass == 3}">
		<c:set var="unitClassName" value="Air Units"/>
		<c:set var="unitClassNameUpper" value="AIR UNITS"/>
	</c:when>
</c:choose>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="${unitClassName}"/>
	<jsp:param name="mainMenuSelection" value="HELP"/>
	<jsp:param name="subMenuSelection" value="${unitClassNameUpper}"/>
</jsp:include>    

<ofl:Cache key="HelpUnit" keyExp="${unitId} ${unitClass}" timeToLiveSeconds="3600">
	<div id="content_left">
		<ofl:Box title="${unitClassName}" className="260" style="height: 481px">
			<c:set var="first" value="1"/>
			<c:forEach items="${units}" var="u">
				<c:if test="${first == 0}">
					<div class="help_menu_divider">
					</div>
				</c:if>
				<div class="help_menu_line">
					<div class="help_menu_tile_image"><img src="${applicationScope.assetsUrl}/unit_images/rd_unit_<fmt:formatNumber minIntegerDigits="2" value="${u.imageNumber}"/>_r.png" alt="unit"/></div>
					<div class="help_menu_tile_image"><img src="${applicationScope.assetsUrl}/unit_images/bl_unit_<fmt:formatNumber minIntegerDigits="2" value="${u.imageNumber}"/>_l.png" alt="unit"/></div>
					<c:choose>
						<c:when test="${selectedUnit.id == u.id}">
							<div class="help_menu_item_selected" style="width: 155px;">
								<c:out value="${u.name}"/>
							</div>
						</c:when>
						<c:otherwise>
							<div class="help_menu_item" style="width: 155px;">
								<a href="<c:url value="${applicationScope.appUrl}/HelpUnit.do"><c:param name="unitId" value="${u.id}"/><c:param name="unitClass" value="${u.unitClassIntValue}"/></c:url>"><c:out value="${u.name}"/></a>
							</div>
						</c:otherwise>
					</c:choose>  
				</div>
			<c:set var="first" value="0"/>
			</c:forEach>
		</ofl:Box>
	</div>

	<div class="vertical_divider">
	</div>

	<div id="content_center">
		<div class="sub_title_466">
			<c:out value="${selectedUnit.name}"/>
		</div>
		
		<div class="help_banner">
			<img src="${applicationScope.assetsUrl}/help/unit_banner_<fmt:formatNumber minIntegerDigits="2" value="${selectedUnit.imageNumber}"/>.png" alt="Unit banner"/>
		</div>
	
		<ofl:Box className="466">
			<p>
				<c:out value="${selectedUnitDescription}"/>
			</p>			
		</ofl:Box>
	
		<ofl:Box title="UNIT INFO" className="466">
			<table class="help_stats_table">
				<tr>
					<td></td>
					<td class="help_stats_divider" rowspan="4"></td>
					<td class="help_stats_header">Air</td>
					<td class="help_stats_divider" rowspan="4"></td>
					<td class="help_stats_header">Sea</td>
					<td class="help_stats_divider" rowspan="4"></td>
					<td class="help_stats_header">Land</td>
					<td class="help_stats_divider" rowspan="4"></td>
				</tr>					
				<tr>
					<td class="help_stats_label">Attack Power</td>
					<td class="help_stats_value">${selectedUnit.strengthVsAir.strengthString}</td>
					<td class="help_stats_value">${selectedUnit.strengthVsWater.strengthString}</td>
					<td class="help_stats_value">${selectedUnit.strengthVsLand.strengthString}</td>
				</tr>
				<tr>
					<td class="help_stats_label">Attack Range</td>
					<td class="help_stats_value">${selectedUnit.strengthVsAir.attackRange}</td>
					<td class="help_stats_value">${selectedUnit.strengthVsWater.attackRange}</td>
					<td class="help_stats_value">${selectedUnit.strengthVsLand.attackRange}</td>
				</tr>
			</table>
	
			<table class="help_stats_table">
				<tr>
					<td></td>
					<td class="help_stats_divider" rowspan="9"></td>
					<td class="help_stats_header">Value</td>
					<td class="help_stats_divider" rowspan="9"></td>
				</tr>					
				<tr>
					<td class="help_stats_label">Armour</td>
					<td class="help_stats_value">${selectedUnit.maxArmour}</td>
				</tr>
				<c:if test="${selectedUnit.maxAmmo > 0}">
					<tr>
						<td class="help_stats_label">Ammo</td>
						<td class="help_stats_value">${selectedUnit.maxAmmo}</td>
					</tr>
				</c:if>
				<tr>
					<td class="help_stats_label">Number of Actions</td>
					<td class="help_stats_value">${selectedUnit.actions}</td>	
				</tr>
				<tr>
					<td class="help_stats_label">Max Movement</td>
					<td class="help_stats_value">${selectedUnit.maxMovement}</td>	
				</tr>
				<tr>
					<td class="help_stats_label">Vision Range</td>
					<td class="help_stats_value">${selectedUnit.visionRange}</td>
				</tr>
				<tr>
					<td class="help_stats_label">Victory Points</td>
					<td class="help_stats_value">${selectedUnit.victoryPoints}</td>	
				</tr>
				<c:if test="${selectedUnit.containerMaxUnits > 0}">
					<tr>
						<td class="help_stats_label">Unit Carrying Capacity</td>
						<td class="help_stats_value">${selectedUnit.containerMaxUnits}</td>
					</tr>
				</c:if>
				<c:if test="${selectedUnit.beDetectedRange < 1000}">
					<tr>
						<td class="help_stats_label">Be Detected Range</td>
						<td class="help_stats_value">${selectedUnit.beDetectedRange}</td>
					</tr>
				</c:if>
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

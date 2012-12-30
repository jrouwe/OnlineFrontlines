<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix='fmt' uri='http://java.sun.com/jsp/jstl/fmt' %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="Game Configuration"/>
	<jsp:param name="mainMenuSelection" value="ADMIN"/>
	<jsp:param name="subMenuSelection" value="CONFIGURATION"/>
</jsp:include>    

<h2>Terrain</h2>

<table class="ptable">
	
	<tr>
		<th>Id</th>
		<th>Name</th>
		<th>Victory Points</th>
		<th>Strength Modifier</th>
		<th>Tiles</th>
		<th>Edge Terrain Tiles</th>
		<th>Open Terrain Tiles</th>
	</tr>
	
	<c:forEach items="${terrain}" var="t">
		<tr>
			<td>${t.id}</td>
			<td>${t.name}</td>
			<td>${t.victoryPoints}</td>
			<td>${t.strengthModifier}%</td>
			<td>
				<c:forEach items="${t.tileProperties}" var="p">
					<img src="${applicationScope.assetsUrl}/terrain_images/tile_<fmt:formatNumber minIntegerDigits="2" value="${p.tileImageNumber}"/>.png"/>
				</c:forEach>
			</td>
			<td>
				<c:forEach items="${t.tileProperties}" var="p">
					<img src="${applicationScope.assetsUrl}/terrain_images/tile_<fmt:formatNumber minIntegerDigits="2" value="${p.edgeTerrainImageNumber}"/>.png"/>
				</c:forEach>
			</td>
			<td>
				<c:forEach items="${t.tileProperties}" var="p">
					<img src="${applicationScope.assetsUrl}/terrain_images/tile_<fmt:formatNumber minIntegerDigits="2" value="${p.openTerrainImageNumber}"/>.png"/>
				</c:forEach>
			</td>
		</tr>
	</c:forEach>
	
</table>

<h2>Units</h2>

<table class="ptable">
	
	<tr>
		<th rowspan="2">Id</th>
		<th rowspan="2">Name</th>
		<th rowspan="2">Class</th>
		<th colspan="2">Image</th>
		<th rowspan="2">Description</th>
		<th rowspan="2">Armour</th>
		<th colspan="3">Attack</th>
		<th rowspan="2">Ammo</th>
		<th rowspan="2">Vision Range</th>
		<th rowspan="2">Movement Points</th>
		<th rowspan="2">Actions</th>
		<th rowspan="2">Victory Points</th>
		<th rowspan="2">Victory Category</th>
		<th rowspan="2">Setup On</th>
		<th rowspan="2">Setup Next To</th>
		<th rowspan="2">Be Detected Range</th>
		<th rowspan="2">Edit</th>
	</tr>
	
	<tr>
		<th>Red</th>
		<th>Blue</th>
		<th>Land</th>
		<th>Water</th>
		<th>Air</th>
	</tr>
		
	<c:forEach items="${units}" var="u">
		<tr>
			<td>${u.id}</td>
			<td>${u.name}</td>
			<td>${u.unitClassStringValue}</td>
			<td><img src="${applicationScope.assetsUrl}/unit_images/rd_unit_<fmt:formatNumber minIntegerDigits="2" value="${u.imageNumber}"/>_l.png"/></td>
			<td><img src="${applicationScope.assetsUrl}/unit_images/bl_unit_<fmt:formatNumber minIntegerDigits="2" value="${u.imageNumber}"/>_l.png"/></td>
			<td>${u.description}</td>	
			<td>${u.maxArmour}</td>
			<td>${u.strengthVsLandStringValue}</td>
			<td>${u.strengthVsWaterStringValue}</td>
			<td>${u.strengthVsAirStringValue}</td>
			<td>${u.maxAmmo}</td>
			<td>${u.visionRange}</td>
			<td>${u.movementPoints}</td>	
			<td>${u.actions}</td>	
			<td>${u.victoryPoints}</td>	
			<td>${u.victoryCategory}</td>
			<td>${u.unitSetupOnStringValue}</td>
			<td>${u.unitSetupNextToStringValue}</td>
			<td>${u.beDetectedRange}</td>
			<td><a href="<c:url value="${applicationScope.appUrl}/UnitEdit.do"><c:param name="unitId" value="${u.id}"/></c:url>">Edit</a></td>
		</tr>
	</c:forEach>
	
</table>

<h2>Units (continued)</h2>

<table class="ptable">
	
	<tr>
		<th rowspan="2">Name</th>
		<th colspan="4">Container</th>
		<th rowspan="2">Transformable To Unit Id</th>
		<th rowspan="2">Transformable Type</th>
		<th rowspan="2">Edit</th>
	</tr>
	<tr>
		<th>Max Units</th>
		<th>Container Unit Ids</th>
		<th>Armour Per Turn (%)</th>
		<th>Ammo Per Turn (%)</th>
	</tr>
			
	<c:forEach items="${units}" var="u">
		<tr>
			<td>${u.name}</td>
			<td>${u.containerMaxUnits}</td>	
			<td>${u.containerUnitIdsStringValue}</td>	
			<td>${u.containerArmourPercentagePerTurn}</td>	
			<td>${u.containerAmmoPercentagePerTurn}</td>	
			<td>${u.transformableToUnitId}</td>
			<td>${u.transformableTypeStringValue}</td>
			<td><a href="<c:url value="${applicationScope.appUrl}/UnitEdit.do"><c:param name="unitId" value="${u.id}"/></c:url>">Edit</a></td>
		</tr>
	</c:forEach>
	
</table>

<h2>Movement Cost Table</h2>

<table class="ptable">

	<tr>
		<th/>
		<c:forEach items="${terrain}" var="t">
			<th>${t.name}</th>
		</c:forEach>
	</tr>			

	<c:forEach items="${movementCostTable}" var="t">
		<tr>
			<c:forEach items="${t}" var="i">
				<td>${i}</td>
			</c:forEach>
		</tr>
	</c:forEach>
	
</table>

<%@include file="/WEB-INF/jsp/layout/bottom.jsp"%>

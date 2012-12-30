<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="Unit Edit"/>
	<jsp:param name="mainMenuSelection" value="ADMIN"/>
	<jsp:param name="subMenuSelection" value="EDIT"/>
</jsp:include>    

<div id="content_center_single_col">
	<ofl:form action="UnitEdit">
		<ofl:Box title="UNIT EDIT" className="466">
			<ofl:label key="unitName" value="${unitConfig.name}"/>
			<ofl:textfield key="maxArmour" value="${unitConfig.maxArmour}"/>
			<ofl:textfield key="maxAmmo" value="${unitConfig.maxAmmo}"/>
			<ofl:textfield key="visionRange" value="${unitConfig.visionRange}"/>
			<ofl:textfield key="movementPoints" value="${unitConfig.movementPoints}"/>
			<ofl:textfield key="actions" value="${unitConfig.actions}"/>
			<ofl:textfield key="containerMaxUnits" value="${unitConfig.containerMaxUnits}"/>
			<ofl:textfield key="containerArmourPercentagePerTurn" value="${unitConfig.containerArmourPercentagePerTurn}"/>
			<ofl:textfield key="containerAmmoPercentagePerTurn" value="${unitConfig.containerAmmoPercentagePerTurn}"/>
			<ofl:textfield key="victoryPoints" value="${unitConfig.victoryPoints}"/>
			<ofl:textfield key="victoryCategory" value="${unitConfig.victoryCategory}"/>
			<ofl:textfield key="beDetectedRange" value="${unitConfig.beDetectedRange}"/>
		</ofl:Box>
		
		<c:forEach items="${strengthList}" var="s">
			<div class="sub_title_466">
				STRENGTH AGAINST <c:out value="${s.name}"/>
			</div>
		
			<ofl:Box className="466">
			    <ofl:textfield key="maxStrengthWithAmmo" name="maxStrengthWithAmmo${s.id}" value="${s.maxStrengthWithAmmo}"/>
			    <ofl:textfield key="maxStrengthWithoutAmmo" name="maxStrengthWithoutAmmo${s.id}" value="${s.maxStrengthWithoutAmmo}"/>
			    <ofl:textfield key="attackRange" name="attackRange${s.id}" value="${s.attackRange}"/>
			</ofl:Box>
		</c:forEach>
		
		<ofl:Box title="MOVEMENT COST" className="466">
			<c:forEach items="${costList}" var="c">
			    <ofl:textfield key="${c.name}" name="cost${c.id}" value="${c.cost > unitConfig.movementPoints? 'X' : c.cost}"/>
			</c:forEach>
		</ofl:Box>

		<ofl:Box title="CONTAINABLE UNITS" className="466">
			<c:forEach items="${containableList}" var="c">
			    <ofl:checkbox key="${c.name}" name="containable${c.id}" value="${c.containable}"/>
			</c:forEach>
			
			<ofl:hidden name="unitId" value="${unitId}"/>
		    <ofl:submit key="submit"/>
		</ofl:Box>
	</ofl:form>

	<p><a href="${applicationScope.appUrl}/GameConfig.do">Game Configuration</a></p>
</div>

<%@include file="/WEB-INF/jsp/layout/bottom.jsp"%>

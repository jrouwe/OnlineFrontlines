<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="Create Game"/>
	<jsp:param name="mainMenuSelection" value="PLAY NOW"/>
	<jsp:param name="subMenuSelection" value="PLAY VERSUS COMPUTER"/>
	<jsp:param name="bodyTagContent" value="onload=\"countryChanged();\""/>
</jsp:include>    

<script type="text/javascript" src="wz_tooltip.js"></script>

<div id="content_center_single_col">
	<ofl:actionerror/>
	
	<c:choose>
		<c:when test="${fn:length(countryConfigs)==0}">
			<ofl:Box title="NO COUNTRIES" className="466">
				No countries available to select.
			</ofl:Box>
		</c:when>	
		<c:otherwise>
			<ofl:form action="DoGameCreate">
				<ofl:Box title="PLAY VERSUS COMPUTER" className="466">		
					<ofl:JavaScript>
						var countryMap = new Object();
						<c:forEach items="${countryConfigs}" var="c">
							countryMap[${c.id}] =  { mapId: ${c.mapId}, mapName : '${c.mapName}', countryTypeId: ${c.countryTypeId}, numUnits: '${c.numUnits}', scoreLimit: ${c.scoreLimit} };
						</c:forEach>
						countryTypes = new Object();	
						<c:forEach items="${countryTypes}" var="t">
							countryTypes[${t.id}] = { name: '${t.name}', desc: '${t.description}' };
						</c:forEach>
						var countryTypeTip = "-";
					
						function countryChanged()
						{
							var countrySelectValue = document.getElementById("countrySelect").value;
							var country = countryMap[countrySelectValue];
							document.getElementById("preview_countryImage").src = "${applicationScope.imagesUrl}/map_" + country.mapId + ".png";
							document.getElementById("preview_mapName").innerHTML = country.mapName;
							document.getElementById("preview_countryType").innerHTML = countryTypes[country.countryTypeId].name;
							document.getElementById("preview_numUnits").innerHTML = country.numUnits;
							document.getElementById("preview_scoreLimit").innerHTML = country.scoreLimit;
							countryTypeTip = countryTypes[country.countryTypeId].desc;
						}
					</ofl:JavaScript>
					<ofl:select key="selectCountryConfig" name="selectedCountryConfig" id="countrySelect" list="${countrySelect}" value="${selectedCountryConfig}" onchange="countryChanged()"/>
				</ofl:Box>
		
				<div id="PBMCreateInvitation_mapPreview">
					<div id="PBMCreateInvitation_previewImage">				
						<img id="preview_countryImage" src="${applicationScope.imagesUrl}/map_dummy.gif" alt="Map preview"/>
					</div>
					
					<div id="PBMCreateInvitation_previewStats">
						<table class="stable">
							<col width="60%"/>
							<col width="40%"/>
							<tr><td class="slabel">MAP NAME:</td><td id="preview_mapName" class="svalue">-</td></tr>
							<tr>
								<td class="slabel">MAP TYPE:</td>
								<td class="svalue">
						    		<a id="preview_countryType" style="border: 0px; text-decoration: none; color: #ffffff" href="" onmouseover="Tip(countryTypeTip)" onmouseout="UnTip()" onclick="return false;">
						    			-
						    		</a>
								</td>
							</tr>
							<tr><td class="slabel">NUMBER OF UNITS:</td><td id="preview_numUnits" class="svalue">-</td></tr>
							<tr><td class="slabel">POINTS NEEDED TO WIN:</td><td id="preview_scoreLimit" class="svalue">-</td></tr>
						</table>
					</div>
				</div>
		
				<ofl:Box className="466">
				    <ofl:hidden name="ai" value="${ai}"/>
				    <ofl:submit key="play"/>
				</ofl:Box>
			</ofl:form>
		</c:otherwise>
	</c:choose>
</div>

<%@include file="/WEB-INF/jsp/layout/bottom.jsp"%>

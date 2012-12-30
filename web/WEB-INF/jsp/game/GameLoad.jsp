<%@ page contentType="text/xml; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<response>
	<code>0</code>
	<map>
		<tile>${tileImageNumbers}</tile>
		<own>${tileOwners}</own>
	</map>
	<slm>${scoreLimit}</slm>
	<fow>${fogOfWarEnabled}</fow>
	<f1r>${faction1IsRed}</f1r>
	<f1s>${faction1Starts}</f1s>
	<lbi>${lobbyId}</lbi>
	<pbm>${playByMail}</pbm>
	<rge>${user != null && user.hasEmail && user.receiveGameEventsByMail? 1 : 0}</rge>
	<c:forEach items="${executedActions}" var="e">
		<act>${e}</act>
	</c:forEach>
	<ofl:Cache key="GameLoad_static" timeToLiveSeconds="3600">
		<c:forEach items="${terrain}" var="t">
			<ter>
				<id>${t.id}</id>
				<nam>${t.name}</nam>
				<vcp>${t.victoryPoints}</vcp>
				<stm>${t.strengthModifier}</stm>
				<c:forEach items="${t.tileProperties}" var="p">
					<img>${p.tileImageNumber},${p.edgeTerrainImageNumber},${p.openTerrainImageNumber}</img>
				</c:forEach>
			</ter>
		</c:forEach>
		<c:forEach items="${units}" var="u">
			<unit>
				<id>${u.id}</id>
				<nam>${u.name}</nam>
				<dsc>${u.description}</dsc>
				<cls>${u.unitClassIntValue}</cls>
				<img>${u.imageNumber}</img>
				<arm>${u.maxArmour}</arm>
				<amm>${u.maxAmmo}</amm>
				<vsr>${u.visionRange}</vsr>
				<mpt>${u.movementPoints}</mpt>	
				<nac>${u.actions}</nac>	
				<cmx>${u.containerMaxUnits}</cmx>	
				<cui>${u.containerUnitIdsStringValue}</cui>	
				<car>${u.containerArmourPercentagePerTurn}</car>	
				<cam>${u.containerAmmoPercentagePerTurn}</cam>	
				<tui>${u.transformableToUnitId}</tui>
				<tty>${u.transformableTypeIntValue}</tty>
				<vcp>${u.victoryPoints}</vcp>
				<isb>${u.isBase? 1 : 0}</isb>
				<son>${u.unitSetupOnStringValue}</son>
				<snx>${u.unitSetupNextToStringValue}</snx>
				<bdr>${u.beDetectedRange}</bdr>
				<c:forEach items="${u.allStrengthProperties}" var="p">
					<sp>${p.enemyUnitClassIntValue},${p.maxStrengthWithAmmo},${p.maxStrengthWithoutAmmo},${p.attackRange}</sp>
				</c:forEach>
				<c:forEach items="${u.allMovementCostProperties}" var="p">
					<mc>${p.terrainId},${p.movementCost}</mc>
				</c:forEach>
			</unit>
		</c:forEach>
		<c:forEach items="${tips}" var="t">
			<tip>
				<txt>${t.text}</txt>
				<img>${t.image}</img>
			</tip>
		</c:forEach>
	</ofl:Cache>
</response>
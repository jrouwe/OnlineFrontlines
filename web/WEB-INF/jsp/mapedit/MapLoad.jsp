<%@ page contentType="text/xml; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<response>
	<code>0</code>
	<map>
		<tile>${tileImageNumbers}</tile>
		<own>${tileOwners}</own>
	</map>
</response>
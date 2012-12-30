<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

			</div>
			<c:if test="${user != null && user.isAdmin}">
				<div id="footer">
					<hr></hr>
					[<a href="${applicationScope.appUrl}/Admin.do">Admin Page</a>]
					[<a href="http://validator.w3.org/check?uri=referer">Validate XHTML</a>]
					[<a href="http://jigsaw.w3.org/css-validator/check/referer">Validate CSS</a>]<br/>
				</div>
			</c:if>
		</div>
	</body>
</html>

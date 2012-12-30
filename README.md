Overview
--------

Online Frontlines is a turn based strategy game that you can play in your browser. The biggest difference with other strategy games is that you can see the entire map on one screen and you cannot create new units. You play the game online versus someone else. You can either play through mail or you can play live.

Screenshots can be found here: [screen shots](https://github.com/jrouwe/OnlineFrontlines/wiki/Screen-shots)

Try out playing the game against yourself here: [play game](http://jrouwe.nl/ofl/game.html)

Installation instructions
-------------------------

The following guide assumes you're running Windows 7.

You need to have MySQL 5.5, Flex 2 SDK and Tomcat 5.5 installed.

Copy everything from the <onlinefrontlines>/tomcat folder to your tomcat setup folder

Make sure a user with 'manager' role is set up in \<tomcat>/conf/tomcat-users.xml and fill in the username / password in \<onlinefrontlines>/build.properties.

Change the 'non-SSL HTTP/1.1 Connector' to:

    <Connector address="localhost" port="80" maxHttpHeaderSize="8192"
               maxThreads="150" minSpareThreads="25" maxSpareThreads="75"
               enableLookups="false" redirectPort="443" acceptCount="100"
               connectionTimeout="20000" disableUploadTimeout="true"
			   compression="on" 
			   compressionMinSize="2048" 
			   noCompressionUserAgents="gozilla,traviata,.*?MSIE 5.*?,.*?MSIE 6.*?" 
			   compressableMimeType="text/html,text/xml,text/javascript"/>

Change the 'SSL HTTP/1.1 Connector' to:

    <Connector port="443" maxHttpHeaderSize="8192"
               maxThreads="150" minSpareThreads="25" maxSpareThreads="75"
               enableLookups="false" disableUploadTimeout="true"
               acceptCount="100" scheme="https" secure="true"
               keystoreFile="${user.home}/.keystore" keystorePass="tomcat"
               clientAuth="false" sslProtocol="TLS" />

Now make a self signed certificate in your home folder:

	cd \Users\<user name>
	"%JAVA_HOME%\bin\keytool" -genkey -keyalg RSA -alias tomcat -storepass tomcat -keysize 2048
	
As 'first and last name' (CN) fill in the domain name you will be running the application on (localhost?). 

Make sure you accept this certificate in your browser.

Edit \<tomcat>/conf/context.xml and add a JDBC resource (in the \<Context> element):

	<!-- Resources -->
	<Resource auth="Container" 
		name="jdbc/onlinefrontlines" 
		type="javax.sql.DataSource" 
		driverClassName="com.mysql.jdbc.Driver" 
		url="jdbc:mysql://localhost:3306/onlinefrontlines?autoReconnect=true&amp;useUnicode=true&amp;characterEncoding=UTF8" 
		username="onlinefrontlines" 
		password="onlinefrontlines"
		removeAbandoned="true"
		removeAbandonedTimeout="300"
		logAbandoned="true"
		validationQuery="SELECT 1" 
		testOnBorrow="true"		
		maxActive="100"
		maxIdle="30" 
		maxWait="10000"
		poolPreparedStatements="true" 
		maxOpenPreparedStatements="256"/>		
		
Override the error page in \<tomcat>/conf/web.xml by adding this in the \<web-app> tag:

    <!-- Error pages -->
    <error-page>
    	<error-code>404</error-code>
    	<location>/NotFound.html</location>
  	</error-page>

    <error-page>
    	<error-code>500</error-code>
    	<location>/ServerError.html</location>
  	</error-page>

	<error-page>
		<exception-type>java.lang.Throwable</exception-type>
		<location>/ServerError.html</location>
	</error-page>

You can override any setting in \<onlinefronlines>/web/WEB-INF/config/global.properties in \<tomcat>/conf/web.xml too:
	
	<context-param>
		<param-name>name</param-name>
		<param-value>value</param-value>
	</context-param>

Now start tomcat using:

catalina start

Edit \<onlinefrontlines>/web/WEB-INF/config/global.properties and fill in the relevant hosts / ports and passwords and facebook information.

Create the database with:

ant createdb

then install the application with

ant install  

Under UNIX
----------
		
Install tomcat:

	apt-get install tomcat5.5
	
Follow the installation instructions for Tomcat on Windows.
	
Edit /etc/default/tomcat5.5 and add:

	JAVA_HOME=/usr/lib/jvm/java-6-sun
	JAVA_OPTS="$JAVA_OPTS -Xmx384m -jvm server"	
	
Edit /usr/share/tomcat5.5/conf/policy.d/03catalina.policy and add the lines:

	permission java.io.FilePermission "${catalina.base}${file.separator}webapps${file.separator}OnlineFrontlines${file.separator}WEB-INF${file.separator}classes${file.separator}logging.properties", "read";
	permission java.lang.RuntimePermission "setContextClassLoader";
	
in the tomcat-juli.jar codebase.

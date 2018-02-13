### Implementing *JASPIC* on JBoss/Wildfly

The first thing to do is decide on the kind of *SAM* you need for your scenario (typically a custom *SAM* for more control or an existing option such as *OpenID Connect (OIDC)*). If you are implementing your own custom *SAM* remember to implement the ```javax.security.auth.message.module.ServerAuthModule``` interface.
In this case we are using a custome one, see *company-sam* (```com.company.security.authCompanyServerAuthModule```)

1. First create a Wildfly Module (this should be when server is down)
	- create a directory in ```<wildfly_home>/modules/com/company/sam/main```
	- create a module.xml
	
		```xml
			<module xmlns="urn:jboss:module:1.1" name="com.comany.sam">
				<resources>
					<resource-root path="java-jwt-3.3.0.jar"/>
					<resource-root path="company-sam-1.0.0-SNAPSHOT.jar"/>
				</resources>

				<dependencies>
					<module name="javaee.api"/>
					<module name="com.fasterxml.jackson.core.jackson-annotations" />
					<module name="com.fasterxml.jackson.core.jackson-core" />
					<module name="com.fasterxml.jackson.core.jackson-databind" />
					<module name="org.apache.commons.codec" />
				</dependencies>
			</module			
		```
	- copy *java-jwt-3.3.0.jar* and *company-sam-1.0.0-SNAPSHOT.jar* into ```<wildfly_home>/modules/com/company/sam/main```
2. Create a new security domain (```company-sd```) in the server configuration (```standalone.xml```)
	```xml
		<security-domain name="company-sd" cache-type="default">
			<authentication-jaspi>
				<auth-module code="com.company.security.auth.CompanyServerAuthModule" module="com.company.sam">
					<module-option name="algorithm" value="RSA"/>
					<module-option name="public.key" value="blahblahblahblahblahblahblahblahblahblahblah"/>
				</auth-module>
			</authentication-jaspi>
		</security-domain>	
	```
3. Linking the *Web Application* application to the *Security Domain*
	- create a new DD (```company-service/src/main/webapp/WEB-INF/jboss-web.xml```)
	```xml
	<?xml version="1.0" encoding="UTF-8"?>
	<jboss-web version="8.0"
			   xmlns="http://www.jboss.com/xml/ns/javaee"
			   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			   xsi:schemaLocation="http://www.jboss.com/xml/ns/javaee http://www.jboss.org/schema/jbossas/jboss-web_8_0.xsd">
		<context-root>/company-service</context-root>
		<security-domain>company-sd</security-domain> <!-- maps to the one defined in the server configuration (standalone.xml) -->
	</jboss-web>
	```
## Verify Deployment

After you've deployed deployed the *company-service-1.0.0-SNAPSHOT.war* you can use *curl* to test the services (or Postman - https://www.getpostman.com)

```bash
# Run without token
curl GET http://localhost:8080/company-service/api/customer/list

# Run using a fake token, any token will do here there is no validation
curl -H "Authorization: Bearer mytokendfafasfsasfsaffkrtwet" GET http://localhost:8080/company-service/api/customer/list

# Formatted option
curl -H "Authorization: Bearer mytokendfafasfsasfsaffkrtwet" GET http://localhost:8080/company-service/api/customer/list | jq
```

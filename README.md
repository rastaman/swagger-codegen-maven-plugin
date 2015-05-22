swagger-codegen-maven-plugin
============================

[![Build Status](https://travis-ci.org/garethjevans/swagger-codegen-maven-plugin.svg?branch=master)](https://travis-ci.org/garethjevans/swagger-codegen-maven-plugin)

A Maven plugin to support the [swagger](http://swagger.io) code generation project

Usage
============================

Add to your `build->plugins` section (default phase is `generate-sources` phase)
```xml
<plugin>
    <groupId>org.garethevans.maven.plugins</groupId>
    <artifactId>swagger-codegen-maven-plugin</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <executions>
        <execution>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                <inputSpec>src/main/resources/api.yaml</inputSpec>
                <language>java</language>
            </configuration>
        </execution>
    </executions>
</plugin>
```

Followed by:

```
mvn clean compile
```

### Configuration parameters

- `inputSpec` - swagger spec file path
- `language` - target generation language
- `output` - target output path (default is `${project.build.directory}/generated-sources/swagger`)
- `templateDirectory` - directory with mustache templates
- `addCompileSourceRoot` - add the output directory to the project as a source root (`true` by default)
- `parameters` - a list of properties to set on the generator (if he has setters and getters for it) :
```
  <plugin>
  	<groupId>org.garethevans.maven.plugins</groupId>
  	<artifactId>swagger-codegen-maven-plugin</artifactId>
  	<version>0.0.1-SNAPSHOT</version>
  	<executions>
  		<execution>
		    <id>generate-client</id>
    		<phase>generate-sources</phase>
    		<goals>
    			<goal>generate</goal>
    		</goals>
    		<configuration>
    			<inputSpec>src/main/resources/api.yaml</inputSpec>
    			<language>java</language>
    		</configuration>
  		</execution>
  		<execution>
    		<id>generate-server</id>
    		<phase>generate-sources</phase>
    		<goals>
    			<goal>generate</goal>
    		</goals>
    		<configuration>
    			<inputSpec>src/main/resources/api.yaml</inputSpec>
    			<language>jaxrs</language>
    			<parameters>
    				<apiPackage>io.expansible.api</apiPackage>
    				<modelPackage>io.expansible.model</modelPackage>
    			</parameters>
    		</configuration>
  		</execution>
  		<execution>
    		<id>generate-scala-client</id>
    		<phase>generate-sources</phase>
    		<goals>
    			<goal>generate</goal>
    		</goals>
    		<configuration>
    			<language>scala</language>
    			<inputSpec>src/main/resources/api.yaml</inputSpec>
    			<parameters>
    				<invokerPackage>io.swagger.scala</invokerPackage>
    				<authPreemptive>true</authPreemptive>    	
    			</parameters>
    		</configuration>
  		</execution>
  		<execution>
    		<id>generate-spring</id>
    		<phase>generate-sources</phase>
    		<goals>
    			<goal>generate</goal>
    		</goals>
    		<configuration>
    			<inputSpec>src/main/resources/api.yaml</inputSpec>
    			<language>spring-mvc</language>
    			<parameters>
    				<title>${project.name} Spring MVC Server</title>
    				<invokerPackage>io.swagger.spring</invokerPackage>
    			</parameters>
    		</configuration>
  		</execution>  	
  	</executions>
  </plugin>
```

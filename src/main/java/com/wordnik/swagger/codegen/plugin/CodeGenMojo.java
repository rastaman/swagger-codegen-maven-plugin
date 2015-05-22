package com.wordnik.swagger.codegen.plugin;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import static com.wordnik.swagger.codegen.plugin.AdditionalParams.TEMPLATE_DIR_PARAM;
import io.swagger.parser.SwaggerParser;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.ServiceLoader;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.reflection.Reflector;
import org.codehaus.plexus.util.reflection.ReflectorException;

import com.wordnik.swagger.codegen.ClientOptInput;
import com.wordnik.swagger.codegen.ClientOpts;
import com.wordnik.swagger.codegen.CodegenConfig;
import com.wordnik.swagger.codegen.DefaultGenerator;
import com.wordnik.swagger.models.Swagger;

/**
 * Goal which generates client/server code from a swagger json/yaml definition.
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class CodeGenMojo extends AbstractMojo {

    /**
     * Location of the output directory.
     */
    @Parameter(name = "output",
            property = "swagger.codegen.maven.plugin.output",
            defaultValue = "${project.build.directory}/generated-sources/swagger")
    private File output;

    /**
     * Location of the swagger spec, as URL or file.
     */
    @Parameter(name = "inputSpec", required = true)
    private String inputSpec;

    /**
     * Folder containing the template files.
     */
    @Parameter(name = "templateDirectory")
    private File templateDirectory;

    /**
     * Client language to generate.
     */
    @Parameter(name = "language", required = true)
    private String language;

    @Parameter
    private Map parameters;

    /**
     * Add the output directory to the project as a source root, so that the
     * generated java types are compiled and included in the project artifact.
     */
    @Parameter(defaultValue = "true")
    private boolean addCompileSourceRoot = true;

    /**
     * The project being built.
     */
    @Parameter(readonly = true, required = true, defaultValue = "${project}")
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException {
        Swagger swagger = new SwaggerParser().read(inputSpec);

        CodegenConfig config = forName(language);
        config.setOutputDir(output.getAbsolutePath());

        if (null != templateDirectory) {
            config.additionalProperties().put(TEMPLATE_DIR_PARAM, templateDirectory.getAbsolutePath());
        }
        
        Reflector parametersSetter = new Reflector();
        if ( parameters != null ) {
            for ( Object k : parameters.keySet() ) {
            	try {
    				Method m = parametersSetter.getMethod(config.getClass(), "set" + StringUtils.capitalise(k.toString()), new Class[] { String.class });
    				if ( m != null ) {
    					m.invoke(config, parameters.get(k));
    				} else {
    					getLog().warn("There is no setter for property '" + k +"' wich take a string as argument");
    				}
    			} catch (Exception e) {
    				throw new MojoExecutionException("Cannot set property '" + k +"' with value '" + parameters.get(k) + "': " + e.getMessage() );
    			}
            }        	
        }
        
        ClientOptInput input = new ClientOptInput().opts(new ClientOpts()).swagger(swagger);
        input.setConfig(config);
        new DefaultGenerator().opts(input).generate();

        if (addCompileSourceRoot) {
            project.addCompileSourceRoot(output.toString());
        }
    }

    private CodegenConfig forName(String name) {
        ServiceLoader<CodegenConfig> loader = ServiceLoader.load(CodegenConfig.class);
        for (CodegenConfig config : loader) {
            if (config.getName().equals(name)) {
                return config;
            }
        }

        // else try to load directly
        try {
            return (CodegenConfig) Class.forName(name).newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Can't load config class with name ".concat(name), e);
        }
    }
}

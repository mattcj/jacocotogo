/**
 * Copyright (C) 2013 Matthew C. Jenkins (matt@helmetsrequired.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.helmetsrequired.jacocotogo;

import java.io.File;
import org.apache.maven.plugin.AbstractMojo;



import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Plugin to allow fetching jacoco data from remote servers where
 *  the 'org.jacoco:type=Runtime MBean' is exposed via JMX
 *
 * @author Matthew C. Jenkins
 */
@Mojo(name = "jmx")
public class JaCoCoToGoJmxMojo extends AbstractMojo {
    /**
     * Whether the build should be failed if JaCoCo execution data can not be fetched
     */
    @Parameter(required = true, property = "jacocotogo.failOnError", defaultValue="false")
    private boolean failOnError;
    /**
     * The serviceURL of the JMX server i.e. 'service:jmx:rmi:///jndi/rmi://myserver.mydomain.com:&lt;portNo&gt;/jmxrmi'
     */
    @Parameter(required = true, property = "jacocotogo.serviceURL")
    private String serviceURL;
    /**
     * The username for the JMX server if authentication is enabled
     */
    @Parameter(property = "jacocotogo.username")
    private String username;
    /**
     * The password for the JMX server if authentication is enabled
     */
    @Parameter(property = "jacocotogo.password")
    private String password;
    /**
     * The file to write with the fetched jacoco data
     */
    @Parameter(required = true, property = "jacocotogo.outputFile", defaultValue = "${project.build.directory}/jacocotogo/jacoco.exec")
    private String outputFile;
    /**
     * whether to reset the coverage statistics after fetching the jacoco data
     */
    @Parameter(property = "jacocotogo.resetAfterFetch", defaultValue = "true")
    private boolean resetAfterFetch;

    /** {@inheritDoc} */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {                                
        File file = new File(outputFile);
        File directory = file.getParentFile();
        if (!directory.exists()) {
            getLog().debug("creating directory: " + directory.getAbsolutePath());
            directory.mkdirs();
        }
        
        try {            
            JaCoCoToGo.fetchJaCoCoDataOverJmx(serviceURL, username, password, file, resetAfterFetch);
        } catch (JaCoCoToGoException ex) {
            getLog().warn("Exception while running plugin.  failOnError = " + failOnError + ". " + ex.getMessage());
            if (failOnError) {                
                throw new MojoExecutionException("Exception while running plugin", ex);
            }
        } catch (JaCoCoToGoValidationException ex) {
            getLog().warn("Exception while running plugin.  failOnError = " + failOnError + ". " + ex.getMessage());
            if (failOnError) {
                throw new MojoFailureException("Exception while running plugin", ex);
            }
        }        
    }
    
    /**
     * <p>Setter for the field <code>failOnError</code>.</p>
     * 
     * @param failOnError whether build should be failed if an error occurs
     */
    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }
    
    /**
     * <p>Setter for the field <code>serviceURL</code>.</p>
     *
     * @param serviceURL a {@link java.lang.String} object.
     */
    public void setServiceURL(String serviceURL) {
        this.serviceURL = serviceURL;
    }
    
    /**
     * <p>Setter for the field <code>username</code>.</p>
     *
     * @param username a {@link java.lang.String} object.
     */
    public void setUsername(String username) {
        this.username = username;
    }        

    /**
     * <p>Setter for the field <code>password</code>.</p>
     *
     * @param password a {@link java.lang.String} object.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * <p>Setter for the field <code>outputFile</code>.</p>
     *
     * @param outputFile a {@link java.lang.String} object.
     */
    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    /**
     * <p>Setter for the field <code>resetAfterFetch</code>.</p>
     *
     * @param resetAfterFetch a boolean.
     */
    public void setResetAfterFetch(boolean resetAfterFetch) {
        this.resetAfterFetch = resetAfterFetch;
    }        
    
}

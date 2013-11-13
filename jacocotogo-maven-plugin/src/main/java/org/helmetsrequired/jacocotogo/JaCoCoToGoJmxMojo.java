/**
 * The MIT License
 * Copyright (c) 2013 Matthew C. Jenkins
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
            throw new MojoExecutionException("Exception while running plugin", ex);
        } catch (JaCoCoToGoValidationException ex) {
            throw new MojoFailureException("Exception while running plugin", ex);
        }        
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

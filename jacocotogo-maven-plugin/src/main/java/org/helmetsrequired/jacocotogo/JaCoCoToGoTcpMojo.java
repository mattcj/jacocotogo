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
 *  the jacoco javaagent is running with output=tcpserver
 *
 * @author Matt Jenkins
 */
@Mojo(name = "tcp")
public class JaCoCoToGoTcpMojo extends AbstractMojo {    
    /**
     * The hostname where the jacoco javaagent is running
     */
    @Parameter(required = true, property = "jacocotogo.hostname")
    private String hostname;
    
    /**
     * The port where the tcpserver is listening
     */
    @Parameter(required = true, property = "jacocotogo.port")
    int port;
    
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
            JaCoCoToGo.fetchJaCoCoDataOverTcp(hostname, port, file, resetAfterFetch);
        } catch (JaCoCoToGoException ex) {
            throw new MojoExecutionException("Exception while running plugin", ex);
        } catch (JaCoCoToGoValidationException ex) {
            throw new MojoFailureException("Exception while running plugin", ex);
        }        
    }

    /**
     * <p>Setter for the field <code>hostname</code>.</p>
     *
     * @param hostname a {@link java.lang.String} object.
     */
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    /**
     * <p>Setter for the field <code>port</code>.</p>
     *
     * @param port a int.
     */
    public void setPort(int port) {
        this.port = port;
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

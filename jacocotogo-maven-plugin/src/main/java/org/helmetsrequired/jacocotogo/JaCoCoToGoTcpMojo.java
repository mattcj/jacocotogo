
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

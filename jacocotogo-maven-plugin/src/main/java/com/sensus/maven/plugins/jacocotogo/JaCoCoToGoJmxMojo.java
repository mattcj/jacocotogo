
package com.sensus.maven.plugins.jacocotogo;

import java.io.File;
import org.apache.maven.plugin.AbstractMojo;



import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Plugin to allow fetching jacoco data from remote servers where 
 *  the 'org.jacoco:type=Runtime MBean' is exposed via JMX
 * @author Matt Jenkins
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
    
    public void setServiceURL(String serviceURL) {
        this.serviceURL = serviceURL;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }        

    public void setPassword(String password) {
        this.password = password;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public void setResetAfterFetch(boolean resetAfterFetch) {
        this.resetAfterFetch = resetAfterFetch;
    }        
    
}


package com.sensus.maven.plugins.jacocotogo;

/**
 * An error resulting from an unexpected failure, server unreachable, file not writeable, etc....
 * 
 * @author Matt Jenkins
 */
public class JaCoCoToGoException extends RuntimeException {

    public JaCoCoToGoException(String message) {
        super(message);
    }

    public JaCoCoToGoException(String message, Throwable cause) {
        super(message, cause);
    }

    public JaCoCoToGoException(Throwable cause) {
        super(cause);
    }
    
}

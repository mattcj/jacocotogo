
package org.helmetsrequired.jacocotogo;

/**
 * An exception which represents an error resulting from user input
 * 
 * @author Matt Jenkins
 */
public class JaCoCoToGoValidationException extends Exception {

    public JaCoCoToGoValidationException(String message) {
        super(message);
    }

    public JaCoCoToGoValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public JaCoCoToGoValidationException(Throwable cause) {
        super(cause);
    }
    
}


package org.helmetsrequired.jacocotogo;

/**
 * An exception which represents an error resulting from user input
 *
 * @author Matt Jenkins 
 */
public class JaCoCoToGoValidationException extends Exception {

    /**
     * <p>Constructor for JaCoCoToGoValidationException.</p>
     *
     * @param message a {@link java.lang.String} object.
     */
    public JaCoCoToGoValidationException(String message) {
        super(message);
    }

    /**
     * <p>Constructor for JaCoCoToGoValidationException.</p>
     *
     * @param message a {@link java.lang.String} object.
     * @param cause a {@link java.lang.Throwable} object.
     */
    public JaCoCoToGoValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * <p>Constructor for JaCoCoToGoValidationException.</p>
     *
     * @param cause a {@link java.lang.Throwable} object.
     */
    public JaCoCoToGoValidationException(Throwable cause) {
        super(cause);
    }
    
}


package org.helmetsrequired.jacocotogo;

/**
 * An error resulting from an unexpected failure, server unreachable, file not writeable, etc....
 *
 * @author Matt Jenkins
 * @version project.version
 */
public class JaCoCoToGoException extends RuntimeException {

    /**
     * <p>Constructor for JaCoCoToGoException.</p>
     *
     * @param message a {@link java.lang.String} object.
     */
    public JaCoCoToGoException(String message) {
        super(message);
    }

    /**
     * <p>Constructor for JaCoCoToGoException.</p>
     *
     * @param message a {@link java.lang.String} object.
     * @param cause a {@link java.lang.Throwable} object.
     */
    public JaCoCoToGoException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * <p>Constructor for JaCoCoToGoException.</p>
     *
     * @param cause a {@link java.lang.Throwable} object.
     */
    public JaCoCoToGoException(Throwable cause) {
        super(cause);
    }
    
}

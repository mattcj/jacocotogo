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

/**
 * An exception which represents an error resulting from user input
 *
 * @author Matthew C. Jenkins 
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

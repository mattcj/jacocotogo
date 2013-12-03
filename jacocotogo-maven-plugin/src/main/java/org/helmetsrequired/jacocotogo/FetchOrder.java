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
import org.apache.maven.plugins.annotations.Parameter;

/**
 *
 * @author Matthew C. Jenkins
 */
public class FetchOrder {
    private FetchType fetchType;
    private String hostname;    
    private File outputFile;
    
    public void setType(String type) throws JaCoCoToGoValidationException {
        if (type == null || type.trim().isEmpty()) {
            throw new JaCoCoToGoValidationException("parameter 'type' is required.");
        }
        try {            
            this.fetchType = FetchType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException ex) {
            
        }
    }
    
    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    public File getOutputFile() {
        return outputFile;
    }

    @Override
    public String toString() {
        return "FetchOrder{" + "fetchType=" + fetchType + ", hostname=" + hostname + ", outputFile=" + outputFile + '}';
    }            
        
}

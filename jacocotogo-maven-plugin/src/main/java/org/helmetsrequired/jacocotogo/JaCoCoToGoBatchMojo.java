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
import java.util.List;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 *
 * @author Matthew C. Jenkins
 */
@Mojo(name="batch")
public class JaCoCoToGoBatchMojo extends AbstractMojo {    
    private static final String DEFAULT_OUTPUT_FILE_PREFIX = "jacoco";
    private static final String DEFAULT_OUTPUT_FILE_SUFFIX = ".exec";
    @Parameter
    private List<FetchOrder> fetchOrders;
    @Parameter(defaultValue = "${project.build.directory}/jacocotogo")
    private File outputDir;

    public void execute() throws MojoExecutionException, MojoFailureException {
        for (int i = 0; i < fetchOrders.size(); i ++) {
            FetchOrder fetchOrder = fetchOrders.get(i);
            if (fetchOrder.getOutputFile() == null) {
                File outputFile = new File(outputDir, DEFAULT_OUTPUT_FILE_PREFIX + (i + 1) + DEFAULT_OUTPUT_FILE_SUFFIX);
                fetchOrder.setOutputFile(outputFile);
            }            
            getLog().info(fetchOrder.toString());
        }        
    }    
}

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
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 *
 * @author Matthew C. Jenkins
 * @since 1.1
 */
@Mojo(name = "batch")
public class JaCoCoToGoBatchMojo extends AbstractMojo {

    private static final String DEFAULT_OUTPUT_FILE_PREFIX = "jacoco";
    private static final String DEFAULT_OUTPUT_FILE_SUFFIX = ".exec";
    @Parameter
    private List<FetchOrder> fetchOrders;
    @Parameter(defaultValue = "${project.build.directory}/jacocotogo")
    private File outputDir;
    @Parameter(defaultValue = "false")
    private boolean failOnError;
    @Parameter(defaultValue = "false")
    private boolean merge;
    @Parameter(defaultValue = "${project.build.directory}/jacocotogo/merged.exec")
    private File mergeFile;  

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (fetchOrders == null || fetchOrders.isEmpty()) {
            handleError(new IllegalArgumentException("No fetchOrders specified."));
            return;
        }
        for (int i = 0; i < fetchOrders.size(); i++) {
            try {
                FetchOrder fetchOrder = fetchOrders.get(i);
                if (fetchOrder.getOutputFile() == null) {
                    File outputFile = new File(outputDir, DEFAULT_OUTPUT_FILE_PREFIX + (i + 1) + DEFAULT_OUTPUT_FILE_SUFFIX);
                    fetchOrder.setOutputFile(outputFile);
                }
                fetchOrder.validate();
                getLog().debug(fetchOrder.toString());
                if (fetchOrder.getFetchType() == FetchType.JMX) {
                    JaCoCoToGo.fetchJaCoCoDataOverJmx(fetchOrder.getServiceURL(), fetchOrder.getUsername(), fetchOrder.getPassword(), fetchOrder.getOutputFile(), fetchOrder.isResetAfterFetch());
                } else if (fetchOrder.getFetchType() == FetchType.TCP) {
                    JaCoCoToGo.fetchJaCoCoDataOverTcp(fetchOrder.getHostname(), fetchOrder.getPort(), fetchOrder.getOutputFile(), fetchOrder.isResetAfterFetch());
                }
            } catch (JaCoCoToGoValidationException ex) {
                handleError(ex);
            } catch (RuntimeException ex) {
                handleError(ex);
            }
        }
        if (merge) {
            try {
                List<File> filesToMerge = new ArrayList<File>();
                for (FetchOrder fetchOrder : fetchOrders) {
                    if (fetchOrder.getOutputFile().canWrite()) {
                        filesToMerge.add(fetchOrder.getOutputFile());
                    }
                }
                JaCoCoToGo.mergeJaCoCoData(filesToMerge, mergeFile);
            } catch (RuntimeException ex) {
                handleError(ex);
            }
        }
    }
    
    private void handleError(Exception ex) throws MojoExecutionException {
        if (failOnError) {
            throw new MojoExecutionException("Error while running plugin.", ex);
        } else {
            getLog().warn("Error while running plugin.  Reason: '" + ex.getMessage() + "'");
        }
    }
}

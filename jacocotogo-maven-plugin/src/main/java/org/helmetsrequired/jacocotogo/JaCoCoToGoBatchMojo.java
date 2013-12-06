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
 * Retrieves JaCoCo execution data from multiple sources, and optionally,
 * merge the results.
 * 
 * @author Matthew C. Jenkins
 * @since 1.1
 */
@Mojo(name = "batch")
public class JaCoCoToGoBatchMojo extends AbstractMojo {

    private static final String DEFAULT_OUTPUT_FILE_PREFIX = "jacoco";
    private static final String DEFAULT_OUTPUT_FILE_SUFFIX = ".exec";
    /**
     * a {@link java.util.List} of {@link org.helmetsrequired.jacocotogo.Source}
     * from which JaCoCo execution data should be fetched.
     */
    @Parameter(required = true)
    private List<Source> sources;
    
    /**
     * The output directory to use as a default.  Can be overridden by specifying
     * the source.outputFile and mergeFile parameters.
     */
    @Parameter(defaultValue = "${project.build.directory}/jacocotogo")
    private File outputDir;
    /**
     * Whether the build should be failed if JaCoCo execution data can not be fetched
     */
    @Parameter(defaultValue = "false")
    private boolean failOnError;
    /**
     * Whether to generate a merged file from the individually collected JaCoCo execution data.
     */
    @Parameter(defaultValue = "false")
    private boolean merge;
    
    /**
     * The file where merged JaCoCo execution data should be written.
     */
    @Parameter(defaultValue = "${project.build.directory}/jacocotogo/merged.exec")
    private File mergeFile;  

    /** {@inheritDoc} */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (sources == null || sources.isEmpty()) {
            handleError(new IllegalArgumentException("No sources specified."));
            return;
        }
        for (int i = 0; i < sources.size(); i++) {
            try {
                Source source = sources.get(i);
                if (source.getOutputFile() == null) {
                    File outputFile = new File(outputDir, DEFAULT_OUTPUT_FILE_PREFIX + (i + 1) + DEFAULT_OUTPUT_FILE_SUFFIX);
                    source.setOutputFile(outputFile);
                }
                source.validate();
                getLog().debug(source.toString());
                if (source.getSourceType() == SourceType.JMX) {
                    JaCoCoToGo.fetchJaCoCoDataOverJmx(source.getServiceURL(), source.getUsername(), source.getPassword(), source.getOutputFile(), source.isResetAfterFetch());
                } else if (source.getSourceType() == SourceType.TCP) {
                    JaCoCoToGo.fetchJaCoCoDataOverTcp(source.getHostname(), source.getPort(), source.getOutputFile(), source.isResetAfterFetch());
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
                for (Source source : sources) {
                    if (source.getOutputFile().canWrite()) {
                        filesToMerge.add(source.getOutputFile());
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

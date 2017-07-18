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

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import org.jacoco.core.tools.ExecFileLoader;
import org.jacoco.core.data.ExecutionDataWriter;
import org.jacoco.core.runtime.RemoteControlReader;
import org.jacoco.core.runtime.RemoteControlWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * JaCoCoToGo class.</p>
 *
 * @author Matthew C. Jenkins
 */
public class JaCoCoToGo {

    private static final Logger logger = LoggerFactory.getLogger(JaCoCoToGo.class);
    private static final String JMX_CREDENTIALS_KEY = "jmx.remote.credentials";
    private static final int MAX_PORT = (int) (Math.pow(2, 16) - 1);
    private static final String JACOCO_OBJECT_NAME_STRING = "org.jacoco:type=Runtime";
    private static final String JACOCO_FETCH_METHOD_NAME = "getExecutionData";

    /**
     * <p>
     * fetchJaCoCoDataOverJmx.</p>
     *
     * @param serviceUrl a {@link java.lang.String} object representing a
     * {@link javax.management.remote.JMXServiceURL}.
     * @param username the username to use for the JMX connection if
     * authentication is enabled.
     * @param password the password to use for the JMX connection if
     * authentication is enabled.
     * @param outputFile a {@link java.io.File} where the retrieved jacoco data
     * should be written.
     * @param resetAfterFetch whether the jacoco data on the remote system
     * should be reset after fetching.
     * @throws org.helmetsrequired.jacocotogo.JaCoCoToGoValidationException if
     * there is a problem with the supplied arguments
     */
    public static final void fetchJaCoCoDataOverJmx(String serviceUrl, String username, String password, File outputFile, boolean resetAfterFetch) throws JaCoCoToGoValidationException {
        // construct JMX Service URL        
        JMXServiceURL url = constructJMXServiceURL(serviceUrl);

        // fetch the execution data
        byte[] executionData = getExecutionDataViaJMX(url, username, password, resetAfterFetch);

        // save to file
        saveExecutionData(executionData, outputFile);
    }

    /**
     * <p>
     * fetchJaCoCoDataOverTcp.</p>
     *
     * @param hostname the hostname where the remote jvm is running
     * @param port the port where the JaCoCo java agent TCP Server is listening
     * @param outputFile a {@link java.io.File} where the retrieved jacoco data
     * should be written.
     * @param resetAfterFetch whether the jacoco data on the remote system
     * should be reset after fetching.
     * @throws org.helmetsrequired.jacocotogo.JaCoCoToGoValidationException if
     * there is a problem with the supplied arguments.
     */
    public static final void fetchJaCoCoDataOverTcp(String hostname, int port, File outputFile, boolean resetAfterFetch) throws JaCoCoToGoValidationException {
        InetAddress hostAddress = checkHostname(hostname);
        checkPort(port);

        // fetch the execution data
        byte[] executionData = getExecutionDataViaJaCoCoTCPServer(hostAddress, port, resetAfterFetch);

        // save to file
        saveExecutionData(executionData, outputFile);
    }

    private static String[] getCredentials(String username, String password) {
        return new String[]{username == null ? "" : username, password == null ? "" : password};
    }

    private static void populateEnvironmentMapWithCredentials(Map<String, Object> envMap, String username, String password) {
        envMap.put(JMX_CREDENTIALS_KEY, getCredentials(username, password));
    }

    private static JMXServiceURL constructJMXServiceURL(String serviceUrl) throws JaCoCoToGoValidationException {
        logger.debug("Constructing JMXServiceURL from String: '{}'", serviceUrl);
        try {
            return new JMXServiceURL(serviceUrl);
        } catch (MalformedURLException ex) {
            throw new JaCoCoToGoValidationException("Could not create JMXServiceURL", ex);
        }
    }

    private static JMXConnector constructJMXConnector(JMXServiceURL url, Map<String, ?> envMap) throws IOException {
        logger.debug("Constructing JMXConnector for JMXServiceURL: '{}'", url);
        return JMXConnectorFactory.newJMXConnector(url, envMap);
    }

    private static ObjectName constructJaCoCoObjectName() throws JaCoCoToGoValidationException {
        logger.debug("Constructing JMX ObjectName for JaCoCo MBean, using String: '{}'", JACOCO_OBJECT_NAME_STRING);
        try {
            return new ObjectName(JACOCO_OBJECT_NAME_STRING);
        } catch (MalformedObjectNameException ex) {
            throw new JaCoCoToGoValidationException("Unable to create ObjectName for JaCoCo MBean", ex);
        }
    }

    private static void saveExecutionData(byte[] executionData, File outputFile) {
        logger.info("Saving JaCoCo execution data to file: '{}'", outputFile.getAbsolutePath());
        if (outputFile.exists()) {
            throw new JaCoCoToGoException("outputFile '" + outputFile.getAbsolutePath() + "' already exists.");
        }
        File outputFileDir = outputFile.getAbsoluteFile().getParentFile();
        if (!outputFileDir.exists()) {
            if (!outputFileDir.mkdirs()) {
                throw new IllegalArgumentException("Failed to create directory: '" + outputFileDir.getAbsolutePath() + "'");
            }
        }

        if (executionData == null) {
            logger.warn("executionData is null, nothing to save");
            return;
        }
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            fos = new FileOutputStream(outputFile);
            bos = new BufferedOutputStream(fos);
            bos.write(executionData);
            bos.flush();
        } catch (IOException ex) {
            throw new JaCoCoToGoException("Error saving execution data to file: " + outputFile.getAbsolutePath(), ex);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ex) {
                    // bummer
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException ex) {
                    // bummer
                }
            }
        }
    }

    /**
     *
     * @param url a {@link javax.management.remote.JMXServiceURL} where the JMX
     * service is running
     * @param username the username to use for the JMX connection if
     * authentication is enabled.
     * @param password the password to use for the JMX connection if
     * authentication is enabled.
     * @param resetAfterFetch whether the JaCoCo data on the remote system
     * should be reset after fetching.
     * @return byte array containing the JaCoCo execution data
     * @throws JaCoCoToGoValidationException if there is a problem with the
     * supplied arguments.
     */
    private static byte[] getExecutionDataViaJMX(JMXServiceURL url, String username, String password, boolean resetAfterFetch) throws JaCoCoToGoValidationException {
        try {
            Map<String, Object> envMap = new HashMap<String, Object>();
            populateEnvironmentMapWithCredentials(envMap, username, password);
            JMXConnector connector = constructJMXConnector(url, envMap);
            connector.connect();
            MBeanServerConnection connection = connector.getMBeanServerConnection();
            ObjectName objectName = constructJaCoCoObjectName();
            logger.info("Invoking method: '{}' on ObjectName: {}", JACOCO_FETCH_METHOD_NAME, objectName);
            Object result = connection.invoke(objectName, JACOCO_FETCH_METHOD_NAME, new Object[]{resetAfterFetch}, new String[]{boolean.class.getName()});
            try {
                byte[] data = (byte[]) result;
                logger.debug("{} bytes of JaCoCo execution data received", data.length);
                return data;
            } catch (ClassCastException ex) {
                throw new JaCoCoToGoException("Expected byte[] but got " + result.getClass().getName(), ex);
            }
        } catch (InstanceNotFoundException ex) {
            throw new JaCoCoToGoException("Could not find JaCoCo MBean at JMXServiceURL: '" + url + "'", ex);
        } catch (MBeanException ex) {
            throw new JaCoCoToGoException("Error fetching execution data from JaCoCo MBean at JMXServiceURL: '" + url + "'", ex);
        } catch (ReflectionException ex) {
            throw new JaCoCoToGoException("Error fetching execution data from JaCoCo MBean at JMXServiceURL: '" + url + "'", ex);
        } catch (IOException ex) {
            throw new JaCoCoToGoException("IOException while communicating with JMXServiceURL: '" + url + "'", ex);
        }
    }

    private static InetAddress checkHostname(String hostname) throws JaCoCoToGoValidationException {
        try {
            logger.debug("Verifying that hostname: '{}' can be resolved.", hostname);
            return InetAddress.getByName(hostname);
        } catch (UnknownHostException ex) {
            throw new JaCoCoToGoValidationException("Unable to resolve hostname: '" + hostname + "'", ex);
        }
    }

    private static void checkPort(int port) throws JaCoCoToGoValidationException {
        if (port < 1 || port > MAX_PORT) {
            throw new JaCoCoToGoValidationException("Invalid port: '" + port + "'");
        }
    }    

    /**
     *
     * @param hostname the hostname where the remote jvm is running.
     * @param port the port where the JaCoCo Java Agent TCP Server is listening.
     * @param resetAfterFetch whether JaCoCo coverage data should be reset after
     * fetch
     * @return a byte array containing the JaCoCo execution data.
     */
    private static byte[] getExecutionDataViaJaCoCoTCPServer(InetAddress address, int port, boolean resetAfterFetch) throws JaCoCoToGoValidationException {
        ByteArrayOutputStream output = null;
        Socket socket = null;
        try {
            // 1. Open socket connection
            socket = new Socket(address, port);
            logger.info("Connecting to {}", socket.getRemoteSocketAddress());
            RemoteControlWriter remoteWriter = new RemoteControlWriter(socket.getOutputStream());
            RemoteControlReader remoteReader = new RemoteControlReader(socket.getInputStream());

            output = new ByteArrayOutputStream();
            ExecutionDataWriter outputWriter = new ExecutionDataWriter(output);
            remoteReader.setSessionInfoVisitor(outputWriter);
            remoteReader.setExecutionDataVisitor(outputWriter);

            // 2. Request dump
            remoteWriter.visitDumpCommand(true, resetAfterFetch);
            remoteReader.read();

            // 3. verify valid JaCoCo execution data
            byte[] outputBytes = output.toByteArray();
            if (outputBytes.length <= 5) {
                throw new JaCoCoToGoException("No JaCoCo execution data received.");
            }

            // 4. Return data
            return outputBytes;
        } catch (final IOException e) {
            throw new JaCoCoToGoException("Unable to dump coverage data", e);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ex) {
                    // bummer
                }
            }
            if (output != null) {
                try {
                    output.close();
                } catch (IOException ex) {
                    // bummer
                }
            }
        }
    }
    
    /**
     * <p>
     * mergeJaCoCoData.</p>
     * 
     * Combines the specified inputFiles into a single merged file.
     * 
     * @param inputFiles a {@link java.util.List} of JaCoCo execution data files to merge.
     * @param mergeFile the {@link java.io.File} where merged data should be written
     */
    public static void mergeJaCoCoData(List<File> inputFiles, File mergeFile) {
        // check the mergeFile
        if (mergeFile == null) {
            throw new IllegalArgumentException("mergeFile is null");
        }
        if (mergeFile.exists()) {
            throw new JaCoCoToGoException("File already exists: '" + mergeFile.getAbsolutePath());
        }
        File mergeFileDir = mergeFile.getAbsoluteFile().getParentFile();
        if (! mergeFileDir.exists()) {
            if (!mergeFileDir.mkdirs()) {
                throw new JaCoCoToGoException("Error creating directory: '" + mergeFileDir.getAbsolutePath() + "'");
            }
        }
        
        // load data from each file
        ExecFileLoader execFileLoader = new ExecFileLoader();
        for (File inputFile : inputFiles) {
            try {
                logger.debug("Loading data from input file: '" + inputFile.getAbsolutePath() + "'");
                execFileLoader.load(inputFile);
            } catch (IOException ex) {
                throw new JaCoCoToGoException("Error loading data from file: '" + inputFile.getAbsolutePath() + "'");
            }
        }
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        logger.info("Writing merged data to '" + mergeFile.getAbsolutePath() + "'");
        ExecutionDataWriter executionDataWriter;
        try {
            fos = new FileOutputStream(mergeFile);
            bos = new BufferedOutputStream(fos);
            executionDataWriter = new ExecutionDataWriter(bos);
            execFileLoader.getSessionInfoStore().accept(executionDataWriter);
            execFileLoader.getExecutionDataStore().accept(executionDataWriter);
        } catch (IOException ex) {
            throw new JaCoCoToGoException("Error saving merged execution data to file: " + mergeFile.getAbsolutePath(), ex);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ex) {
                    // bummer
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException ex) {
                    // bummer
                }
            }
        }
    }
}

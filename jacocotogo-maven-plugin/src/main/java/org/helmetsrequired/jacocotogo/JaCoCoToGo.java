/**
 * The MIT License
 * Copyright (c) 2013 Matthew C. Jenkins
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
import org.jacoco.core.data.ExecutionDataWriter;
import org.jacoco.core.runtime.RemoteControlReader;
import org.jacoco.core.runtime.RemoteControlWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>JaCoCoToGo class.</p>
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
     * <p>fetchJaCoCoDataOverJmx.</p>
     *
     * @param serviceUrl a {@link java.lang.String} object.
     * @param username a {@link java.lang.String} object.
     * @param password a {@link java.lang.String} object.
     * @param outputFile a {@link java.io.File} object.
     * @param resetAfterFetch a boolean.
     * @throws org.helmetsrequired.jacocotogo.JaCoCoToGoValidationException if any.
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
     * <p>fetchJaCoCoDataOverTcp.</p>
     *
     * @param hostname a {@link java.lang.String} object.
     * @param port a int.
     * @param outputFile a {@link java.io.File} object.
     * @param resetAfterFetch a boolean.
     * @throws org.helmetsrequired.jacocotogo.JaCoCoToGoException if any.
     * @throws org.helmetsrequired.jacocotogo.JaCoCoToGoValidationException if any.
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
     * @param url
     * @param username
     * @param password
     * @param resetAfterFetch
     * @return byte array containing the jacoco execution data
     * @throws JaCoCoToGoException
     * @throws JaCoCoToGoValidationException
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
            Object result = connection.invoke(objectName, JACOCO_FETCH_METHOD_NAME, new Object[]{true}, new String[]{boolean.class.getName()});
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
     * @param hostname
     * @param port
     * @param resetAfterFetch whether JaCoCo coverage data should be reset after
     * fetch
     * @return the jacoco coverage data
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

            // 3. Request dump
            remoteWriter.visitDumpCommand(true, resetAfterFetch);
            remoteReader.read();            
            
            return output.toByteArray();
        } catch (final IOException e) {
            throw new RuntimeException("Unable to dump coverage data", e);
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
}

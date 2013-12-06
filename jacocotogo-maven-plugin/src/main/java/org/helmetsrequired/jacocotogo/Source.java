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
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a single source for JaCoCo execution data.
 * 
 * @author Matthew C. Jenkins
 */
public class Source {
    private final static Logger logger = LoggerFactory.getLogger(Source.class);
    private static final int MAX_PORT = (int) (Math.pow(2, 16) - 1);
    private static final String DEFAULT_JMX_URL_PREFIX = "service:jmx:rmi:///jndi/rmi://";
    private static final String DEFAULT_JMX_URL_SUFFIX = "/jmxrmi";
    
    /**
     * a {@link org.helmetsrequired.jacocotogo.SourceType} representing whether
     * this source is accessed via TCP or JMX
     */
    private SourceType sourceType;
    
    /**
     * Input parameter for the type of the source, should be either jmx or tcp.
     * Not necessary if serviceURL is specified.
     */
    private String type;
    
    /**
     * the hostname of the remote system.  Not necessary if serviceURL is specified.
     */
    private String hostname;
    
    /**
     * the port to use for accessing the remote system.
     * Not necessary if serviceURL is specified.
     */
    private int port;
    
    /**
     * The file where the jacoco execution data should be written.
     */
    private File outputFile;
    
    /**
     * A username for jmx access if authentication is required by the remote system.
     */
    private String username;
    
    /**
     * A password for jmx access if authentication is required by the remote system.
     */
    private String password;
    
    /**
     * The serviceURL representing where the jacoco data can be retrieved.  For JMX access
     * see {@link javax.management.remote.JMXServiceURL} for format details.  For
     * TCP access the serviceURL should take the format: 'tcp://myhost.mydomain.com:port'
     * 
     * If the serviceURL is specified it takes precedence over the type, hostname, and port parameters.
     */
    private String serviceURL;
    
    /**
     * whether to reset the coverage statistics after fetching the jacoco data
     */
    private boolean resetAfterFetch = true;
    
    /**
     * 
     * @param type a {@link java.lang.String} representing the type of source.  Should be either 'tcp' or 'jmx'.
     *  Not required when 'serviceURL' is set.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 
     * @return a {@link java.lang.String} representing the type of source.
     */
    public String getType() {
        return type;
    }

    /**
     * 
     * @return a {@link org.helmetsrequired.jacocotogo.SourceType} representing the type of source.
     *  This is set during validation based on 'serviceURL' if provided, or 'type' if 'serviceURL' is
     *  not supplied.
     */
    public SourceType getSourceType() {
        return sourceType;
    }        

    /**
     * 
     * @return a {@link java.lang.String} specifying the hostname where the remote JVM 
     *  is located.
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * 
     * @param hostname a {@link java.lang.String} specifying the hostname where the remote JVM 
     *  is located.  Only required if 'serviceURL' is not set.
     */
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    /**
     * 
     * @return the port number where JaCoCo data can be accessed on the remote server, either
     *  via TCP or JMX.  Only required if 'serviceURL' is not set.
     */
    public int getPort() {
        return port;
    }

    /**
     * 
     * @param port the port number where JaCoCo data can be accessed on the remote server, either
     *  via TCP or JMX.  Only required if 'serviceURL' is not set.
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * 
     * @param outputFile optional {@link java.io.File} specifying where JaCoCo execution data
     *  will be written.  If omitted, defaults to '${project.build.directory}/jacocotogo/jacoco[n].exec'
     *  where [n] is a index which corresponds to the order of the source.
     */
    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    /**
     * 
     * @return {@link java.io.File} specifying where JaCoCo execution data
     *  will be written.
     */
    public File getOutputFile() {
        return outputFile;
    }

    /**
     * 
     * @return the {@link java.lang.String} representing the username for authentication
     *  to JMX server.
     */
    public String getUsername() {
        return username;
    }

    /**
     * 
     * @param username the {@link java.lang.String} representing the username for authentication
     *  to JMX server.  Only necessary if authentication is enabled on the JMX location.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 
     * @return the {@link java.lang.String} representing the password for authentication
     *  to JMX server.
     */
    public String getPassword() {
        return password;
    }

    /**
     * 
     * @param password the {@link java.lang.String} representing the password for authentication
     *  to JMX server.  Only necessary if authentication is enabled on the JMX location.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 
     * @return a {@link java.lang.String} with the serviceURL representing where
     *  the jacoco data can be retrieved.
     * 
     *  If the serviceURL is specified it takes precedence over the type, hostname, and port parameters.
     */
    public String getServiceURL() {
        return serviceURL;
    }

    /**
     * 
     * @param serviceURL a {@link java.lang.String} of the serviceURL representing where
     *  the jacoco data can be retrieved.  For JMX access see {@link javax.management.remote.JMXServiceURL}
     *  for format details.  For TCP access the serviceURL should take the
     *  format: 'tcp://myhost.mydomain.com:port'
     * 
     *  If the serviceURL is specified it takes precedence over the 'type', 'hostname', and 'port' parameters.
     */
    public void setServiceURL(String serviceURL) {
        this.serviceURL = serviceURL;
    }

    /**
     * 
     * @return whether the coverage statistics will be reset after fetching the jacoco data.
     */
    public boolean isResetAfterFetch() {
        return resetAfterFetch;
    }

    /**
     * 
     * @param resetAfterFetch whether the coverage statistics should be reset after fetching the jacoco data.
     */
    public void setResetAfterFetch(boolean resetAfterFetch) {
        this.resetAfterFetch = resetAfterFetch;
    }

    /**
     * Validates that valid input parameters are specified.
     */
    public void validate() {
        if (serviceURL == null) {
            // type is required
            if (type == null) {
                throw new IllegalArgumentException("Parameter 'type' is missing.  It is required if 'serviceURL' is not set.");
            }
            try {                
                sourceType = SourceType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException ex) {                            
                throw new IllegalArgumentException("Parameter 'type' has invalid value: '" + type + "' valid values are: '" + SourceType.values().toString(), ex);
            }
            if (hostname == null) {
                throw new IllegalArgumentException("Parameter 'hostname' is missing.  It is required if 'serviceURL' is not set.");
            }
            validateHostname();
            validatePort();
            if (sourceType == SourceType.JMX) {
                constructJMXServiceURL();
            }
        } else {
            parseServiceURL();
        }

    }

    private InetAddress validateHostname() {
        if (hostname == null || hostname.trim().isEmpty()) {
            throw new IllegalArgumentException("Parameter 'hostname' is not provided.");
        }
        logger.debug("Verifying that hostname: '{}' can be resolved.", hostname);
        try {
            return InetAddress.getByName(hostname);
        } catch (UnknownHostException ex) {
            throw new RuntimeException("Could not resolve hostname: '" + hostname + "'.", ex);
        }
    }

    private void validatePort() {
        if (port < 1 || port > MAX_PORT) {
            throw new IllegalArgumentException("Invalid 'port': '" + port + "'");
        }
    }   

    private void constructJMXServiceURL() {
        serviceURL = DEFAULT_JMX_URL_PREFIX + hostname + ":" + port + DEFAULT_JMX_URL_SUFFIX;
    }

    private void parseServiceURL() {
        if (serviceURL == null) {
            throw new IllegalArgumentException("Null 'serviceURL'.  It is required if 'type', 'hostname', and 'port' are not set.");
        }
        String[] tokens = serviceURL.split(":");
        if (tokens.length < 3) {
            throw new IllegalArgumentException("Invalid 'serviceURL'.");
        }
        if (tokens[0].equalsIgnoreCase("tcp")) {
            parseTCPServiceURL(tokens);
        } else if (tokens[0].equals("service") && tokens[1].equals("jmx")) {
            parseJMXServiceURL(tokens);
        } else {
            throw new IllegalArgumentException("Invalid 'serviceURL'.  Expected 'serviceURL' to start with 'tcp' or 'service:jmx'");
        }
    }

    private void parseTCPServiceURL(String[] tokens) {
        if (tokens.length > 3) {
            throw new IllegalArgumentException("Invalid 'serviceURL'.  For tcp 'serviceURL' should be in the format of: 'tcp://<hostname>:<portno>'.");
        }
        sourceType = SourceType.TCP;
        if (!tokens[1].startsWith("//")) {
            throw new IllegalArgumentException("Invalid 'serviceURL'.  For tcp 'serviceURL' should be in the format of: 'tcp://<hostname>:<portno>'.");
        }
        hostname = tokens[1].substring(2);
        validateHostname();
        port = Integer.parseInt(tokens[2]);
        validatePort();
    }

    private void parseJMXServiceURL(String[] tokens) {
        sourceType = SourceType.JMX;
    }

    @Override
    public String toString() {
        return "Source{" + "sourceType=" + sourceType + ", type=" + type + ", hostname=" + hostname + ", port=" + port + ", outputFile=" + outputFile + ", username=" + username + ", password=" + (password == null ? null : "*****" ) + ", serviceURL=" + serviceURL + ", resetAfterFetch=" + resetAfterFetch + '}';
    }

    
        
}

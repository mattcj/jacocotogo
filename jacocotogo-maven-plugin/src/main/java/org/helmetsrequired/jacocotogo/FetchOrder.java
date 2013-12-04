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
 *
 * @author Matthew C. Jenkins
 */
public class FetchOrder {

    private final static Logger logger = LoggerFactory.getLogger(FetchOrder.class);
    private static final int MAX_PORT = (int) (Math.pow(2, 16) - 1);
    private static final String DEFAULT_JMX_URL_PREFIX = "service:jmx:rmi:///jndi/rmi://";
    private static final String DEFAULT_JMX_URL_SUFFIX = "/jmxrmi";
    private FetchType fetchType;
    private String type;
    private String hostname;
    private int port;
    private File outputFile;
    private String username;
    private String password;
    private String serviceURL;
    private boolean resetAfterFetch = true;

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public FetchType getFetchType() {
        return fetchType;
    }        

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getServiceURL() {
        return serviceURL;
    }

    public void setServiceURL(String serviceURL) {
        this.serviceURL = serviceURL;
    }

    public boolean isResetAfterFetch() {
        return resetAfterFetch;
    }

    public void setResetAfterFetch(boolean resetAfterFetch) {
        this.resetAfterFetch = resetAfterFetch;
    }

    public void validate() {
        if (serviceURL == null) {
            // type is required
            if (type == null) {
                throw new IllegalArgumentException("Parameter 'type' is missing.  It is required if 'serviceURL' is not set.");
            }
            try {                
                fetchType = FetchType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException ex) {                            
                throw new IllegalArgumentException("Parameter 'type' has invalid value: '" + type + "' valid values are: '" + FetchType.values().toString(), ex);
            }
            if (hostname == null) {
                throw new IllegalArgumentException("Parameter 'hostname' is missing.  It is required if 'serviceURL' is not set.");
            }
            validateHostname();
            validatePort();
            if (fetchType == FetchType.JMX) {
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
        fetchType = FetchType.TCP;
        if (!tokens[1].startsWith("//")) {
            throw new IllegalArgumentException("Invalid 'serviceURL'.  For tcp 'serviceURL' should be in the format of: 'tcp://<hostname>:<portno>'.");
        }
        hostname = tokens[1].substring(2);
        validateHostname();
        port = Integer.parseInt(tokens[2]);
        validatePort();
    }

    private void parseJMXServiceURL(String[] tokens) {
        fetchType = FetchType.JMX;
    }

    @Override
    public String toString() {
        return "FetchOrder{" + "fetchType=" + fetchType + ", type=" + type + ", hostname=" + hostname + ", port=" + port + ", outputFile=" + outputFile + ", username=" + username + ", password=" + (password == null ? null : "*****")  + ", serviceURL=" + serviceURL + ", resetAfterFetch=" + resetAfterFetch + '}';
    }
        
}

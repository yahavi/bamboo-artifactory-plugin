/*
 * Copyright (C) 2010 JFrog Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jfrog.bamboo.util;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads and serves the contents of the plugins properties file
 *
 * @author Noam Y. Tenne
 */
public class PluginProperties {

    private static final String PLUGIN_KEY = "plugin.key";
    private static final String PLUGIN_DESCRIPTOR_KEY = "plugin.descriptor.key";
    public static final String GRADLE_DEPENDENCY_FILENAME_KEY = "gradle.dependency.file.name";
    public static final String IVY_DEPENDENCY_FILENAME_KEY = "ivy.dependency.file.name";
    public static final String MAVEN3_DEPENDENCY_FILENAME_KEY = "maven3.dependency.file.name";

    private static Properties pluginProperties;

    static {
        try {
            pluginProperties = getPluginProperties();
        } catch (IOException e) {
            throw new RuntimeException("Error occurred while loading the plugin properties file.", e);
        }
    }

    public static String getPluginKey() {
        return getPropertyValue(PLUGIN_KEY);
    }

    public static String getPluginDescriptorKey() {
        return getPropertyValue(PLUGIN_DESCRIPTOR_KEY);
    }

    public static String getPropertyValue(String propertyKey) {
        return pluginProperties.getProperty(propertyKey);
    }

    private static Properties getPluginProperties() throws IOException {
        InputStream pluginPropertiesStream = null;
        try {
            pluginPropertiesStream =
                    Thread.currentThread().getContextClassLoader().getResourceAsStream("artifactory.plugin.properties");

            if (pluginPropertiesStream == null) {
                throw new IllegalStateException("Could not find artifactory.plugin.properties resource.");
            }
            Properties pluginProperties = new Properties();
            pluginProperties.load(pluginPropertiesStream);
            return pluginProperties;
        } finally {
            IOUtils.closeQuietly(pluginPropertiesStream);
        }
    }
}

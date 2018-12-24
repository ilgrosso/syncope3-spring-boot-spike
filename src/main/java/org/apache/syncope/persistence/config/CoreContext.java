/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.syncope.persistence.config;

import java.net.MalformedURLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileUrlResource;

//@Configuration
public class CoreContext {

    private static final Logger LOG = LoggerFactory.getLogger(CoreContext.class);

//    @Value("${conf.directory}")
//    private String confDirectory;

//    @Bean
//    public static PropertySourcesPlaceholderConfigurer confDirectoryPropertyConfigurer() throws MalformedURLException {
//        LOG.error("MMMMMMMMMMMMMMMMMMMMMMMM2 " + confDirectory);
//        PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
//        pspc.setOrder(1);
//        pspc.setIgnoreResourceNotFound(true);
//        pspc.setIgnoreUnresolvablePlaceholders(true);
//        pspc.setLocations(
//                new FileUrlResource("file:/" + confDirectory + "/persistence.properties"),
//                new FileUrlResource("file:/" + confDirectory + "/domains/*.properties"),
//                new FileUrlResource("file:/" + confDirectory + "/security.properties"),
//                new FileUrlResource("file:/" + confDirectory + "/connid.properties"),
//                new FileUrlResource("file:/" + confDirectory + "/mail.properties"),
//                new FileUrlResource("file:/" + confDirectory + "/logic.properties"),
//                new FileUrlResource("file:/" + confDirectory + "/workflow.properties"),
//                new FileUrlResource("file:/" + confDirectory + "/provisioning.properties"));
//        return pspc;
//    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer classpathPropertyConfigurer() {
        LOG.error("MMMMMMMMMMMMMMMMMMMMMMMM3 ZAZA");
        System.out.println("MMMMMMMMMMMMMMMMMMMMMMMM3");
        
        PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
        pspc.setLocations(
                new ClassPathResource("persistence.properties"),
                new ClassPathResource("domains/*.properties"),
                new ClassPathResource("security.properties"),
                new ClassPathResource("connid.properties"),
                new ClassPathResource("mail.properties"),
                new ClassPathResource("logic.properties"),
                new ClassPathResource("workflow.properties"),
                new ClassPathResource("provisioning.properties"));
        return pspc;
    }
}

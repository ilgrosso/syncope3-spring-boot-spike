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

import org.apache.syncope.core.provisioning.api.AnyObjectProvisioningManager;
import org.apache.syncope.core.provisioning.api.GroupProvisioningManager;
import org.apache.syncope.core.provisioning.api.UserProvisioningManager;
import org.apache.syncope.core.provisioning.api.cache.VirAttrCache;
import org.apache.syncope.core.provisioning.api.propagation.PropagationTaskExecutor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.env.Environment;

@ImportResource("classpath:/provisioningContext.xml")
@Configuration
public class ProvisioningContext implements EnvironmentAware {

    private Environment env;

    @Override
    public void setEnvironment(final Environment env) {
        this.env = env;
    }

    @ConditionalOnMissingBean
    @Bean
    public PropagationTaskExecutor propagationTaskExecutor()
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        return (PropagationTaskExecutor) Class.forName(env.getProperty("propagationTaskExecutor")).
                newInstance();
    }

    @ConditionalOnMissingBean
    @Bean
    public UserProvisioningManager userProvisioningManager()
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        return (UserProvisioningManager) Class.forName(env.getProperty("userProvisioningManager")).
                newInstance();
    }

    @ConditionalOnMissingBean
    @Bean
    public GroupProvisioningManager groupProvisioningManager()
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        return (GroupProvisioningManager) Class.forName(env.getProperty("groupProvisioningManager")).
                newInstance();
    }

    @ConditionalOnMissingBean
    @Bean
    public AnyObjectProvisioningManager anyObjectProvisioningManager()
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        return (AnyObjectProvisioningManager) Class.forName(env.getProperty("anyObjectProvisioningManager")).
                newInstance();
    }

    @ConditionalOnMissingBean
    @Bean
    public VirAttrCache virAttrCache()
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        return (VirAttrCache) Class.forName(env.getProperty("virAttrCache")).
                newInstance();
    }
}

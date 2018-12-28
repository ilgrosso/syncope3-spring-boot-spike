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

import org.apache.syncope.core.logic.LogicInvocationHandler;
import org.apache.syncope.core.logic.init.ClassPathScanImplementationLookup;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.env.Environment;

@ImportResource("classpath:/logicContext.xml")
@Configuration
public class LogicContext implements EnvironmentAware {

    private Environment env;

    @Override
    public void setEnvironment(final Environment env) {
        this.env = env;
    }

    @Bean
    public LogicInvocationHandler logicInvocationHandler() {
        return new LogicInvocationHandler();
    }

    @Bean
    public ClassPathScanImplementationLookup classPathScanImplementationLookup()
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        return (ClassPathScanImplementationLookup) Class.forName(env.getProperty("classPathScanImplementationLookup")).
                newInstance();
    }
}

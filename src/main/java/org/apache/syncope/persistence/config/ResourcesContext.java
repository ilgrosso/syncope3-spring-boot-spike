package org.apache.syncope.persistence.config;

import org.apache.syncope.core.spring.ResourceWithFallbackLoader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResourcesContext {

    @Value("${content.directory}")
    private String contentDirectory;

    // Master domain files
    @Bean(name = "MasterProperties")
    public ResourceWithFallbackLoader masterProperties() {
        ResourceWithFallbackLoader masterProperties = new ResourceWithFallbackLoader();
        masterProperties.setPrimary("file:" + contentDirectory + "/domains/Master.properties");
        masterProperties.setFallback("classpath:domains/Master.properties");
        return masterProperties;
    }

    @Bean(name = "MasterContentXML")
    public ResourceWithFallbackLoader masterContentXML() {
        ResourceWithFallbackLoader masterContentXML = new ResourceWithFallbackLoader();
        masterContentXML.setPrimary("file:" + contentDirectory + "/domains/MasterContent.xml");
        masterContentXML.setFallback("classpath:domains/MasterContent.xml");
        return masterContentXML;
    }

    // Two domain files
    @Bean(name = "TwoProperties")
    public ResourceWithFallbackLoader twoProperties() {
        ResourceWithFallbackLoader twoProperties = new ResourceWithFallbackLoader();
        twoProperties.setPrimary("file:" + contentDirectory + "/domains/Two.properties");
        twoProperties.setFallback("classpath:domains/Two.properties");
        return twoProperties;
    }

    @Bean(name = "TwoContentXML")
    public ResourceWithFallbackLoader twoContentXML() {
        ResourceWithFallbackLoader twoContentXML = new ResourceWithFallbackLoader();
        twoContentXML.setPrimary("file:" + contentDirectory + "/domains/TwoContent.xml");
        twoContentXML.setFallback("classpath:domains/TwoContent.xml");
        return twoContentXML;
    }

    // persistenceContext files
    @Bean
    public ResourceWithFallbackLoader viewsXML() {
        ResourceWithFallbackLoader viewsXML = new ResourceWithFallbackLoader();
        viewsXML.setPrimary("file:" + contentDirectory + "/views.xml");
        viewsXML.setFallback("classpath:views.xml");
        return viewsXML;
    }

    @Bean
    public ResourceWithFallbackLoader indexesXML() {
        ResourceWithFallbackLoader indexesXML = new ResourceWithFallbackLoader();
        indexesXML.setPrimary("file:" + contentDirectory + "/indexes.xml");
        indexesXML.setFallback("classpath:indexes.xml");
        return indexesXML;
    }

}

package com.example.demo;

import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Cluster;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@AutoConfigureAfter({RedisAutoConfiguration.class})
public class RedisConfig_jedis {
    private static final Logger log = LoggerFactory.getLogger(RedisConfig_jedis.class);

    @Bean(name = {"JedisConnectionFactory"})
    @Primary
    public JedisConnectionFactory createJedisConnectionFactory(RedisProperties properties, JedisPoolConfig poolConfig) {
        RedisClusterConfiguration clusterConfiguration = getClusterConfiguration(properties);
        JedisConnectionFactory factory;
        if (clusterConfiguration != null) {
            factory = new JedisConnectionFactory(clusterConfiguration, poolConfig);
        } else {
            factory = new JedisConnectionFactory(poolConfig);
        }
        return factory;
    }

    @Bean(name = {"RedisConnectionFactory"})
    @Autowired
    public RedisConnectionFactory createRedisConnectionFactory(@Qualifier("JedisConnectionFactory") JedisConnectionFactory factory) {
        return factory;
    }

    private static RedisClusterConfiguration getClusterConfiguration(RedisProperties properties) {
        Cluster cluster = properties.getCluster();
        if (cluster != null) {
            RedisClusterConfiguration config = new RedisClusterConfiguration(cluster.getNodes());
            if (cluster.getMaxRedirects() != null) {
                config.setMaxRedirects(cluster.getMaxRedirects());
            }

            return config;
        } else {
            return null;
        }
    }

    @Bean
    public JedisPoolConfig jedisPoolConfig(RedisProperties properties) {
        return new JedisPoolConfig();
    }

    @Bean
    public RedisTemplate<Object, Object> redisTemplate(@Qualifier("RedisConnectionFactory") RedisConnectionFactory connectionFactory) {
        log.info("initializing RedisTemplate!");
        RedisTemplate<Object, Object> template = new RedisTemplate();
        template.setConnectionFactory(connectionFactory);
        Jackson2JsonRedisSerializer serializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setVisibility(PropertyAccessor.ALL, Visibility.ANY);
        mapper.enableDefaultTyping(DefaultTyping.NON_FINAL);
        serializer.setObjectMapper(mapper);
        template.setValueSerializer(serializer);
        template.setKeySerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }
}

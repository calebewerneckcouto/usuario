package com.javanauta.usuario.infrastructure.config;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.time.Duration;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.data.redis.port:6379}")
    private int redisPort;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory(){
        RedisStandaloneConfiguration serverConfiguration = new RedisStandaloneConfiguration();
        serverConfiguration.setHostName(redisHost);
        serverConfiguration.setPort(redisPort);


        io.lettuce.core.SocketOptions socketOptions = SocketOptions.builder()
                .connectTimeout(Duration.ofMillis(2000))
                .build();

        ClientOptions clientOptions = ClientOptions.builder()
                .socketOptions(socketOptions)
                .build();

        LettuceClientConfiguration clientConfiguration = LettuceClientConfiguration.builder()
                .clientOptions(clientOptions)
                .commandTimeout(Duration.ofMillis(2000))
                .shutdownTimeout(Duration.ofMillis(100))
                .build();

        LettuceConnectionFactory factory = new LettuceConnectionFactory(serverConfiguration,clientConfiguration);

        factory.afterPropertiesSet();
        return factory;


    }
}

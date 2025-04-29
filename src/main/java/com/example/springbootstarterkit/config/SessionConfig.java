package com.example.springbootstarterkit.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession
public class SessionConfig implements BeanClassLoaderAware {
  private ClassLoader loader;
  @Override public void setBeanClassLoader(ClassLoader cl) { this.loader = cl; }

  @Bean
  public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
    ObjectMapper om = new ObjectMapper();
    om.activateDefaultTyping(
        om.getPolymorphicTypeValidator(),
        ObjectMapper.DefaultTyping.NON_FINAL,
        JsonTypeInfo.As.PROPERTY
    );
    om.registerModules(SecurityJackson2Modules.getModules(this.loader));
    return new GenericJackson2JsonRedisSerializer(om);
  }
}

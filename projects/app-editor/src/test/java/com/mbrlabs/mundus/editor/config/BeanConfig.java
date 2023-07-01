package com.mbrlabs.mundus.editor.config;

import org.mockito.Mockito;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

//@Configuration
public class BeanConfig {

//    @Bean
    public BeanPostProcessor spyPostProcessor() {
        return new BeanPostProcessor() {

            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                return Mockito.spy(bean);
            }
        };
    }
}

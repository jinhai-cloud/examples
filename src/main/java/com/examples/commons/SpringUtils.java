package com.examples.commons;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class SpringUtils implements BeanFactoryPostProcessor, ApplicationContextAware {
    private static ConfigurableListableBeanFactory beanFactory;
    private static ApplicationContext applicationContext;

    @SuppressWarnings("NullableProblems")
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        SpringUtils.beanFactory = beanFactory;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtils.applicationContext = applicationContext;
    }

    public static ListableBeanFactory getBeanFactory() {
        ListableBeanFactory factory = beanFactory != null ? beanFactory : applicationContext;
        return Objects.requireNonNull(factory, "No ConfigurableListableBeanFactory or ApplicationContext injected");
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        return (T) getBeanFactory().getBean(name);
    }

    public static <T> T getBean(Class<T> clazz) {
        return getBeanFactory().getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return getBeanFactory().getBean(name, clazz);
    }

    public static String getProperty(String key) {
        Objects.requireNonNull(applicationContext, "No ApplicationContext injected");
        return applicationContext.getEnvironment().getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        Objects.requireNonNull(applicationContext, "No ApplicationContext injected");
        return applicationContext.getEnvironment().getProperty(key, defaultValue);
    }

    public static <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        Objects.requireNonNull(applicationContext, "No ApplicationContext injected");
        return applicationContext.getEnvironment().getProperty(key, targetType, defaultValue);
    }
}

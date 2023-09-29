package org.example.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
@Component
public class SpringBeanRegister implements ApplicationContextAware {
    private DefaultListableBeanFactory defaultListableBeanFactory;
    private ApplicationContext applicationContext;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
        this.defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
    }

    public void registerBean(String beanName, Class<?> clazz) {
        lock.writeLock().lock();
        try {
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
            defaultListableBeanFactory.registerBeanDefinition(beanName, beanDefinitionBuilder.getRawBeanDefinition());
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void unRegisterBean(String beanName) {
        lock.writeLock().lock();
        try {
            DefaultListableBeanFactory beanFactory = getBeanFactory();
            if (beanFactory.containsBeanDefinition(beanName)) {
                beanFactory.destroySingleton(beanName);
                beanFactory.removeBeanDefinition(beanName);
                log.info("注销[{}]成功！", beanName);
            } else {
                log.info("不存在[{}]，不需要注销！", beanName);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    private DefaultListableBeanFactory getBeanFactory() {
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
        return (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
    }

    public Object getBean(String name) {
        return applicationContext.getBean(name);
    }
}
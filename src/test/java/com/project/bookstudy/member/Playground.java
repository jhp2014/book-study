package com.project.bookstudy.member;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;

@SpringBootTest
public class Playground {

    @Autowired
    ApplicationContext applicationContext;

    @Test
    void checkAllBean() {
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        Arrays.stream(beanDefinitionNames)
                .forEach(name -> {
                    System.out.println("applicationContext = " + applicationContext.getBean(name));
                });
    }

}

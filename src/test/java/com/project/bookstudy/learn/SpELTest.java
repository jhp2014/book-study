package com.project.bookstudy.learn;

import org.junit.jupiter.api.Test;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class SpELTest {

    @Test
    void SpringExpressionTest() {

        String key = "#{name}";

        SpelExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("name", "value");



    }


}

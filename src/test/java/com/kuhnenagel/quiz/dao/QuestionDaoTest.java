package com.kuhnenagel.quiz.dao;

import com.kuhnenagel.quiz.model.Question;
import com.kuhnenagel.quiz.model.Response;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class QuestionDaoTest {

    @Autowired
    QuestionDao questionDao;

    @Autowired
    Flyway flyway;

    @BeforeEach
    public void setup() {
        flyway.migrate();
    }

    @Test
    void ableToSaveAndLoadQuestion() {
        Question question = new Question("Content", "Topic", 1, List.of(new Response("content", true)));
        Question returnedQuestion = questionDao.save(question);
        assertNotNull(returnedQuestion);
    }
}
package com.kuhnenagel.quiz.dao;

import com.kuhnenagel.quiz.model.Question;
import com.kuhnenagel.quiz.model.Quiz;
import com.kuhnenagel.quiz.model.Response;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class QuestionDaoTest {

    @Autowired
    QuestionDao questionDao;

    @Autowired
    Flyway flyway;

    @BeforeEach
    public void setup() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    void ableToSaveAndLoadQuestion() {
        Question question = new Question("Content", "Topic", 1, List.of(new Response("content", true)));
        Question returnedQuestion = questionDao.save(question);
        assertNotNull(returnedQuestion);
    }

    @Test
    void ableToUpdateQuestion() {
        Question questionResponse = questionDao.save(new Question("Content", "topic1", 1, List.of(new Response("content", true))));
        assertEquals(1, questionResponse.getRank());
        questionResponse.setRank(5);
        Question questionResponse2 = questionDao.save(questionResponse);
        assertEquals(5, questionResponse2.getRank());
    }

    @Test
    void ableToUpdateQuestionContent() {
        Question questionResponse = questionDao.save(new Question("Content", "topic1", 1, List.of(new Response("content", true))));
        questionDao.updateQuestionContent(questionResponse.getId(), "new, beautiful content");
        Optional<Question> questionOptional = questionDao.findById(questionResponse.getId());
        assertTrue(questionOptional.isPresent());
        assertEquals("new, beautiful content", questionOptional.get().getContent());
    }

    @Test
    void ableToGetQuestionsByTopicAnd() {
        Question questionOneTopicOne = new Question("Content", "topic1", 1, List.of(new Response("content", true)));
        Question questionTwoTopicOne = new Question("Content", "topic1", 1, List.of(new Response("content", true)));
        Question questionThreeTopicTwo = new Question("Content", "topic2", 1, List.of(new Response("content", true)));
        questionDao.save(questionOneTopicOne);
        questionDao.save(questionTwoTopicOne);
        questionDao.save(questionThreeTopicTwo);

        List<Question> questionList = questionDao.findByTopic("topic1");
        assertNotNull(questionList);
        assertEquals(2, questionList.size());
        assertTrue(questionList.contains(questionOneTopicOne));
        assertTrue(questionList.contains(questionTwoTopicOne));

        Question questionFromDb = questionDao.findByTopic("topic2").get(0);

        questionDao.deleteQuestion(questionFromDb);

        assertTrue(questionDao.findById(questionFromDb.getId()).isEmpty());
        assertTrue(questionDao.findByTopic("topic2").isEmpty());
    }

    @Test
    void ableToSaveQuizAndAddQuestionToIt() {
        Question questionOneTopicOne = new Question("Content", "topic1", 1, List.of(new Response("content", true)));
        Question questionTwoTopicOne = new Question("Content", "topic1", 1, List.of(new Response("content", true)));
        Question questionThreeTopicTwo = new Question("Content", "topic2", 1, List.of(new Response("content", true)));
        Quiz quiz = new Quiz("name", List.of(questionOneTopicOne, questionTwoTopicOne));

        Quiz savedQuiz = questionDao.saveQuiz(quiz);
        questionDao.addQuestionToQuiz(savedQuiz.getId(), questionThreeTopicTwo);
        List<Question> list = questionDao.findByTopic("topic2");
        assertNotNull(list);
        assertFalse(list.isEmpty());
        assertEquals(savedQuiz.getId(), list.get(0).getQuizId());
    }

}
package com.kuhnenagel.quiz.dao;

import com.kuhnenagel.quiz.model.Question;
import com.kuhnenagel.quiz.model.Response;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class QuestionDao {

    private static final String INSERT_QUESTION =
            "INSERT INTO public.question (topic, content, rank) "
                    + " VALUES "
                    + " (?, ?, ?)";
    private static final String UPDATE_QUESTION =
            "UPDATE question "
                    + "SET"
                    + "topic =?"
                    + "content =?"
                    + "rank =?"
                    + " WHERE id = ?";
    private static final String DELETE_QUESTION =
            "DELETE FROM question "
                    + "WHERE id = ?";

    private static final String UPDATE_RESPONSE =
            "UPDATE response "
                    + "SET"
                    + " content = ? "
                    + " correct = ? "
                    + " WHERE id = ?";
    private static final String INSERT_RESPONSE =
            "INSERT INTO response (content, correct) "
                    + " VALUES "
                    + " (?, ?)";

    private static final String FIND_QUESTION_BY_TOPIC =
            "SELECT * FROM question WHERE topic = ?";

    private final JdbcTemplate jdbcTemplate;

    public QuestionDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Transactional
    public Question save(Question question) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        if (question.getId() != null) {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection
                        .prepareStatement(UPDATE_QUESTION); //todo make separate method
                ps.setString(1, question.getTopic());
                ps.setString(2, question.getContent());
                ps.setInt(3, question.getRank());
                ps.setLong(4, question.getId());
                return ps;
            }, keyHolder);
        } else {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection
                        .prepareStatement(INSERT_QUESTION);
                ps.setString(1, question.getTopic());
                ps.setString(2, question.getContent());
                ps.setInt(3, question.getRank());
                return ps;
            }, keyHolder);
        }
        question.setId((long) keyHolder.getKey());
        saveAllResponses(question);
        return question;
    }

    private void saveAllResponses(Question question) {
        question.getResponses().forEach(response -> {
            response.setQuestionId(question.getId());
        });
    }

    private Response saveResponse(Response response) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        if (response.getId() != null) {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(UPDATE_RESPONSE);
                ps.setString(1, response.getContent());
                ps.setBoolean(2, response.isCorrect());
                ps.setLong(3, response.getId());
                return ps;
            }, keyHolder);
        } else {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(INSERT_RESPONSE);
                ps.setString(1, response.getContent());
                ps.setBoolean(2, response.isCorrect());
                return ps;
            }, keyHolder);

        }
        response.setId((long) keyHolder.getKey());
        return response;
    }

    public void updateQuestionContent(Question question) {

    }

    public void addQuestionToQuiz(long quizId, Question question) {
        //todo add quiz into SQL

    }

    @Transactional
    public void deleteQuestion(Question question) {
        deleteResponsesOf(question);
        deleteQuestion(question.getId());
    }

    private void deleteResponsesOf(Question question) {
        question.getResponses().forEach(this::deleteResponse);
    }

    public void deleteResponse(Response response) {
        //todo deletion of R here
    }

    private void deleteQuestion(long questionId) {
        jdbcTemplate.update(DELETE_QUESTION, questionId);
    }

    public List<Question> findByTopic(String topic) {
        List<Question> questionList = new ArrayList<>();
        for (Map map : jdbcTemplate.queryForList(FIND_QUESTION_BY_TOPIC, topic)) {
            questionList.add(getQuestionForDbRow(map));
        }
        return questionList;
    }

    private Question getQuestionForDbRow(Map row) {
        Long questionId = (Long) row.get("id");
        Question question = new Question();
        question.setId(questionId);
        question.setContent((String) row.get("content"));
        question.setTopic((String) row.get("topic"));
        question.setRank((Integer) row.get("rank"));
        question.setResponses(getResponsesByQuestionId(question));
        return question;
    }

    private List<Response> getResponsesByQuestionId(Question question) {
        List<Response> responseList = new ArrayList<>();
        //todo the same as Question List method
        return responseList;
    }
}

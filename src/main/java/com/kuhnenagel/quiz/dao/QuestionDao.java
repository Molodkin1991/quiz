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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Transactional
public class QuestionDao {

    private static final String INSERT_QUESTION =
            "INSERT INTO question (topic, question_content, rank) "
                    + " VALUES "
                    + " (?, ?, ?)";
    private static final String UPDATE_QUESTION =
            "UPDATE question "
                    + "SET"
                    + "topic =?"
                    + "content =?"
                    + "rank =?"
                    + " WHERE id = ?";

    private static final String UPDATE_QUESTION_QUIZ_ID =
            "UPDATE question "
                    + "SET"
                    + "quiz_id =?"
                    + " WHERE id = ?";
    private static final String UPDATE_QUESTION_CONTENT =
            "UPDATE question "
                    + "SET"
                    + "content =?"
                    + " WHERE id = ?";
    private static final String DELETE_QUESTION =
            "DELETE FROM question "
                    + "WHERE id = ?";
    private static final String DELETE_RESPONSE =
            "DELETE FROM response "
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

    private static final String FIND_QUESTION_BY_ID =
            "SELECT * FROM question WHERE id = ?";

    private static final String FIND_RESPONSE_BY_QUESTION_ID =
            "SELECT * FROM response WHERE question_id = ?";

    private final JdbcTemplate jdbcTemplate;

    public QuestionDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Question save(Question question) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        if (question.getId() != null) {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection
                        .prepareStatement(UPDATE_QUESTION, Statement.RETURN_GENERATED_KEYS);
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
        if (keyHolder.getKey() != null) {
            question.setId((Integer) keyHolder.getKey());
            saveAllResponses(question);
        }
        return question;
    }

    private void saveAllResponses(Question question) {
        question.getResponses().forEach(response -> response.setQuestionId(question.getId()));
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
        response.setId((Integer) keyHolder.getKey());
        return response;
    }

    public void updateQuestionContent(long questionId, String content) {
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(UPDATE_QUESTION_CONTENT);
            ps.setString(1, content);
            ps.setLong(2, questionId);
            return ps;
        });
    }

    public void addQuestionToQuiz(long quizId, Question question) {
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(UPDATE_QUESTION_QUIZ_ID);
            ps.setLong(1, quizId);
            ps.setLong(2, question.getId());
            return ps;
        });
    }

    public void deleteQuestion(Question question) {
        deleteResponsesOf(question);
        deleteQuestion(question.getId());
    }

    private void deleteResponsesOf(Question question) {
        question.getResponses().forEach(this::deleteResponse);
    }

    public void deleteResponse(Response response) {
        jdbcTemplate.update(DELETE_RESPONSE, response);
    }

    private void deleteQuestion(long questionId) {
        jdbcTemplate.update(DELETE_QUESTION, questionId);
    }

    public Optional<Question> findById(Integer id) {
        return jdbcTemplate.queryForList(FIND_QUESTION_BY_ID, id)
                .stream().findFirst().map(this::getQuestionForDbRow);
    }

    public List<Question> findByTopic(String topic) {
        List<Question> questionList = new ArrayList<>();
        for (Map<String, Object> map : jdbcTemplate.queryForList(FIND_QUESTION_BY_TOPIC, topic)) {
            questionList.add(getQuestionForDbRow(map));
        }
        return questionList;
    }

    private List<Response> getResponsesByQuestionId(Question question) {
        List<Response> responseList = new ArrayList<>();
        for (Map<String, Object> map : jdbcTemplate.queryForList(FIND_RESPONSE_BY_QUESTION_ID, question.getId())) {
            responseList.add(getResponseForDbRow(map));
        }
        return responseList;
    }

    private Response getResponseForDbRow(Map<String, Object> row) {
        return new Response((Integer) row.get("id"), (String) row.get("content"), (Boolean) row.get("correct"));
    }

    private Question getQuestionForDbRow(Map<String, Object> row) {
        Integer questionId =(Integer) row.get("id");
        Question question = new Question();
        question.setId(questionId);
        question.setContent((String) row.get("question_content"));
        question.setTopic((String) row.get("topic"));
        question.setRank((Integer) row.get("rank"));
        question.setResponses(getResponsesByQuestionId(question));
        return question;
    }
}

package com.kuhnenagel.quiz.dao;

import com.kuhnenagel.quiz.model.Question;
import com.kuhnenagel.quiz.model.Quiz;
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

    private static final String WHERE_ID_IS = " WHERE id = ? ";

    private static final String INSERT_QUESTION =
            "INSERT INTO question (topic, question_content, rank) "
                    + " VALUES "
                    + " (?, ?, ?)";
    private static final String UPDATE_QUESTION =
            "UPDATE question "
                    + " SET "
                    + " topic = ?, "
                    + " question_content = ?, "
                    + " rank = ? "
                    + WHERE_ID_IS;

    private static final String INSERT_QUIZ =
            "INSERT INTO quiz (name) VALUES (?) ";

    private static final String UPDATE_QUIZ =
            "UPDATE quiz "
                    + " SET "
                    + " name = ? "
                    + WHERE_ID_IS;
    private static final String UPDATE_QUESTION_QUIZ_ID =
            "UPDATE question "
                    + " SET "
                    + " quiz_id = ? "
                    + WHERE_ID_IS;
    private static final String UPDATE_QUESTION_CONTENT =
            "UPDATE question "
                    + " SET "
                    + " question_content = ? "
                    + WHERE_ID_IS;

    private static final String DELETE_QUESTION =
            "DELETE FROM question " + WHERE_ID_IS;
    private static final String DELETE_RESPONSE =
            "DELETE FROM response " + WHERE_ID_IS;
    private static final String DELETE_RESPONSE_BY_QUESTION_ID
            = "DELETE FROM response WHERE question_id = ?";

    private static final String UPDATE_RESPONSE =
            "UPDATE response "
                    + " SET "
                    + " response_content = ?, "
                    + " correct = ? "
                    + WHERE_ID_IS;
    private static final String INSERT_RESPONSE =
            "INSERT INTO response (response_content, correct, question_id) "
                    + " VALUES "
                    + " (?, ?, ?) ";

    private static final String FIND_QUESTION_BY_TOPIC =
            "SELECT * FROM question WHERE topic = ? ";

    private static final String FIND_QUESTION_BY_ID =
            "SELECT * FROM question " + WHERE_ID_IS;

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
                        .prepareStatement(INSERT_QUESTION, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, question.getTopic());
                ps.setString(2, question.getContent());
                ps.setInt(3, question.getRank());
                return ps;
            }, keyHolder);
        }
        question.setId((Integer) keyHolder.getKeyList().get(0).get("id"));
        saveAllResponses(question);
        return question;
    }

    private void saveAllResponses(Question question) {
        for (Response response : question.getResponses()) {
            response.setQuestionId(question.getId());
            saveResponse(response);
        }
    }

    private Response saveResponse(Response response) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        if (response.getId() != null) {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(UPDATE_RESPONSE, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, response.getContent());
                ps.setBoolean(2, response.isCorrect());
                ps.setLong(3, response.getId());
                return ps;
            }, keyHolder);
        } else {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(INSERT_RESPONSE, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, response.getContent());
                ps.setBoolean(2, response.isCorrect());
                ps.setInt(3, response.getQuestionId());
                return ps;
            }, keyHolder);

        }
        response.setId((Integer) keyHolder.getKeyList().get(0).get("id"));
        return response;
    }

    public void updateQuestionContent(long questionId, String content) {
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(UPDATE_QUESTION_CONTENT, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, content);
            ps.setLong(2, questionId);
            return ps;
        });
    }

    public Quiz saveQuiz(Quiz quiz) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        if (quiz.getId() != null) {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection
                        .prepareStatement(UPDATE_QUIZ, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, quiz.getName());
                ps.setLong(2, quiz.getId());
                return ps;
            }, keyHolder);
        } else {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection
                        .prepareStatement(INSERT_QUIZ, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, quiz.getName());
                return ps;
            }, keyHolder);
        }
        Integer quizId = (Integer) keyHolder.getKeyList().get(0).get("id");
        for (Question question : quiz.getQuestions()) {
            question.setQuizId(quizId);
            save(question);
        }
        quiz.setId(quizId);
        return quiz;
    }

    public void addQuestionToQuiz(long quizId, Question question) {
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(UPDATE_QUESTION_QUIZ_ID, Statement.RETURN_GENERATED_KEYS);
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
        jdbcTemplate.update(DELETE_RESPONSE_BY_QUESTION_ID, question.getId());
    }

    public void deleteResponse(Response response) {
        jdbcTemplate.update(DELETE_RESPONSE, response.getId());
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
        return new Response((Integer) row.get("id"), (String) row.get("response_content"), (Boolean) row.get("correct"));
    }

    private Question getQuestionForDbRow(Map<String, Object> row) {
        Integer questionId = (Integer) row.get("id");
        Question question = new Question();
        question.setId(questionId);
        question.setContent((String) row.get("question_content"));
        question.setTopic((String) row.get("topic"));
        question.setRank((Integer) row.get("rank"));
        question.setQuizId((Integer) row.get("quiz_id"));
        question.setResponses(getResponsesByQuestionId(question));
        return question;
    }
}

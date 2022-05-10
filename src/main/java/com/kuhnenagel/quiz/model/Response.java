package com.kuhnenagel.quiz.model;

import java.util.Objects;

public class Response {
    private Long id;
    private Long questionId;
    private String content;
    private boolean correct;

    public Response(String content, boolean correct) {
        new Response(null, content, correct);
    }

    public Response(Long questionId, String content, boolean correct) {
        new Response(null, questionId, content, correct);
    }

    public Response(Long id, Long questionId, String content, boolean correct) {
        this.id = id;
        this.questionId = questionId;
        this.content = content;
        this.correct = correct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Response)) return false;
        Response response = (Response) o;
        return correct == response.correct && Objects.equals(id, response.id) && Objects.equals(questionId, response.questionId) && Objects.equals(content, response.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, questionId, content, correct);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }
}

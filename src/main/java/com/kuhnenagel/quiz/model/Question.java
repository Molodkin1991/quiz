package com.kuhnenagel.quiz.model;

import java.util.List;
import java.util.Objects;

public class Question {
    private Integer id;
    private String content;
    private String topic;
    private Integer rank;
    private List<Response> responses;

    public Question() {
    }

    public Question(Integer id, String content, String topic, Integer rank, List<Response> responses) {
        this.id = id;
        this.content = content;
        this.topic = topic;
        this.rank = rank;
        this.responses = responses;
    }

    public Question(String content, String topic, Integer rank, List<Response> responses) {
        this.content = content;
        this.topic = topic;
        this.rank = rank;
        this.responses = responses;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Question)) return false;
        Question question = (Question) o;
        return Objects.equals(content, question.content) && Objects.equals(topic, question.topic) && Objects.equals(rank, question.rank);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, topic, rank, responses);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public List<Response> getResponses() {
        return responses;
    }

    public void setResponses(List<Response> responses) {
        this.responses = responses;
    }
}

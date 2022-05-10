package com.kuhnenagel.quiz.model;

import java.util.List;
import java.util.Objects;

public class Quiz {
    private List<Question> questions;

    public Quiz(List<Question> questions) {
        this.questions = questions;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Quiz)) return false;
        Quiz quiz = (Quiz) o;
        return Objects.equals(questions, quiz.questions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(questions);
    }
}

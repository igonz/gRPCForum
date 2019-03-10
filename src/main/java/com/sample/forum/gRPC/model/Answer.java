package com.sample.forum.gRPC.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "answer")
public class Answer extends DatabaseObject {
    private String answerContent;

    @ManyToOne
    @JoinColumn(name="question_id", nullable=false)
    private Question question;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    public String getAnswerContent() {
        return answerContent;
    }

    public void setAnswerContent(String answerContent) {
        this.answerContent = answerContent;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

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

    public String getAnswerContent() {
        return answerContent;
    }

    public void setAnswerContent(String answerContent) {
        this.answerContent = answerContent;
    }
}

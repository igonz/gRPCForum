package com.sample.forum.gRPC.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "user")
public class User extends DatabaseObject {
    private String username;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<Question> questions = new LinkedHashSet<>();
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<Answer> answers = new LinkedHashSet<>();

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(Set<Question> questions) {
        this.questions = questions;
    }

    public Set<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(Set<Answer> answers) {
        this.answers = answers;
    }
}

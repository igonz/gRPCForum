package com.sample.forum.gRPC.service;

import com.google.common.collect.Lists;
import com.sample.forum.gRPC.model.Question;
import com.sample.forum.gRPC.repository.QuestionRepository;

import java.util.List;

public class QuestionServiceImpl implements IQuestionService {
    QuestionRepository repository;

    public QuestionServiceImpl(QuestionRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Question> getQuestions() {
        return Lists.newArrayList(repository.findAll());
    }
}

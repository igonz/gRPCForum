package com.sample.forum.gRPC.service;

import com.sample.forum.gRPC.model.Question;

import java.util.List;

public interface IQuestionService {
    List<Question> getQuestions();
}

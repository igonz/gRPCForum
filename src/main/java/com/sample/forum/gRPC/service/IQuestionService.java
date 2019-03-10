package com.sample.forum.gRPC.service;

import com.sample.forum.gRPC.model.Answer;
import com.sample.forum.gRPC.model.Question;

import java.util.List;

public interface IQuestionService {
    List<Question> getQuestions();
    Question saveQuestion(Long userID, String questionContent);
    Answer saveAnswer(Long questionID, Long userID, String answerContent);
}

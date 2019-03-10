package com.sample.forum.gRPC.service;

import com.google.common.collect.Lists;
import com.sample.forum.gRPC.model.Answer;
import com.sample.forum.gRPC.model.Question;
import com.sample.forum.gRPC.model.User;
import com.sample.forum.gRPC.repository.AnswerRepository;
import com.sample.forum.gRPC.repository.QuestionRepository;
import com.sample.forum.gRPC.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class QuestionServiceImpl implements IQuestionService {
    private QuestionRepository questionRepository;
    private AnswerRepository answerRepository;
    private UserRepository userRepository;

    @Autowired
    public QuestionServiceImpl(QuestionRepository questionRepository, AnswerRepository answerRepository, UserRepository userRepository) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Question> getQuestions() {
        return Lists.newArrayList(questionRepository.findAll());
    }


    @Override
    public Question saveQuestion(Long userID, String questionContent) {
        Optional<User> userOptional = userRepository.findById(userID);
        if(userOptional.isPresent()) {
            Question question = new Question();
            question.setQuestionContent(questionContent);

            User user = userOptional.get();
            user.getQuestions().add(question);
            question.setUser(user);
            return questionRepository.save(question);
        }
        return null;
    }

    @Override
    public Answer saveAnswer(Long questionID, Long userID, String answerContent) {
        Optional<User> userOptional = userRepository.findById(userID);
        Optional<Question> byId = questionRepository.findById(questionID);

        if(userOptional.isPresent() && byId.isPresent()) {
            Question question = byId.get();
            User user = userOptional.get();

            Answer answer = new Answer();
            answer.setAnswerContent(answerContent);
            answer.setQuestion(question);
            user.getAnswers().add(answer);
            answer.setUser(user);
            question.getAnswers().add(answer);
            answer = answerRepository.save(answer);
//            questionRepository.save(question);
            return answer;
        }
        return null;
    }

}

package com.sample.forum.gRPC.server;

import com.sample.forum.gRPC.AnswerMessage;
import com.sample.forum.gRPC.QuestionGrpc;
import com.sample.forum.gRPC.QuestionMessage;
import com.sample.forum.gRPC.QuestionsRequest;
import com.sample.forum.gRPC.QuestionsResponse;
import com.sample.forum.gRPC.model.Answer;
import com.sample.forum.gRPC.model.Question;
import com.sample.forum.gRPC.service.IQuestionService;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class QuestionImpl extends QuestionGrpc.QuestionImplBase {
    private IQuestionService questionService;

    @Autowired
    public QuestionImpl(IQuestionService questionService) {
        this.questionService = questionService;
    }

    @Override
    public void getQuestions(QuestionsRequest request, StreamObserver<QuestionsResponse> responseObserver) {
        List<Question> questions = questionService.getQuestions();

        List<QuestionMessage> questionMessageList = new ArrayList<>();
        for(Question question : questions) {
            Set<Answer> answers = question.getAnswers();
            List<AnswerMessage> answerMessageList = new ArrayList<>();
            answers.forEach(answer -> {
                AnswerMessage message = AnswerMessage.newBuilder()
                        .setAnswerContent(answer.getAnswerContent())
                        .build();

                answerMessageList.add(message);
            });

            QuestionMessage questionMessage = QuestionMessage.newBuilder()
                    .setQuestionContent(question.getQuestionContent())
                    .addAllAnswer(answerMessageList)
                    .build();
            questionMessageList.add(questionMessage);
        }

        QuestionsResponse response = QuestionsResponse.newBuilder()
                .addAllQuestion(questionMessageList)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}

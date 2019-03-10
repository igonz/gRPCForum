package com.sample.forum.gRPC.server;

import com.sample.forum.gRPC.AnswerMessage;
import com.sample.forum.gRPC.AnswerResponse;
import com.sample.forum.gRPC.NewAnswerRequest;
import com.sample.forum.gRPC.NewQuestionRequest;
import com.sample.forum.gRPC.QuestionGrpc;
import com.sample.forum.gRPC.QuestionMessage;
import com.sample.forum.gRPC.QuestionsRequest;
import com.sample.forum.gRPC.QuestionsResponse;
import com.sample.forum.gRPC.UserMessage;
import com.sample.forum.gRPC.model.Answer;
import com.sample.forum.gRPC.model.Question;
import com.sample.forum.gRPC.service.IQuestionService;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
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
                        .setId(answer.getId())
                        .setUserMessage(UserMessage.newBuilder()
                                .setId(answer.getUser().getId())
                                .setUsername(answer.getUser().getUsername())
                                .build())
                        .setAnswerContent(answer.getAnswerContent())
                        .build();

                answerMessageList.add(message);
            });

            QuestionMessage questionMessage = QuestionMessage.newBuilder()
                    .setId(question.getId())
                    .setQuestionContent(question.getQuestionContent())
                    .setUserMessage(UserMessage.newBuilder()
                            .setId(question.getUser().getId())
                            .setUsername(question.getUser().getUsername())
                            .build())
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

    @Override
    public void postQuestion(NewQuestionRequest request, StreamObserver<QuestionsResponse> responseObserver) {
        String questionContent = request.getNewQuestion().getQuestionContent();
        Long userID = request.getNewQuestion().getUserID();
        if(questionContent == null || questionContent.trim().length() == 0) {
            StatusRuntimeException e = new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription("Question contents cant be empty"));
            responseObserver.onError(e);
            //responseObserver.onCompleted();
        } else {

            Question question = questionService.saveQuestion(userID, questionContent);

            QuestionsResponse response = QuestionsResponse.newBuilder()
                    .addQuestion(QuestionMessage.newBuilder().setQuestionContent(question.getQuestionContent()).build())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void postAnswer(NewAnswerRequest request, StreamObserver<AnswerResponse> responseObserver) {
        Answer answer = questionService.saveAnswer(
                request.getQuestionID(),
                request.getUserID(),
                request.getAnswer().getAnswerContent());

        if(answer == null) {
            responseObserver.onError(new StatusRuntimeException(Status.NOT_FOUND));
        } else {
            responseObserver.onNext(AnswerResponse.newBuilder()
                    .setAnswer(AnswerMessage.newBuilder()
                            .setId(answer.getId())
                            .setAnswerContent(answer.getAnswerContent())
                            .build())
                    .build());
            responseObserver.onCompleted();
        }
    }
}

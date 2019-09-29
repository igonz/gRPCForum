package com.sample.forum.gRPC.server;

import com.sample.forum.gRPC.*;
import com.sample.forum.gRPC.model.Answer;
import com.sample.forum.gRPC.model.Question;
import com.sample.forum.gRPC.service.IQuestionService;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

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

            SyntheticQueue.newQuestionPosted(response);
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
            AnswerResponse answerResponse = AnswerResponse.newBuilder()
                    .setAnswer(AnswerMessage.newBuilder()
                            .setId(answer.getId())
                            .setAnswerContent(answer.getAnswerContent())
                            .build())
                    .build();

            SyntheticQueue.newAnswerPosted(String.valueOf(request.getQuestionID()), answerResponse);
            responseObserver.onNext(answerResponse);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void watchQuestionForNewAnswer(WatchQuestionRequest request, StreamObserver<AnswerResponse> responseObserver) {
        SyntheticQueue.registerListenerForQuestion(request.getQuestionId() + "_" + request.getUserId(), responseObserver);
    }

    @Override
    public void unwatchQuestionForNewAnswer(UnWatchQuestionRequest request, StreamObserver<UnWatchQuestionResponse> responseObserver) {
        SyntheticQueue.removeListenerForQuestion(request.getQuestionId() + "_" + request.getUserId());
        responseObserver.onNext(UnWatchQuestionResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void watchForNewQuestion(WatchNewQuestionRequest request, StreamObserver<QuestionsResponse> responseObserver) {
        SyntheticQueue.registerListenerForNewQuestion(String.valueOf(request.getUserId()), responseObserver);
    }

    @Override
    public void unwatchForNewQuestion(UnWatchNewQuestionRequest request, StreamObserver<UnWatchNewQuestionResponse> responseObserver) {
        SyntheticQueue.removeListenersForNewQuestion(String.valueOf(request.getUserId()));
        responseObserver.onNext(UnWatchNewQuestionResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<RegisterUserAuditRequest> registerUserAudit(StreamObserver<RegisterUserAuditResponse> responseObserver) {
       return new StreamObserver<RegisterUserAuditRequest>() {
           @Override
           public void onNext(RegisterUserAuditRequest value) {
               System.out.println("User: [" + value.getUserId() + "] is executing action: [" + value.getUserAction().name()+ "]" + " on question: [" + value.getQuestionId() + "]");
           }

           @Override
           public void onError(Throwable t) {
               System.err.println("ERROR ON USER AUDIT: " + t.getMessage());
           }

           @Override
           public void onCompleted() {
               System.out.println("End of User Audit");
               responseObserver.onCompleted();
           }
       };
    }

    private Map<Long, List<StreamObserver<ChatResponse>>> chatBroadcast = new HashMap<>();
    @Override
    public StreamObserver<ChatRequest> chat(StreamObserver<ChatResponse> responseObserver) {
        return new StreamObserver<ChatRequest>() {
            @Override
            public void onNext(ChatRequest value) {
                List<StreamObserver<ChatResponse>> streamObservers = chatBroadcast.get(value.getChatId());
                if(streamObservers == null) {
                    streamObservers = new ArrayList<>();
                }
                if(!streamObservers.contains(responseObserver)) {
                    streamObservers.add(responseObserver);
                }

                System.out.println("[SERVER] New Message in Chat ID [" + value.getChatId() + "] from User ID [" + value.getUserId() + "] Message is: " + value.getChatMessage());
                for(StreamObserver<ChatResponse> streamObserver : streamObservers) {
                    streamObserver.onNext(
                            ChatResponse.newBuilder()
                                    .setChatId(value.getChatId())
                                    .setChatMessage(value.getChatMessage())
                                    .setUserId(value.getUserId())
                                    .build()
                    );
                }
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {

            }
        };
    }
}

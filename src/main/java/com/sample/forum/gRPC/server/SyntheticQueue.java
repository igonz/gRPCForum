package com.sample.forum.gRPC.server;

import com.sample.forum.gRPC.AnswerResponse;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;

import java.util.HashMap;
import java.util.Map;

public class SyntheticQueue {
    private static Map<String, StreamObserver<AnswerResponse>> listenersForAnswers = new HashMap<>();

    public static void registerListenerForQuestion(String id, StreamObserver<AnswerResponse> streamObserver) {
        listenersForAnswers.put(id, streamObserver);
    }

    public static void removeListenerForQuestion(String id) {
        System.out.println("REMOVING OBSERVER!");
        StreamObserver<AnswerResponse> remove = listenersForAnswers.remove(id);
        if(remove != null) {
            remove.onCompleted();
        }
    }

    public static void newAnswerPosted(String questionId, AnswerResponse answerResponse) {
        Map<String, StreamObserver<AnswerResponse>> copy = new HashMap<>(listenersForAnswers);

        for(Map.Entry<String, StreamObserver<AnswerResponse>> entry : copy.entrySet()) {
            String key = entry.getKey();
            StreamObserver<AnswerResponse> observer = entry.getValue();

            String[] tokens = key.split("_");
            String mappedQuestionId = tokens[0];
            String userId = tokens[1];


            if(((ServerCallStreamObserver<AnswerResponse>)observer).isCancelled()) {
                removeListenerForQuestion(mappedQuestionId + "_" + userId);
            } else {
                observer.onNext(answerResponse);
            }
        }
    }
}

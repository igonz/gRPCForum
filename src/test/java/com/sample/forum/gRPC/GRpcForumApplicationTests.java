package com.sample.forum.gRPC;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GRpcForumApplicationTests {

    private ManagedChannel managedChannel;

    @Autowired
    private Environment env;

    @Before
    public void setupClientTest() {
//        Integer port = env.getProperty("forum.server.port", Integer.class, 8080);
        try {
            managedChannel = NettyChannelBuilder.forAddress("localhost",443)
                    .sslContext(GrpcSslContexts.forClient().trustManager(new ClassPathResource("localhost.crt").getFile()).build())
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        managedChannel = ManagedChannelBuilder.forAddress("localhost", 443)
//                .usePlaintext()
//                .build();
    }

    @Test
    public void contextLoads() {
    }

//	@Test
//	public void questionGrpcServiceTest() {
//		QuestionGrpc.QuestionBlockingStub blockingStub = QuestionGrpc.newBlockingStub(managedChannel);
//		QuestionsResponse questions = blockingStub.getQuestions(QuestionsRequest.newBuilder().build());
//
//		Assert.assertEquals("Whats a good way to implement a GRPC Microservice?", questions.getQuestionList().get(0).getQuestionContent());
//	}

//	@Test
//	public void postQuestionWithError() {
//		QuestionGrpc.QuestionBlockingStub blockingStub = QuestionGrpc.newBlockingStub(managedChannel);
//		try{
//			QuestionsResponse response = blockingStub.postQuestion(NewQuestionRequest.newBuilder().setNewQuestion(
//					NewQuestionMessage.newBuilder().setQuestionContent("   ").build()
//			).build());
//
//		} catch (StatusRuntimeException e) {
//			Assert.assertEquals("Question contents cant be empty", e.getStatus().getDescription());
//		}
//	}
//
	@Test
	public void postQuestion() {
		QuestionGrpc.QuestionBlockingStub blockingStub = QuestionGrpc.newBlockingStub(managedChannel);
		try{
			QuestionsResponse response = blockingStub.postQuestion(NewQuestionRequest.newBuilder()
					.setNewQuestion(NewQuestionMessage.newBuilder()
							.setQuestionContent("Is GRPC great?")
							.setUserID(1L)
							.build())
					.build());
			Assert.assertEquals(1, response.getQuestionList().size());
		} catch (StatusRuntimeException e) {

		}
	}
//
//	@Test
//	public void postAnswerToQuestion() {
//		QuestionGrpc.QuestionBlockingStub blockingStub = QuestionGrpc.newBlockingStub(managedChannel);
//		AnswerResponse response = blockingStub.postAnswer(NewAnswerRequest.newBuilder()
//				.setAnswer(AnswerMessage.newBuilder().setAnswerContent("This is from Test").build())
//				.setQuestionID(1L)
//				.setUserID(2L)
//				.build());
//	}

//	@Test
//	public void watchQuestionForNewAnswer() {
//		QuestionGrpc.QuestionStub stub = QuestionGrpc.newStub(managedChannel);
//		stub.watchQuestionForNewAnswer(
//				WatchQuestionRequest.newBuilder()
//						.setQuestionId(1L)
//						.build(), new StreamObserver<AnswerResponse>() {
//					@Override
//					public void onNext(AnswerResponse value) {
//						System.out.println("ANSWER RECEIVED " + value.getAnswer().getAnswerContent());
//					}
//
//					@Override
//					public void onError(Throwable t) {
//
//					}
//
//					@Override
//					public void onCompleted() {
//
//					}
//				});
//
//		QuestionGrpc.QuestionBlockingStub blockingStub = QuestionGrpc.newBlockingStub(managedChannel);
//		AnswerResponse response = blockingStub.postAnswer(NewAnswerRequest.newBuilder()
//				.setAnswer(AnswerMessage.newBuilder().setAnswerContent("Watched Test").build())
//				.setQuestionID(1L)
//				.setUserID(2L)
//				.build());
//
//		blockingStub = QuestionGrpc.newBlockingStub(managedChannel);
//		blockingStub.unwatchQuestionForNewAnswer(UnWatchQuestionRequest.newBuilder().setQuestionId(1L).build());
//
//		try {
//			Thread.sleep(5000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//
//		blockingStub = QuestionGrpc.newBlockingStub(managedChannel);
//		response = blockingStub.postAnswer(NewAnswerRequest.newBuilder()
//				.setAnswer(AnswerMessage.newBuilder().setAnswerContent("Will not Broadcast Watched Test").build())
//				.setQuestionID(1L)
//				.setUserID(2L)
//				.build());
//
//		try {
//			Thread.sleep(5000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//
//	}

//	@Test
//	public void watchForNewQuestionTest() {
//		QuestionGrpc.QuestionStub stub = QuestionGrpc.newStub(managedChannel);
//		stub.watchForNewQuestion(WatchNewQuestionRequest.newBuilder().setUserId(1).build(),
//				new StreamObserver<QuestionsResponse>() {
//					@Override
//					public void onNext(QuestionsResponse value) {
//						System.out.println("NEW QUESTION RECEIVED " + value.getQuestionList().get(0).getQuestionContent());
//					}
//
//					@Override
//					public void onError(Throwable t) {
//
//					}
//
//					@Override
//					public void onCompleted() {
//
//					}
//				});
//
//		QuestionGrpc.QuestionBlockingStub blockingStub = QuestionGrpc.newBlockingStub(managedChannel);
//		QuestionsResponse response = blockingStub.postQuestion(NewQuestionRequest.newBuilder()
//				.setNewQuestion(NewQuestionMessage.newBuilder()
//						.setQuestionContent("New Watch Question Test?")
//						.setUserID(1L)
//						.build())
//				.build());
//
//		try {
//			Thread.sleep(5000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//	}

//    @Test
//    public void registerUserActionTest() {
//        QuestionGrpc.QuestionStub stub = QuestionGrpc.newStub(managedChannel);
//        StreamObserver<RegisterUserAuditRequest> requestObserver = stub.registerUserAudit(new StreamObserver<RegisterUserAuditResponse>() {
//            @Override
//            public void onNext(RegisterUserAuditResponse value) {
//
//            }
//
//            @Override
//            public void onError(Throwable t) {
//
//            }
//
//            @Override
//            public void onCompleted() {
//                System.out.println("Closed connection with Server");
//            }
//        });
//
//        try {
//            requestObserver.onNext(
//                    RegisterUserAuditRequest
//                            .newBuilder()
//                            .setQuestionId(1)
//                            .setUserId(1)
//                            .setUserAction(UserAction.VIEWING_QUESTION)
//                            .build()
//            );
//        } catch (Exception e) {
//            requestObserver.onError(e);
//        }
//
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        requestObserver.onCompleted();
//    }

//    @Test
//    public void chatTest() {
//        QuestionGrpc.QuestionStub stub = QuestionGrpc.newStub(managedChannel);
//        StreamObserver<ChatRequest> streamObserver = stub.chat(new StreamObserver<ChatResponse>() {
//            @Override
//            public void onNext(ChatResponse value) {
//                System.out.println("[CLIENT] Received Message for Chat ID [" + value.getChatId() + "] from User ID [" + value.getUserId() + "] Message is: " + value.getChatMessage());
//            }
//
//            @Override
//            public void onError(Throwable t) {
//
//            }
//
//            @Override
//            public void onCompleted() {
//
//            }
//        });
//
//        streamObserver.onNext(ChatRequest.newBuilder()
//                .setUserId(1)
//                .setChatId(1)
//                .setChatMessage("What up?")
//                .build()
//        );
//
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        streamObserver.onNext(ChatRequest.newBuilder()
//                .setUserId(2)
//                .setChatId(1)
//                .setChatMessage("Nothing Much. You?")
//                .build()
//        );
//
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        streamObserver.onNext(ChatRequest.newBuilder()
//                .setUserId(1)
//                .setChatId(1)
//                .setChatMessage("Chillin")
//                .build()
//        );
//
//        streamObserver.onCompleted();
//    }

    @After
    public void endOfTest() throws InterruptedException {
        managedChannel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
}


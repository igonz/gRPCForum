package com.sample.forum.gRPC;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GRpcForumApplicationTests {

	private ManagedChannel managedChannel;

	@Autowired
	private Environment env;

	@Before
	public void setupClientTest() {
		Integer port = env.getProperty("forum.server.port", Integer.class, 8080);
		managedChannel = ManagedChannelBuilder.forAddress("localhost", port)
			.usePlaintext()
			.build();
	}

	@Test
	public void contextLoads() {
	}

	@Test
	public void questionGrpcServiceTest() {
		QuestionGrpc.QuestionBlockingStub blockingStub = QuestionGrpc.newBlockingStub(managedChannel);
		QuestionsResponse questions = blockingStub.getQuestions(QuestionsRequest.newBuilder().build());

		Assert.assertEquals("Whats a good way to implement a GRPC Microservice?", questions.getQuestionList().get(0).getQuestionContent());
	}

	@Test
	public void postQuestionWithError() {
		QuestionGrpc.QuestionBlockingStub blockingStub = QuestionGrpc.newBlockingStub(managedChannel);
		try{
			QuestionsResponse response = blockingStub.postQuestion(NewQuestionRequest.newBuilder().setNewQuestion(
					NewQuestionMessage.newBuilder().setQuestionContent("   ").build()
			).build());

		} catch (StatusRuntimeException e) {
			Assert.assertEquals("Question contents cant be empty", e.getStatus().getDescription());
		}
	}

	@Test
	public void postQuestion() {
		QuestionGrpc.QuestionBlockingStub blockingStub = QuestionGrpc.newBlockingStub(managedChannel);
		try{
			QuestionsResponse response = blockingStub.postQuestion(NewQuestionRequest.newBuilder()
					.setNewQuestion(NewQuestionMessage.newBuilder()
							.setQuestionContent("New Question from Test?")
							.setUserID(1L)
							.build())
					.build());
			Assert.assertEquals(1, response.getQuestionList().size());
		} catch (StatusRuntimeException e) {

		}
	}

	@Test
	public void postAnswerToQuestion() {
		QuestionGrpc.QuestionBlockingStub blockingStub = QuestionGrpc.newBlockingStub(managedChannel);
		AnswerResponse response = blockingStub.postAnswer(NewAnswerRequest.newBuilder()
				.setAnswer(AnswerMessage.newBuilder().setAnswerContent("This is from Test").build())
				.setQuestionID(1L)
				.setUserID(2L)
				.build());
		System.out.println("TEST");
	}

	@After
	public void endOfTest() throws InterruptedException {
		managedChannel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
	}
}


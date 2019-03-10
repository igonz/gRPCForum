package com.sample.forum.gRPC.repository;

import com.sample.forum.gRPC.model.Answer;
import org.springframework.data.repository.CrudRepository;

public interface AnswerRepository extends CrudRepository<Answer, Long> {
}

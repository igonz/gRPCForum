package com.sample.forum.gRPC.repository;

import com.sample.forum.gRPC.model.Question;
import org.springframework.data.repository.CrudRepository;

public interface QuestionRepository extends CrudRepository<Question, Long> {
}

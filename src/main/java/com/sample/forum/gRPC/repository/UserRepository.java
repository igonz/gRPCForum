package com.sample.forum.gRPC.repository;

import com.sample.forum.gRPC.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}

package com.lld.chat.privatechat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.lld.chat.privatechat.model.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    List<User> findByUsernameContainingIgnoreCase(String username);

}

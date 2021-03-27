package com.falczy.java.kubernetes.persistence;

import com.falczy.java.kubernetes.domain.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

}


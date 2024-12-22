package com.splitwise.splitwisespring.repository;
import com.splitwise.splitwisespring.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    /**
     * Finds user based on username
     * @param username
     * @return
     */
 User findByUserName(String username);
}

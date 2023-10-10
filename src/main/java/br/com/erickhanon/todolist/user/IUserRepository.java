package br.com.erickhanon.todolist.user;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface IUserRepository extends JpaRepository<UserModel, UUID>{
    List<UserModel> findByEmail(String email);
    
}

package br.com.erickhanon.todolist.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    
    @Autowired
    private IUserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity create(@RequestBody UserModel userModel) {
       var user = this.userRepository.findByEmail(userModel.getEmail());
         if (user.size() > 0) {
              return ResponseEntity.badRequest().body("Email já cadastrado!");
            }
        var userCreated = this.userRepository.save(userModel);
        return ResponseEntity.ok(userCreated);
    }

}

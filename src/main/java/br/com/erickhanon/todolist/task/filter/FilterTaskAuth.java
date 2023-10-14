package br.com.erickhanon.todolist.task.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.erickhanon.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
                var servletPath = request.getServletPath();
                if (servletPath.equals("/users/create")) {
                    filterChain.doFilter(request, response);
                    return;
                }
        var token = request.getHeader("Authorization");
        var encodedToken = token.substring("Basic".length()).trim();
        byte[] decodedToken = Base64.getDecoder().decode(encodedToken);
        var decodedTokenString = new String(decodedToken);
        String[] credentials = decodedTokenString.split(":");
        String email = credentials[0];
        String password = credentials[1];
        var user = this.userRepository.findByEmail(email);
        if (user.size() == 0) {
            System.out.println("Usuário não encontrado");
            response.sendError(401, "Usuário não encontrado");
            return;
        } else {
            var userFound = user.get(0);
            var passwordMatch = BCrypt.verifyer().verify(password.toCharArray(), userFound.getPassword());
            if (passwordMatch.verified) {
                request.setAttribute("userId",userFound.getId());
                filterChain.doFilter(request, response);
                return;
            } else {
                response.sendError(401, "Senha incorreta");
                return;
            }
        }

    }

}

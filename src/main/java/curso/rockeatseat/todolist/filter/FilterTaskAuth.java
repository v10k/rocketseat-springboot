package curso.rockeatseat.todolist.filter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import curso.rockeatseat.todolist.user.IUserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

//        Pegar a autenticação (usuário e senha)
        var authorization = request.getHeader("Authorization").substring("Basic".length()).trim();
        byte[] authDecode = Base64.getDecoder().decode(authorization);
        String authString = new String(authDecode);
        String[] credentials = authString.split(":");

//        Validar usuário
        var user = this.userRepository.findByUsername(credentials[0]);
        if(user == null) {
            response.sendError(401, "Usuário sem autorização");
        } else {
            var passwordVerify = BCrypt.verifyer().verify(credentials[1].toCharArray(), user.getPassword());
            if (passwordVerify.verified) {
                filterChain.doFilter(request, response);
            } else {
                response.sendError(401);
            }
        }
//        Validar senha

    }

}

package pl.lingaro.od.workshop.security.config;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import pl.lingaro.od.workshop.security.data.User;

import javax.persistence.EntityManager;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.logging.Logger;

@Service
public class JPAAuthenticationProvider implements AuthenticationProvider {
    private static final Logger log = Logger.getLogger(JPAAuthenticationProvider.class.getName());
    /**
     * MessageDigest is not thread safe - ensure one instance per thread
     */
    private final static ThreadLocal<MessageDigest> MD5 = ThreadLocal.withInitial(JPAAuthenticationProvider::getMD5);
    private final EntityManager em;

    public JPAAuthenticationProvider(EntityManager entityManager) {
        this.em = entityManager;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();
        log.info(username);
        String jpaql = "select u from User u where " +
                "login=:username AND " +
                "passwordHash=:password";
        log.info(jpaql);
        final List<User> resultList = em.createQuery(jpaql, User.class)
                .setParameter("username", username)
                .setParameter("password", md5sum(password))
                .getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        User user = resultList.get(0);
        return new UsernamePasswordAuthenticationToken(
                user.getLogin(),
                user.getPasswordHash(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    /**
     * @return base64(md5sum(argument))
     */
    private static String md5sum(String password) {
        return Base64.getEncoder().encodeToString(
                MD5.get().digest(
                        Optional.ofNullable(password)
                                .orElse("")
                                .getBytes(StandardCharsets.UTF_8)
                )
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class
                .isAssignableFrom(authentication));
    }

    private static MessageDigest getMD5() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * User/password generation tool.
     * 1) Execute with parameters: login pass
     * 2) Add the generated line to src/main/resources/data.sql
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException("Missing arguments: [login] [pass]");
        }
        final String login = args[1];
        final String pass = md5sum((args[0]));
        String insert = String.format("INSERT INTO User(login, password_hash) VALUES ('%s','%s');",
                login,
                pass);
        System.out.println(insert);
    }
}

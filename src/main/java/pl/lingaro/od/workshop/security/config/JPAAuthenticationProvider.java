package pl.lingaro.od.workshop.security.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class JPAAuthenticationProvider {

    /**
     * User/password generation tool.
     * 1) Execute with parameters: login pass
     * 2) Add the generated line to src/main/resources/data.sql
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException("Missing arguments: [login] [pass]");
        }
        final String login = args[0];
        final String pass = new BCryptPasswordEncoder().encode(args[1]);
        String insert = String.format("INSERT INTO User(login, password_hash) VALUES ('%s','%s');",
                login,
                pass);
        System.out.println(insert);
    }
}

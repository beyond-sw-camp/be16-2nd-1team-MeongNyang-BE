package com.beyond.meongnyang.common.loader;

import com.beyond.meongnyang.user.entity.Role;
import com.beyond.meongnyang.user.entity.User;
import com.beyond.meongnyang.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component

public class InitialDataLoader implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.name}")
    private String name;

    @Value("${admin.nickname}")
    private String nickname;

    @Value("${admin.password}")
    private String password;

    @Value("${admin.email}")
    private String email;

    @Override
    public void run(String... args) throws Exception {
        if(this.userRepository.findByEmail(email).isPresent()) {
            return;
        }
        User admin = User.builder()
                .email(email)
                .password(this.passwordEncoder.encode(password))
                .name(name)
                .nickname(nickname)
                .role(Role.ADMIN)
                .point(Integer.MAX_VALUE)
                .delYn("N")
                .build();
        this.userRepository.save(admin);
    }
}

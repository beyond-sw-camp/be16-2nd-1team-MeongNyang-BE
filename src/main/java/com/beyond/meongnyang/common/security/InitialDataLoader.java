package com.beyond.meongnyang.common.security;

import com.beyond.meongnyang.user.domain.Role;
import com.beyond.meongnyang.user.domain.User;
import com.beyond.meongnyang.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component

public class InitialDataLoader implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if(this.userRepository.findByEmail("wooyoungking@gmail.com").isPresent()) {
            return;
        }
        User admin = User.builder()
                .email("wooyoungking0228@gmail.com")
                .password(this.passwordEncoder.encode("우영킹왕짱맨이다이거야"))
                .name("이우영")
                .nickname("우영킹왕짱")
                .phone("01055598067")
                .role(Role.ADMIN)
                .point(Integer.MAX_VALUE)
                .build();
        this.userRepository.save(admin);
    }
}

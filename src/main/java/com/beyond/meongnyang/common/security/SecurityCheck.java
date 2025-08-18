package com.beyond.meongnyang.common.security;

import com.beyond.meongnyang.common.CommonService;
import com.beyond.meongnyang.common.customexception.BlockDeniedException;
import com.beyond.meongnyang.user.entity.Role;
import com.beyond.meongnyang.user.entity.User;
import com.beyond.meongnyang.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("securityCheck")
@RequiredArgsConstructor
public class SecurityCheck {
    private final CommonService commonService;
    private final UserRepository userRepository;
    public boolean checkUserAccess() {
        User user = commonService.getCurrentUser();

        if (user.getRole() == Role.TEMPORARY_BLOCK || user.getRole() == Role.PERMANENT_BLOCK) {
            throw new BlockDeniedException("차단된 사용자 입니다.");
        }
        return true;
    }
}

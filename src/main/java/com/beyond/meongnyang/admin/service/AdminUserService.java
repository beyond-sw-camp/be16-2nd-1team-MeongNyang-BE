package com.beyond.meongnyang.admin.service;

import com.beyond.meongnyang.admin.dto.AdminUserUpdateReq;
import com.beyond.meongnyang.admin.dto.UserStatisticsReq;
import com.beyond.meongnyang.admin.dto.UserStatisticsRes;
import com.beyond.meongnyang.user.entity.User;
import com.beyond.meongnyang.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminUserService {
    private final UserRepository userRepository;

    // 회원가입 승인
    public Long approveUser(Long id) {
            // 유저 id값을 받아와서
            User user = userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("없는 회원입니다."));
            // 해당 유저 회원가입 승인
            user.approve();
            // 승인여부 db 저장
            userRepository.save(user);
        return user.getId();
    }

    // 회원정보 수정
    public Long updateUser(Long id, AdminUserUpdateReq req) {
        User user = userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("없는 회원입니다."));
        //엔티티 수정
        user.updateUser(req);
        return user.getId();
    }

    // 회원정보 삭제
    public Long deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("없는 회원입니다."));
        //delYn = Y로 변경
        user.softDelete();
        return user.getId();
    }

//    // 회원가입 통계
//    @Transactional(readOnly = true)
//    public List<UserStatisticsRes> findUserSignupStatistics(UserStatisticsReq req) {
//        switch (req.getGrain().toLowerCase()) {
//            case "daily":
//                return userRepository.countSignupsByDay(req.getFrom(), req.getTo());
//            case "weekly":
//                return userRepository.countSignupsByWeek(req.getFrom(), req.getTo());
//            case "monthly":
//                return userRepository.countSignupsByMonth(req.getFrom(), req.getTo());
//            default:
//                throw new IllegalArgumentException("지원하지 않는 grain 값입니다. (daily | weekly | monthly)");
//        }
//    }


}

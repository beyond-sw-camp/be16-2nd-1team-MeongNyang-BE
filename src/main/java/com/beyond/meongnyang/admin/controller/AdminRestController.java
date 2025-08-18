package com.beyond.meongnyang.admin.controller;

import com.beyond.meongnyang.admin.dto.AdminUserUpdateReq;
import com.beyond.meongnyang.admin.dto.ReportResultReq;
import com.beyond.meongnyang.admin.dto.UserStatisticsReq;
import com.beyond.meongnyang.admin.dto.UserStatisticsRes;
import com.beyond.meongnyang.admin.service.AdminUserService;
import com.beyond.meongnyang.admin.service.ReportService;
import com.beyond.meongnyang.common.dto.CommonRes;
import com.beyond.meongnyang.market.service.MarketService;
import com.beyond.meongnyang.user.dto.UserDetailRes;
import com.beyond.meongnyang.user.dto.UserListRes;
import com.beyond.meongnyang.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminRestController {
    private final ReportService reportService;
    private final UserService userService;
    private final AdminUserService adminUserService;
    private final MarketService marketService;

    // 회원 관련 관리자 기능
    // 회원가입 승인
    @PatchMapping("/user/{id}/approve")
    public ResponseEntity<?> approveUser(@PathVariable Long id) {
        Long userId = adminUserService.approveUser(id);
        return new ResponseEntity<>(CommonRes.ofSuccess(userId, HttpStatus.OK.value(), "회원가입 승인 완료"), HttpStatus.OK);
    }

    // 전체회원목록 조회
    @Transactional(readOnly = true)
    @GetMapping("/users")
    public ResponseEntity<?> findAllUser(Pageable pageable) {
        Page<UserListRes> userList = userService.findAllUser(pageable);
        return new ResponseEntity<>(CommonRes.ofSuccess(userList, HttpStatus.OK.value(), "전체회원목록 조회 완료"), HttpStatus.OK);
    }
    // 회원정보 수정
    @PatchMapping("/user/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id,
                                        @RequestBody AdminUserUpdateReq req) {
        Long userId = adminUserService.updateUser(id, req);
        return new ResponseEntity<>(CommonRes.ofSuccess(userId, HttpStatus.OK.value(), "회원정보수정 완료"), HttpStatus.OK);
    }
    // 회원정보 삭제
    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Long userId = adminUserService.deleteUser(id);
        return new ResponseEntity<>(CommonRes.ofSuccess(userId, HttpStatus.OK.value(), "회원정보삭제 완료"), HttpStatus.OK);
    }

    // 회원가입 통계
    @GetMapping("/users/statistics")
    public ResponseEntity<?> findUserSignupStatistics(UserStatisticsReq req) {
        List<UserStatisticsRes> res = adminUserService.findUserSignupStatistics(req);
        return new ResponseEntity<>(CommonRes.ofSuccess(res, HttpStatus.OK.value(), "회원가입통계 조회 완료"), HttpStatus.OK);
    }


    // 탈퇴하지 않은 회원목록 조회
    @GetMapping("/users/list")
    public ResponseEntity<?> findAll() {
        List<UserListRes> userList = this.userService.findAll();
        return new ResponseEntity<>(CommonRes.ofSuccess(userList, HttpStatus.OK.value(), "회원 목록 조회 완료"), HttpStatus.OK);
    }

    // 회원 상세 조회
    @GetMapping("/users/detail/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        UserDetailRes user = this.userService.findById(id);
        return new ResponseEntity<>(CommonRes.ofSuccess(user, HttpStatus.OK.value(), "회원 상세 조회 완료"), HttpStatus.OK);
    }
    // 거래글 관련 관리자 기능
    // 전체거래글 조회
    @GetMapping("markets/posts")
    public ResponseEntity<?> marketPostList(@PageableDefault(value = 9, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ResponseEntity<>(
                CommonRes.ofSuccess(
                        marketService.marketPostList(pageable),
                        HttpStatus.OK.value(),
                        "거래글 목록 조회에 성공했습니다."
                ), HttpStatus.OK
        );
    }
    // 거래글 삭제
    @DeleteMapping("markets/{id}")
    public ResponseEntity<?> deleteMarketPost(@PathVariable("id") Long id) {
        marketService.deleteMarketPost(id);
        return new ResponseEntity<>(
                CommonRes.ofSuccess(
                        id,
                        HttpStatus.OK.value(),
                        "거래글을 삭제했습니다."
                ), HttpStatus.OK
        );
    }

    // 일기 관련 관리자 기능
    // 채팅 관련 관리자 기능
    // 신고 처리 기능
    // 모든 신고 조회
    @GetMapping("/reports")
    public ResponseEntity<?> findAllReports(@PageableDefault(size=10, direction = Sort.Direction.DESC)Pageable pageable){
        return new ResponseEntity<>(CommonRes.ofSuccess(reportService.findAll(pageable), HttpStatus.OK.value(), "회원 상세 조회 완료"), HttpStatus.OK);
    }

    // 신고 타입별 조회(검색)
    // 신고 상세 내용
    @GetMapping("/reports/{id}")
    public ResponseEntity<?> findReportsById(@PathVariable("id") Long id){
        return new ResponseEntity<>(CommonRes.ofSuccess(reportService.findById(id), HttpStatus.OK.value(), "회원 상세 조회 완료"), HttpStatus.OK);
    }

    // 신고처리
    // ToDo : SSE를 이용한 실시간 차단 로직 구현 필요
    @PostMapping("/reports/{id}")
    public ResponseEntity<?> processReports(@PathVariable("id") Long id, @RequestBody ReportResultReq reportResultReq){
        return new ResponseEntity<>(CommonRes.ofSuccess("", HttpStatus.OK.value(), "회원 상세 조회 완료"), HttpStatus.OK);
    }
}

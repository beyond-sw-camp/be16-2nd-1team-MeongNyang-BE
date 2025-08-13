package com.beyond.meongnyang.admin.controller;

import com.beyond.meongnyang.admin.dto.ReportResultReq;
import com.beyond.meongnyang.admin.service.ReportService;
import com.beyond.meongnyang.common.dto.CommonRes;
import com.beyond.meongnyang.user.dto.UserDetailRes;
import com.beyond.meongnyang.user.dto.UserListRes;
import com.beyond.meongnyang.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    /** 회원 관련 관리자 기능 **/
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

    // 서비스 이용 차단 해제
    @PostMapping("/users/bans/{id}")
    public ResponseEntity<?> unbanUser(@PathVariable Long id) {
        userService.unbanByAdmin(id);
        return new ResponseEntity<>(CommonRes.ofSuccess("해당 이용자에 대한 서비스 이용 차단이 해제되었습니다.", HttpStatus.OK.value(), "차단 해제 완료"), HttpStatus.OK);
    }
    /** 일기 관련 관리자 기능 **/
    /** 채팅 관련 관리자 기능 **/
    /** 신고 처리 기능 **/
    // 모든 신고 조회
    @GetMapping("/reports")
    public ResponseEntity<?> findAllReports(@PageableDefault(size=10, direction = Sort.Direction.DESC)Pageable pageable){
        return new ResponseEntity<>(CommonRes.ofSuccess(reportService.findAll(pageable), HttpStatus.OK.value(), "신고 목록 조회 완료"), HttpStatus.OK);
    }

    // 신고 상세 내용
    @GetMapping("/reports/{id}")
    public ResponseEntity<?> findReportsById(@PathVariable("id") Long id){
        return new ResponseEntity<>(CommonRes.ofSuccess(reportService.findById(id), HttpStatus.OK.value(), "신고 상세 조회 완료"), HttpStatus.OK);
    }

    // 신고 처리
    @PostMapping("/reports/{id}")
    public ResponseEntity<?> processReports(@PathVariable("id") Long id, @RequestBody ReportResultReq reportResultReq){
        reportService.processReport(id,reportResultReq);
        return new ResponseEntity<>(CommonRes.ofSuccess("신고 처리가 완료되었습니다.", HttpStatus.OK.value(), "신고 처리 완료"), HttpStatus.OK);
    }
}

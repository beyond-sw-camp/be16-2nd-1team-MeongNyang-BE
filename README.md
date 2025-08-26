## 🐾 멍멍냥냥
<p align = "center">
    <img width="128" height="128" alt="image" src="https://github.com/user-attachments/assets/ec18fb22-4a1a-439c-92b6-ac0dc3cd1ac7" />
</p>

## 👥 팀원 소개
<table>
    <tr>
    <td align="center"> 윤수오(팀장)</td>
    <td align="center"> 정지완(리뷰어)</td>
    <td align="center"> 김지현(팀원)</td>
    <td align="center"> 이우영(팀원)</td>
  </tr>
  <tr>
    <td align="center"><a href="https://github.com/SuOhYoon" target="_blank"><img src="https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=github&logoColor=white"/></a>
    </td>
    <td align="center"><a href="https://github.com/FOJF" target="_blank"><img src="https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=github&logoColor=white"/></a>
    </td>
    <td align="center"><a href="https://github.com/userkimjihyeon" target="_blank"><img src="https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=github&logoColor=white"/></a> 
    </td>
    <td align="center"><a href="https://github.com/ggj0228" target="_blank"><img src="https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=github&logoColor=white"/></a>
    </td>
  </tr>
</table>
<br>

## 기술 스택
### 🗄️ Database
![MariaDB](https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=mariadb&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)

### 💻 Backend
![Java 17](https://img.shields.io/badge/Java%2017-007396?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![WebSocket](https://img.shields.io/badge/WebSocket-000000?style=for-the-badge&logo=websocket&logoColor=white)
![STOMP](https://img.shields.io/badge/STOMP-000000?style=for-the-badge)

### 🎨 Frontend
![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black)
![Vue.js](https://img.shields.io/badge/Vue.js-4FC08D?style=for-the-badge&logo=vue.js&logoColor=white)
![Vuetify](https://img.shields.io/badge/Vuetify-1867C0?style=for-the-badge&logo=vuetify&logoColor=white)
![Axios](https://img.shields.io/badge/Axios-5A29E4?style=for-the-badge&logo=axios&logoColor=white)

### 🧑‍💻 Tools
![Notion](https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=notion&logoColor=white)
![Git](https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white)
![GitHub](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white)
![Figma](https://img.shields.io/badge/Figma-F24E1E?style=for-the-badge&logo=figma&logoColor=white)

## 주요 기능
- 🏠 **홈**: 커뮤니티 소개 및 메인 페이지
- 🐕 **반려동물 관리**: 반려동물 등록, 수정, 조회
- 📖 **다이어리**: 반려동물과의 일상 기록
- 🛒 **마켓**: 반려동물 관련 상품 거래
- 💬 **채팅**: 실시간 채팅방 기능
- 👤 **사용자 관리**: 회원가입, 로그인, 프로필 관리
- 🛡️ **관리자**: 관리자 전용 기능


## 팀원별 주요 사용 기술 소개
<details>
    <summary>윤수오</summary>
    <details><summary>🔐 SSE를 이용한 사용자 차단/차단해제</summary>
        
### 서비스 개요
사용자로부터 차단 요청이 들어오면, 서버는 DB에서 대상 사용자 ID를 조회한 뒤 **enum 상태값**을 변경하여 차단 상태로 전환합니다.  
이후 차단/차단해제 이벤트를 **Redis Pub/Sub**과 **SSE(Server-Sent Events)**를 통해 프론트엔드로 전송합니다.  
프론트엔드는 메시지를 수신하면 즉시 **Access Token을 재발급**받아 갱신된 권한을 반영하고, 차단된 사용자는 서비스 접근이 제한됩니다.

### 주요 기술 스택
- **Spring Boot**: REST API 및 서비스 로직 구현
- **JPA & Enum**: 사용자 상태 관리 (`기간차단`, `영구차단` 등)
- **SSE (Server-Sent Events), Redis Pub/Sub**: 실시간 권한 변경 알림 전송
- **JWT**: Access Token & Refresh Token 기반 인증 관리

### 특징
- **실시간 반영**: SSE를 통한 지연 없는 권한 동기화
- **무상태 인증 구조**: 서버에 세션을 저장하지 않고, Refresh Token으로 Access Token 재발급
- **확장성**: 다수의 프론트엔드 클라이언트에 안정적인 이벤트 전송 가능
- **보안성**: 차단 즉시 토큰 갱신을 강제하여 이전 권한으로 서비스 접근 불가
    </details>
</details>
<details>
    <summary>정지완</summary>
</details>    
<details>
    <summary>김지현</summary>
</details> 
<details>
    <summary>이우영</summary>
</details>   


## 화면 설계서
<div style="font-size: 1.5em; font-weight: bold; margin-top: 20px;">
  <a href='https://www.figma.com/design/9EuV7bZ8gteSS0VeWFtBZj/%EB%A9%94%EC%9D%B8-%ED%8E%98%EC%9D%B4%EC%A7%80?node-id=274-579&t=P1AzF8eUgYE37t51-0' style="text-decoration: none; color: inherit;">
    화면 설계서
  </a>
</div>

## 요구사항 정의 및 명세서(SRS)
<div style="font-size: 1.5em; font-weight: bold; margin-top: 20px;">
  <a href='https://docs.google.com/spreadsheets/d/1_HHbkM-qIh_VRlckDM2gnEV2z01KgckhMvfeGRZ-UAQ/edit?gid=809966690#gid=809966690' style="text-decoration: none; color: inherit;">
    요구사항 명세서
  </a>
</div>

## WBS
<div style="font-size: 1.5em; font-weight: bold; margin-top: 20px;">
  <a href='https://docs.google.com/spreadsheets/d/1_HHbkM-qIh_VRlckDM2gnEV2z01KgckhMvfeGRZ-UAQ/edit?gid=382613662#gid=382613662' style="text-decoration: none; color: inherit;">
    WBS
  </a>
</div>


## ERD
<div style="font-size: 1.5em; font-weight: bold; margin-top: 20px;">
  <a href='https://www.erdcloud.com/d/fRviLvokK3rgCy2iS' style="text-decoration: none; color: inherit;">
    ERD
  </a>
</div>

## 테스트 결과서
<details>
  <summary>일기</summary>
  <details><summary>홈 화면(전체 일기 목록)</summary>
    <img width="1440" height="900" alt="image" src="https://github.com/user-attachments/assets/20271e9c-20bf-414e-b01a-ad45ef1204eb" />
  </details>

  <details><summary>대시보드</summary>
    <img width="1440" height="900" alt="image" src="https://github.com/user-attachments/assets/d95b9355-75be-4f31-84d0-fa3ba9e32ac6" />
  </details>

  <details><summary>내 일기 목록</summary>
      <img width="1440" height="900" alt="image" src="https://github.com/user-attachments/assets/e9288040-431e-451c-acfe-479b9dd2bb3c" />
  </details>

  <details><summary>내 일기 작성</summary>
    <img width="1440" height="900" alt="image" src="https://github.com/user-attachments/assets/ed698147-87d0-42ee-90d6-2463fe54f47a" />
    <img width="1440" height="900" alt="image" src="https://github.com/user-attachments/assets/0a1fc5e4-8cc3-4723-8526-0cf1e89e35b7" />
  </details>
  <details><summary>내 일기 수정</summary>
      <img width="1440" height="900" alt="image" src="https://github.com/user-attachments/assets/619c5a8a-1d2a-4f16-813f-051ca5b5e2f6" />
  </details>
  <details><summary>일기 상세 조회</summary>
    <img width="1440" height="900" alt="image" src="https://github.com/user-attachments/assets/aa59ea25-0d0a-43fd-baf8-88c37ac20f85" />
  </details>
  <details><summary>댓글 조회</summary>
    <img width="1440" height="900" alt="image" src="https://github.com/user-attachments/assets/cfd549d0-3e65-4431-b51b-1eb5af8da0f3" />

  </details>
  <details><summary>좋아요 조회</summary>
    <img width="1440" height="900" alt="image" src="https://github.com/user-attachments/assets/8a47c9ab-1086-423b-a6df-8fbd1906799d" />
  </details>
  <details><summary>검색 결과</summary>
    <img width="1440" height="900" alt="image" src="https://github.com/user-attachments/assets/ff76f2d1-acce-4932-940c-78a09bb27ed4" />
  </details>
  <details><summary>팔로우 조회</summary>
    <img width="1440" height="900" alt="image" src="https://github.com/user-attachments/assets/5f3e4e26-27cf-43a6-b921-5d3f99010477" />
  </details>
</details>

<details>
  <summary>마켓</summary>
  <details><summary>홈 화면(전체 거래글 목록)</summary>
     <img width="1919" height="867" alt="Image" src="https://github.com/user-attachments/assets/bd58c40e-df87-420a-9277-814a5c17ebbd" />
  </details>

  <details><summary>거래글 작성</summary>
     <img width="1917" height="866" alt="Image" src="https://github.com/user-attachments/assets/c839646a-00cd-4a69-9bcc-ecc4256e349a" />
  </details>

  <details><summary>거래글 수정</summary>
    <img width="1919" height="870" alt="Image" src="https://github.com/user-attachments/assets/4b6476b5-c4af-4454-ab41-5a6935d2b585" />
  </details>

  <details><summary>거래글 상세</summary>
    <img width="1916" height="868" alt="Image" src="https://github.com/user-attachments/assets/21db1744-c24f-4595-b2eb-ba743969d48e" />
  </details>
</details>

<details>
  <summary>채팅</summary>
    
  <details><summary>채팅방 목록 조회</summary>
      <img width="1728" height="1117" alt="스크린샷 2025-08-26 오후 2 01 50" src="https://github.com/user-attachments/assets/890c71ab-274f-434a-9a56-13decf8a3828" />
  </details>

  <details><summary>채팅방 생성</summary>
https://github.com/user-attachments/assets/86f6cb9a-825c-4519-b866-6a8543fa6d6c
  </details>

  <details><summary>퀵 메세지</summary>
https://github.com/user-attachments/assets/f2c1b6a4-29ed-4e90-b58e-ffdd8eea2f7f
  </details>

  <details><summary>SSE 채팅방 목록 갱신</summary>
https://github.com/user-attachments/assets/fac8e7d8-8fab-425f-9341-ddfb83e8b021
  </details>
  
  <details><summary>메세지 전송</summary>
https://github.com/user-attachments/assets/dfe16ea2-be8c-47a1-b7b4-3bc30ce8a72c
  </details>
  
  <details><summary>파일 드래그 앤 드롭</summary>
https://github.com/user-attachments/assets/54976259-5eef-4a44-88d0-03bf5cc2bf19
  </details>
  
  <details><summary>파일 전송</summary> 
https://github.com/user-attachments/assets/cf7a55a0-cf0d-425c-a430-27c2d727b9e1
  </details>
  
  <details><summary>이미지 뷰어</summary> 
https://github.com/user-attachments/assets/1a796fe7-c6e6-456c-9354-c7897ca5c88c
  </details>
  
  <details><summary>최하단 이동</summary> 
https://github.com/user-attachments/assets/15dcd759-1349-4265-912f-358d0307401c
  </details>
  
  <details><summary>참여자 목록 조회</summary> 
        <img width="1728" height="1117" alt="스크린샷 2025-08-26 오후 2 17 38" src="https://github.com/user-attachments/assets/2ec6332b-6b57-41dd-af24-4aa314f2f78c" />
  </details>
  
  <details><summary>채팅방 초대</summary> 
https://github.com/user-attachments/assets/c09dfa1d-54f2-4fe3-9395-09dafe59b952
  </details>
  
  <details><summary>채팅방 나가기</summary> 
https://github.com/user-attachments/assets/70c6eea7-7b0a-4b10-890a-09fd4b34534f
  </details>
</details>

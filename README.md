# Spring_SecondHand
* 중고거래 사이트

![1_메인_3](https://github.com/jooro122/Spring_SecondHand/assets/121988218/16974e32-0f65-450d-a58b-9dfa9257f901)

# 🧐 개발기간 및 담당파트
* 개발 기간 : 2023.03.10 ~ 2023.04.07 (5주)

* 참여 인원 : Backend 4명

* 담당 구현 파트

  * 프로젝트 PM / DB 설계

  * 게시판 CRUD 및 검색기능

  * 상품 상세페이지(찜하기 기능)
 

# ⚙️ 프로젝트 개발 환경
* Frontend : HTML, CSS, JavaScript, AJAX, Thymeleaf

* Backend : Spring Boot, Spring Data JPA

* Database : Hibernate, MariaDB

* OpenAPI : 카카오 지도검색, BootStrap


# 📜프로젝트 구현기능
# 회원

* 회원가입 / 로그인, 로그아웃 / 회원정보 변경 / 비밀번호 변경, 회원탈퇴
  + 로그인 방식은 session 방식 사용
  + 회원탈퇴시 PK로 지정된 게시물들이 모두 자동 삭제(cascade 옵션 사용)

![회원가입](https://github.com/jooro122/Spring_SecondHand/assets/121988218/f6282d5a-21e3-4c95-aa73-0c922ec91c96)

# 게시판

* 판매 상품 등록 / 상품 내용 수정 / 판매 상품 삭제 / 찜하기 / 카카오 지도검색
  + 찜하기 기능을 클릭할 시 찜목록 게시판에 추가(Ajax를 통해 찜하기 / 찜취소 통제)
  + session ID와 DB에 저장된 ID를 비교하여 찜버튼 통제
  + 카카오맵 API를 활용하여 지도 위치 표시

![상품등록삭제](https://github.com/jooro122/Spring_SecondHand/assets/121988218/ffde80fa-534b-4823-865d-d4c892344058)

# 대화
* 메시지 보내기 / 읽지 않은 메시지 표시
  + DB설계를 통해 보낸사람 / 받는사람으로 구분하여 날짜순으로 정렬
  + 웹페이지가 로드될 때마다 Ajax 요청을 통하여 새로운 쪽지를 주기적으로 확인 가능

![대화](https://github.com/jooro122/Spring_SecondHand/assets/121988218/a7f15082-1854-4e6f-84cd-ec42b066b14e)

# 카테고리 & 검색
* 카테고리별 검색 / 전체 페이지 검색
  + Pageable를 통하여 게시물을 정렬하고 if 조건문으로 각각 해당하는(제목,내용,작성자등) 조건으로 검색 구현
  + 다만, 여기서는 조건을 막아놓고 제목+내용+작성자로만 검색되게 구현
  + 카테고리 검색은 카테고리 클릭한 값을 비교하여 일치하면 해당 카테고리가 나오게 페이징 구현
  
![카테고리_검색](https://github.com/jooro122/Spring_SecondHand/assets/121988218/af03afdf-4ac2-4798-bbf4-0aedcdb7c0b3)



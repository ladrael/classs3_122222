<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>       
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<!-- css/main.css 파일 불러오기 -->
<link href="css/inc.css" rel="stylesheet" type="text/css">
<link href="css/subpage.css" rel="stylesheet" type="text/css">
<script src="js/jquery-3.6.4.js"></script>
</head>
<body>
	<header>
		<jsp:include page="../inc/top.jsp"></jsp:include>
	</header>
	<article id="joinForm">
		<h1>회원 정보</h1>
		<form action="MemberUpdate.me" method="post" name="joinForm">
			<table border="1">
				<tr>
					<th class="td_left">이름</th>
					<td class="td_right">
						<input type="text" name="name" id="name" required="required" value = "${member.name }">
						<span id="checkNameResult"></span>
					</td>
				</tr>
				<tr>
					<th class="td_left">ID</th>
					<td class="td_right">
						<input type="text" name="id" id="id" value="${member.id }" readonly="readonly">
<!-- 						<input type="button" value="중복확인" id="btnCheckId"> -->
						<span id="checkIdResult"></span>
					</td>
				</tr>
				<tr>
					<th class="td_left">비밀번호</th>
					<td class="td_right">
						<input type="text" name="passwd" id="passwd" placeholder="8 ~ 16글자 사이 입력" required="required">
						<span id="checkPasswdResult"></span>
					</td>
				</tr>
				<tr>
					<th class="td_left">변경할 비밀번호</th>
					<td class="td_right">
						<input type="password" name="newPasswd" id="passwd2" placeholder = "변경할 경우 입력">
						<span id="checkPasswd2Result"></span>
					</td>
				</tr>
				<tr>
					<th class="td_left">E-Mail</th>
					<td class="td_right">
						
<%-- 						<input type="text" name="email1" class="email" required="required" value = "${member.email1 }">@ --%>
<%-- 						<input type="text" name="email2" class="email" required="required"value = "${member.email2 }"> --%>
						<c:set var="arrEmail" value="${fn:split(member.email,'@')}"/>
<%-- 						<c:forEach var="item" items="${arrEmail }"></c:forEach> --%>
						<input type="text" name="email1" class="email" required="required" value = "${arrEmail[0] }">@
						<input type="text" name="email2" class="email" required="required"value = "${arrEmail[1] }">
						
						<select id="emailDomain" onchange="selectDomain(this.value)">
							<option value="">직접입력</option>
							<option value="naver.com">naver.com</option>
							<option value="nate.com">nate.com</option>
							<option value="gmail.com">gmail.com</option>
						</select>
					</td>
				</tr>
				<tr>
					<th class="td_left">성별</th>
					<td class="td_right">
						<!-- 라디오버튼 그룹에 모두 required 속성 설정 시 하나 필수 선택 요구 -->
						<input type="radio" name="gender" value="남" <c:if test="${member.gender eq '남' }">checked</c:if>>남
						<input type="radio" name="gender" value="여" <c:if test="${member.gender eq '여' }">checked</c:if>>여
					</td>
				</tr>
				<tr>
					<td colspan="2" id="btnArea">
						<input type="submit" value="정보수정">
						<input type="reset" value="초기화">
						<input type="button" value="돌아가기" onclick="history.back()">
						<input type="button" value="회원탈퇴" onclick="location='MemberQiutForm.me'">
					</td>
				</tr>
			</table>
		</form>
	</article>
</body>
</html>














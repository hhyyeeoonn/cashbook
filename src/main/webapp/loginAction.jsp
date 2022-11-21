<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>\
<%@ page import = "vo.*" %>
<%@ page import = "dao.*" %>
<%
	// 뷰가 없는 구조 M도 여기에 있는 것이 아니라 호출하는 것이라 실질적으로는 C만 있는 페이지
	// C
	String memberId = request.getParameter("memberId");
	String memberPw = request.getParameter("memberPw");

	
	Member paramMember = new Member();
	paramMember.setMemberId(memberId);
	paramMember.setMemberPw(memberPw);

	// 분리된 M(모델)을 호출
	MemberDao memberDao = new MemberDao(); // C
	Member resultMember = memberDao.login(paramMember); // login이라는 메서드 자체가 M
	
	String redirectUrl = "/loginForm.jsp";
	
	if(resultMember != null) {
		session.setAttribute("loginMember", resultMember);
		redirectUrl = "/cash/cashList.jsp";
	}

	// redirect
	response.sendRedirect(request.getContextPath()+redirectUrl);

%>
package dao;

import java.sql.*;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;

import util.DBUtil;
import vo.Member;


import java.net.*;
import java.util.*;

public class MemberDao {
	// 관리자 : 회원레벨수정
	public int upadateMemberLevel(Member member) throws Exception {
		return 0;
	}
	
	// 관리자 : 멤버수 
	public int selectMemberCount() throws Exception {
		int memberCnt=0;
		
		DBUtil dbUtil=new DBUtil();
		Connection conn=dbUtil.getConnection();
		String sql="SELECT COUNT(*) cnt FROM member";
		PreparedStatement stmt=conn.prepareStatement(sql);
		ResultSet rs=stmt.executeQuery();
		if(rs.next()) {
			memberCnt=rs.getInt("cnt");
		}
		
		dbUtil.close(rs, stmt, conn);
		return memberCnt;
	}
	
		
	// 관리자 : 멤버 리스트
	public ArrayList<Member> selectMemberlistByPage(int beginRow, int rowPerPage) throws Exception {
		ArrayList<Member> memberList = new ArrayList<Member>();
		DBUtil dbUtil = new DBUtil();
		Connection conn = dbUtil.getConnection();
		String sql = "SELECT member_id memberId, member_level memberLevel, member_name memberName, createdate FROM member ORDER BY createdate DESC LIMIT ?, ?";
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setInt(1, beginRow);
		stmt.setInt(2, rowPerPage);
		ResultSet rs= stmt.executeQuery();
		while(rs.next()) {
			Member m = new Member();
			m.setMemberId(rs.getString("memberId"));
			m.setMemberLevel(rs.getInt("memberLevel"));
			m.setMemberName(rs.getString("memberName"));
			m.setCreatedate(rs.getString("createdate"));
			memberList.add(m);
	      }
				
		dbUtil.close(rs, stmt, conn);
		return memberList;
	}
	
	// 관리자 : 멤버 강퇴
	public int deleteMember(int memberNo) throws Exception { // 메서드 오버로딩 이름은 같으나 매개변수가 달라 다른 역할을 수행함
		int deleteResult = 0;
		DBUtil dbUtil = new DBUtil();
		Connection conn = dbUtil.getConnection();
		String sql = "DELETE FROM member WHERE member_No=?";
		PreparedStatement stmt=conn.prepareStatement(sql);  
		stmt.setInt(1, memberNo);
		deleteResult=stmt.executeUpdate();
		
		dbUtil.close(null, stmt, conn);
		return deleteResult;
	}
	
	// 로그인
	public Member login(Member paramMember) throws Exception { // 다형성 : 부모타입으로 자식타입을 감싸는 것 
		Member resultMember = null;
		
		/*
		Class.forName("");
		Connection conn = DriverManager.getConnection()"jdbc..";
		-> DB를 연결하는 코드(명령들)가 Dao 메서드를 거의 공동으로 중복된다.
		-> 중복되는 코드를 하나의 이름(메서드)으로 만들자
		-> 입력값과 반환값 결정해야 한다
		-> 입력값은 없고 반환값은 Connection타입의 결과값이 남아야 한다.
		*/
		
		DBUtil dbUtil = new DBUtil();
		Connection conn = dbUtil.getConnection();
		String sql = "SELECT member_id memberId, member_level memberLevel, member_name memberName FROM member WHERE member_id=? AND member_pw=PASSWORD(?)";
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setString(1, paramMember.getMemberId());
		stmt.setString(2, paramMember.getMemberPw());
		ResultSet rs = stmt.executeQuery();
		if(rs.next()) {
			resultMember = new Member();
			resultMember.setMemberId(rs.getString("memberId"));
			resultMember.setMemberName(rs.getString("memberName"));
			resultMember.setMemberLevel(rs.getInt("memberLevel"));
		}
		
		rs.close(); // rs그 자체를 return하려면 실행되고 있어야 하는 코드가 너무 많아짐 그래서 ArrayList에 복사해서 return 하는 것
		stmt.close();
		conn.close();
		return resultMember;
	}
	
	// 회원가입 insertLoginAction.jsp
	public int insertMember(Member paramMember) throws Exception {
		int resultRow = 0;
		DBUtil dbUtil = new DBUtil();
		Connection conn = dbUtil.getConnection();
		String sql = "INSERT INTO member(member_id, member_pw, member_name, updatedate, createdate) values(?, PASSWORD(?), ?, CURDATE(), CURDATE())";
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setString(1, paramMember.getMemberId());
		stmt.setString(2, paramMember.getMemberPw());
		stmt.setString(3, paramMember.getMemberName());
		resultRow = stmt.executeUpdate();
		
		/*
		stmt.close();
		conn.close();
		*/
		dbUtil.close(null, stmt, conn); // rs값은 없으므로 null
		return resultRow;
	}
	
	
	// 중복확인 insertLoginAction.jsp
	public String failLogin(Member paramMember) throws Exception {
		String idMsg=null;
		String targetUrl=null;
		DBUtil dbUtil = new DBUtil();
		String idSql="SELECT member_id FROM member WHERE member_id = ?";
		Connection conn = dbUtil.getConnection();
		PreparedStatement idStmt = conn.prepareStatement(idSql);
		idStmt.setString(1, paramMember.getMemberId());
		ResultSet rs = idStmt.executeQuery();
		if(rs.next()){
			idStmt=conn.prepareStatement(idSql);
			idMsg=URLEncoder.encode("중복된 ID", "utf-8"); 
			targetUrl="/insertLoginForm.jsp?idMsg="+idMsg;
		}
		rs.close();
		idStmt.close();
		conn.close();
		
		return targetUrl;
	}
	/*
	// 회원가입 1)id 중복확인 2)회원가입
	
	// 반환값 t:이미 존재, f:사용 가능 
	  public boolean selectMemberIdCk(String memberId) throws Exception {
      boolean result = false;
      DBUtil dbUtil = new DBUtil();
      Connection conn = dbUtil.getConnection();
      String sql = "SELECT member_id FROM member WHERE member_id = ?";
      PreparedStatement stmt = conn.prepareStatement(sql);
      stmt.setString(1, memberId);
      ResultSet rs = stmt.executeQuery();
      	if(rs.next()) {
         	result = true;
      	}
      dbUtil.close(rs, stmt, conn);
      return result;
      }

	 */
	
	
	// 회원정보수정 updateMemberAction.jsp
	public int updateMember(Member member, Member paramMember) throws Exception {
		int updateResultRow=0;
		DBUtil dbUtil = new DBUtil();
		Connection conn = dbUtil.getConnection();
		String sql="UPDATE member SET member_name=?, createdate=CURDATE() WHERE member_id=? AND member_pw=PASSWORD(?)"; 
		PreparedStatement stmt=conn.prepareStatement(sql);  
		stmt.setString(1, paramMember.getMemberName());
		stmt.setString(2, paramMember.getMemberId());
		stmt.setString(3, paramMember.getMemberPw());
		updateResultRow=stmt.executeUpdate();

		stmt.close();
		conn.close();
		return updateResultRow;
	}
	
	// 회원탈퇴 deleteMemberAction.jsp
	public int deleteMember(Member member, Member paramMember) throws Exception {
		int deleteResult = 0;
		DBUtil dbUtil = new DBUtil();
		Connection conn = dbUtil.getConnection();
		String sql = "DELETE FROM member WHERE member_id=? AND member_pw=PASSWORD(?)";
		PreparedStatement stmt=conn.prepareStatement(sql);  
		stmt.setString(1, paramMember.getMemberId());
		stmt.setString(2, paramMember.getMemberPw());
		deleteResult=stmt.executeUpdate();
		
		dbUtil.close(null, stmt, conn);
		return deleteResult;
	}
}
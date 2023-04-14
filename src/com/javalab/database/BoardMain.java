package com.javalab.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BoardMain {

	// [멤버변수]
	// 1. oracle 드라이버 이름 문자열 상수
	public static final String DRIVER_NAME = "oracle.jdbc.driver.OracleDriver";

	// 2. oracle 데이터베이스 접속 경로(url) 문자열 상수
	public static final String DB_URL = "jdbc:oracle:thin:@127.0.0.1:1521:orcl";

	// 3. 데이터베이스 접속 객체
	public static Connection con = null;

	// 4. query 실행 객체
	public static PreparedStatement pstmt = null;

	// 5. select 결과 저장 객체
	public static ResultSet rs = null;

	// 6. oracle 계정(id/pwd)
	public static String oracleId = "board";

	// 7. oracle password
	public static String oraclePwd = "1234";
	
	public static void main(String[] args) {
		
		//1. 디비 접속 메소드 호출
		connectDB();
		
		// 2. 게시물 목록 조회
		getBoardList();
		
		// 3. 새글 등록 (오류)
		//   새글 등록이 완료되었으면 주석처리 한 후에 답글 등록으로 이동
		// insertNewBoard();
		
		// 4. 답글 등록 (아직)
		//  어떤 게시물에 답글을 달지 부모 게시글의 정보를 전달해야함.
		int replyGroup = 29; // 부모글의 그룹번호
		int replyOrder = 1; // 부모글의 그룹내 순서
		int replyIndent = 1; // 부모글의 들여쓰기
		
		insertReply(replyGroup, replyOrder, replyIndent);
		
		// 5. 게시물 목록 조회(반드시 1번~5번까지)
		int startNo = 1;
		int length = 10;
		getBoardListTopN(startNo, length);
		
		// 6. 중간에 특정 부분 조회(5번~9번까지)
		startNo = 5;
		length = 9;
		getBoardListPart(startNo, length);
		
		// 7. 게시물 조회수 증가
		int bno = 40; // 조회수를 증가시킬 게시물 번호
		updateCount(bno);
		
		// 8. 수정
		//  5번 게시물의 제목을 "다섯번째 글"로 수정하시오
		bno = 34;
		String newTitle = "다섯번째 글";
		updateTitle(bno, newTitle);
		
		// 9. user01님이 작성한 게시물을 모두 삭제하시오.
		bno = 27; // 삭제할 게시물 번호
		deleteBoard(bno);
		
		// 자원반납
		closeResource();
		
	} //end main

	//1. 디비 접속 메소드 호출
	private static void connectDB() {
		try {
			Class.forName(DRIVER_NAME);
			System.out.println("드라이버 로드 성공");
			
			con = DriverManager.getConnection(DB_URL, oracleId, oraclePwd);
			System.out.println("커넥션 객체 생성 성공");
			
		} catch (ClassNotFoundException e) {
			System.out.println("드라이버 ERR! : " + e.getMessage());
			System.out.println("나는 1번");
		} catch (SQLException e) {
			System.out.println("SQL ERR! : " + e.getMessage());
			System.out.println("나는 1번");
		}
	} // end 1. 디미 접속 메소드 호출
	
	// 2. 게시물 목록 조회
	private static void getBoardList() {
		try {
			String sql = " select bno, title, content, member_id, count, created_date,";
				   sql += "  reply_group, reply_order, reply_indent";
				   sql += " from tbl_board";
			
		   pstmt = con.prepareStatement(sql);
		   System.out.println("pstmt 객체 생성 성공");
		   
		   rs = pstmt.executeQuery();
		   System.out.println("                                    게시물 목록 조회                                                                    ");
		   System.out.println("=================================================================================");
		   System.out.println(" bno" + "        " +"       title"+"\t"+"\t"+"content"+" "+"member_id"+" "+"count"+" "+"created_date"+"reply_group"+" "+"reply_order"+" "+"reply_indent");
		   System.out.println("=================================================================================");
		   
		   while (rs.next()) {
			System.out.println(rs.getInt("BNO") + "\t"
							  + rs.getString("TITLE") + "\t"
							  + rs.getString("CONTENT") + "\t"
							  + rs.getString("MEMBER_ID")+ "\t"
							  + rs.getInt("COUNT")+ "\t"
							  + rs.getDate("CREATED_DATE")+ "\t"
							  + rs.getInt("REPLY_GROUP")+ "\t"
							  + rs.getInt("REPLY_ORDER")+ "\t"
							  + rs.getInt("REPLY_INDENT"));
		} // while end
		   
		} catch (SQLException e) {
			System.out.println("SQL ERR! : " + e.getMessage());
			System.out.println("나는 2번");
		} finally {
			// 자원반납 메소드
			closeResource();
		}
		System.out.println();
	} // end 2. 게시물 목록 조회
	
//	// 3. 새글 등록
//	private static void insertNewBoard() {
//		String title = "잘못은 우리별에 있어";
//		String content = "네가 좋으면 나도 좋아";
//		String memberId = "user01";
//
//		try {
//			String sql = "Insert into TBL_BOARD(BNO,TITLE,CONTENT,MEMBER_ID,COUNT,CREATED_DATE,";
//				   sql += " REPLY_GROUP,REPLY_ORDER,REPLY_INDENT)";
//				   sql += " values (seq_bno.nextval,?,?,?,0,to_date('23/04/15','RR/MM/DD'),seq_bno.currval,0,0)";
//				   
//			pstmt = con.prepareStatement(sql);
//			pstmt.setString(1, title);
//			pstmt.setString(2, content);
//			pstmt.setString(3, memberId);
//			
//			int result = pstmt.executeUpdate();
//			if (result > 0) {
//				System.out.println("입력 성공");
//			} else {
//				System.out.println("입력 실패");
//			}
//			
//		} catch (SQLException e) {
//			System.out.println("SQL ERR! : " + e.getMessage());
//			System.out.println("나는 3번");
//		} finally {
//			closeResource();
//		}
//	} // end. 3. 새글 등록
	
	// 4. 답글 등록
	private static void insertReply(int replyGroup, int replyOrder, int replyIndent) {
		String tilte = "기후위기의 정책방안";
		String content = "주변 국가와의 정책비교";
		String memberId = "user03";
		
		try {
			String sql = "update tbl_board";
				   sql += " set reply_order = reply_order+1";
				   sql += " where reply_group = ?";
				   sql += " and reply_order>?";
		pstmt = con.prepareStatement(sql);
		pstmt.setInt(1, replyGroup); // 부모 그룹
		pstmt.setInt(2, replyOrder);
		
		int result = pstmt.executeUpdate();
		if (result > 0) {
			System.out.println("기존 답글의 컬럼 +1 변경 성공");
		} else {
			System.out.println("기존 답글의 컬럼 +1 변경 실패");
		}
		sql = "Insert into TBL_BOARD(BNO,TITLE,CONTENT,MEMBER_ID,COUNT,CREATED_DATE,";
		sql += " REPLY_GROUP,REPLY_ORDER,REPLY_INDENT)";
		sql += " values (seq_bno.nextval,?,?,?,0,to_date('23/04/15','RR/MM/DD'),?,?,?)";
		
		pstmt = con.prepareStatement(sql);
		pstmt.setString(1, tilte);
		pstmt.setString(2, content);
		pstmt.setString(3, memberId);
		pstmt.setInt(4, replyGroup);
		pstmt.setInt(5, replyOrder+1);
		pstmt.setInt(6, replyIndent+1);
		
		int resultNo = pstmt.executeUpdate();
		if (resultNo > 0) {
			System.out.println("답글 저장 성공");
		} else {
			System.out.println("답글 저장 실패");
		}

		} catch (SQLException e) {
			System.out.println("SQL ERR! : " + e.getMessage());
			System.out.println("나는 4번");
		} finally {
			closeResource();
		}
		System.out.println();
		
	} // end 4. 답글 등록
	
	// 5. 게시물 목록 조회(반드시 1번~5번까지)
	private static void getBoardListTopN(int startNo, int length) {
		try {
			String sql = "select B.*";
				   sql += " from (select b.* from tbl_board b order by bno asc)b";
				   sql += " where rownum between ? and ?";
				   
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, startNo);
			pstmt.setInt(2, length);
			rs = pstmt.executeQuery();
			
			System.out.println("                  5.  BNO "+startNo+" ~ "+length+"  게시물 목록 조회                                     ");
			System.out.println("=================================================================================");
			System.out.println(" bno" + "        " +"       title"+"\t"+"\t"+"content"+" "+"member_id"+" "+"count"+" "+"created_date"+"reply_group"+" "+"reply_order"+" "+"reply_indent");
			System.out.println("=================================================================================");
			   
			while (rs.next()) {
			System.out.println(rs.getInt("BNO") + "\t"
							  + rs.getString("TITLE") + "\t"
							  + rs.getString("CONTENT") + "\t"
							  + rs.getString("MEMBER_ID")+ "\t"
							  + rs.getInt("COUNT")+ "\t"
							  + rs.getDate("CREATED_DATE")+ "\t"
							  + rs.getInt("REPLY_GROUP")+ "\t"
							  + rs.getInt("REPLY_ORDER")+ "\t"
							  + rs.getInt("REPLY_INDENT"));
			} // while end
			
		} catch (SQLException e) {
			System.out.println("SQL ERR! : " + e.getMessage());
			System.out.println("나는 5번");
		} finally {
			closeResource();
		}
		System.out.println();
	} // end 5. 게시물 목록 조회
	
	// 6. 중간에 특정 부분 조회(5번~9번까지)
	private static void getBoardListPart(int startNo, int length){
		try {
			String sql = "select b.*";
				   sql += " from (select rownum rum, a.* from (select b.* from tbl_board b";
				   sql += " order by bno asc)a)b";
				   sql += " where rum between ? and ?";
				   
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, startNo);
			pstmt.setInt(2, length);
			rs = pstmt.executeQuery();
			
			System.out.println("                  5.  BNO "+startNo+" ~ "+length+"  게시물 목록 조회                                     ");
			System.out.println("=================================================================================");
			System.out.println(" bno" + "        " +"       title"+"\t"+"\t"+"content"+" "+"member_id"+" "+"count"+" "+"created_date"+"reply_group"+" "+"reply_order"+" "+"reply_indent");
			System.out.println("=================================================================================");
			   
			while (rs.next()) {
			System.out.println(rs.getInt("BNO") + "\t"
							  + rs.getString("TITLE") + "\t"
							  + rs.getString("CONTENT") + "\t"
							  + rs.getString("MEMBER_ID")+ "\t"
							  + rs.getInt("COUNT")+ "\t"
							  + rs.getDate("CREATED_DATE")+ "\t"
							  + rs.getInt("REPLY_GROUP")+ "\t"
							  + rs.getInt("REPLY_ORDER")+ "\t"
							  + rs.getInt("REPLY_INDENT"));
			} // while end
			
		} catch (SQLException e) {
			System.out.println("SQL ERR! : " + e.getMessage());
			System.out.println("나는 6번");
		} finally {
			closeResource();
			System.out.println();
		}
	} // end 6. 특정 부분 조회
	
	// 7. 게시물 조회수 증가
	private static void updateCount(int bno) {
		try {
			String sql = "update tbl_board";
				   sql += " set count = count + 1";
				   sql += " where bno = ?";
		
		  pstmt =con.prepareStatement(sql);
		  pstmt.setInt(1, bno);
		  rs = pstmt.executeQuery();
		  
		  int result = pstmt.executeUpdate();
		  if (result > 0) {
			System.out.println("조회수 증가");
		} else {
			System.out.println("조회수 증가 실패");
		}
		  
		} catch (SQLException e) {
			System.out.println("SQL ERR! : " + e.getMessage());
			System.out.println("나는 7번");
		} finally {
			closeResource();
			System.out.println();
		}
	} // end 7. 게시물 조회수 증가
	
	// 8. 수정
	private static void updateTitle(int bno, String newTitle) {
		try {
			String sql = "update tbl_board";
				   sql += " set title = ?";
				   sql += " where bno = ?";
				   
		pstmt = con.prepareStatement(sql);
		pstmt.setInt(2, bno);
		pstmt.setString(1, newTitle);
		rs = pstmt.executeQuery();
		
		int result = pstmt.executeUpdate();
		if (result > 0) {
			System.out.println("타이틀 수정 성공");
		} else {
			System.out.println("타이틀 수정 실패");
		}
		
		} catch (SQLException e) {
			System.out.println("SQL ERR! : " + e.getMessage());
			System.out.println("나는 8번");
		} finally {
			closeResource();
			System.out.println();
		}
	} // end 8. 수정
	
	// 9. user01님이 작성한 게시물을 삭제하시오.
	private static void deleteBoard(int bno) {
		try {
			String sql = "delete from tbl_board";
				   sql += " where bno = ?";
				   
		pstmt = con.prepareStatement(sql);
		pstmt.setInt(1, bno);
		rs = pstmt.executeQuery();
		
		int result = pstmt.executeUpdate();
		if (result > 0) {
			System.out.println("삭제 완료");
		} else {
			System.out.println("삭제 실패");
		}
		} catch (SQLException e) {
			System.out.println("SQL ERR! : " + e.getMessage());
			System.out.println("나는 9번");
		} finally {
			closeResource();
			System.out.println();
		}
	} // end 9. 게시물 삭제.
	
	// 자원반납
	private static void closeResource() {
		try {
			if (rs != null) {
				rs.close();
			} if (pstmt != null) {
				pstmt.close();
			}
		} catch (SQLException e) {
			System.out.println("SQL ERR! : " + e.getMessage());
		}
	} // end 자원반납
	
}

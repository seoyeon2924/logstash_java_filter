package org.logstashplugins;

import static org.logstashplugins.TelgrmParsingFilterTgw.telgrmMap;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// FIXME 1 로직과 설정은 분리하자 (+passwrod 뺄 수 있을까?)
// FIXME 2 repository inf 추가
// FIXME 3 sqlite로 바꿔보기

//fixme 이름바꾸기
// 리팩토링 버전
public class TelgramDao {

    public TelgramDao() {
        this.getTelgrm();
    }

    public List<Telgrm> getTelgrm() {
        List<Telgrm> telgrms = new ArrayList<>();
        PreparedStatement pstmt = null;

        try (Connection conn = connect()) {  //fixme Try-with-resources
            String sql = "SELECT telgrm_no, field, field_size FROM telgrm_info ORDER BY telgrm_no, field_no";
            pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            String previousTelgrmNo = "";

            while (rs.next()) {
                String currentTelgrmNo = rs.getString("telgrm_no");

                if (!previousTelgrmNo.equals("#") && !currentTelgrmNo.equals(previousTelgrmNo)) {
                    telgrmMap.put(previousTelgrmNo, telgrms);
                    telgrms = new ArrayList<>();
                }

                Telgrm telgrm = new Telgrm(rs.getString("field"), rs.getInt("field_size"));
                telgrms.add(telgrm);
                previousTelgrmNo = currentTelgrmNo;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("SQL구문 Error");
        } finally { // fixme 적절한 null check
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException ignore) {
            }
        }
        return telgrms;
    }

    //fixme 비밀번호 분리, url 숨기기??
    private Connection connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager
                    .getConnection("jdbc:mysql://198.13.47.188:19762/elastic",
                            "mrbluesky",
                            "kang12!@");
        } catch (ClassNotFoundException e) { // fixme try catch 한개로
            throw new IllegalStateException("드라이버가 없음");
        } catch (SQLException e) {
            throw new IllegalStateException("커넥션이 안됨");
        } catch (Exception e) {
            throw new IllegalStateException("시스템실 연락!");
        }
    }
}

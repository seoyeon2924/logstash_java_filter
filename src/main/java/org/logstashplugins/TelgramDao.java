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
// FIXME 3 sqlite로 변경

// fixme 이름바꾸기
// 리팩토링 버전
public class TelgramDao {

    public TelgramDao() {
        this.getTelgrm();
    }

    public void getTelgrm() {
        List<Telgrm> telgrms = new ArrayList<>();
        PreparedStatement pstmt = null;

        try (Connection conn = connect()) {  //fixme Try-with-resources

            String sql = "SELECT telgrm_no, field, field_size,chan_con_yn,chan_con_field FROM telgrm_info ORDER BY telgrm_no,field_no";
            pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            String previousTelgrmNo = "";

            while (rs.next()) {
                String currentTelgrmNo = rs.getString("telgrm_no");

                if (!previousTelgrmNo.equals("#") && !currentTelgrmNo.equals(previousTelgrmNo)) {
                    telgrmMap.put(previousTelgrmNo, telgrms);
                    telgrms = new ArrayList<>();

                }

                Telgrm telgrm = new Telgrm(rs.getString("field"), rs.getInt("field_size"),
                        rs.getString("chan_con_yn"), rs.getString("chan_con_field"));
                telgrms.add(telgrm);
                previousTelgrmNo = currentTelgrmNo;
            }

            telgrmMap.put(previousTelgrmNo, telgrms);

            System.out.println(telgrmMap);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new IllegalStateException("SQL구문 Error");
        } finally { // fixme 적절한 null check
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException ignore) {
            }
        }
    }


    private Connection connect() {
        try {
            Class.forName("org.sqlite.JDBC");

            String dbFile = "/Users/seoyeon/Desktop/telgrm.db";
           // String dbFile = "/app/logstash/sqlite/telgrm.db";

            return DriverManager
                    .getConnection("jdbc:sqlite:" + dbFile);

        } catch (ClassNotFoundException e) { // fixme try catch 한개로
            throw new IllegalStateException("드라이버가 없음");
        } catch (SQLException e) {
            throw new IllegalStateException("커넥션이 안됨");
        } catch (Exception e) {
            throw new IllegalStateException("시스템실 연락!");
        }
    }
}

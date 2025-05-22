package com.cake7.application.repository;

import com.cake7.application.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

@Slf4j
public class MemberRepositoryV2 {
    private final DataSource dataSource;

    public MemberRepositoryV2(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Member save(Member member) throws SQLException {
        String sql = "INSERT INTO member (id, money) VALUES (?, ?)";

        Connection connection = null;
        PreparedStatement pstmt = null;

        try {
            connection = getConnection();
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, member.getId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate();
            return member;
        } catch (SQLException e) {
            log.error("error={}", e.getMessage());
            throw new SQLException(e);
        } finally {
            close(connection, pstmt, null);
        }
    }

    public Member findById(String id) throws SQLException {
        String sql = "select * from member where id = ?";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                Member member = new Member();
                member.setId(rs.getString("id"));
                member.setMoney(rs.getInt("money"));
                return member;
            } else {
                throw new NoSuchElementException("member not found memberId="
                        + id);
            }
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, rs);
        }
    }

    public Member findById(Connection connection, String id) throws SQLException {
        String sql = "SELECT * FROM member WHERE id = ?";
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, id);
            rs = pstmt.executeQuery();
            if(rs.next()) {
                Member member = new Member();
                member.setId(rs.getString("id"));
                member.setMoney(rs.getInt("money"));
                return member;
            }
            throw new NoSuchElementException("not found member");
        } catch (SQLException e) {
            log.error("error={}", e.getMessage());
            throw new SQLException(e);
        } finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(pstmt);
        }
    }

    public void update(String id, int money) throws SQLException {
        String sql = "UPDATE member SET money = ? WHERE id = ?";
        PreparedStatement pstmt = null;
        Connection connection = null;
        try {
            connection = getConnection();
            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, id);

            int resulSize = pstmt.executeUpdate();
            log.info("resulSize={}", resulSize);
        } catch (SQLException e) {
            log.error("error={}", e.getMessage());
            throw new SQLException(e);
        } finally {
            close(connection, pstmt, null);
        }
    }

    public void update(Connection connection, String id, int money) throws SQLException {
        String sql = "UPDATE member SET money = ? WHERE id = ?";
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, id);

            int resulSize = pstmt.executeUpdate();
            log.info("resulSize={}", resulSize);
        } catch (SQLException e) {
            log.error("error={}", e.getMessage());
            throw new SQLException(e);
        } finally {
            JdbcUtils.closeStatement(pstmt);
        }
    }

    public void delete(String id) throws SQLException {
        String sql = "DELETE FROM member WHERE id = ?";

        Connection connection = null;
        PreparedStatement pstmt = null;
        try {
            connection = getConnection();
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("error={}", e.getMessage());
            throw new SQLException(e);
        } finally {
            close(connection, pstmt, null);
        }
    }

    private void close(Connection connection, Statement stmt, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        JdbcUtils.closeConnection(connection);
    }

    private Connection getConnection() throws SQLException {
        Connection connection = dataSource.getConnection();
        log.info("connection={}, class={}", connection, connection.getClass());
        return connection;
    }
}

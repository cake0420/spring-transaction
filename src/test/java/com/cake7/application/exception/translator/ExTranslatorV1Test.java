package com.cake7.application.exception.translator;

import com.cake7.application.domain.Member;
import com.cake7.application.repository.ex.MyDBException;
import com.cake7.application.repository.ex.MyDuplicateKeyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

import static com.cake7.application.connection.ConnectionConst.*;

public class ExTranslatorV1Test {

    Repository repository;
    Service service;

    @BeforeEach
    void init() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(DB_URL, DB_USER, DB_PASSWORD);
        repository = new Repository(dataSource);
        service = new Service(repository);
    }

    @Test
    void duplicateKeyTest() {
        service.create("myId");
        service.create("myId");
    }

    @Slf4j
    @RequiredArgsConstructor
    static class Service {
        private final Repository repository;

        public void create(String id) {
            try {
                repository.save(new Member(id, 0));
            } catch (MyDuplicateKeyException e) {
                log.info("키 중복 복구 시도");
                String retryId = generateNewId(id);
                log.info("retryId={}", retryId);
                repository.save(new Member(retryId, 0));
            } catch (MyDBException e) {
                log.info("예외 계층 에러");
                throw e;
            }
        }

        private String generateNewId(String id) {
            return id + new Random().nextInt(10000);
        }
    }

    @RequiredArgsConstructor
    @Slf4j
    static class Repository {
        private final DataSource dataSource;

        public Member save(Member member) {
            String sql = "insert into member(id, money) values(?, ?)";
            Connection connection = null;
            PreparedStatement pstmt = null;

            try {
                connection = dataSource.getConnection();
                pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, member.getId());
                pstmt.setInt(2, member.getMoney());
                pstmt.executeUpdate();
                return member;
            } catch (SQLException e) {
                if (e.getErrorCode() == 1062) {
                    throw new MyDuplicateKeyException(e);
                }
                throw new MyDBException(e);
            } finally {
                JdbcUtils.closeStatement(pstmt);
                JdbcUtils.closeConnection(connection);
            }

        }

    }
}

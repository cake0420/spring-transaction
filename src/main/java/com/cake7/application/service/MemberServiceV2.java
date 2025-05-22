package com.cake7.application.service;

import com.cake7.application.domain.Member;
import com.cake7.application.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final DataSource dataSource;
    private final MemberRepositoryV2 repository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Connection connection = dataSource.getConnection();
        
        try {
            connection.setAutoCommit(false);
            bizLogic(connection, fromId, toId, money);
            connection.commit();
        } catch (Exception e) {
            connection.rollback();
            throw new IllegalStateException(e);
        } finally {
            release(connection);
        }
        
    }

    private void bizLogic(Connection connection, String fromId, String toId, int money) throws SQLException {
        Member fromMember = repository.findById(connection, fromId);
        Member toMember = repository.findById(connection, toId);


        repository.update(connection, fromId, fromMember.getMoney() - money);

        validation(toMember);
        repository.update(connection, toId, toMember.getMoney() + money);
    }

    private void release(Connection connection) {
        if(connection != null) {
            try {
                connection.setAutoCommit(true); // 커넥션 풀을 고려
                connection.close();
            } catch (Exception e) {
                log.error("error", e);
            }
        }
    }

    private void validation(Member toMember) {
        if(toMember.getId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }

}

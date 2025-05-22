package com.cake7.application.repository;

import com.cake7.application.domain.Member;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static com.cake7.application.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
class MemberRepositoryVOTest {

    MemberRepositoryVO repository;


    @BeforeEach
    void beforeEach() {

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(DB_URL);
        dataSource.setUsername(DB_USER);
        dataSource.setPassword(DB_PASSWORD);
        dataSource.setMaximumPoolSize(10);
        dataSource.setPoolName("MyPool");
        repository = new MemberRepositoryVO(dataSource);
    }


    @Test
    void crud() throws SQLException {
        Member member = new Member("1", 1000);
        repository.save(member);

        Member findMember = repository.findById(member.getId());
        log.info("findMember={}", findMember);
        assertThat(findMember).isEqualTo(member);

        repository.update(member.getId(), 200000);
        Member updateMember = repository.findById(member.getId());
        log.info("findMember2={}", updateMember);
        assertThat(updateMember.getMoney()).isEqualTo(200000);

        repository.delete(member.getId());
        assertThatThrownBy(() -> repository.findById(member.getId()))
                .isInstanceOf(NoSuchElementException.class);
    }
}
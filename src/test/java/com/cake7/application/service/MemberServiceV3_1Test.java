package com.cake7.application.service;

import com.cake7.application.domain.Member;
import com.cake7.application.repository.MemberRepositoryV3;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import java.sql.SQLException;

import static com.cake7.application.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberServiceV3_1Test {
    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    private MemberServiceV3_1 service;
    private MemberRepositoryV3 repository;

    @BeforeEach
    void before() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(DB_URL, DB_USER, DB_PASSWORD);
        repository = new MemberRepositoryV3(dataSource);

        PlatformTransactionManager transactionManager = new DataSourceTransactionManager();
        service = new MemberServiceV3_1(transactionManager,repository);
    }

    @AfterEach
    void after() throws SQLException {
        repository.delete(MEMBER_A);
        repository.delete(MEMBER_B);
        repository.delete(MEMBER_EX);
    }

    @Test
    @DisplayName("정상 이체")
    void accountTransfer() throws SQLException {

        //given
        Member memberA = new Member(MEMBER_A,   10000);
        Member memberEX = new Member(MEMBER_EX, 10000);

        repository.save(memberA);
        repository.save(memberEX);

        //when
        assertThatThrownBy(() -> service.accountTransfer(memberA.getId(), memberEX.getId(), 2000))
                .isInstanceOf(IllegalStateException.class);


        //then
        Member findMemberA = repository.findById(memberA.getId());
        Member findMemberB = repository.findById(memberEX.getId());
        assertThat(findMemberA.getMoney()).isEqualTo(10000);
        assertThat(findMemberB.getMoney()).isEqualTo(10000);
    }


}
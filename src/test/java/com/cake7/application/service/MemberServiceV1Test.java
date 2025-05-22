package com.cake7.application.service;

import com.cake7.application.domain.Member;
import com.cake7.application.repository.MemberRepositoryVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;

import static com.cake7.application.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


/*
* 트랜잭션 없으면 문제 발생
*
* */

class MemberServiceV1Test {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    private MemberServiceV1 service;
    private MemberRepositoryVO repository;

    @BeforeEach
    void before() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(DB_URL, DB_USER, DB_PASSWORD);
        repository = new MemberRepositoryVO(dataSource);
        service = new MemberServiceV1(repository);
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
        assertThat(findMemberA.getMoney()).isEqualTo(8000);
        assertThat(findMemberB.getMoney()).isEqualTo(10000);
    }


}
package com.cake7.application.service;

import com.cake7.application.domain.Member;
import com.cake7.application.repository.MemberRepository;
import com.cake7.application.repository.MemberRepositoryV4_1;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


/*
 *  예외 누수 문제 해결
 *  SQLException 해
 *
 *  MemberRepository 인터페이스에 의존
 * */
@Slf4j
@SpringBootTest
class MemberServiceV4Test {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";


    @TestConfiguration
    static class TestConfig {

        @Bean
        PlatformTransactionManager transactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }

        @Bean
        MemberRepository memberRepositoryV4(DataSource dataSource) {
            return new MemberRepositoryV4_1(dataSource);
        }

        @Bean
        MemberServiceV4 memberServiceV4(DataSource dataSource) {
            return new MemberServiceV4(memberRepositoryV4(dataSource));
        }
    }


    @Autowired
    private MemberServiceV4 service;

    @Autowired
    private MemberRepository repository;

    @AfterEach
    void after() {
        repository.delete(MEMBER_A);
        repository.delete(MEMBER_B);
        repository.delete(MEMBER_EX);
    }


    @Test
    void AppCheck() {
        log.info("repository={}", repository.getClass());
        log.info("service={}", service.getClass());
        Assertions.assertThat(AopUtils.isAopProxy(service)).isTrue();
        Assertions.assertThat(AopUtils.isAopProxy(repository)).isFalse();
    }

    @Test
    @DisplayName("정상 이체")
    void accountTransfer() {

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
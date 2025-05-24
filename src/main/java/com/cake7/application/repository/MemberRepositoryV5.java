package com.cake7.application.repository;

import com.cake7.application.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;

/*
*
*  예외 누수 문제 해결
*  체크 예외를 런타임 예외로 변경
*  MemberRepository 인터페이스 사용
*  throws SQLException 제거
* */
@Slf4j
public class MemberRepositoryV5 implements MemberRepository{
    private final JdbcTemplate jdbcTemplate;

    public MemberRepositoryV5(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Member save(Member member) {
        String sql = "INSERT INTO member (id, money) VALUES (?, ?)";
        jdbcTemplate.update(sql, member.getId(), member.getMoney());

        return member;
    }

    @Override
    public Member findById(String id) {
        String sql = "SELECT * FROM member WHERE id = ?";

        return jdbcTemplate.queryForObject(sql, memberRowMapper() , id);
    }

    private RowMapper<Member> memberRowMapper() {
        return (rs, rowNum) -> {
            Member member = new Member();
            member.setId(rs.getString("id"));
            member.setMoney(rs.getInt("money"));
            return member;
        };
    }
    @Override
    public void update(String id, int money) {
        String sql = "UPDATE member SET money = ? WHERE id = ?";
        jdbcTemplate.update(sql, money, id);
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM member WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

}

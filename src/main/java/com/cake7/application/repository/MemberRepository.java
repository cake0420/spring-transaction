package com.cake7.application.repository;

import com.cake7.application.domain.Member;

public interface MemberRepository{
    Member findById(String id);
    Member save(Member member);
    void update(String id, int money);
    void delete(String id);
}

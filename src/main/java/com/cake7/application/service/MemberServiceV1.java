package com.cake7.application.service;

import com.cake7.application.domain.Member;
import com.cake7.application.repository.MemberRepositoryVO;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;

@RequiredArgsConstructor
public class MemberServiceV1 {

    private final MemberRepositoryVO repository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Member fromMember = repository.findById(fromId);
        Member toMember = repository.findById(toId);


        repository.update(fromId, fromMember.getMoney() - money);

        validation(toMember);
        repository.update(toId, toMember.getMoney() + money);
    }

    private void validation(Member toMember) {
        if(toMember.getId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }

}

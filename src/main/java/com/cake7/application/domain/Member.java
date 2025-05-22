package com.cake7.application.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Member {
    private String id;
    private int money;

    public Member() {}

    public Member(String id, int money) {
        this.id = id;
        this.money = money;
    }
}

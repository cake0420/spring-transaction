package com.cake7.application.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class UncheckedExceptionTest {

    @Test
    void unchecked_catch() {
        Service service = new Service();
        service.callCheck();
    }

    @Test
    void unchecked_throw() {
        Service service = new Service();
        assertThatThrownBy(service::callThrow)
                .isInstanceOf(MyUncheckedException.class);
    }

    /*
    *  RuntimeException을 상속받은 예외는 언체크 예외가 된
    *
    * */
    static class MyUncheckedException extends RuntimeException {
        public MyUncheckedException(String message) {
            super(message);
        }
    }


    /*
    *  Unchecked 예외는
    * 예외를 잡거나 던지지 않아도 된다.
    * 예외를 잡지 않으면 자동으로 앞으로 던진다/
    *
    * */

    static class Service {
        Repository repository = new Repository();
        public void callCheck() {
            try {
                repository.call();
            } catch (MyUncheckedException e) {
                log.info("예외처리. message={}", e.getMessage(), e);
            }
        }

        /*
        *  에외를 잡지 않아도 된다. 자연스럽게 상위로 넘어간다
        *  체크 예외와 다르게 throws 예외 선언을 하지 않아도 된다.
        * */
        public void callThrow() {
            repository.call();
        }
    }
    static class Repository {
        public void call() {
            throw new MyUncheckedException("ex");
        }
    }
}

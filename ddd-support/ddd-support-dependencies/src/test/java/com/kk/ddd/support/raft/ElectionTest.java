package com.kk.ddd.support.raft;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <br>
 *
 * @author mm
 */
@Slf4j
@ActiveProfiles("dev")
class ElectionTest {
    @BeforeAll
    public static void setLevel() {
        ((Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).setLevel(Level.INFO);
    }

    @Test
    void test() throws Exception {
        new Thread(new Election(0, 9000, "0=9000,1=9001,2=9002")).start();
        new Thread(new Election(1, 9001, "0=9000,1=9001,2=9002")).start();
        new Thread(new Election(2, 9002, "0=9000,1=9001,2=9002")).start();
        Thread.sleep(100000);
    }
}

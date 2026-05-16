package com.kk.ddd.support.grl;

import static org.junit.jupiter.api.Assertions.*;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.kk.ddd.support.grl.ClusterMembersResult.ClusterStatus;
import java.net.InetAddress;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;

/**
 * <br>
 *
 * @author kingk
 */
@Slf4j
@ActiveProfiles("dev")
class TokenServerTest {

    @BeforeAll
    public static void setLevel() {
        ((Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).setLevel(Level.INFO);
    }

    @Test
    void test() throws Exception {
        var servers = List.of("localhost:10001", "localhost:10002", "localhost:10003");
        String localIP = InetAddress.getLocalHost().getHostAddress();
        servers = servers.stream().map(s -> s.replace("localhost", localIP)).toList();
        assertEquals(3, servers.size());
        assertTrue(servers.stream().allMatch(s -> s.contains(localIP)));
        //
        var alice = new TokenServerConfig();
        alice.setAlias("alice");
        alice.setListenPort(10001);
        alice.setServers(servers);
        var server1 = new TokenServer(alice);
        //
        var bob = new TokenServerConfig();
        bob.setAlias("bob");
        bob.setListenPort(10002);
        bob.setServers(servers);
        var server2 = new TokenServer(bob);
        //
        var carl = new TokenServerConfig();
        carl.setAlias("carl");
        carl.setListenPort(10003);
        carl.setServers(servers);
        var server3 = new TokenServer(carl);
        //
        Thread.sleep(10_000);
        assertEquals(3, server1.allMembers().members().size());
        assertEquals(3, server2.allMembers().members().size());
        assertEquals(3, server3.allMembers().members().size());
        //
        // Test TokenServer rate limiting
        server1.register("api-A", 100);
        // RateLimiter accumulates permits at 100/s; tryAcquire(permits, 0)
        // succeeds when enough time (10ms per permit) has passed since last acquire.
        Thread.sleep(100); // let some permits accumulate
        assertTrue(server1.acquire("api-A", 1));
        assertFalse(server1.acquire("non-existent", 1));

        // Acquiring permits > rate() is always rejected
        assertFalse(server1.acquire("api-A", 200));

        // Update rate and verify rate check
        server1.register("api-A", 200);
        Thread.sleep(100);
        assertTrue(server1.acquire("api-A", 1));
        assertFalse(server1.acquire("api-A", 300));
        //
        server3.stop();
        //
        Thread.sleep(server3.getMaxRemoveTimeout());
        assertEquals(2, server1.allMembers().members().size());
        assertEquals(2, server2.allMembers().members().size());
        assertEquals(ClusterStatus.NOT_RUNNING, server3.allMembers().status());
        //
        server2.stop();
        //
        Thread.sleep(server3.getMaxRemoveTimeout());
        assertEquals(ClusterStatus.LEFT, server1.allMembers().status());
        assertEquals(ClusterStatus.NOT_RUNNING, server2.allMembers().status());
        assertEquals(ClusterStatus.NOT_RUNNING, server3.allMembers().status());
    }
}

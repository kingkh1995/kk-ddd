package com.kk.ddd.support.grl;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link TokenClient}. <br>
 *
 * @author mm
 */
class TokenClientTest {

    private TokenClientConfig createDefaultConfig() {
        var config = new TokenClientConfig();
        config.setServers(List.of("localhost:8080", "localhost:8081"));
        config.setKey("test-api");
        return config;
    }

    @Test
    void shouldConstructWithValidConfig() {
        var client = new TokenClient(createDefaultConfig());
        assertNotNull(client);
        assertTrue(client.isRunning());
    }

    @Test
    void shouldRejectPermitsExceedingMax() {
        var client = new TokenClient(createDefaultConfig());
        assertFalse(client.tryAcquire(201));      // > maxPermitsPerRequest(200)
        assertFalse(client.tryAcquire(Integer.MAX_VALUE));
        assertFalse(client.canAcquire(201));
    }

    @Test
    void shouldAcceptPermitsUpToMax() {
        var client = new TokenClient(createDefaultConfig());
        assertTrue(client.canAcquire(200));        // == maxPermitsPerRequest, should be accepted
        assertTrue(client.canAcquire(1));
    }

    @Test
    void shouldRejectNonPositivePermits() {
        var client = new TokenClient(createDefaultConfig());
        assertFalse(client.tryAcquire(0));
        assertFalse(client.tryAcquire(-1));
    }

    @Test
    void shouldRejectTryAcquireWithoutRunLoop() {
        // Without the run loop, flag remains false so tryAcquire always returns false.
        // This is expected behavior - start() must be called first.
        var client = new TokenClient(createDefaultConfig());
        assertFalse(client.tryAcquire(10));
        assertFalse(client.tryAcquire(1));
    }

    @Test
    void shouldBeStoppable() {
        var client = new TokenClient(createDefaultConfig());
        assertTrue(client.isRunning());
        client.stop();
        assertFalse(client.isRunning());
    }
}

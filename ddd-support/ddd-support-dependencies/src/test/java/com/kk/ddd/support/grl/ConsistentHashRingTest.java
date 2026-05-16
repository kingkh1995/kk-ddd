package com.kk.ddd.support.grl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link ConsistentHashRing}. <br>
 *
 * @author kingk
 */
class ConsistentHashRingTest {

    @Test
    void shouldRebuildFromCsv() {
        var ring = new ConsistentHashRing();
        ring.rebuild("host1:8080,host2:8080,host3:8080");
        assertEquals(3, ring.serverCount());
    }

    @Test
    void shouldReturnSameServerForSameKey() {
        var ring = new ConsistentHashRing();
        ring.rebuild("host1:8080,host2:8080,host3:8080,host4:8080,host5:8080");
        String s1 = ring.findServer("key-001");
        String s2 = ring.findServer("key-001");
        assertEquals(s1, s2);
    }

    @Test
    void shouldDistributeKeysAcrossServers() {
        var ring = new ConsistentHashRing();
        ring.rebuild("host1:8080,host2:8080,host3:8080");
        // Use many keys to increase hit probability across servers
        java.util.Set<String> servers = new java.util.HashSet<>();
        for (int i = 0; i < 100; i++) {
            servers.add(ring.findServer("key-" + i));
        }
        // With 3 servers and 100 keys, we should see at least 2 different servers
        assertTrue(servers.size() >= 2,
                "100 keys should map to at least 2 different servers, got: " + servers);
    }

    @Test
    void shouldThrowOnEmptyRing() {
        var ring = new ConsistentHashRing();
        assertThrows(IllegalStateException.class, () -> ring.findServer("key"));
    }

    @Test
    void shouldHandleBlankOrNullCsvGracefully() {
        var ring = new ConsistentHashRing();
        ring.rebuild(null);
        ring.rebuild("");
        ring.rebuild("  ");
        // Ring remains empty, findServer should throw
        assertThrows(IllegalStateException.class, () -> ring.findServer("key"));
    }

    @Test
    void shouldReturnZeroServerCountOnEmptyRing() {
        var ring = new ConsistentHashRing();
        assertEquals(0, ring.serverCount());
    }

    @Test
    void shouldHandleSingleServer() {
        var ring = new ConsistentHashRing();
        ring.rebuild("host1:8080");
        assertEquals(1, ring.serverCount());
        assertEquals("host1:8080", ring.findServer("any-key"));
    }

    @Test
    void shouldBeDeterministicAfterMultipleRebuilds() {
        var ring = new ConsistentHashRing();
        ring.rebuild("host1:8080,host2:8080");

        String before = ring.findServer("test-key");

        // Rebuild with same CSV should produce same mapping
        ring.rebuild("host1:8080,host2:8080");
        String after = ring.findServer("test-key");

        assertEquals(before, after, "Same CSV should produce same mapping");
    }
}

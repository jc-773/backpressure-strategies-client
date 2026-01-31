package com.backpressure_strategies.bp_strategies.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.backpressure_strategies.bp_strategies.model.WebTraffic;

public class WebTrafficQueueTest {

    private WebTrafficDeque queue;

    private WebTrafficDeque queueMedium;

    @BeforeEach
    void setUp() {
        // queue = new WebTrafficDeque(2);
        queueMedium = new WebTrafficDeque(5);
    }

    @Test
    void verifyAccurateLoadFactor() {
        // Arrange
        WebTraffic event = new WebTraffic("testUrl", "1234", 17698260);
        WebTraffic event2 = new WebTraffic("testUrl", "1234", 17698260);
        // WebTraffic event3 = new WebTraffic("testUrl", "1234", 17698260);
        queue.insertHead(event);
        queue.insertHead(event2);
        // queue.insertHead(event3);
        var expected = 1.0;

        // Act
        var actual = queue.getQueueLoadFactor();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    void verifyWebEventNotAddedAtCapacity() {
        // Arrange
        WebTraffic event = new WebTraffic("testUrl", "1234", 17698260);
        WebTraffic event2 = new WebTraffic("testUrl", "1234", 17698260);
        WebTraffic event3 = new WebTraffic("testUrl", "1234", 17698260);
        queue.insertHead(event);
        queue.insertHead(event2);
        queue.insertHead(event3);
        var expected = 2;

        // Act
        var actual = queue.getLength();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    void verifyDrain() {
        // Arrange - fill up queue to the cap, even though default load factor is 75%
        WebTraffic event = new WebTraffic("testUrl", "1234", 17698260);
        WebTraffic event2 = new WebTraffic("testUrl", "1234", 17698260);
        WebTraffic event3 = new WebTraffic("testUrl", "1234", 17698260);
        WebTraffic event4 = new WebTraffic("testUrl", "1234", 17698260);
        WebTraffic event5 = new WebTraffic("testUrl", "1234", 17698260);

        queueMedium.insertHead(event);
        queueMedium.insertHead(event2);
        queueMedium.insertHead(event3);
        queueMedium.insertHead(event4);
        queueMedium.insertHead(event5);

        var expected = .4;

        // Act
        queueMedium.drain();

        // Assert
        var currentLf = queueMedium.getQueueLoadFactor();
        assertEquals(expected, currentLf);
    }
}

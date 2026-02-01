package com.backpressure_strategies.bp_strategies.common;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.backpressure_strategies.bp_strategies.model.WebTraffic;

/*
 * My own custom queue built as a secondary buffer for WebTraffic events
 */

public class WebTrafficDeque {
    // Setup
    private static Logger log = LoggerFactory.getLogger(WebTrafficDeque.class);
    private static final double LOAD_FACTOR = 0.75;
    // Node
    private Node head;
    private Node tail;
    private int length;
    private int queueCapacity;

    public WebTrafficDeque(int size) {
        // I'm creating a dummy node
        this.head = new Node(new WebTraffic("dummy", "dummy", 1));
        this.tail = new Node(new WebTraffic("dummy", "dummy", 1));
        this.head.next = tail;
        this.tail.prev = head;
        this.length = 0;
        this.queueCapacity = size;
    }

    public synchronized WebTraffic get(int index) {
        if (index < 0 || index >= this.length)
            throw new IndexOutOfBoundsException("index exceeds capacity of queue");
        var node = head.next;
        var current = 0;
        while (node != null) {
            if (index == current) {
                return node.event;
            }
            current++;
            node = node.next;
        }
        return null;
    }

    public synchronized void insertHead(WebTraffic event) {
        if (isQueueAtCapacity()) {
            return;
        }
        var lf = getQueueLoadFactor();
        if (lf < LOAD_FACTOR) {
            var node = new Node(event);
            node.next = head.next;
            head.next = node;
            if (node.next == null)
                tail = node;
            this.length++;
        }
    }

    public synchronized void insertTail(WebTraffic event) {
        if (isQueueAtCapacity()) {
            return;
        }
        var lf = getQueueLoadFactor();
        if (lf < LOAD_FACTOR) {
            tail.next = new Node(event);
            tail = tail.next;
            this.length++;
        }
    }

    // removes and returns the last node
    public synchronized WebTraffic pop() {
        if (isEmpty())
            throw new IllegalStateException("Queue is empty");
        var node = tail.prev;
        var replaceNode = node.prev;
        replaceNode.next = tail;
        tail.prev = replaceNode;
        return node.event;
    }

    // removes but does not return a node
    public synchronized boolean remove(int index) {
        var node = head;
        var current = 0;
        while (node != null && current < index) {
            current++;
            node = node.next;
        }

        if (node != null && node.next != null) {
            if (node.next == tail)
                tail = node;
            node.next = node.next.next;
            this.length--;
            return true;
        }
        return false;
    }

    public synchronized List<WebTraffic> drain() {
        var listOfDrainedEvents = new ArrayList<WebTraffic>();
        while (!isEmpty()) {
            var event = pop();
            log.info("Draining event: {}", event);
            listOfDrainedEvents.add(event);
        }
        return listOfDrainedEvents;
    }

    /*
     * drains until the below the threshold of the default load factor of 75%
     * removed events are added into a queue source
     */

    public synchronized ArrayList<WebTraffic> getValues() {
        var node = head.next;
        var list = new ArrayList<WebTraffic>();
        while (node != null) {
            list.add(node.event);
            node = node.next;
        }
        return list;
    }

    // determines when to continue adding back items to the queue
    public synchronized double getQueueLoadFactor() {
        return (double) this.length / this.queueCapacity;
    }

    public int getLength() {
        return this.length;
    }

    public boolean isEmpty() {
        return this.head.next == this.tail;
    }

    private boolean isQueueAtCapacity() {
        return this.length == this.queueCapacity;
    }

}

class Node {
    WebTraffic event;
    Node next;
    Node prev;

    public Node(WebTraffic event) {
        this.event = event;
    }
}

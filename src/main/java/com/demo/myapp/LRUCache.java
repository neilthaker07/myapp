package com.demo.myapp;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

class Node {
    int key;
    int val;
    Node next;
    Node prev;
    Node(int key, int val) {
        this.key = key;
        this.val = val;
    }
}

class LRUCache {

    private static final int DEFAULT_SHARD_COUNT = 16;

    private final Shard[] shards;
    private final int shardMask;

    public LRUCache(int capacity) {
        int shardCount = Integer.highestOneBit(Math.clamp(capacity, 1, DEFAULT_SHARD_COUNT));
        this.shardMask = shardCount - 1;
        this.shards = new Shard[shardCount];
        int perShardCapacity = Math.max(1, capacity / shardCount);
        for (int i = 0; i < shardCount; i++) {
            shards[i] = new Shard(perShardCapacity);
        }
    }

    public int get(int key) {
        return shardFor(key).get(key);
    }

    public void put(int key, int val) {
        shardFor(key).put(key, val);
    }

    private Shard shardFor(int key) {
        return shards[spread(Integer.hashCode(key)) & shardMask];
    }

    // Supplemental hash spreading (same idea ConcurrentHashMap uses) so keys
    // with a poorly-distributed hashCode don't cluster onto a few shards.
    private static int spread(int h) {
        return (h ^ (h >>> 16)) & 0x7fffffff;
    }

    /**
     * An independent LRU cache: its own map, its own doubly linked list, its
     * own lock. Threads routed to different shards never contend with each
     * other; eviction and recency ordering are only exact within a shard, not
     * across the whole cache.
     */
    private static class Shard {
        private final Map<Integer, Node> map;
        private final Node first;
        private final Node last;
        private final int capacity;
        private final ReentrantLock lock = new ReentrantLock();

        Shard(int capacity) {
            this.map = new HashMap<>();
            this.capacity = capacity;
            first = new Node(-1, -1);
            last = new Node(-1, -1);
            first.next = last;
            last.prev = first;
        }

        int get(int key) {
            lock.lock();
            try {
                if (!map.containsKey(key)) {
                    return -1;
                }
                Node node = map.get(key);
                remove(node);

                Node newNode = new Node(key, node.val);
                insertAtFirst(newNode);

                map.put(key, newNode);
                return newNode.val;
            } finally {
                lock.unlock();
            }
        }

        void put(int key, int val) {
            lock.lock();
            try {
                if (map.containsKey(key)) {
                    Node node = map.get(key);
                    remove(node);

                    Node newNode = new Node(key, val);
                    insertAtFirst(newNode);

                    map.put(key, newNode);
                } else {
                    if (capacity == map.size()) {
                        Node lastNode = last.prev;
                        map.remove(lastNode.key);
                        remove(lastNode);
                    }
                    Node newNode = new Node(key, val);
                    insertAtFirst(newNode);

                    map.put(key, newNode);
                }
            } finally {
                lock.unlock();
            }
        }

        private void insertAtFirst(Node node) {
            Node tmp = first.next;
            first.next = node;
            node.next = tmp;
            node.prev = first;
            tmp.prev = node;
        }

        private void remove(Node node) {
            Node prev = node.prev;
            Node next = node.next;
            prev.next = next;
            next.prev = prev;
        }
    }
}

/**
 * Your LRUCache object will be instantiated and called as such:
 * LRUCache obj = new LRUCache(capacity);
 * int param_1 = obj.get(key);
 * obj.put(key,value);
 */

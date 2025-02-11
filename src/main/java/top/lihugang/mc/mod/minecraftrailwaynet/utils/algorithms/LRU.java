package top.lihugang.mc.mod.minecraftrailwaynet.utils.algorithms;

import java.util.*;

public class LRU<K, V> {
    Map<K, Pair<V, ListIterator<K>>> hashmap = new HashMap<>();
    List<K> list = new LinkedList<>();

    public boolean has(K key) {
        return hashmap.containsKey(key);
    }

    public V get(K key) {
        return hashmap.get(key).first;
    }

    public void put(K key, V value) {
        if (this.has(key)) {
            hashmap.get(key).second.remove();
        }
        list.addFirst(key);
        hashmap.put(key, new Pair<>(value, list.listIterator()));
    }

    public Pair<K, V> remove() {
        K key = list.getLast();
        Pair<V, ListIterator<K>> data = hashmap.get(key);
        V value = data.first;
        data.second.remove();
        hashmap.remove(key);
        return new Pair<>(key, value);
    }

    public boolean empty() {
        return hashmap.isEmpty();
    }

    public List<Pair<K, V>> getAllData() {
        ArrayList<Pair<K, V>> result = new ArrayList<>();
        for (Map.Entry<K, Pair<V, ListIterator<K>>> entry : hashmap.entrySet()) {
            result.add(new Pair<>(entry.getKey(), entry.getValue().first));
        }
        return result;
    }

    public void clear() {
        hashmap.clear();
        list.clear();
    }
}

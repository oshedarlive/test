/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package radhey.util;

import java.util.Map.Entry;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 *
 * @author hoshi
 */
public class SimpleMapIgnoreCase<K,V> implements java.util.Map<K,V> {
    List<K> keys;
    List<K> uppercaseKeys;
    List<V> values;
    Set<K> keyset;
    public SimpleMapIgnoreCase(){
        keys=new ArrayList<K>();
        uppercaseKeys=new ArrayList<K>();
        values=new ArrayList<V>();
    }

    public SimpleMapIgnoreCase(int initialCapacity){
        keys=new ArrayList<K>(initialCapacity);
        uppercaseKeys=new ArrayList<K>(initialCapacity);
        values=new ArrayList<V>(initialCapacity);
    }
    public int size() {
        return keys.size();
    }

    public boolean isEmpty() {
        return keys.isEmpty();
    }

    public boolean containsKey(Object key) {
        if(key instanceof String)
            key=((String)key).toUpperCase();
        return uppercaseKeys.contains(key);
    }

    public boolean containsValue(Object value) {
        return values.contains(value);
    }

    public V get(Object key) {
        if(key instanceof String)
            key=((String)key).toUpperCase();
        int index=uppercaseKeys.indexOf(key);
        if(index>=0)
            return values.get(index);
        return null;
    }

    public V put(K key, V value) {
        K uppercaseKey=key;
        if(uppercaseKey instanceof String)
            uppercaseKey=(K)((String)uppercaseKey).toUpperCase();
        int index=uppercaseKeys.indexOf(uppercaseKey);
        if(index>=0)
            return values.set(index, value);
        keys.add(key);
        uppercaseKeys.add(uppercaseKey);
        values.add(value);
        keyset=null;
        return null;
    }

    public K getKey(Object value){
        int index=values.indexOf(value);
        if(index>=0)
            return keys.get(index);
        return null;
    }

    public V remove(Object key) {
        if(key instanceof String)
            key=((String)key).toUpperCase();
        int index;
        index=uppercaseKeys.indexOf(key);
        if(index>=0){
            keys.remove(index);
            uppercaseKeys.remove(index);
            keyset=null;
            return values.remove(index);
        }
        return null;
    }

    public void putAll(Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void clear() {
        keys.clear();
        uppercaseKeys.clear();
        values.clear();
        keyset=null;
    }

    public Set<K> keySet() {
        if(keyset==null)
            keyset=new CopyOnWriteArraySet<K>(keys);
        return keyset;
    }

    public Collection<V> values() {
        return values;
    }

    public Set<Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}

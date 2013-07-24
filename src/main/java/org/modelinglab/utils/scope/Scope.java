/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.modelinglab.utils.scope;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author gortiz
 */
public class Scope<K,V> implements IScope<K,V> {

    final private LinkedList<Map<K,V>> scopes;
    final private LinkedList<Map<K,V>> seen;
    
    public Scope() {
        scopes = new LinkedList<Map<K, V>>();
        scopes.addFirst(new HashMap<K, V>());
        
        seen = new LinkedList<Map<K, V>>();
    }
    
    public Scope(int size) {
        scopes = new LinkedList<Map<K, V>>();
        scopes.addFirst(new HashMap<K, V>(size));
        
        seen = new LinkedList<Map<K, V>>();
    }
    
    private void undig() {
        Map<K,V> scope = seen.pollLast();
        while(scope != null) {
            scopes.addLast(scope);
            scope = seen.pollLast();
        }
    }
        
    private void dig(int scopeLevel) {
        final int diff = scopes.size() - scopeLevel - 1;
        if(diff < 0) {
            throw new IllegalArgumentException("Wrong scope level.");
        }
        for (int i = 0; i < diff; i++) {
            seen.addLast(scopes.removeLast());
        }
    }

    @Override
    public V set(K name, V value, int scopeLevel) {
        return scopes.get(scopeLevel).put(name, value);
    }
    
    @Override
    public V put(K name, V value) {
        for(Map<K, V> scope : scopes) {
            if(scope.containsKey(name)) {
                return scope.put(name, value);
            }
        }
        return scopes.getLast().put(name, value);
    }

    @Override
    public boolean containsKey(K key) {
        for(Map<K, V> scope : scopes) {
            if(scope.containsKey(key))
                return true;
        }
        return false;
    }

    @Override
    public Set<K> getAllKeys(int scopeLevel) {
        dig(scopeLevel);
        Set<K> vars = scopes.getLast().keySet();
        undig();
        
        return vars;
    }
    
    @Override
    public Set<K> getAllKeys() {
        Set<K> vars = new HashSet<K>();
        for(Map<K,V> scope : scopes) {
            vars.addAll(scope.keySet());
        }
        return vars;
    }

    @Override
    public V get(K key) {
        for(Map<K, V> scope : scopes) {
            if(scope.containsKey(key))
                return scope.get(key);
        }
        return null;
    }

    @Override
    public V get(K key, int scopeLevel) {
        dig(scopeLevel);
        V value = get(key);
        undig();
        
        return value;
    }
    

    @Override
    public void clear() {
        while(scopes.size() > 1) {
            scopes.getLast().clear();
            scopes.removeLast();
        }
        scopes.getLast().clear();
    }

    @Override
    public int addScope() {
        scopes.add(new HashMap<K, V>());
        return scopes.size() - 1;
    }

    @Override
    public void removeScope() {
        if(scopes.size() == 1) {
            throw new IllegalStateException("This context is empty.");
        }
        
        scopes.removeLast();
    }

    @Override
    public Scope<K, V> clone() {
        Scope<K, V> clon = new Scope<K, V>(scopes.getFirst().size());
        clon.scopes.getLast().putAll(this.scopes.getLast());
        boolean first = true;
        
        for(Map<K, V> scope : scopes) {
            if(first) {
                first = false;
            }
            else {
                clon.addScope();
                clon.scopes.getLast().putAll(scope);
            }
        }
        return clon;
    }

    @Override
    public int getScopeCounter() {
        return scopes.size();
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.modelinglab.utils.scope;

import java.io.Serializable;
import java.util.Set;

/**
 *
 * @author gortiz
 */
public interface IScope<K,V> extends Serializable, Cloneable {

    public V get(K key);
    public V get(K key, int scopeLevel);
    
    public boolean containsKey(K key);
    
    public Set<K> getAllKeys();
    public Set<K> getAllKeys(int scope);
    
    public V put(K key, V value);
    public V set(K key, V value, int scopeLevel);
    
    public void clear();
    
    public IScope<K, V> clone();
    
    public int addScope();
    public void removeScope();
    
    public int getScopeCounter();
}

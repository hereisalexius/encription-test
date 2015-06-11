/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafiles;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.List;
import org.jdesktop.observablecollections.ObservableCollections;

/**
 *
 * @author hereisalexius
 */
public class KeysSet {

    private List<String> keys;

    public KeysSet() {
        this.keys = ObservableCollections.observableList(Arrays.asList(new String[]{"", "", "", ""}));
    }

    public KeysSet(String... keys) {
        if (keys.length != 4) {
            throw new IllegalArgumentException("Arrays size should be 4!");
        }
        this.keys = ObservableCollections.observableList(Arrays.asList(keys));

    }

    public List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        if (keys.size() != 4) {
            throw new IllegalArgumentException("Arrays size should be 4!");
        }
        this.keys = ObservableCollections.observableList(keys);
    }

//    private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
//
//    public void addPropertyChangeListener(PropertyChangeListener listener) {
//        changeSupport.addPropertyChangeListener(listener);
//    }
//
//    public void removePropertyChangeListener(PropertyChangeListener listener) {
//        changeSupport.removePropertyChangeListener(listener);
//    }

}

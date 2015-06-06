/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafiles;

/**
 *
 * @author hereisalexius
 */
public class KeysSet {

    private String[] keys;

    public KeysSet(String... keys) {
        if (keys.length != 4) {
            throw new IllegalArgumentException("Arrays size should be 4!");
        }
        this.keys = keys;

    }

    public String[] getKeys() {
        return keys;
    }

    public void setKeys(String[] keys) {
        if (keys.length != 4) {
            throw new IllegalArgumentException("Arrays size should be 4!");
        }
        this.keys = keys;
    }

}

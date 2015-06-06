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
public class UnsignedInteger {

    private int normalValue;

    public UnsignedInteger() {
        this.normalValue = 0;
    }

    public UnsignedInteger(int normalValue) {
        this.normalValue = normalValue;
    }

    public int getNormalValue() {
        return normalValue;
    }

    public void setNormalValue(int normalValue) {
        this.normalValue = normalValue;
    }

    public long getUnsignedValue() {
        return normalValue & 0xffffffffl;
    }

    @Override
    public String toString() {
        return String.valueOf(normalValue & 0xffffffffl);
    }

}

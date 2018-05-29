/*
 * Реализация параметра любого типа
 * (av, 21.12.2017)
*/
package com.tvcom.telematic;

import java.util.*;
import com.tvcom.telematics.commons.Param;

public class ParamImpl implements Param {
    private Object value;
    private Date time;

    public ParamImpl() {
    }

    public ParamImpl(Object value, Date time) {
        this.value = value;
        this.time = time;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public Date getTime() {
        return time;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}

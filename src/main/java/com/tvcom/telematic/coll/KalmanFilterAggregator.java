/*
 * Простейшая реализация фильтра Калмана (см. habrahabr.ru/post/140274/)
 * Alex Tues (RSM)
 * Versions: 08.11.2017
 *           22.12.2017
 *           16.04.2018
 *           24.05.2018
*/
package com.tvcom.telematic.coll;

import java.util.*;

import org.apache.commons.lang3.ArrayUtils;

import com.tvcom.telematics.commons.Param;
import com.tvcom.telematics.commons.api.SensorValuesAggregator;
import com.tvcom.telematics.commons.api.annotation.AggregatorService;
import com.tvcom.telematics.commons.exc.AbstractTelematicServerException;
import com.tvcom.telematic.ParamImpl;

@AggregatorService("kalman")
public class KalmanFilterAggregator implements SensorValuesAggregator{
    // Коэффициенты для расчета фильтра Калмана
    private static final double INITIAL_COVARIANCE = 0.10;
    private static final double F = 1.00,
                                Q = 2.00,
                                H = 1.00,
                                R = 15.00;
    private double              state      = 0.00,
                                covariance = 0.00;

    // Наложить фильтр Калмана на массив значений
    @Override
    public Object aggregate(Param[] params) throws AbstractTelematicServerException {
        // 0. Предварительная проверка
        if(params == null || params.length == 0) {
            return new ParamImpl[0];
        }
        
        /*
        for(int i = 0; i < params.length; i++) {
            System.out.println(params[i].getValue() == null);
        }
        */

        // 1. Исключить из входного массива null-элементы
        Param[] pure = removeNulls(params);

        // 2. Окончательная проверка
        if(pure == null || pure.length == 0) {
            return new ParamImpl[0];
        }

        // 3. Извлечь из параметра числовые значения
        double[] val = new double[pure.length];
        for(int i = 0; i < pure.length; i++) {
            val[i] = (Double)pure[i].getValue();
        }

        // 4. Применить фильтр Калмана
        double[] res = filtration(val);

        // 5. Дополнить результат временным штампом
        ParamImpl[] responce = new ParamImpl[pure.length];
        for(int i = 0; i < pure.length; i++) {
            ParamImpl p = new ParamImpl();
            p.setValue(res[i]);
            p.setTime(pure[i].getTime());
            responce[i] = p;
        }
        
        // 6. Вернуть результат
        return responce;
    }

    // Исключить из входного массива null-элементы
    private Param[] removeNulls(Param[] p) {
        List<Param> list = new ArrayList<>();
        
        // Сформировать список без null-элементов
        for(int i = 0; i < p.length; i++) {
            if(p[i].getValue() != null) list.add(p[i]);
        }
        return (list.isEmpty() ? null : list.toArray(new Param[0]));
    }

    // Инициализировать
    private void setState(double state, double covariance) {
        this.state = state;
        this.covariance = covariance;
    }

    // Подогнать
    private void correct(double data) {
        double x0 = F * state;
        double p0 = F * covariance * F + Q;

        double k = H * p0 / (H * p0 * H + R);
        state = x0 + k * (data - H * x0);
        covariance = (1 - k * H) * p0;
    }

    // И наконец-то...
    private double[] filtration(double[] values){
        if(values.length == 0) return values;

        List<Double> filtered = new ArrayList<>();
        setState(values[0], INITIAL_COVARIANCE);
        for (int i = 0; i < values.length; i++) {
            correct(values[i]);
            filtered.add(Math.round(state*100)/100.0d);
        }
        return ArrayUtils.toPrimitive(filtered.toArray(new Double[0]));
    }
}

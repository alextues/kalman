package com.tvcom.telematic;

import java.io.*;
import java.text.*;

import org.junit.*;
import org.json.simple.*;
import org.json.simple.parser.*;

import com.tvcom.telematic.coll.KalmanFilterAggregator;
import com.tvcom.telematics.commons.exc.AbstractTelematicServerException;

public class KalmanFilterAggregatorTest {

    //@Ignore
    @Test
    public void testNumAggregator() throws AbstractTelematicServerException {
        // Тестовый JSON-файл
        final String PATH_TO_JSON = "./src/test/resources/j.json";
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssSSS");

        // Параметры для передачи методу collect
        ParamImpl[] params = null;

        // Обработать JSON-файл
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;
        try {
            jsonObject = (JSONObject) jsonParser.parse(new FileReader(PATH_TO_JSON));
            JSONArray jsonArray = (JSONArray) jsonObject.get("fuel");

            int size = jsonArray.size();
            // Собрать параметры в массив
            if(size > 0) {
                params = new ParamImpl[size];
                for (int i = 0; i < size; i++) {
                    
                    JSONObject jsonRow = (JSONObject)jsonArray.get(i);
                    // Сырые данные из json
                    if(jsonRow.containsKey("value")) {
                        double value = Double.valueOf(jsonRow.get("value").toString());
                        //System.out.println("VALUE=" + value);
                        // Очередной элемент массива параметров (значение и дата + время)
                        ParamImpl p = new ParamImpl();
                        p.setValue(value);
                        p.setTime(sdf.parse(String.valueOf(jsonRow.get("time"))));
                        params[i] = p;
                    } else {
                        //System.out.println("NO VALUE=" + value);
                        // Очередной элемент массива параметров (значение и дата + время)
                        ParamImpl p = new ParamImpl();
                        p.setValue(null);
                        p.setTime(sdf.parse(String.valueOf(jsonRow.get("time"))));
                        params[i] = p;
                    }
                }
            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }

        // Наложить фильтр
        if(params != null) {
            System.out.println("Input array length=" + params.length);
            KalmanFilterAggregator nag = new KalmanFilterAggregator();
            Object res = nag.aggregate(params);

            // Тестовая распечатка
            Object [] obj = (Object[]) res;
            for(int i = 0; i < obj.length; i++) {
                ParamImpl p = (ParamImpl)obj[i];
                System.out.printf("[%6d] %s -> %6.2f%n", i, p.getTime(), p.getValue());
            }
            System.out.println("Output array length=" + obj.length);
        }
    }
}

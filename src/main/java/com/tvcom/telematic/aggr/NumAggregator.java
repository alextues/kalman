package com.tvcom.telematic.aggr;

import com.tvcom.telematics.commons.Param;
import com.tvcom.telematics.commons.api.SensorValuesAggregator;
import com.tvcom.telematics.commons.api.annotation.AggregatorService;
import com.tvcom.telematics.commons.exc.AbstractTelematicServerException;

@AggregatorService("num")
public class NumAggregator implements SensorValuesAggregator{
    public Object aggregate(Param[] params) throws AbstractTelematicServerException {
        return 0;
    }
}

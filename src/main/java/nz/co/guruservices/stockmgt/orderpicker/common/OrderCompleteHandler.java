package nz.co.guruservices.stockmgt.orderpicker.common;

import nz.co.guruservices.stockmgt.orderpicker.model.Order;

public interface OrderCompleteHandler {
    void complete(Order order);
}

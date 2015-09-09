package nz.co.guruservices.stockmgt.orderpicker.common;

import nz.co.guruservices.stockmgt.orderpicker.model.Order;
import nz.co.guruservices.stockmgt.orderpicker.model.OrderStatus;

public interface OrderHandler {
    void process(Order order, OrderStatus newStatus);
}

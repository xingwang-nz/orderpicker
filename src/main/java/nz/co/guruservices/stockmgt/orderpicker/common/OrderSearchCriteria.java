package nz.co.guruservices.stockmgt.orderpicker.common;

import java.util.ArrayList;
import java.util.List;

import nz.co.guruservices.stockmgt.orderpicker.model.OrderStatus;

public class OrderSearchCriteria {

    private String username;

    private List<OrderStatus> statusList = new ArrayList<>();

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username == null ? "" : username.trim();
    }

    public void addSearchStatus(final OrderStatus orderStatus) {
        statusList.add(orderStatus);
    }

    public List<OrderStatus> getStatusList() {
        return statusList;
    }

    public void setStatusList(final List<OrderStatus> statusList) {
        this.statusList = statusList;
    }

}

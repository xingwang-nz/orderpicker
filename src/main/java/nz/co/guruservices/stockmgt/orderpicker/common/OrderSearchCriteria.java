package nz.co.guruservices.stockmgt.orderpicker.common;

public class OrderSearchCriteria {

    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username == null ? "" : username.trim();
    }

}

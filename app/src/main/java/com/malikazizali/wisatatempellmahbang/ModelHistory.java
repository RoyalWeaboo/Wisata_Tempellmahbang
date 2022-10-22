package com.malikazizali.wisatatempellmahbang;

public class ModelHistory {
    String id,total,status;

    public ModelHistory(String id, String total, String status) {
        this.id = id;
        this.total = total;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getTotal() {
        return total;
    }

    public String getStatus() {
        return status;
    }
}

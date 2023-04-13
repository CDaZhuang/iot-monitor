package org.cdaz.representation.entity;

public class Operation {
    public static final int TYPE_ADD = 0;
    public static final int TYPE_REMOVE = 1;
    private int type;
    private String clientId;

    public Operation(int type, String clientId) {
        this.type = type;
        this.clientId = clientId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}

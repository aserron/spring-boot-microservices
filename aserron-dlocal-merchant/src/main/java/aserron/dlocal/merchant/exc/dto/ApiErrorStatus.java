package aserron.dlocal.merchant.exc.dto;

import java.io.Serializable;

public class ApiErrorStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    private int code;
    private String name;
    private String reason;

    public ApiErrorStatus() {
    }

    public ApiErrorStatus(int code, String name, String reason) {
        this.code = code;
        this.name = name;
        this.reason = reason;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}

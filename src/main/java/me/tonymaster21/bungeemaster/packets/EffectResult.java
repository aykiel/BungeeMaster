package me.tonymaster21.bungeemaster.packets;

import java.io.Serializable;

/**
 * @author Andrew Tran
 */
public class EffectResult implements Serializable{
    private static final long serialVersionUID = 8199629052744350162L;
    private Object object;
    private boolean success;
    private String error;
    private Throwable throwable;

    public EffectResult(boolean success) {
        this.success = success;
    }

    public EffectResult(boolean success, String error) {
        this.success = success;
        this.error = error;
    }

    public EffectResult(boolean success, String error, Throwable throwable) {
        this.success = success;
        this.error = error;
        this.throwable = throwable;
    }

    public EffectResult(boolean success, Throwable throwable) {
        this.success = success;
        this.throwable = throwable;
    }

    public EffectResult(boolean success, Object object) {
        this.success = success;
        this.object = object;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }
}

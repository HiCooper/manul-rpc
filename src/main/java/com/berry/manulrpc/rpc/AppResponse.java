package com.berry.manulrpc.rpc;

import lombok.Data;

import java.lang.reflect.Field;

/**
 * Created with IntelliJ IDEA.
 *
 * @author HiCooper.
 * @version 1.0
 * @date 2020/12/4
 * fileName：AppResponse
 * Use：
 */
@Data
public class AppResponse implements Result {

    private Object result;

    private Throwable exception;

    @Override
    public Object recreate() throws Throwable {
        if (exception != null) {
            try {
                // get Throwable class
                Class<?> clazz = exception.getClass();
                while (!clazz.getName().equals(Throwable.class.getName())) {
                    clazz = clazz.getSuperclass();
                }
                // get stackTrace value
                Field stackTraceField = clazz.getDeclaredField("stackTrace");
                stackTraceField.setAccessible(true);
                Object stackTrace = stackTraceField.get(exception);
                if (stackTrace == null) {
                    exception.setStackTrace(new StackTraceElement[0]);
                }
            } catch (Exception e) {
                // ignore
            }
            throw exception;
        }
        return result;
    }
}

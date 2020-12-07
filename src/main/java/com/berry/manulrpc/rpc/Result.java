package com.berry.manulrpc.rpc;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 *
 * @author HiCooper.
 * @version 1.0
 * @date 2020/12/4
 * fileName：Result
 * Use：
 */
public interface Result extends Serializable {
    Object recreate() throws Throwable;
}

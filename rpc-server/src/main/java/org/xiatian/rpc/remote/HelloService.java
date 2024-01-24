package org.xiatian.rpc.remote;

import org.xiatian.rpc.remote.dto.HelloObject;

/**
 * 测试用api的接口
 */
public interface HelloService {

    String hello(HelloObject object);

}

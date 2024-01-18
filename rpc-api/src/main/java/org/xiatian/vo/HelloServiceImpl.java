package org.xiatian.vo;

import java.util.logging.Logger;


public class HelloServiceImpl implements HelloService {
    private static final Logger log = Logger.getLogger(HelloServiceImpl.class.getName());

    @Override
    public String hello(HelloObject object) {
        log.info("接收到："+object.getMessage());
        return "这是掉用的返回值，id=" + object.getId();
    }
}

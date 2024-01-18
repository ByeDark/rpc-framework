package org.xiatian.service;

public interface CommonSerializer {

    byte[] serialize(Object obj) throws Exception;

    Object deserialize(byte[] bytes, Class<?> clazz) throws Exception;

    int getCode();

    static CommonSerializer getByCode(int code) {
        switch (code) {
            case 1:
                return new JsonSerializer();
            case 0:
                return new KryoSerializer();
            default:
                return null;
        }
    }
}

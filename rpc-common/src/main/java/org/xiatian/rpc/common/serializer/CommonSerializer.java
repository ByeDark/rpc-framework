package org.xiatian.rpc.common.serializer;

/**
 * 通用的序列化反序列化接口
 */
public interface CommonSerializer {

    Integer KRYO_SERIALIZER = 0;
    Integer JSON_SERIALIZER = 1;
    Integer HESSIAN_SERIALIZER = 2;

    Integer DEFAULT_SERIALIZER = KRYO_SERIALIZER;

    static CommonSerializer getByCode(int code) {
        return switch (code) {
            case 0 -> new KryoSerializer();
            case 1 -> new JsonSerializer();
            case 2 -> new HessianSerializer();
            default -> null;
        };
    }

    byte[] serialize(Object obj);

    Object deserialize(byte[] bytes, Class<?> clazz);

    int getCode();
}

package org.xiatian.rpc.common.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xiatian.rpc.common.enumeration.PackageType;
import org.xiatian.rpc.common.enumeration.RpcError;
import org.xiatian.rpc.common.exception.RpcException;
import org.xiatian.rpc.common.serializer.CommonSerializer;
import org.xiatian.rpc.entity.RpcRequest;
import org.xiatian.rpc.entity.RpcResponse;

import java.util.List;

/**
 * 通用的解码拦截器
 */
public class CommonDecoder extends ReplayingDecoder {

    private static final Logger logger = LoggerFactory.getLogger(CommonDecoder.class);
    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //先读取4个字节
        int magic = in.readInt();
        if (magic != MAGIC_NUMBER) {
            logger.error("不识别的协议包: {}", magic);
            throw new RpcException(RpcError.UNKNOWN_PROTOCOL);
        }
        //再读取4个字节，判断包类型
        int packageCode = in.readInt();
        Class<?> packageClass;
        if (packageCode == PackageType.REQUEST_PACK.getCode()) {
            packageClass = RpcRequest.class;
        } else if (packageCode == PackageType.RESPONSE_PACK.getCode()) {
            packageClass = RpcResponse.class;
        } else {
            logger.error("不识别的数据包: {}", packageCode);
            throw new RpcException(RpcError.UNKNOWN_PACKAGE_TYPE);
        }
        //判断使用的是哪个序列化器
        int serializerCode = in.readInt();
        //得到具体的序列化器
        CommonSerializer serializer = CommonSerializer.getByCode(serializerCode);
        if (serializer == null) {
            logger.error("不识别的反序列化器: {}", serializerCode);
            throw new RpcException(RpcError.UNKNOWN_SERIALIZER);
        }
        //得到数据包的长度，可以防止粘包
        int length = in.readInt();
        byte[] bytes = new byte[length];
        //读取数据
        in.readBytes(bytes);
        //将数据进行反序列化成对象
        Object obj = serializer.deserialize(bytes, packageClass);
        //放到输出队列里
        out.add(obj);
    }
}

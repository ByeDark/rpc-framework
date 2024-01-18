package org.xiatian.service;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.xiatian.dto.RpcRequest;
import org.xiatian.dto.RpcResponse;
import org.xiatian.enumeration.PackageType;

import java.util.List;
import java.util.logging.Logger;

public class CommonDecoder extends ReplayingDecoder {

    private static final Logger logger = Logger.getLogger(CommonDecoder.class.getName());
    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int magic = in.readInt();
        if(magic != MAGIC_NUMBER) {
            logger.info("不识别的协议包: "+ magic);
            throw new Exception();
        }
        int packageCode = in.readInt();
        Class<?> packageClass;
        if(packageCode == PackageType.REQUEST_PACK.getCode()) {
            packageClass = RpcRequest.class;
        } else if(packageCode == PackageType.RESPONSE_PACK.getCode()) {
            packageClass = RpcResponse.class;
        } else {
            logger.info("不识别的数据包: "+ packageCode);
            throw new Exception();
        }
        int serializerCode = in.readInt();
        CommonSerializer serializer = CommonSerializer.getByCode(serializerCode);
        if(serializer == null) {
            logger.info("不识别的反序列化器: "+serializerCode);
            throw new Exception();
        }
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes);
        Object obj = serializer.deserialize(bytes, packageClass);
        out.add(obj);
    }
}

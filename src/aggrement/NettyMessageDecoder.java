package aggrement;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
/*
 * 消息解码类
 * LengthFieldBasedFrameDecoder支持自动的TCP粘包和半包处理，只需要给出标识消息长度的字段偏移量
 * 和消息长度自身所占的字节数，netty就能自动实现对半包的处理
 * 对于业务解码器来说，调用父类LengthFieldBasedFrameDecoder的解码方法后，返回的就是整包消息或者为空，
 * 如果为空说明是个半包消息，直接返回继续由I/O线程读取后续的码流
 */
public class NettyMessageDecoder extends 
   LengthFieldBasedFrameDecoder{
	
	MarshallingDecoder marshallingDecoder;
	
	public NettyMessageDecoder(int maxFrameLength,int lengthFieldOffset,
			int lengthFieldLength) throws IOException{
		
		super(maxFrameLength,lengthFieldOffset,lengthFieldLength);
		marshallingDecoder=new MarshallingDecoder();
	}
	
	@Override
	protected Object decode(ChannelHandlerContext ctx,ByteBuf in) throws Exception {
		ByteBuf frame=(ByteBuf) super.decode(ctx,in);
		if(frame==null){
			return null;
		}
		NettyMessage message=new NettyMessage();
		Header header=new Header();
		header.setCrcCode(in.readInt));
		header.setLength(in.readLong());
		header.setSessionID(in.readLong());
		header.setType(in.readByte());
		header.setPriority(in.readByte());
		
		int size=in.readerInt();
		if(size>0){
			Map<String,Object> attch=new HashMap<String,Object>(size);
			int keySize=0;
			byte[] keyArray=null;
			String key=null;
			for(int i=0;i<size;i++){
				keySize=in.readInt();
				keyArray=new byte[keySize];
				in.readBytes(keyArray);
				key=new String(keyArray,"UTF-8");
				attch.put(key,marshallingDecoder.decode(in));
			}
			keyArray=null;
			key=null;
			header.setAttachment(attch);
		}
		
		if(in.readableBytes()>4){
			message.setBody(marshallingDecoder.decode(in));
			
		}
		message.setHeader(header);
		return message;
	}

}

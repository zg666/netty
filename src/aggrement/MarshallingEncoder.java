package aggrement;

import java.io.IOException;

import org.jboss.marshalling.Marshaller;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.marshalling.MarshallingDecoder;
/*
 * 消息解码类
 */
public class MarshallingEncoder {
	
	private static final byte[] LENGTH_PLACEHOLDER=new byte[4];
	Marshaller marshaller;
	
	public MarshallingEncoder() throws IOException{
		marshaller=MarshallingCodeFactory.buildMarshalling();
		
	}
	
	protected void encode(Object msg,ByteBuf out) throws Exception{
		try {
			int lengthPos=out.writeIndex();
			out.writeBytes(LENGTH_PLACEHOLDER);
			ChannelBufferByteOutput output=new ChannelBufferByteOutput(out);
			marshaller.start(output);
			marshaller.writeObject(msg);
			marshaller.finish();
			out.setInt(lengthPos,out.writerIndex()-lengthPos-4);
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			marshaller.close();
		}
		
		public class NettyMessageDecoder extends LengthFieldBasedFrameDecoder {
			MarshallingDecoder marshallingDecoder;
			
			public NettyMessageDecoder(int maxFrameLength,int lenthFieldOffset,
					int lenthFieldLength) throws IOException {
				super(maxFrameLength,lengthFieldOffset,lengthFieldLength);
				marshallingDecoder=new MarshallingDecoder();
			}
		}
		
		protected Object decode(ChannelHandlerContext ctx,
				ByteBuf in) throws Exception {
			ByteBuf frame=(ByteBuf) super.decode(ctx,in);
			if(frame==null){
				return null;
			}
			NettyMessage message=new NettyMessage();
			Header header=new Header();
			header.setCrcCode(in.readerInt());
			header.setLength(in.readerInt());
			header.setSessionID(in.readLong());
			header.setType(in.readByte());
			header.setPriority(in.readByte());
			
			int size=in.readInt();
			if(size>0){
				Map<String,Object> attach=new HashMap<String,Object>(size);
				int keySize=0;
				byte[] keyArray=null;
				String key=null;
				for(int i=0;i<size;i++){
					keySize=in.readInt();
					keyArray=new byte[keySize];
					in.readBytes(keyArray);
					key=new String(keyArray,"UTF-8");
					attach.put(key,marshallingDecoder.decode(in));
				}
				keyArray=null;
				key=null;
				header.setAttachment(attach);
			}
			keyArray=null;
			key=null;
			header.setAttachment(attach);
		}
		if(in.readableBytes()>4){
			message.setBody(marshallingDecoder.decode(in));
		}
		message.setHeader(header);
		return message;
	
	}
}

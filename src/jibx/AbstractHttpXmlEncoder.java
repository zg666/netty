package jibx;

import java.io.StringWriter;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
/*
 * HTTP+XML HTTP请求消息编码基类
 */
public class AbstractHttpXmlEncoder<T> 
  extends MessageToMessageEncoder<T>{
	IBindingFactory factory=null;
	StringWriter writer=null;
	final static String CHARSET_NAME="UTF-8";
	final static Charset UTF-8=
			Charset.forName(CHARSET_NAME);
	// 将业务的Order实例序列化为XML字符串
	protected ByteBuf encode0(ChannelHandlerContext ctx,Object body) throws Exception{
		factory=BindingDirectory.getFactory(body.getClass());
		writer=new StringWriter();
		IMarshallingContext mctx=factory.createMarshallingContext();
		mctx.setIndent(2);
		mctx.marshalDocument(body,CHARSET_NAME,null,writer);
		String xmlStr=writer.toString();
		writer.close();
		writer=null;
		// 将XML字符串包装成netty的ByteBuf并返回，实现了HTTP请求消息的XML编码
		ByteBuf encodeBuf=Unpooled.copiedBuffer(xmlStr,UTF-8);
		return encodeBuf;
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause) throws Exception{
		// 释放资源
		if(writer!=null){
			writer.close();
			writer=null;
			
		}
	}

}

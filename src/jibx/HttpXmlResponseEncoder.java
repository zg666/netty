package jibx;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
/*
 * HTTP+XML 应答消息编码类
 */
public class HttpXmlResponseEncoder 
    extends AbstractHttpXmlEncoder<HttpXmlResponse>{
      
	protected void encode(ChannelHandlerContext ctx,
			HttpXmlResponse msg,List<Object> out
			) throws Exception{
		ByteBuf body=encode0(ctx,msg.getResult());
		FullHttpResponse response=msg.getHttpResponse();
		// 对应答消息进行判断，如果业务侧已经构造了HTTP应答消息
		// 则利用业务已有应答消息重新复制一个新的HTTP应答消息
		if(response==null){
			response=new DefaultFullHttpResponse(HTTP_1_1,OK,body);
		} else{
			response=new DefaultFullHttpResponse(msg.getHttpResponse()
					.getProtocolVersion(),msg.getHttpResponse().getStatus(),body);
		}
		// 设置消息体的内容格式为"text/xml"，然后在消息头中设置消息体的长度
		response.headers().set(CONTENT_TYPE,"text/xml");
		setContentLength(response,body,readableBytes);
		// 把编码后的DefaultFullHttpResponse对象添加到编码结果列表中
		// 由后续Netty的HTTP编码类进行二次编码
		out.add(response);
	}
	}

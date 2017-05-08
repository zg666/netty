package jibx;

import java.net.InetAddress;
import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
/*
 * HTTP+XML HTTP请求消息编码类
 * 
 */
public class HttpXmlRequestEncoder 
      extends AbstarctHttpXmlEncoder<HttpXmlRequest>{
	
    @Override
    protected void encode(ChannelHandlerContext ctx,HttpXmlRequest msg,List<Object> out) 
    throws Exception{
    	// 调用父类的encode0，将业务需要发送的POJO对象Order实例通过jibx序列化为XML字符串
    	// 随后将它封装成netty的bytebuf
    	ByteBuf body=encode0(ctx,msg.getBody());
    	FullHttpRequest request=msg.getRequest();
    	// 对消息头进行判断，如果业务侧自定义和定制了消息头，则使用业务侧设置的HTTP消息头
    	// 如果业务侧没有设置，则构造新的HTTP消息头
    	if(request==null){
    		// 构造和设置默认的HTTP消息头，由于通常情况下HTTP通信双方更关注消息体本身，
    		// 所以这里采用了硬编码方式，如果要产品化，可以做成XML配置文件，允许业务自定义配置,
    		// 以提升定制的灵活性
    		request=new DefaultFullHttpRequest(HttpVersion.HTTP_1_1,HttpMethod.GET,"/do",body);
    		HttpHeaders headers=request.headers();
    		headers.set(HttpHeaders.Names.HOST,InetAddress.getLocalHost().getHostAddress());
    		headers.set(HttpHeaders.Names.CONNECTION,HttpHeaders.Value.CLOSE);
    		headers.set(HttpHeaders.Names.ACCEPT_ENCODING,HttpHeaders.Value.GZIP.toString()+','+
    				HttpHeaders.Value.DEFLATE.toString());
    		headers.set(HttpHeaders.Names.ACCEPT_CHARSET,"ISO-8859,utf-8,q=0.7,*;q=0.7");
    		headers.set(HttpHeaders.Names.USER_AGENT,"Netty xml Http Client side");
    		headers.set(HttpHeaders.Names.ACCEPT,"text/html,application/xhtml+xml,application/xml;q=0.9;q=0.8");
    	}
    	// 由于请求消息消息体不为空，也没有chunk方式，所以在HTTP消息头中设置消息体的长度conent-length
    	// 完成消息体的XML序列化后将重新构造的HTTP请求消息加入到out中
    	// 由后续netty的http请求编码器继续对HTTP请求消息进行编码
    	HttpHeaders.setContentLength(request,body.eradableBytes());
    	out.add(request);
}

}

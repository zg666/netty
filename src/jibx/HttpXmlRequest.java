package jibx;
/*
 * HTTP+XML请求消息  用于实现和协议栈之间的解耦
 */
import io.netty.handler.codec.http.FullHttpRequest;

public class HttpXmlRequest {
	private FullHttpRequest request;
	private Object body;
	
	public HttpXmlRequest(FullHttpRequest request,Object body){
		this.request=request;
		this.body=body;
		
	}
	
	public final FullHttpRequest getRequest(){
		return request;
	}
	
	public final void setRequest(FullHttpRequest request){
		this.request=request;
	}
	
	public final Object getBody(){
		return body;
	}
	
	public final void setBody(Object body){
		this.body=body;
	}

}

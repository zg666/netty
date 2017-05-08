package aggrement;

import com.sun.xml.internal.ws.api.message.Header;

public class NettyMessage {
	
	private Header header;  // 消息头
	private Object body; // 消息体
	
	public final Header getHeader(){
		return header;
	}
	
	public final void setHeader(Header header) {
		this.header=header;
	}
	
	public final Object getBody(){
		return body;
	}
    
	public final void setBody(Object body) {
		this.body=body;
	}
	
	
	public String toString(){
		return "NettyMessage [header="+header+"]";
	}
	
}

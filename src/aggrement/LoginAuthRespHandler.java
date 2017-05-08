package aggrement;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.awt.TrayIcon.MessageType;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/*
 * 服务端的握手接入和安全认证
 */
public class LoginAuthRespHandler extends ChannelHandlerAdapter{
    
	private Map<String,Boolean> nodeCheck=new ConcurrentHashMap<String,Boolean>();
	// 分别定义了重复登录保护和IP认证的白名单列表，主要用于提升握手的可靠性
	private String[] whitekList={"127.0.0.1","192.168.1.104"};
	
	@Override
	public void channelRead(ChannelHandlerContext ctx,Object msg) throws Exception {
		NettyMessage message=(NettyMessage) msg;
		
		/*
		 * 用于接入认证，首先根据客户端的原地址(/127.0.0.1:12088)进行重复登录判断
		 * 如果客户端已经登录成功，拒绝重复登录，以防止由于客户端重复登录导致的句柄泄漏
		 * 
		 */
		// 如果是握手请求消息,处理，其他消息透传
		if(message.getHeader()!=null&&message.getHeader().getType()==MessageType.LOGIN_REQ.value)) {
			String nodeIndex=ctx.channel().remoteAddress().toString();
			NettyMessage loginResp=null;
			// 重复登录，拒绝
			if(nodeCheck.containsKey(nodeIndex)) {
				loginResp=buildResponse((byte)-1);
			}  else{
				// 通过ChannelHandlerContext的Channel接口获取客户端的
				//InetSocketAddress地址，从中取得发送方的原地址信息，通过原地址进行白名单校验
				// 校验通过握手成功，否则握手失败，
				InetSocketAddress address=(InetSocketAddress) ctx.channel().remoteAddress();
				String ip=address.getAddress().getHostAddress();
				boolean isOK=false;
				for(String WIP:whiteList){
					if(WIP.equals(ip)){
						isIK=true;
						break;
					}
				}
				// 通过buildResponse构造握手应答消息返回客户端
				loginResp=isOK?buildResponse((byte) 0):buildResponse((byte) -1);
				if(isOK)
					nodeCheck.put(nodeIndex,true);
			}
			System.out.println("The login response is:"+loginResp+"body["+loginResp.getBody()+"]");
			ctx.writeAndFlush(loginResp);
			
		} else {
			ctx.fireChannelRead(msg);
		}
	}
	
	private NettyMessage buildResponse(byte result){
		NettyMessage messge=new NettyMessage();
		Header header=new Header();
		header.setType(MessageType.LOGIN_RESP.value());
		message.setHeader(header);
		message.setBody(result);
		return message;
	}
	
	public void exceptionCautht(ChannelHandlerContext ctx,Throwable cause) throws Exception{
		nodeCheck.remove(ctx.channel().remoteAddress().toString());
		ctx.close();
		ctx.fireExceptionCautht(cause);
	}
}

package aggrement;

import java.awt.TrayIcon.MessageType;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
/*
 * 握手认证客户端，用于在通道激活时发起握手请求
 */
public class LoginAuthReqHandler extends ChannelHandlerAdapter{
	
	/*
	 * 当客户端跟服务端TCP三次握手成功之后，由客户端构造握手请求消息发送给服务端
	 * 由于采用IP白名单认证机制，因此，不需要携带消息体，消息体为空，消息类型为3
	 * 握手请求消息，握手请求发送之后，按照协议规范，服务端需要返回握手应答消息
	 * (non-Javadoc)
	 * @see io.netty.channel.ChannelHandlerAdapter#channelActive(io.netty.channel.ChannelHandlerContext)
	 */
	public void channelActive(ChannelHandlerContext ctx) throws Exception{
		ctx.writeAndFlush(buildLoginReq());
	}
     
	/*
	 * 对握手应答消息进行处理，首先判断消息是否是握手应答消息
	 * 如果不是，直接透传给后面的ChannelHandler进行处理；首先判断是握手应答消息
	 * 对应答结果进行判断
	 * (non-Javadoc)
	 * @see io.netty.channel.ChannelHandlerAdapter#channelRead(io.netty.channel.ChannelHandlerContext, java.lang.Object)
	 */
	public void channelRead(ChannelHandlerContext ctx,Object msg) throws Exception{
		NettyMessage message=(NettyMessage) msg;
		// 如果是握手应答消息，需要判断是否认证成功
		if(message.getHeader()!=null)&&
		message.getHeader().getType()==MessageType.LOGIN_RESP.value()){
			byte loginResult=(byte) message.getBody();
			if(loginResult!=(byte) 0) {
				// 握手失败,关闭连接
				ctx.close();
			} else {
				System.out.println("Login is ok:"+message);
				ctx.fireChannelRead(msg);
			}
		}  else
			ctx.fireChannelRead(msg);
	}
		
	private NettyMessage buildLoginReq(){
		NettyMessage message=new NettyMessage();
		Header header=new Header();
		header.setType(MessageType.LOGIN_REQ.value());
		message.setHeader(header);
		return message;
	}
	
	public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause) throws Exception{
		ctx.fireExceptionCaught(cause);
	}
	
	
	
	
}

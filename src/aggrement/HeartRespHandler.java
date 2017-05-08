package aggrement;

import java.awt.TrayIcon.MessageType;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
/*
 * 服务端心跳应答
 */
public class HeartRespHandler extends ChannelHandlerAdapter{
	@Override
	public void channelRead(ChannelHandlerContext ctx,Object msg) throws Exception{
		NettyMessage message=(NettyMessage) msg;
		// 返回心跳应答消息
		if(message.getHeader()!=null&&message.getHeader().getType()==MessageType.HEARTBEAT_REQ.value()) {
			System.out.println("Receive client heart beat message :--->"+message);
			ctx.writeAndFlush(heartBeat);
		} else 
			ctx.fireChannelRead(msg);
	}
	 private NettyMessage buildHeatBeat() {
		 NettyMessage message=new NettyMessage();
		 Header header=new Header();
		 header.setType(header);
		 message.setHeader(header);
		 return message;
	}

}

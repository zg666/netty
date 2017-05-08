package aggrement;

import io.netty.channel.ChannelHandlerContext;

/*
 * 服务端的心跳应答
 */
public class HeartBeatRespHandler extends ChannelHandlerAdapter{
	/*
	 * 服务端的心跳Handler非常简单，接收到心跳请求消息之后，构造心跳应答消息返回，
	 * 并打印接收和发送的心跳消息
	 * 
	 *心跳超时的实现非常简单，直接利用Netty的ReadTimeoutHandler机制，当一定周期内
	 *(默认值50s)没有读取到对方任何消息时，需要主动关闭链路。如果是客户端，重新发起连接；
	 * 如果是服务器，释放资源，清除客户端登录缓存信息，等待服务端重连
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx,Object msg) throws Exception{
		NettyMessage message=(NettyMessage) msg;
		// 返回心跳应答消息
		if(message.getHeader()!=null&&message.getHeader().getType()==MessageType.HEARTBEAT_REQ.value()) {
			System.out.println("Receive client heart beat message:-->"+message);
			NettyMessage heartBeat=buildHeatBeat();
			System.out.println("Send beat response message to client:--->"+heatBeat);
			ctx.writeAndFlush(heartBeat);
		} else{
			ctx.fireChannelRead(msg);
		}
		private NettyMessage buildHeatBeat() {
			NettyMessage message=new NettyMessage();
			Header header=new Header();
			header.setType(MessageType.HEARTBEAT_RESP.value());
			message.setHeader(header);
			return message;
		}
	}

}

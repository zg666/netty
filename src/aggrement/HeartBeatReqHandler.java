package aggrement;

import java.awt.TrayIcon.MessageType;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.ScheduledFuture;

/*
 * 客户端发送心跳消息
 */
public class HeartBeatReqHandler extends ChannelHandlerAdapter{
	
	private volatile ScheduledFuture<?> heartBeat;
	
	@Override
	public void channelRead(ChannelHandlerContext ctx,Object msg) throws Exception {
		NettyMessage message=(NettyMessage) msg;
		// 握手成功，主动发送心跳消息
		/*
		 * 握手成功之后，握手请求handler会继续将握手成功消息向下透传，
		 * heartBeatReqHandler接收到之后对消息进行判断，如果是握手成功消息，
		 * 则启动无限循环定时器用于定期发送心跳消息，由于NioEventLoop是一个schedule，
		 * 因此它支持定时器的执行。
		 * 心跳定时器的单位的单位是毫秒，默认为5000，即每5秒发送一条心跳消息
		 */
		if(message.getHeader()!=null&&message.getHeader().getType()==MessageType.LOGIN_RESP.value()){
			heartBeat=ctx.executor().scheduleAtFixedRate(
					new HeartBeatReqHander.HeartBeatTask(ctx),0,5000,TimeUnit.MILLISECONDS);
		} else if(message.getHeader()!=null&&
				message.getHeader().getType()==MessageType.HEARTBEAT_RESP.value()){
			System.out.println("Client receive server heart beat message:--->"+message);
		
		} else 
			ctx.fireChannelRead(msg);
	}
	
	@Override
	public void run(){
		NettyMessage heatbeat=buildHeatBeat();
		System.out.println("Client send heart beat message to server:--->"+heatBeat);
		ctx.writeAndFlush(heatBeat);
	}
	
	private NettyMessage buildHeatBeat(){
		NettyMessage message=new NettyMessage();
		Header header=new Header();
		header.setType(MessageType.HEARTBEAT_REQ.value());
		message.setHeader(header);
		return message;
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause) throws Exception{
		if(heartBeat!=null){
			heartBeat.cancel(true);
			heartBeat=null;
		}
		ctx.fireExceptionCaught(cause);
	}

}

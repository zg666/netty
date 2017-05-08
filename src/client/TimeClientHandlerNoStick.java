package client;

import java.util.logging.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
/*
 * 利用LineBasedFrameDecoder和StringDecoder解决TCP粘包问题
 */

public class TimeClientHandlerNoStick extends ChannelHandlerAdapter{

	
	private static final Logger logger=Logger.getLogger(TimeClientHandler.class.getName());
	private int counter;
	private byte[] req;
	
	public TimeClientHandlerNoStick(){
		req=("QUERY TIME ORDER"+System.getProperty("line.separator")).getBytes();
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx){
		ByteBuf message=null;
		for(int i=0;i<100;i++){
			message=Unpooled.buffer(req.length);
			message.writeBytes(req);
			ctx.writeAndFlush(message);
		}
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx,Object msg) throws Exception{
		// 此时msg已经是解码成字符串之后的应答消息
		String body=(String) msg;
		System.out.println("Now is:"+body+";the counter is"+ ++counter);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
		// 释放资源
		logger.warning("Unexpected exception from downstream:"+cause.getMessage());
		ctx.close();
	}
}

package server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
/*
 * 利用LineBasedFrameDecoder解决TCP粘包问题
 */
public class TimeServerHandlerNoStick extends ChannelHandlerAdapter{
      
	private int counter;
	@Override
	public void channelRead(ChannelHandlerContext ctx,Object msg) throws Exception{
		// 可以发现接收到的msg就是删除回车换行符后的请求消息，
		// 不需要额外考虑处理读半包问题,也不需要对请求消息进行编码
	String body=(String) msg;
  System.out.println("The time server receive order:"+body+";the counter is"+ ++counter);
		String currentTime="QUERY TIME ORDER".equalsIgnoreCase(body)?
				new java.util.Date(System.currentTimeMillis()).toString():"BAD ORDER";
			ByteBuf resp=Unpooled.copiedBuffer(currentTime.getBytes());	
	  
			ctx.writeAndFlush(resp);
	}

	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
		ctx.close();
	}
}

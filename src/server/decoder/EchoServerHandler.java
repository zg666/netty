package server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
/*
 * 利用LineBasedFrameDecoder解决TCP粘包问题
 */
public class EchoServerHandler extends ChannelHandlerAdapter{
      
	int counter =0;
	@Override
	public void channelRead(ChannelHandlerContext ctx,Object msg) throws Exception{
		
		String body=(String) msg;
		System.out.println("This is"+ ++counter +"times receive client: ["+body+"]" );
		body+="$_";
			ByteBuf echo=Unpooled.copiedBuffer(body.getBytes());	
			ctx.writeAndFlush(echo);
	}

	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
		cause.printStackTrace();
		ctx.close();  // 发生异常，关闭链路
	}
}

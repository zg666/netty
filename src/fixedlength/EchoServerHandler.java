package fixedlength;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
/*
 * 打印读取的消息
 */
public class EchoServerHandler extends ChannelHandlerAdapter{
          
	@Override
	public void channelRead(ChannelHandlerContext ctx,Object msg) throws Exception{
		System.out.println("Receive client:["+msg+"]");
		
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
		cause.printStackTrace();
		ctx.close();  // 发生异常，关闭链路
	}
}

package decoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
/*
 * 利用DelimiterBasedFrameDecoder完成以分隔符作为码流结束的标识
 */
public class EchoServerHandler extends ChannelHandlerAdapter{
      
	int counter =0;
	@Override
	public void channelRead(ChannelHandlerContext ctx,Object msg) throws Exception{
		
		String body=(String) msg;
		System.out.println("This is"+ ++counter +"times receive client: ["+body+"]" );
		// 由于设置DelimiterBasedFrameDecoder过滤掉了分隔符，
		// 所以返回给客户端时需要在请求消息尾部加上"$_",最后创建ByteBuf，将原始消息重新返回给客户端
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

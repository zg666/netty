package server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
/*
 * 对网络事件进行读写操作
 */
public class TimeServerHandler extends ChannelHandlerAdapter{
      
	@Override
	public void channelRead(ChannelHandlerContext ctx,Object msg) throws Exception{
		// ByteBuf类似于JDK中的java.nio.ByteBuffer对象
		ByteBuf buf=(ByteBuf) msg;
		// readableBytes方法可以获取缓冲区可读的字节数
		byte[] req=new byte[buf.readableBytes()];
		buf.readBytes(req);
		String body=new String(req,"UTF-8");
		System.out.println("The time server receive order:"+body);
		String currentTime="QUERY TIME ORDER".equalsIgnoreCase(body)?
				new java.util.Date(System.currentTimeMillis()).toString():"BAD ORDER";
			ByteBuf resp=Unpooled.copiedBuffer(currentTime.getBytes());	
			// 异步发送应答消息给客户端，把待发送的消息放到发送缓冲数组里
			ctx.write(resp);
	}
	/*
	 * 从性能角度考虑，为了防止频繁地唤醒Selector进行消息发送,
	 * Netty的write方法并不直接将消息写入SocketChannel中，
	 * 调用write方法只是把待发送的消息放到发送缓冲数组中，
	 * 再调用flush方法，将发送缓冲区中的消息全部写到SocketChannel中
	 * (non-Javadoc)
	 * @see io.netty.channel.ChannelHandlerAdapter#channelReadComplete(io.netty.channel.ChannelHandlerContext)
	 */
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception{
		// 将发送队列中的消息写入到SocketChannel中发送给对方
		ctx.flush();
		
	}
	
	/*当发生异常时，关闭ChannelHandlerContext，释放和ChannelHandlerContext相关联的句柄等资源
	 * (non-Javadoc)
	 * @see io.netty.channel.ChannelHandlerAdapter#exceptionCaught(io.netty.channel.ChannelHandlerContext, java.lang.Throwable)
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
		ctx.close();
	}
}

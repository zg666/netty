package client;

import java.util.logging.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;



public class TimeClientHandler extends ChannelHandlerAdapter{

	
	private static final Logger logger=Logger.getLogger(TimeClientHandler.class.getName());
	private final ByteBuf firstMessage;
	
	public TimeClientHandler(){
		byte[] req="QUERY TIME ORDER".getBytes();
		firstMessage=Unpooled.buffer(req.length);
		firstMessage.writeBytes(req);
	}
	
	/*
	 * 当客户端和服务端TCP链路建立成功之后，
	 * Netty的NIO线程会调用channelActive方法，发送查询时间的指令给服务端
	 * (non-Javadoc)
	 * @see io.netty.lActive(channel.ChannelHandlerAdapter#channeio.netty.channel.ChannelHandlerContext)
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx){
		// 将请求消息发送给服务端
		ctx.writeAndFlush(firstMessage);
	}
	
	/*
	 * 当服务端返回应答消息时，channelRead方法被调用
	 * (non-Javadoc)
	 * @see io.netty.channel.ChannelHandlerAdapter#channelRead(io.netty.channel.ChannelHandlerContext, java.lang.Object)
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx,Object msg) throws Exception{
		// 从Netty的ByteBuf中读取并打印应答消息
		ByteBuf buf=(ByteBuf) msg;
		byte[] req=new byte[buf.readableBytes()];
		buf.readBytes(req);
		String body=new String(req,"UTF-8");
		System.out.println("Now is:"+body);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
		// 释放资源
		logger.warning("Unexpected exception from downstream:"+cause.getMessage());
		ctx.close();
	}
}

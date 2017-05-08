package udp;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.ThreadLocalRandom;

public class ChineseProverbServerHandler
  extends SimpleChannelInboundHandler<DatagramPacket>{
     
	private static final String[] DICTIONARY={"Netty权威指南1","Netty权威指南2"};
	
	private String nextQuote(){
		// 由于chineseproverbserverhandler存在多线程并发操作的可能，所以使用了netty的线程安全随机类ThreadLocalRandom
		int quotedId=ThreadLocalRandom.current().nextInt(DICTIONARY.length);
		return DICTIONARY[quoteId];
	}
	
	/*
	 * netty对udp进行了封装，因此，接收到的是netty封装后的io.netty.channel.socket.datagrampacket对象
	 */
	@Override
	public void messageReceived(ChannelHandlerContext ctx,DatagramPacket packet) throws Exception{
		// 将packet内容转换为字符串（利用bytebuf的toString(Charset)方法）
		// 然后对请求消息进行合法性判断：如果是“谚语字典查询?”,则构造应答消息返回
		String req=packet.content().toString();
		System.out.println(req);
	
	
	if("netty".equals(req)){
		// datagrampacket有2个参数：1.需要发送的内容，为bytebuf,2.目的地址，包括ip和端口
		// 可以直接从发送的报文datagrampacket中获取
		ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer("netty："+nextQuote(),CharsetUtil.UTF-8)),packet.sender()));
		
	}
}    
	@Override 
	public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
		ctx.close();
		cause.printStackTrace();
	}
     
}

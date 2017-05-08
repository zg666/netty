package udp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

public class ChineseProverbClientHandler 
   extends SimpleChannelInboundHandler<DatagramPacket>{

    @Override
    public void messageReceived(ChannelHandlerContext ctx,DatagramPacket msg) throws Exception{
    	// 接收到服务端的消息之后将其转成字符串
    	String response=msg.content().toString(CharsetUtil.UTF_8);
    	// 如果是指定的字符串开头，说明没有发送丢包
    	if(response.startsWith("netty:")){
    		System.out.println(response);
    		ctx.close();
    	}
}    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause) throws Exception{
    	cause.printStackTrace();
    	ctx.close();
    }

}

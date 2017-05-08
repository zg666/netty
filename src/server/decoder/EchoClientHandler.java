package server.decoder;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class EchoClientHandler extends ChannelHandlerAdapter{
         private int counter;
         static final String ECHO_REQ="Welcome to Netty.$_";
         
         public EchoClientHandler(){
        	 
         }
         
         @Override
         public void channelActive(ChannelHandlerContext ctx){
        	 for(int i=0;i<10;i++){
        		 ctx.writeAndFlush(Unpooled.copiedBuffer(ECHO_REQ.getBytes()));
        	 }
         }
         
         public void channelRead(ChannelHandlerContext ctx,Object msg) throws Exception{
        	 ctx.flush();
         }
         
         @Override
         public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
        	 cause.printStackTrace();
        	 ctx.close();
         }
}

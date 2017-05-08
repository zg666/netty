package protobuf;

import java.util.ArrayList;
import java.util.List;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class SubReqClientHandler extends ChannelHandlerAdapter{
         
	public SubReqClientHandler(){
		
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx){
		for(int i=0;i<10;i++){
			ctx.write(subReq(i));
		}
		ctx.flush();
	}
	
	private SubscribeReqProto.SubscribeReq subReq(int i){
		SubscribeReqProto.SubscribeReq.Builder builder=SubscribeReqProto.SubscribeReq.newBuilder();
		builder.setSubReqID(i);
		builder.setUserName("zhengguang");
		builder.setProductName("Netty Book For Protobuf");
		List<String> address=new ArrayList<>();
		address.add("wuhan");
		address.add("shanghai");
		address.add("beijing");
		return builder.build();
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx,Object msg) throws Exception{
		System.out.println("Receive server response:["+msg+"]");
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception{
		ctx.flush();
	}
	
	@Override
	public void exceptionCaucht(ChannelHandlerContext ctx,Throwable cause){
		cause.printStackTrace();
		ctx.close();
	}
	
	
}

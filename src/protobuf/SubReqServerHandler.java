package protobuf;

public class SubReqServerHandler extends ChannelHandlerAdapter{
    
	@Override
	public void channelRead(ChannelHandlerContext ctx,Object msg) throws Exception{
		SubscribeReqProto.SubscribeReq req=(SubscribeReqProto.SubscribeReq) msg;
		if("zhengguang".equalsIgnoreCase(req.getUserName())) {
			System.out.println("Service accept client subscribe req:["+req.toString()+"]");
			ctx.writeAndFlush(req.getSubReqID()));
		}
	}
	
	private SubscribeRespProto.SubscribeResp resp(int subReqID) {
		SubscribeRespProto.SubscribeReq.Builder builder=SubscribeRespProto.SubscribeResp
				.newBuilder();
		builder.setSubReqID(subReqID);
		builder.setRespCode(0);
		builder.setDesc("Netty book order succeed,3 days later,sent "
				+ "to the desianated address");
		return builder.build();
	}
	
	@Override
	public viod exceptionCaught(ChannelHandlerContext ctx,Throwable ctx) {
		cause.printStackTrace();
		ctx.close(); // 发送异常，关闭链路
	}
}

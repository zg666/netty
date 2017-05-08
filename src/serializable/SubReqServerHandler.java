package serializable;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/*
 * 订购服务处理
 */
public class SubReqServerHandler extends ChannelHandlerAdapter{
    @Override
    public void channelRead(ChannelHandlerContext ctx,Object msg) throws Exception{
    	SubscribeReq req=(SubscribeReq) msg;
    	// 对订购者的用户名进行合法性校验，校验通过后打印订购请求消息，
    	// 构造订购成功应答消息立即发送给客户端
    	if("zhengguang".equalsIgnoreCase(req.getUserName())){
    		System.out.println("Service accept client subscribe req : ["+req.toString()+"]");
    		ctx.writeAndFlush(resp(req.getSubReqID()));
    	}
    }
    
    private SubscribeResp resp(int subReqID){
    	SubscribeResp resp=new SubscribeResp();
    	resp.setSubReqID(subReqID);
    	resp.setRespCode(0);
    	resp.setDesc("Netty book order succeed,3 days later"
    			+ ",sent to the designated address");
    	return resp;
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
    	cause.printStackTrace();
    	ctx.close(); // 发生异常，关闭链路
    }
    
    
}

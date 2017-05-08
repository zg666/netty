package jibx;

import com.sun.jndi.cosnaming.IiopUrl.Address;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

public class HttpXmlServerHandler extends
   SimpleChannelInboundHandler<HttpXmlRequest> {


     @Override
     public void messgeReceived(final ChannelHandlerContext ctx,HttpXmlRequest xmlRequest)
      throws Exception{
    	 HttpRequest request=xmlRequest.getRequest();
    	 Order order=(Order)xmlRequest.getBody();
    	 System.out.println("Http server receive request:"+order);
    	 dobusiness(order);
    	 ChannelFuture future=ctx.writeAndFlush(new HttpXmlResponse(null,order));
    	 if(!isKeepAlive(request)) {
    		 future.addListener(new GenericFutureListener<Future<? super void >> () {
    			 public void operationComplete(Future future) throws Exception{
    				 ctx.close();
    			 }
    		 }};
    	 }
        }

       private void dobusiness(Order order){
         order.getCustomer().setFirstName("郑");
         order.getCustomer.setLastName("广");
         List<String> midNames=new ArrayList<String>();
         midName.add("李元芳");
         order.getCustomer().setMiddleNames(midNames);
         Address address=order.getBillTo();
         address.setCity("武汉");
         address.setCountry("大道");
         address.setState("东阳大道");
         address.setPostCode("123");
         order.setBillTo(address);
         order.setShipTo(address);

       }
       
       @Override
       public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause) throws Exception{
    	   
    	   cause.printStackTrace();
    	   if(ctx.channel().isActive()){
    		   sendError(ctx,INTERNAL_SERVER_ERROR);
       }
       }
       
       private static void sendError(ChannelHandler ctx,HttpResponseStatus status){
    	   FullHttpResponse response=new DefaultFullHttpResponse
    			   (HTTP_1_1,status,Unpooled.copiedBuffer("失败:"+status.toString()+"\r\n",charsetUtil.UTF-8));
    	   response.headers().set(CONTENT_TYPE,"text/plain";charset=UTF-8);
    	   ctx.writeAndFlush(response).addListener(ChannelFutureListener).CLOSE;
       }
}

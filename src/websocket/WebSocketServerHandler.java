package websocket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.util.CharsetUtil;
import io.netty.util.ResourceLeakDetector.Level;

import com.sun.istack.internal.logging.Logger;

public class WebSocketServerHandler 
    extends SimpleChannel InboundHandler<Object>{
	 private static final Logger logger=Logger.getLogger(WebSocketServerHandler.class.getName());
	 
	 private WebSocketServerHandshaker handshaker;
	 
	 @Override
	 public void messageReceived(ChannelHandlerContext ctx,Object msg) throws Exception {
		 // 第一次握手请求消息由HTTP协议承载，所以它是一个HTTP消息，
		 // 执行handleHttpRequest方法来处理WebSocket握手请求
		 // 传统的HTTP接入
		 if(msg instanceof FullHttpRequest){
			 handleHttpRequest(ctx,(FullHttpRequest) msg);
		 }
		 // WebSocket接入
		 else if(msg instanceof WebSocketFrame){
			 handleWebSocketFrame(ctx,(WebSocketFrame) msg);
		 }
	 }
	 
	 @Override
	 public void channelReadComplete(ChannelHandlerContext ctx) throws Exception{
		 ctx.flush();
	 }
     
	 private void handleHttpRequest(ChannelHandlerContext ctx,FullHttpRequest req) throws Exception{
		  // 对握手请求消息进行判断，如果消息头中没有包含Upgrade字段或者值不是WebSocket,则返回400响应
		 // 如果HTTP解码失败，返回HTTP异常
		 if(!req.getDecoderResult().isSuccess()||(!"websocket".equals(req.headers().get("Upgrade")))){
			 sendHttpResponse(ctx,req,new DefaultFullHttpResponse(HTTP_1_1,BAD_REQUEST));
			 return;
		 } 
		 // 构造握手响应返回，本机测试
		 WebSocketServerHandshakeFactoty wsFactory =new 
				 WebSocketServerHandshakeFactoty("ws://localhost:8080/websocket",null,false);
		 if(handshaker==null){
			 WebSocketServerHandshakeFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel());
			 
		 } else{
			 handshaker.handshake(ctx.channel(),req);
		 }
	 }
	 /*
	  * 客户端通过文本框请求消息给服务端，websocketserverhandler接收到的是已经编码后
	  * 的websocketframe消息
	  */
	 private void handleWebSocketFrame(ChannelHandlerContext ctx,WebSocketFrame frame){
		
		 /*
		  * 对websocket请求消息进行处理，首先需要对控制帧进行判断
		  * 
		  * 关闭websocket连接，
		  */
		 // 判断是否是关闭链路的指令 
		 // 如果是关闭链路的控制消息，就调用websocketserverhandlshaker的close方法
		 if(frame instanceof CloseWebSocketFrame){
			 handshaker.close(ctx.channel()),(CloseWebSocketFrame) frame.retain());
			 return;
		 }
		 // 判断是否是Ping消息
		 // 如果是维持链路的ping消息，则构造ping消息返回
		 if(frame instanceof PingWebSocketFrame){
			 ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
			 return;
		 }
		 /*
		  * 由于本例程的websocket通信双方使用的都是文本消息，所以对请求消息的类型进行判断
		  */
		 // 本例程仅支持文本消息，不支持二进制消息
		 if(!(frame instanceof TextWebSocketFrame)) {
			 throw new UnsupportedOperationException(String.format("%s frame types not supported",frame.getClass().getName());
		 }
		 /*
		  * 从textwebsocket中获取请求消息字符串，对它处理后通过构造新的textwebsocketframe
		  * 消息返回给客户端，
		  */
		 // 返回应答消息
		 String request=((TextWebSocketFrame) frame).text();
		 if(logger.isLoggable(Level.FINE)) {
			 Logger.fine(String.format("%s received %s",ctx.channel(),request));
			 
		 }
		 // 由于握手应答时动态增加了textwebsocketframe的编码类
		 ctx.channel().write(
				 new TextWebSocketFrame(request+",欢迎使用Netty WebSocket服务，现在时刻:"+new java.uti.Date.toString()));
				 )
		
	 }
	 private static void sendHttpResponse(ChannelHandlerContext ctx,
			 FullHttpRequest req,FullHttpResponse res) {
		 // 返回应答给客户端
		 if(res.getStatus().code()!=200) {
			 ByteBuf buf=Unpooled.copiedBuffer(res.getSatus().toString(),CharsetUtil.UTF-8);
			 res.content().writeBytes(buf);
			 buf.release();
			 setContentLength(res,res.content().readableBytes());
			 
			 
		 }
		 // 如果是非Keep-Alive，关闭链接
		 ChannelFuture f=ctx.channel().waitAndFlush(res);
		 if(!isKeepAlive(req)||res.getStatus.code()!=200) {
			 f.addListener(ChannelFutureListener.CLOSE);
		 }
	 }
	 
	 @Override
	 public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause) throws Exception{
		 cause.printStackTrace();
		 ctx.close();
	 }
}

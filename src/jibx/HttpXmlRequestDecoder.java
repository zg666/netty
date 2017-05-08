package jibx;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.HttpResponseStatus;
/*
 * HTTP+XML http请求消息解码类
 */
public class HttpXmlRequestDecoder 
   extends AbstactHttpXmlDecode<FullHttpRequest>{
	
	public HttpXmlRequestDecoder(Class<?> clazz) {
		this(clazz,false);
	}
	// HttpXmlRequestDecoder有2个参数，分别为需要解码的对象的类型信息和
	// 是否打印HTTP消息体码流的码流开关，码流开关默认关闭
	public HttpXmlRequestDecoder(Class<?> clazz,boolean isPrint){
		super(clazz,isPrint);
	}
   
	@Override
	protected void decode(ChannelHandlerContext arg0,FullHttpRequest arg1,
			List<Object> arg2) throws Exception{
		// 对HTTP请求消息本身的解码结果进行判断，如果已经失败，二次解码就无意义
		if(!arg1.getDecodeResult().isSuccess()){
			sendError(arg0,BAD_REQUEST);
			return;
		}
		// 通过HttpXmlRequest和反序列化后的Order对象构造HttpXmlRequest实例，
		// 最后将它添加到解码结果list列表中
		HttpXmlRequest request=new HttpXmlRequest(arg1,decode0(arg0,arg1.content()));
		arg2.add(request);
	}
	
	/*
	 * 如果HTTP消息本身解码失败，则构造处理结果异常的HTTP应答消息返回给客户端。
	 * 作为演示程序，本例子没有考虑XML消息解码失败后的异常封装和处理，
	 * 在商用项目中需要统一的异常处理机制，提升协议栈的健壮性和可靠性
	 */
	private static void sentError(ChannelHandlerContext ctx,HttpResponseStatus status){
		FullHttpResponse response=new DefaultFullHttpResponse(HTTP_1_1,status,
				Unpooled.copiedBuffer("Failure:"+status.toString()+"\r\n",CharsetUtil.UTF_8));
		reponse.headers().set(CONTENT_TYPE,"text/plain,charset-UTF-8");
		ctx.writeAndFlush(reponse).addListener(ChannelFutureListener.CLOSE);
	}
}

package http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Pattern;

public class HttpFileServerHandler extends 
    SimpleChannelInboundHandler<FullHttpRequest>{
     private final String url;
     public HttpFileServerHandler(String url){
    	 this.url=url;
     }
     
     @Override
     public void messageReceived(ChannelHandlerContext ctx,
    		 FullHttpRequest request) throws Exception{
    	 // 对HTTP请求消息的解码结果进行判断，如果解码失败，直接构造HTTP400错误返回
    	 if(!request.getDecoderResult().isSucccess()){
    		 sendError(ctx,BAD_REQUEST);
    		 return;
    	 }
    	 // 判断请求行中的方法，如果不是GET，则构造HTTP405错误返回
    	 if(request.getMethod()!=GET){
    		 sendError(ctx,METHOD_NOT_ALLOWED);
    		 return;
    	 }
    	 final String url=request.getUri();
    	 final String path=sanitizeUri(uri);
    	 // 如果构造的URI不合法，则返回HTTP403错误
    	 if(path==null){
    		 sendError(ctx,FORBIDDEN);
    		 return;
    	 }
    	 // 使用新组装的URI路径构造File对象
    	 File file=new File(path);
    	 // 如果文件不存在或者是系统隐藏文件，则构造HTTP404异常返回
    	 if(file.isHidden()||!file.exists()){
    		 sendError(ctx,NOT_FOUND);
    		 return;
    	 }
    	 // 如果文件是目录，则发送目录的链接给客户端浏览器
    	 if(file.isDirectory()){
    		 if(uri.endsWith("/")){
    			 sendListing(ctx,file);
    		 } else{
    			 sendRedirect(ctx,uri+'/');
    		 }
    		 return;
    	 }
    	 if(!file.isFile()){
    		 sendError(ctx,FORBIDDEN);
    		 return;
    	 }
    	 RandomAccessFile randomAccessFile=null;
    	 try {
			randomAccessFile=new RandomAccessFile(file,"r"); // 以只读的方式打开文件
		} catch (FileNotFoundException e) {
			// TODO: handle exception
			sendError(ctx,NOT_FOUND);
			return;
		}
    	 // 获取文件的长度，构造成功的HTTP应答消息
    	 long fileLength=randomAccessFile.length();
    	 HttpResponse response=new DefaultHttpResponse(HTTP_1_1,OK);
    	 setContentLength(response,fileLength);
    	 //  在消息头设置contentlength和content type
    	 setContentTypeHeader(reponse,file);
    	 // 判断是否是keep-alive 如果是，则在应答消息头设置connection为keep-alive
    	  if(isKeepAlive(request)) {
    		  response.headers().set(CONNECTION,HttpHeaders.values.KEEP_ALIVE);
    		  
    	  }
    	  // 发送响应消息
    	  ctx.write(response);
    	  // 通过netty的chunkedfile对象直接将文件写入到发送缓冲区中
    	  ChannelFuture sendFileFuture;
    	  sendFileFuture=ctx.write(new ChunkedFile(randomAccessFile,0,fileLength,8192,
    			  ctx.newProgressivePromise()));
    	  // 为sendFileFuture增加GenericFutureListner
    	  sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
    		  @Override
    		  public void operationprogressed(ChannelProgressiveFuture future,
    				  long progress,long total) {
    			  if(total<0) {
    				  System.err.println("Transfer progress:"+progress);
    			  } else{
    				  System.err.println("Transfer progress:"+progress+"/"+total);
    			  }
    		  }
    		  // 如果发送完成，打印"Transfer complete"
    		  @Override
    		  public void operationComplete(ChannelProgressiveFuture future) throws Exception{
    			  System.out.println("Transfer complete:");
    		  }
    		  
    	  });
    	  // 如果使用chunked编码，最后需要发送一个编码结束的空消息体，将LastHttp的EMPLY_LAST_CONTENT发送到缓冲区
    	  // 标识所有的消息体已经发送完成，同时调用flush方法将之前在发送缓冲区的消息刷新到SocketChannel中发送给对方
    	  ChannelFuture lastContentFuture=ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
    	 // 如果是非keey-alive，最后一包消息发送完成之后，服务端要主动关闭连接
    	  if(!isKeepAlive(request)) {
    		  lastContentFuture.addListener(ChannelFutureListener.CLOSE);
    	  }
     }
          @Override
          public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause) throw Exception{
        	  cause.printStackTrace();
        	  if(ctx.channel().isActive()){
        		  sendError(ctx,INTERNAL_SERVER_ERROR);
        	  }
        	  }
          private static final Pattern INSECURE_URI=Pattern.compile("."[<>&\]."");
          private String sanitizeUri(String uri){
        	  try{
        		  // 解码
        		  uri=URLDecoder.decode(uri,"UTF-8");
        		  
        	  }catch(UnsupportedEncodingException e){
        		  try {
					uri=URLDecoder.decode(uri,"ISO-8859-1");
				} catch (Exception e2) {
					// TODO: handle exception
					throw new Error();
				}
        	  }
        	  // 解码成功后对URI进行合法性判断，如果URI与允许访问的URI一致或者是其子目录（文件）则校验通过，
        	  // 否则返回空
        	  if(!uri.startsWith(url)){
        		  return null;
        	  }
        	  if(!url.startsWith("/")) {
        		  return null;
        	  }
        	  // 将硬编码的文件路径分隔符替换为本地操作系统的文件路径分隔符
        	  uri=uri.replace('/', File.separatorChar);
        	  // 对新的URI作二次合法性校验，如果校验失败则返回空.
        	  if(uri.contains(File.separator+'.')||uri.contains('.'+File.separator)
        			  ||uri.startsWith(".")||uri.endsWith(".")||INSECURE_URI.matches(uri,matches)) {
        		  return null;
        	  }
        	  // 对文件进行拼接，使用当前运行程序所在的工程目录+URI构造绝对路径返回
        	  return System.getProperty("user.dir")+File.separator+uri;
     }
          private static final Pattern ALLOWED_FILE_NAME=Pattern.compile("[A-Za-z0-9][-_A-Za-z0-9\\.]*");
          
          private static void sendListing(ChannelHandlerContext ctx,File dir){
        	  // 创建成功的HTTP响应消息
        	  FullHttpResponse response=new DefaultFullHttpResponse(HTTP_1_1,OK);
        	  // 设置消息头类型为"text/html;charset=UTF-8"
        	  response.headers().set(CONTENT_TYPE,"text/html;charset-UTF-8");
        	  StringBuilder buf=new StringBuilder();
        	  String dirPath=dir.getPath();
        	  buf.append("<!DOCTYPE html>\r\n");
        	  buf.append("<html><head><title>");
        	  buf.append(dirPath);
        	  buf.append("目录：");
        	  buf.append("</title></head><body>\r\n");
        	  buf.append("<h3>");
        	  buf.append(dirPath).append("目录：");
        	  buf.append("</h3>\r\n");
        	  buf.append("<url>");
        	  buf.append("<li>链接：<a href=\"../\">..</a></li>\r\n");
        	  // 用于展示根目录下的所有文件和文件夹，同时使用超链接来标识
        	  for(File f:dir.listFiles()){
        		  if(f.isHidden()||!f.canRead()){
        			  continue;
        		  }
        	  }
        	  String name=f.getName();
        	  if(!ALLOWED_FILE_NAME.matcher(name).matches()) {
        		  continue;
        	  }
        	  buf.append("<li>链接:<a href=\"");
        	  buf.append(name);
        	  buf.append("\">");
        	  buf.append(name);
        	  buf.append("</a></li>\r\n");
          }
          buf.append("</ul></body></html>\r\n");
          // 分配消息的缓冲对象，
          ByteBuf buffer=Unpooled.copleBuffer(buf,CharsetUtil.UTF-8);
          
          response.content().writeBytes(buffer);
          // 释放缓冲区
          buffer.release();
          // 将缓冲区的响应消息发送到缓冲区并刷新到SocketChannel中
          ctx.writeAndFlush(reponse).addLisener(ChannelFutureLisener.CLOSE);
   }
      private static void sendRedirect(ChannelHandlerContext ctx,String newUri) {
    	  FullHttpResponse response=new DefaultFullHttpResponse(HTTP_1_1,FOUND);
    	  response.headers().set(LOCATION,newUri);
    	  ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    	  
}
      private static void sendError(ChannelHandlerContext ctx,HttpResponseStatus status){
    	  FullHttpResponse response=new DefaultFullHttpResponse
    			  (HTTP_1_1,status,Unpooled.copiedBuffer("Failure:"+status.toString()+"\r\n",CharsetUtil.UTF_8));
    	  response.headers().set(CONTENT_TYPE,"text/plain; charset-UTF-8");
    	  ctx.writeAndFlush(response).addLisener(ChannelFutureListener.CLOSE);
    	  
      }
      
      private static void setContentTypeHeader(HttpResponse response,File file){
    	  MimetypeFileTypeMap mimeTypeMap=new MimetypeFileTypeMap();
    	  response.headers().
    	  set(CONTENT_TYPE,mimeTypeMap.getContentType(file.getPaht));
      }
  }

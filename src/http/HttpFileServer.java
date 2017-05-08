package http;

import marshalling.SubReqClient;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
/*
 * HTTP服务端
 */
public class HttpFileServer {
	
	private static final String DEFAULT_URL="/src/com/phei/netty/";
	public void run(final int port,final Stinr url) throws Exception {
		EventLoopGroup bossGroup =new NioEventLoopGroup();
		EventLoopGroup workerGroup =new NioEventLoopGroup();
		try {
			ServerBootstrap b=new ServerBootstrap();
			b.group(bossGroup,workerGroup)
			.channel(NioServerSocketChannel.class)
			.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception{
					// 添加HTTP请求消息解码器
					ch.pipeline().addLast("http-decoder",new HttpRequestDecoder());
					// 添加HttpObjectAggregator解码器，作用是将多个消息转换为单一的FullHttpRequest或者FullHttpResponse
					// 原因是HTTP解码器在每个HTTP消息中会生成多个消息对象
					ch.pipeline().addLast("http-aggregator",new HttpObjectAggregator(65536));
				// 新增HTTP响应编码器，对HTTP响应消息进行编码
					ch.pipeline().addLast("http-encoder",new HttpRequestEncoder());
			    // 新增Chunked handler，主要作用是支持异步发送大的码流（例如大的文件传输），
					// 但不占用过多的内存，防止发生java内存溢出错误
					ch.pipeline().addLast("http-chunked",new ChunkedWriteHandler());
					// 添加HttpFileServerHandler,用于文件服务器的业务逻辑处理
					ch.pipeline().addLast("fileServerHandler",new HttpFileServerHandler(url));
				
				}
			});
			ChannelFuture future=b.bind("192.168.1.102",port).sync();
			System.out.println("http文件目录服务器启动，网址是："+http://192.168.1.102:+"+port+url);
	future.channel().closeFuture().sync();
		} catch (Exception e) {
			// TODO: handle exception
		} finally{
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
		
	}
	public static void main(String[] args) throws Exception{
		int port=8080;
		if(args!=null&&args.length>0){
			try {
				port=Integer.valueOf(args[0]);
				
			} catch (NumberFormatException e) {
				// TODO: handle exception
				e.printStackTrace();
				
			}
			
		}
		String url=DEFAULT_URL;
		if(args.length>1)
			url=args[1];
		new HttpFileServer().run(port,url);

		
	}

}

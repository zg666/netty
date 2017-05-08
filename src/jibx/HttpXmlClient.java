package jibx;

import java.net.InetSocketAddress;

import client.TimeClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpResponseDecoder;

public class HttpXmlClient {
	
	public void connect(int port) throws Exception{
		// 配置客户端NIO线程组
		EventLoopGroup group =new NioEventLoopGroup();
		try {
			Bootstrap b=new Bootstrap();
			b.group(group).channel(NioSocketChannel.class)
			.option(ChannelOption.TCP_NODELAY,true)
			.handler(new ChannelInitializer<SocketChannel>(){
				@Override
				public void initChannel(SocketChannel ch) throws Exception{
					// 将二进制码流解码成为HTTP的应答消息
					ch.pipeline().addLast("http-decoder",new HttpResponseDecoder());
					// 负责将1个HTTP请求消息的多个部分合并成一条完整的HTTP消息
					ch.pipeline().addLast("http-aggregator",new HttpObjectAggregator(65536));
					// XML解码器
					// 将前面开发的XML解码器HttpXmlResponseDecoder添加到ChannelPipeline中
					// 它有2个参数，分别是解码对象的类型信息和码流开关，这样就实现了HTTP+XML应答消息的自动解码
					ch.pipeline().addLast(
							"xml-decoder",
							new HttpXmlResponseDecoder(Order.class,true);
					// 将HttpRequestEncoder解码器添加到ChannelPipeline中时，需要注意顺序，
				   // 编码的时候是按照从尾到头的顺序调度执行的，它后面放的是我们自定义开发的
					// HTTP+XML请求消息解码器HttpXmlRequestEncoder
					ch.pipeline().addLast("http-encoder",new HttpRequestEncoder());
					ch.pipeline().addLast("xml-encoder,new HttpXmlRequestEncoder());
					ch.pipeline().addLast("xmlClientHandler",new HttpXmlClientHandler());
							
				}
				});
			// 发起异步连接操作
					ChannelFuture f=b.connect(new InetSocketAddress(port),sync());
			// 等待客户端链路关闭
					f.channel().closeFuture().sync();
		} catch (Exception e) {
			// TODO: handle exception
		} finally{
			// 优雅退出，释放NIO线程组
			group.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) throws Exception{
		int port=8080;
		if(args!=null&&args.length>0){
			try {
				port=Integer.valueOf(args[0]);
				
			} catch (NumberFormatException e) {
				// TODO: handle exception
				
			}
			new HttpXmlClient().connect(port);
		}
	}

}

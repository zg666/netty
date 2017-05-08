package websocket;

import server.TimeServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

public class WebSocketServer {
	public void run(int port) throws Exception{
		EventLoopGroup bossGroup=new NioEventLoopGroup();
		EventLoopGroup workerGroup=new NioEventLoopGroup();
		try {
			ServerBootstrap b=new ServerBootstrap();
			b.group(bossGroup,workerGroup)
			.channel(NioServerSocketChannel.class)
			.childHanlder(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception{
					Channelpipeline pipeline=ch.pipeline();
				// 添加HttpServerCode，将请求消息和应答消息编码或者解码为HTTP消息
					pipeline.addLast("http-codec",new HttpServerCodec());
					// 增加HttpObjectAggregator，目的是将HTTP消息的多个部分组合成一条完整的HTTP消息
					pipeline.addLast("aggregator",new HttpObjectAggregator(65536));
					//  添加ChunkedWriteHandler,来向客户端发送HTML5文件
					// 它主要用于支持浏览器和服务端进行WebSocket通信
					ch.pipeline().addLast("http-chunked",new ChunkedWriteHandler());
					// 增加WebSocket服务端handler
					pipeline.addLast("handler",new WebSocketServerHandler());
				}
			});
			Channel ch=b.bind(port).sync().channel();
			System.out.println("Web socket server started at port"+port+'.');
			System.out.println("Open your browser and navigate "+port+'/');
			ch.closeFuture().sync();
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
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		new WebSocketServer().run(port);
	}

}

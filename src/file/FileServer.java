package file;

import websocket.WebSocketServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class FileServer {
	
	public void run(int port) throws Exception{
		EventLoopGroup bossGroup =new NioEventLoopGroup();
		EventLoopGroup workerGroup=new NioEventLoopGroup();
		try {
			ServerBootstrap b=new ServerBootstrap();
			b.group(bossGroup,workerGroup)
			.channel(NioServerSocketChannel.class)
			.option(ChannelOption<T>.SO_BACKLOG,100)
			.childHandler(new ChannelInitializer<SocketChannel>() {
				public void initChannel(SocketChannel ch) throws Exception{
					ch.pipeline().addLast(
							// 将文件内容编码成字符串，因为bingding.xml的内容是纯文本的
							// 所以在CMD控制台可见
							new StringEncoder(CharsetUtil.UTF-8),
					  // 两个解码器组合起来就是文本换号解码器		
							// 按照回车换行符对数据报进行解码
							new LineBasedFrameDecoder(1024),
							// 将数据报解码成字符串
							new StringDecoder(CharsetUtil.UTF-8),
							new FileServerHandler());
						
				}
			});
				ChannelFuture f=b.bind(port).sync();
			 System.out.println("Start file server at port:"+port);
			 f.channel().closeFuture().sync();
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			// 优雅停机
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
		new FileServer().run(port);
	}

}

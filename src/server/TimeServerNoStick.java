package server;



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
/*
 * 利用LineBasedFrameDecoder解决TCP粘包问题
 */
public class TimeServerNoStick {
	
	public void bind(int port) throws Exception{
		// 配置服务端的NIO线程组
		EventLoopGroup bossGroup =new NioEventLoopGroup();
		EventLoopGroup workerGroup =new NioEventLoopGroup();
	try {
		// 用于启动NIO服务器的辅助启动类，降低服务端的开发复杂度
		ServerBootstrap b=new ServerBootstrap();
		b.group(bossGroup,workerGroup)
		.channel(NioServerSocketChannel.class)
		.option(ChannelOption.SO_BACKLOG,1024)
		.childHandler(new ChildChannelHandler());
		// 绑定端口，同步等待成功
		ChannelFuture f=b.bind(port).sync();
		// 等待服务端监听端口关闭
		f.channel().closeFuture().sync();
	
		
	} catch (Exception e) {
		// TODO: handle exception
	}finally{
		// 优雅退出，释放线程池资源
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
	}
	
	}
	
	private class ChildChannelHandler extends ChannelInitializer <SocketChannel> {
		@Override
		protected void initChannel(SocketChannel arg0) throws Exception{
			arg0.pipeline().addLast(new LineBasedFrameDecoder(1024));
			arg0.pipeline().addLast(new StringDecoder());
			arg0.pipeline().addLast(new TimeServerHandlerNoStick());
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
		}
		new TimeServerNoStick().bind(port);
	}

}

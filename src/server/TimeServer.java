package server;



import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class TimeServer {
	
	public void bind(int port) throws Exception{
		// 配置服务端的NIO线程组,实际就是Reactor线程组
		// 一个用于服务端接收客户端的连接，另一个用于进行SocketChannel的网络读写
		EventLoopGroup bossGroup =new NioEventLoopGroup();
		EventLoopGroup workerGroup =new NioEventLoopGroup();
	try {
		// 用于启动NIO服务器的辅助启动类，降低服务端的开发复杂度
		ServerBootstrap b=new ServerBootstrap();
		b.group(bossGroup,workerGroup)
		 .channel(NioServerSocketChannel.class)
		 .option(ChannelOption.SO_BACKLOG,1024)   // 配置TCP，此处将它的backlog设置为1024
		 .childHandler(new ChildChannelHandler());  
		// 绑定端口，同步阻塞方法sync，同步等待成功，主要用于异步操作的通知回调
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
			arg0.pipeline().addLast(new TimeServerHandler());
		}
	}
	
	public static void main(String[] args) throws Exception{
		int port=8080;
		if(args!=null&&args.length>0){
			try {
				port=Integer.valueOf(args[0]);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		new TimeServer().bind(port);
	}

}

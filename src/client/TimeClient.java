package client;





import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class TimeClient {
	
	public void connect(int port,String host) throws Exception{
		// 配置客户端NIO线程组，处理IO读写
		EventLoopGroup group=new NioEventLoopGroup();
		try {
			// 创建客户端辅助启动类
			Bootstrap b=new Bootstrap();
			b.group(group)
			// 客户端的Channel设置为NioSocketChannel，
			.channel(NioSocketChannel.class)  
			.option(ChannelOption.TCP_NODELAY,true)   // 对其进行配置
			// 此处为了简单，直接创建匿名内部类，实现initChannel方法
			// 其作用是当创建NioSocketChannel成功之后，
			//在初始化它时将它的ChannelHandler设置到ChannelPipeline中，用于处理网络I/O事件
			.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception{
					
					
					ch.pipeline().addLast(new TimeClientHandler());
				}
			});
			// 发起异步连接操作
			ChannelFuture f=b.connect(host,port).sync();
			// 等待客户端链路关闭
			f.channel().closeFuture().sync();
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			// 优雅退出,释放NIO线程组
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
			new TimeClient().connect(port,"127.0.0.1");
		}
	}

}

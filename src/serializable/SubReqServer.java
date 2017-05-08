package serializable;




import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
/*
 * 订购服务端
 */
public class SubReqServer {
	
	public void bind(int port) throws Exception{
		// 配置服务端的NIO线程组
		EventLoopGroup bossGroup =new NioEventLoopGroup();
		EventLoopGroup workerGroup =new NioEventLoopGroup();
	try {
		// 用于启动NIO服务器的辅助启动类，降低服务端的开发复杂度
		ServerBootstrap b=new ServerBootstrap();
		b.group(bossGroup,workerGroup).
		channel(NioServerSocketChannel.class)
		.option(ChannelOption.SO_BACKLOG,100)
		.handler(new LoggingHandler(LogLevel.INFO))
		.childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) throws Exception{
        // 使用weakCachingConcurrentResolver创建线程安全的weakreferenceMap对类加载器进行缓存
				// 它支持多线程并发访问，当虚拟机内存不足，会释放缓存中的内存，防止内存泄漏
				// 这里设置为1M，在例程中足够使用
				ch.pipeline().addLast(
						new ObjectDecoder(
								1024*1024,
								ClassResolvers
								.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
			// 在消息发送的时候自动将实现Serializable的POJO对象进行解码，
				// 因此用户无须亲自对对象进行手工序列化，只需要关注自己的业务逻辑处理即可，
				// 对象序列化和反序列化都由Netty对象解编码器完成
				ch.pipeline().addLast(new ObjectEncoder());  
				// 将订购处理handler SubReqServerHandler添加到ChannelPipeline的尾部用于业务逻辑处理
				ch.pipeline().addLast(new SubReqServerHandler());
			}
			});
			
		
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

	
	public static void main(String[] args) throws Exception{
		int port=8080;
		if(args!=null&&args.length>0){
			try {
				port=Integer.valueOf(args[0]);
			} catch (NumberFormatException e) {
				// TODO: handle exception
			}
		}
		new SubReqServer().bind(port);
	}

}

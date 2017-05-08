package protobuf;

import serializable.SubReqClient;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.socket.SocketChannel;

public class SubReqServer {
	public void bind(int port) throws Exception{
		// 配置服务端的NIO线程组
		EventLoopGroup bossGroup =new NioEventLoopGroup();
		EventLoopGroup workerGroup=new NioEventLoopGroup();
		try {
			ServerBootstrap b=new ServerBootstrap();
			b.group(bossGroup,workerGroup)
			.channel(NioServerSocketChannel.class)
			.option(ChannelOption.SO_BACKLOG,100)
			.handler(new LoggingHandler(LogLevel.INFO))
			.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch){
					// 用于半包处理
					ch.pipeline().addLast(
							new ProtobufVarint32FrameDecoder());
					// 添加解码器，参数是com.google.protobuf.MessageLite
					// 实际上就是要告诉ProtobufDecoder需要解码的目标类是什么
					ch.pipeline().addLast(
							new ProtobufDecoder(
									SubscribeReqProto.SubscribeReq
									.getDefaultInstance()));
					ch.pipeline().addLast(
							new ProtobufVarint32LengthFieldPrepender());
					ch.pipeline().addLast(new protobufEncoder());
					ch.pipeline().addLast(new SubReqServerHandler());
							)
				});
				// 绑定端口，同步等待成功
				ChannelFuture f=b.bind(port).sync();
				// 等待服务器监听端口关闭
				f.channel().closeFuture().sync();
		} catch (Exception e) {
			// TODO: handle exception
		} finally{
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
				new SubReqClient().connect(port,"127.0.0.1");
			}
		}

}

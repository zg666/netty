package aggrement;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;

public class NettyClient {
	
	private ScheduleExceptionService executor=
			Executors.newScheduleThreadPool(1);
	EventLoopGroup group=new NioEventLoopGroup();
	public void connect(int port,String host) throws Exception {
		// 配置客户端NIO线程组
		try {
			Bootstrap b=new Bootstrap();
			b.group(group).channel(NioSocketChannel.class)
			.option(ChannelOption.TCP_NODELAY,true)
			.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception{
					// netty消息解码，为了防止由于单条消息过大导致的内存溢出或者畸形
					// 码流导致解码错位引起内存分配失败，我们对单条消息最大长度进行了限制
					ch.pipeline().addLast(
							new NettyMessageDecoder(1024*1024,4,4));
					// 增加netty消息编码器，用于协议消息的自动编码
					ch.pipeline().addLast("MessageEncoder",new NettyMessageEncoder());
					/*
					 * 心跳超时的实现非常简单，直接利用Netty的ReadTimeoutHandler机制，当一定周期内
	 *(默认值50s)没有读取到对方任何消息时，需要主动关闭链路。如果是客户端，重新发起连接；
	 * 如果是服务器，释放资源，清除客户端登录缓存信息，等待服务端重连
					 */
					ch.pipeline().addLast("readTimeoutHandler",new ReadTimeoutHandler(50));
					// 增加握手请求Handler
					ch.pipeline().addLast("LoginAuthHandler",new LoginAuthReqHandler());
					// 增加心跳请求handler
					ch.pipeline().addLast("HeartBeatHandler",new HeartBeatReqHandler()
					    new HeartBeatReqHandler());
				}
				});
			// 发起异步连接操作
			/*
			 * 发起TCP连接的代码与之前不同，这里绑定了本地端口，主要用于服务端重复登录保护
			 * 另外，从产品管理角度而言，一般情况下不允许系统随便使用随机端口
			 */
			ChannelFuture future=b.connect(
					new InetSocketAddress(host,port),
					new InetSocketAddress(NettyConstant.LOCALIP,NettyConstant.LOCAL_PORT.sync());
					future.channel().closeFuture().sync();
					
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			// 所有资源释放完成之后，清空资源，再次发起重连操作
			executor.execute(new Runnable() {
				@Override
				public void run(){
					try {
						TimeUnit.SECONDS.sleep(5);
						try {
							connect(NettyConstant.PORT,NettyConstant.REMOTEIP); // 发起重连操作
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}
			});
		}
	}
	
	public static void main(String[] args throws Exception) {
		new NettyClient().connect(NettyConstant.PORT, NettyConstant.REMOTEIP);
	}

}

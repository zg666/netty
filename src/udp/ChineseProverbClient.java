package udp;

import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;

/*
 * UDP的客户端和服务端代码很相似，唯一不同为：
 * udp客户端会主动构造请求消息，向本网段内的所有主机广播请求消息，对于服务端而言
 * 接收到广播请求消息后向广播消息的发起方进行定点发送
 */
public class ChineseProverbClient {
      
	public void run(int port) throws Exception{
		EventLoopGroup group=new NioEventLoopGroup();
		try {
			Bootstrap b=new Bootstrap();
			b.group(group).channel(NioDatagramChannel.class)
			.option(ChannelOption.SO_BROADCAST,true)
			.handler(new ChineseProverbClientHandler());
			Channel ch=b.bind(0).sync().channel();
			// 向网段内的所有机器广播UDP消息
			ch.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer("netty权威指南",
					CharsetUtil.UTF_8),new InetSocketAddress("255.255.255.255",port))).sync();
		// 消息广播之后，客户端等待15s用于接收服务端的应答消息，然后退出并释放资源
			if(!ch.closeFuture().await(15000)) {
				System.out.println("查询超时");
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
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
				e.printStackTrace();
			}
		}
		new ChineseProverbClient().run(port);
	}
}

package udp;
import websocket.WebSocketServer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;


public class ChineseProverbServer {
	
	public void run(int port) throws Exception{
		EventLoopGroup  group=new NioEventLoopGroup();
		try {
			Bootstrap b=new Bootstarp();
			// 由于使用UDP通信，在创建channel的时候需要通过NioDatagramChannel来创建
			// 随后设置socket参数支持广播，最后设置业务处理handler
			b.group(group).channel(NioDatagramChannel.class)
			.option(ChannelOption.SO_BROADCAST,true)
			.handler(new ChineseProverServerHandler());
			b.bind(port).sync().channel().closeFuture().awit();
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
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		new ChineseProverbServer().run(port);
	}

}

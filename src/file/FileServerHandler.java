package file;

import java.io.File;
import java.io.RandomAccessFile;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.FileRegion;
import io.netty.channel.SimpleChannelInboundHandler;

public class FileServerHandler  extends SimpleChannelInboundHandler<String>{
    
	private static final String CR=System.getProperty("line.separator");
	
	public void messageReceived(ChannelHandlerContext ctx,String msg) throws Exception{
		File file=new File(msg);
		// 如果文件不存在，构造异常消息返回。
		if(file.exists()){
			if(!file.isFile()){
				ctx.writeAndFlush("Not a file:"+file+ CR);
				return;
			}
			ctx.write(file+""+file.length()+CR);
			// 如果文件存在，使用RandomAccessFile以只读方式打开文件
			RandomAccessFile randomAccessFile=new RandomAccessFile(msg,"r");
			// 如果netty提供的DefaultFileRegion进行文件传输
			// 有3个参数:
			/*
			 *  FileChannel：文件通道，用于对文件进行读写操作
			 *  Position：文件操作的指针位置，读取或者写入的起始点
			 *  count：操作的总字节数
			 */
			
			FileRegion region=new DefaultFileRegion(
				randomAccessFile.getChannel(),0,randomAccessFile.length());
				ctx.write(region);
			
		} else {
			ctx.writeAndFlush("File not found"+file+ CR);
		}
	}
	
	public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause) throws exception{
		cause.printStackTrace();
		ctx.close();
	}
}

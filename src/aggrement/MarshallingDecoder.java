package aggrement;

import io.netty.buffer.ByteBuf;

import org.jboss.marshalling.ByteInput;
import org.jboss.marshalling.Unmarshaller;
/*
 * 消息解码工具类
 */
public class MarshallingDecoder {
	
	private final Unmarshaller unmarshaller;
	
    public MarshllingDecoder() throws IOException{
    	unmarshller=MarshallerCodeFactory.buildUnMarshalling();
    	
    }
    
    protected Object decode(ByteBuf in) throws Exception{
    	int ObjectSize=in.readInt();
    	ByteBuf buf=in.slice(in.readerIndex(),objectSize);
    	ByteInput input=new ChannelBufferByteInput(buf);
    	try {
			unmarshaller.start(input);
			Object obj=unmarshaller.readObject();
			unmarshaller.finish();
			in.readerIndex(in.readerIndex()+objectSize);
			return obj;
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			unmarshaller.close();
		}
    }
}

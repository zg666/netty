package marshalling;

import io.netty.handler.codec.marshalling.DefaultMarshallerProvider;
import io.netty.handler.codec.marshalling.DefaultUnmarshallerProvider;
import io.netty.handler.codec.marshalling.MarshallerProvider;
import io.netty.handler.codec.marshalling.MarshallingDecoder;
import io.netty.handler.codec.marshalling.MarshallingEncoder;
import io.netty.handler.codec.marshalling.UnmarshallerProvider;

import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;

public class MarshallingCodeCFactory {
	
	public static MarshallingDecoder buildMarshallingDecoder() {
	// 参数serial表示创建的是java序列化工厂对象
		final MarshallerFactory marshallerFactory=Marshalling.
				getProvidedMarshallerFactory("serial");
		final MarshallingConfiguration configuration=new MarshallingConfiguration();
		// 设置版本号为5
		configuration.setVersion(5);
		UnmarshallerProvider provider=new 
				DefaultUnmarshallerProvider(marshallerFactory,configuration);
		// 最大长度为1M
		MarshallingDecoder decoder=new MarshallingDecoder(provider,1024);
		return decoder;
	}
	
	/*/
	 * 创建jboss marshalling编码器marshallingencoder
	 */
    public static MarshallingEncoder buildMarshallingEncoder() {
    	final MarshallerFactory marshallerFactory=Marshalling.
				getProvidedMarshallerFactory("serial");
		final MarshallingConfiguration configuration=new MarshallingConfiguration();
		configuration.setVersion(5);
		MarshallerProvider provider=new DefaultMarshallerProvider(
				marshallerFactory,configuration);
		MarshallingEncoder encoder=new MarshallingEncoder(provider);
		return encoder;
				
    }
}

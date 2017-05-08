package jibx;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public class TestOrder {
	
	private IBindingFactory factory=null;
	private StringWriter writer=null;
	private StringReader reader=null;
	private final static String CHARSET_NAME="UTF-8";
	private String encode2Xml(Order order) throws JiBXException,IOException{
		factory=BindingDirecoty.getFactory(Order.class);
		writer=new StringWriter();
		// 通过IBindingFactory构造Marshalling上下文
		ImarshallingContext mctx=factory.createMarshallingContext();
		mctx.setindent(2);
		// 通过marshallDocument将Order序列化为StringWriter
		mactx.marshalDocument(order,CHARSET_NAME,null,writer);
		String xmlStr=writer.toString();
		writer.close();
		System.out.println(xmlStr.toString());
		return xmlStr;
	}
	public static void main(String[] args) throws JiBXException,IOException{
		TestOrder test=new TestOrder();
		Order order=OrderFactory.create(123);
		String body=test.encode2Xml(order);
		Order order=test.decode2Order(body);
		System.out.println(order2);
		
	}

}

package protobuf;

import java.util.List;

public class TestSubscribeReqProto {
	
	private static byte[] encode(SubscribeReqproto.SubscribeReq req){
		return req.toByteArray();
	}
	
	private static SubscibeReqProto.SubscribeReq decode(byte[] body)
	throws InvalidProtocolBufferException{
		return SubscribeReqProto.SubscribeReq.parseFrom(body);
	}
    
	private static SubscribeReqProto.SubscribeReq createSubscribeReq(){
		SubscribeReqProto.SubscribeReq.Builder builder=
				SubscribeReqProto.SubscribeReq.newBuilder();
		builder.setSubReqID(1);
		builder.setUserName("zhengguang");
		builder.setProductName("Netty Book");
		List<String> address=new ArrayList<>();
		address.add("wuhan");
		address.add("shanghai");
		address.add("beijing");
		builder.addAllAddress(address);
		return builder.build();
	}
	
	public static void main(String[] args) throws InvalidprotocolBufferException{
		SubscribeReqProto.SubscirbeReq req=CreateSubscribeReq();
		System.out.println("Before encode:"+req.toString());
		SubscribeReqProto.SubscribeReq req2=decode(encode(req));
		System.out.println("After decode:"+req.toString());
		System.out.println("Assert equal: -->"+req2.equals(req));
	}
}

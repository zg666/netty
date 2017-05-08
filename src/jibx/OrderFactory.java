package jibx;

public class OrderFactory {
	public static Order create(long orderID){
		Order order=new order();
		order.setOrderNumber(orderID);
		order.setTotal(9999.999f);
		Address address=new Address();
		address.setCity("武汉市");
		address.setCountry("中国");
		address.setPostCode("11");
		address.setState("湖北省");
		address.setStreet1("龙阳大道");
		order.setBillTo(address);
		Customer customer=new Customer();
		customer.setCustomerNumber(orderID);
		customer.setFirstName("郑");
		customer.setLastName("广");
		order.setCustomer(customer);
		order.setShipping(Shipping.INTERNATIONAL_MAIL);
		order.setShipTo(address);
		return order;
	}

}

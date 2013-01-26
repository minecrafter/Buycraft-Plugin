package net.buycraft.packages;

public class PackageModal 
{
	private int id;
	private String name;
	private String price;
	private int order;
	
	public PackageModal(int id, String name, String price, int order)
	{
		this.id = id;
		this.name = name;
		this.price = price;
		this.order = order;
	}
	
	public int getId()
	{
		return id;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getPrice()
	{
		return price;
	}
	
	public int getOrder()
	{
		return order;
	}
}

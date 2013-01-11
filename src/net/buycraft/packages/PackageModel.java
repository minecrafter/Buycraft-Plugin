package net.buycraft.packages;

/**
 * Represents a package data structure
 */
public class PackageModel 
{
	private int id;
	private String name;
	private String price;
	private int order;
	
	public PackageModel(int id, String name, String price, int order)
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
	
	public Integer getOrder()
	{
		return order;
	}
}

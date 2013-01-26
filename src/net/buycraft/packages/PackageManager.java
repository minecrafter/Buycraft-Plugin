package net.buycraft.packages;

import java.util.ArrayList;
import java.util.List;

public class PackageManager 
{
	private List<PackageModal> packagesForSale;
	
	public PackageManager()
	{
		this.packagesForSale = new ArrayList<PackageModal>();
	}
	
	public void add(int id, String name, String price, int order)
	{
		packagesForSale.add(new PackageModal(id, name, price, order));
	}
	
	public List<PackageModal> getPackagesForSale()
	{
		return packagesForSale;
	}
	
	public PackageModal getPackageById(int packageId)
	{
		for(PackageModal packageModel : packagesForSale)
		{
			if(packageModel.getId() == packageId)
			{
				return packageModel;
			}
		}
		
		return null;
	}
}

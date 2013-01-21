package net.buycraft.packages;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages all packages available for sale
 */
public class PackageManager 
{
	private List<PackageModal> packagesForSale;
	
	public PackageManager()
	{
		this.packagesForSale = new ArrayList<PackageModal>();
	}
	
	/**
	 * Add a package to the package list
	 */
	public void add(int id, String name, String price, int order)
	{
		packagesForSale.add(new PackageModal(id, name, price, order));
	}
	
	/**
	 * Returns all packages currently loaded and available for purchase
	 */
	public List<PackageModal> getPackagesForSale()
	{
		return packagesForSale;
	}
	
	/**
	 * Return a package from its ID
	 */
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

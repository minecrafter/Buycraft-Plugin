package net.buycraft.packages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class PackageManager {
    private List<PackageCategory> packageCategories;
    private List<PackageModal> packagesForSale;

    public PackageManager() {
        this.packageCategories = new ArrayList<PackageCategory>();
        this.packagesForSale = new ArrayList<PackageModal>();
    }

    public synchronized void addCategory(int categoryId, String name, String description, int guiItemId) {
        packageCategories.add(new PackageCategory(categoryId, name, description, guiItemId));
    }

    public synchronized void add(int categoryId, int id, int materialId, String name, String description, String price) {
        PackageCategory category = getPackageCategory(categoryId);
        packagesForSale.add(new PackageModal(category, id, materialId, name, description, price, packagesForSale.size() + 1));
    }

    public synchronized void cleanCategories() {
        int nextId = 1;
        Iterator<PackageCategory> it = packageCategories.iterator();
        while (it.hasNext()) {
            PackageCategory p = it.next();
            if (p.getPackages().isEmpty()) {
                it.remove();
            } else {
                p.niceId = p.getId() != 0 ? nextId++ : 0;
            }
        }
    }
    
    public synchronized boolean hasPackages() {
        return !this.packageCategories.isEmpty();
    }

    public synchronized List<PackageCategory> getCategories() {
        return Collections.unmodifiableList(packageCategories);
    }

    public synchronized PackageCategory getPackageCategory(int categoryId) {
        for (PackageCategory c : packageCategories) {
            if (c.getId() == categoryId)
                return c;
        }
        return null;
    }

    public synchronized PackageCategory getPackageCategoryByNiceId(int categoryId) {
        for (PackageCategory c : packageCategories) {
            if (c.getNiceId() == categoryId)
                return c;
        }
        return null;
    }

    public synchronized List<PackageModal> getPackagesForSale() {
        return Collections.unmodifiableList(packagesForSale);
    }

    public synchronized PackageModal getPackageById(int packageId) {
        for (PackageModal packageModel : packagesForSale) {
            if (packageModel.getId() == packageId) {
                return packageModel;
            }
        }

        return null;
    }

    public synchronized PackageModal getPackageByOrderId(int orderId) {
        for (PackageModal packageModel : packagesForSale) {
            if (packageModel.getOrder() == orderId) {
                return packageModel;
            }
        }

        return null;
    }

    public synchronized void reset() {
        packageCategories.clear();
        packagesForSale.clear();
    }
}

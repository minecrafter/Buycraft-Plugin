package net.buycraft.util;

public class PackageCommand implements Comparable<PackageCommand> {
    public final String command;
    public final long runtime;
    
    public PackageCommand(String command, int tickDelay)
    {
        this.command = command;
        this.runtime = System.currentTimeMillis() + tickDelay * 50L;
    }

    public int compareTo(PackageCommand o) {
        // If the objects are the same return 0
        if (this == o)
            return 0;

        if (runtime > o.runtime)
            return 1;

        if (runtime < o.runtime)
            return -1;

        // Make sure we never have two similar commands match
        return hashCode() > o.hashCode() ? 1 : -1;
    }
}

package net.buycraft.util;


public class PackageCommand implements Comparable<Object> {
    private final int id;
    public final String username;
    public final String command;
    public final long runtime;

    public PackageCommand(int id, String username, String command, int tickDelay)
    {
        this.id = id;
        this.username = username;
        this.command = command;
        this.runtime = System.currentTimeMillis() + tickDelay * 50L;
    }

    public int getId()
    {
        return id;
    }

    public int compareTo(Object o) {
        // If the objects are the same return 0
        if (this == o)
            return 0;

        if (o.getClass() == Integer.class) {
            return compareTo((Integer) o);
        } else if (o instanceof PackageCommand) {
            return compareTo((PackageCommand) o);
        }

        // Just do something random
        return hashCode() > o.hashCode() ? 1: -1;
    }

    public int compareTo(PackageCommand o) {
        if (id == o.id)
            return 0;

        if (runtime > o.runtime)
            return 1;

        if (runtime < o.runtime)
            return -1;

        // Make sure the commands are ordered correctly
        return id > o.id ? 1 : -1;
    }

    public int compareTo(Integer i) {
        return id > i ? 1 : id == i ? 0 : -1;
    }
}

package net.buycraft.util;

import java.util.concurrent.atomic.AtomicInteger;

public class PackageCommand implements Comparable<PackageCommand> {
    private final static AtomicInteger nextId = new AtomicInteger(Integer.MIN_VALUE);

    private final int id;
    public final String command;
    public final long runtime;

    public PackageCommand(String command, int tickDelay)
    {
        this.id = nextId.getAndIncrement();
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

        // Make sure the commands are ordered correctly
        return id > o.id ? 1 : -1;
    }
}

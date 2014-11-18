package com.ikkerens.skyblock;

import java.util.HashSet;

import com.ikkerens.skyblock.model.SkyBlockWorld;

public class Config {
    private final boolean                  allowBuildingInUnownedSegments;
    private final boolean                  allowChestStealing;
    private final boolean                  allowPvP;
    private final HashSet< SkyBlockWorld > worlds;

    public Config() {
        this.allowBuildingInUnownedSegments = true;
        this.allowChestStealing = false;
        this.allowPvP = true;

        this.worlds = new HashSet< SkyBlockWorld >();
    }

    void init( final SkyBlockPlugin plugin ) {
        for ( final SkyBlockWorld world : this.worlds )
            world.init( plugin );
    }

    void save( final SkyBlockPlugin plugin ) {
        for ( final SkyBlockWorld world : this.worlds )
            world.save( plugin );
    }

    public void addWorld( final SkyBlockWorld world ) {
        this.worlds.add( world );
    }

    public boolean allowBuildingInUnownedSegments() {
        return this.allowBuildingInUnownedSegments;
    }

    public boolean allowChestStealing() {
        return this.allowChestStealing;
    }

    public boolean allowPvP() {
        return this.allowPvP;
    }
}

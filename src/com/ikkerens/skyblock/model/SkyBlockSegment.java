package com.ikkerens.skyblock.model;

import com.mbserver.api.game.Location;

public class SkyBlockSegment {
    private final int x, z;
    private Location  spawn;

    public SkyBlockSegment( final int x, final int z ) {
        this.x = x;
        this.z = z;
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    public Location getSpawn() {
        return this.spawn;
    }

    public void setSpawn( final Location spawn ) {
        this.spawn = spawn;
    }

    @Override
    public boolean equals( final Object other ) {
        if ( !( other instanceof SkyBlockSegment ) )
            return false;

        final SkyBlockSegment segment = (SkyBlockSegment) other;
        return ( this.x == segment.x ) && ( this.z == segment.z );
    }
}

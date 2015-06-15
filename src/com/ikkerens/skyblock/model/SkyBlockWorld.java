package com.ikkerens.skyblock.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.ikkerens.skyblock.SkyBlockPlugin;

import com.mbserver.api.Constructors;
import com.mbserver.api.game.Chest;
import com.mbserver.api.game.Player;
import com.mbserver.api.game.World;

public class SkyBlockWorld {
    private transient World         world;

    private String                  name;
    private int                     segmentSize;

    private transient WorldInfoFile segments;

    public SkyBlockWorld( final World world, final int segmentSize ) {
        this();
        this.world = world;
        this.segmentSize = segmentSize;

        this.name = world.getWorldName();
    }

    private SkyBlockWorld() {
        // Used for gson deserialization
    }

    public void init( final SkyBlockPlugin plugin ) {
        this.world = plugin.getServer().getWorld( this.name );
        this.segments = plugin.getServer().getConfigurationManager().load( plugin, WorldInfoFile.class, "world_" + this.name );
        this.segments.checkForExpansion( this );

        plugin.linkWorld( this.world, this );
    }

    public void save( final SkyBlockPlugin plugin ) {
        plugin.getServer().getConfigurationManager().save( plugin, this.segments, "world_" + this.name );
    }

    public World getWorld() {
        return this.world;
    }

    public int getSegmentSize() {
        return this.segmentSize;
    }

    public SkyBlockSegment getSegment( final Player player ) {
        return this.segments.playerSegments.get( player.getLoginName() );
    }

    public SkyBlockSegment grant( final Player player ) {
        final SkyBlockSegment segment;

        // Obtain a random segment from the unclaimed list and assign it to this player
        synchronized ( this.segments ) {
            this.segments.checkForExpansion( this );
            Collections.shuffle( this.segments.unclaimedSegments );

            final Iterator< SkyBlockSegment > iterator = this.segments.unclaimedSegments.iterator();
            segment = iterator.next();
            iterator.remove();
        }

        this.segments.playerSegments.put( player.getLoginName(), segment );

        final int[] sizes = SkyBlockPlugin.schematic.getSizes();

        final int oX = ( segment.getX() + ( this.segmentSize / 2 ) ) - ( sizes[ 0 ] / 2 );
        final int oY = 60 + ( new Random().nextInt( 40 ) - 20 );
        final int oZ = ( segment.getZ() + ( this.segmentSize / 2 ) ) - ( sizes[ 1 ] / 2 );

        // Place the sky block building
        for ( int x = 0; x < sizes[ 0 ]; x++ )
            for ( int y = 0; y < sizes[ 1 ]; y++ )
                for ( int z = 0; z < sizes[ 2 ]; z++ ) {
                    final short id = SkyBlockPlugin.schematic.getBlock( x, y, z );
                    if ( ( id & 0x00FF ) == 161 )
                        segment.setSpawn( Constructors.newLocation( this.world, oX + x, oY + y, oZ + z ) );
                    else {
                        this.world.setBlock( oX + x, oY + y, oZ + z, id );
                        if ( ( id & 0x00FF ) == 54 ) {
                            final Chest chest = (Chest) this.world.getBlock( oX + x, oY + y, oZ + z ).getBlockData();
                            int slot = 0;
                            chest.setItemSlot( slot++, Constructors.newItemStack( (short) 103, 1 ), false );
                            chest.setItemSlot( slot++, Constructors.newItemStack( (short) 113, 1 ), false );
                            chest.setItemSlot( slot++, Constructors.newItemStack( (short) 59, 1 ), false );
                            chest.setItemSlot( slot++, Constructors.newItemStack( (short) 327, 1 ), false );
                            chest.setItemSlot( slot++, Constructors.newItemStack( (short) 403, 1 ), false );
                            chest.setItemSlot( slot++, Constructors.newItemStack( (short) 300, 1 ), true );
                        }
                    }
                }

        return segment;
    }

    public static class WorldInfoFile {
        private int                                  searchRadius;
        private final Map< String, SkyBlockSegment > playerSegments;
        private final List< SkyBlockSegment >        unclaimedSegments;

        public WorldInfoFile() {
            this.playerSegments = new HashMap< String, SkyBlockSegment >();
            this.unclaimedSegments = new ArrayList< SkyBlockSegment >();
        }

        public synchronized void checkForExpansion( final SkyBlockWorld sbWorld ) {
            while ( this.unclaimedSegments.size() == 0 ) {
                if ( this.searchRadius == 0 )
                    this.checkClear( sbWorld, 0, 0 );

                for ( int dim = ( -this.searchRadius - sbWorld.segmentSize ); dim < ( this.searchRadius + sbWorld.segmentSize ); dim += sbWorld.segmentSize ) {
                    this.checkClear( sbWorld, dim, -this.searchRadius - sbWorld.segmentSize );
                    this.checkClear( sbWorld, dim, this.searchRadius + sbWorld.segmentSize );
                    this.checkClear( sbWorld, -this.searchRadius - sbWorld.segmentSize, dim );
                    this.checkClear( sbWorld, this.searchRadius + sbWorld.segmentSize, dim );
                }
                this.searchRadius += sbWorld.segmentSize; // Increase radius for next search
            }
        }

        private void checkClear( final SkyBlockWorld sbWorld, final int ax, final int az ) {
            final SkyBlockSegment segment = new SkyBlockSegment( ax, az );

            if ( this.unclaimedSegments.contains( segment ) )
                return;

            for ( int x = ax; x < ( ax + sbWorld.segmentSize ); x++ )
                for ( int z = az; z < ( az + sbWorld.segmentSize ); z++ )
                    for ( int y = 0; y < 128; y++ )
                        if ( sbWorld.world.getBlockID( x, y, z ) != 0 )
                            return; // Segment has blocks in it, not fit for skyblock use

            // Segment free
            this.unclaimedSegments.add( segment );
        }
    }
}

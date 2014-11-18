package com.ikkerens.skyblock.events;

import com.ikkerens.skyblock.SkyBlockPlugin;
import com.ikkerens.skyblock.model.SkyBlockSegment;
import com.ikkerens.skyblock.model.SkyBlockWorld;

import com.mbserver.utils.math.MathUtils;

import com.mbserver.api.events.BlockEvent;
import com.mbserver.api.events.EventHandler;
import com.mbserver.api.events.Listener;
import com.mbserver.api.game.Location;

public class UnownedSegmentBuildListener implements Listener {
    private final SkyBlockPlugin plugin;

    public UnownedSegmentBuildListener( final SkyBlockPlugin plugin ) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockAction( final BlockEvent event ) {
        final Location loc = event.getLocation();
        final SkyBlockWorld world = this.plugin.getWorld( loc.getWorld() );

        if ( world != null ) {
            final SkyBlockSegment segment = world.getSegment( event.getPlayer() );

            if ( ( segment == null ) || // Player doesn't own a plot
                ( ( loc.getX() - MathUtils.modulo( loc.getBlockX(), world.getSegmentSize() ) ) != segment.getX() ) || // This plot is not the players plot
                ( ( loc.getZ() - MathUtils.modulo( loc.getBlockZ(), world.getSegmentSize() ) ) != segment.getZ() ) )
                event.setCancelled( true );
        }
    }
}

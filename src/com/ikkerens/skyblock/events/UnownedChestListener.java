package com.ikkerens.skyblock.events;

import com.ikkerens.skyblock.SkyBlockPlugin;
import com.ikkerens.skyblock.model.SkyBlockSegment;
import com.ikkerens.skyblock.model.SkyBlockWorld;
import com.ikkerens.skyblock.util.MathUtils;

import com.mbserver.api.events.ChestOpenEvent;
import com.mbserver.api.events.EventHandler;
import com.mbserver.api.events.Listener;
import com.mbserver.api.game.Location;

public class UnownedChestListener implements Listener {
    private final SkyBlockPlugin plugin;

    public UnownedChestListener( final SkyBlockPlugin plugin ) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockAction( final ChestOpenEvent event ) {
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

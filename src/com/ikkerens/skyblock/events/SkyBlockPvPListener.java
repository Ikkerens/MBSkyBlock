package com.ikkerens.skyblock.events;

import com.ikkerens.skyblock.SkyBlockPlugin;

import com.mbserver.api.events.EventHandler;
import com.mbserver.api.events.Listener;
import com.mbserver.api.events.PlayerPvpEvent;

public class SkyBlockPvPListener implements Listener {
    private final SkyBlockPlugin plugin;

    public SkyBlockPvPListener( final SkyBlockPlugin plugin ) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPvP( final PlayerPvpEvent event ) {
        if ( this.plugin.getWorld( event.getLocation().getWorld() ) != null )
            event.setCancelled( true );
    }
}

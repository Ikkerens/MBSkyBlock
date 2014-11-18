package com.ikkerens.skyblock.events;

import com.ikkerens.skyblock.SkyBlockPlugin;
import com.ikkerens.skyblock.model.SkyBlockSegment;
import com.ikkerens.skyblock.model.SkyBlockWorld;

import com.mbserver.api.CommandExecutor;
import com.mbserver.api.CommandSender;
import com.mbserver.api.events.EventHandler;
import com.mbserver.api.events.Listener;
import com.mbserver.api.events.PostPlayerLoginEvent;
import com.mbserver.api.events.RunMode;
import com.mbserver.api.game.Location;
import com.mbserver.api.game.Player;
import com.mbserver.api.game.World;

public class SkyBlockEntryListener implements Listener, CommandExecutor {
    private final SkyBlockPlugin plugin;

    public SkyBlockEntryListener( final SkyBlockPlugin plugin ) {
        this.plugin = plugin;
    }

    @Override
    public void execute( final String command, final CommandSender sender, final String[] args, final String label ) {
        if ( !( sender instanceof Player ) ) {
            sender.sendMessage( "This command can only be executed by a player!" );
            return;
        }

        final Player player = (Player) sender;

        final World world;
        if ( args.length == 0 ) {
            world = this.plugin.getMainWorld();
            if ( world == null ) {
                player.sendMessage( "There is more than 1 skyblock world, please specify one." );
                player.sendMessage( "Usage: /skyblock <world>" );
                return;
            }
        } else {
            world = this.plugin.getServer().getWorld( args[ 0 ] );
            if ( this.plugin.getWorld( world ) == null ) {
                player.sendMessage( "'" + args[ 0 ] + "' is not an existing skyblock world!" );
                return;
            }
        }

        this.movePlayer( player, world );
    }

    @EventHandler( concurrency = RunMode.BLOCKING )
    public void onLogin( final PostPlayerLoginEvent event ) {
        final Location loc = event.getPlayer().getLocation();
        if ( this.plugin.getWorld( loc.getWorld() ) != null )
            if ( loc == loc.getWorld().getSpawn() )
                this.movePlayer( event.getPlayer(), loc.getWorld() );
    }

    private void movePlayer( final Player player, final World targetWorld ) {
        final SkyBlockWorld world = this.plugin.getWorld( targetWorld );

        SkyBlockSegment segment = world.getSegment( player );
        if ( segment == null )
            // Assign a new segment to this player
            segment = world.grant( player );

        player.teleport( segment.getSpawn() );
    }
}

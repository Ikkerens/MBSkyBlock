package com.ikkerens.skyblock;

import com.ikkerens.skyblock.model.SkyBlockWorld;
import com.ikkerens.skyblock.util.EmptyWorldGenerator;

import com.mbserver.api.CommandExecutor;
import com.mbserver.api.CommandSender;
import com.mbserver.api.game.World;

public class CreateWorldCommand implements CommandExecutor {
    private final SkyBlockPlugin plugin;

    public CreateWorldCommand( final SkyBlockPlugin plugin ) {
        this.plugin = plugin;
    }

    @Override
    public void execute( final String command, final CommandSender sender, final String[] args, final String label ) {
        if ( !sender.hasPermission( "ikkerens.skyblock.create" ) ) {
            sender.sendMessage( "You do not have permission to use /" + label );
            return;
        }

        final String worldName;
        final int segmentSize;
        try {
            if ( ( args.length != 1 ) && ( args.length != 2 ) )
                throw new IllegalArgumentException();

            worldName = args[ 0 ];

            segmentSize = args.length == 2 ? Integer.parseInt( args[ 1 ] ) : 64;
        } catch ( final IllegalArgumentException e ) {
            sender.sendMessage( "Usage: /" + label + " <worldname> [segmentsize=64]" );
            return;
        }

        sender.sendMessage( "Creating new skyblock world..." );
        new Thread( "SkyBlockCreateThread" ) {
            @Override
            public void run() {
                final World world = CreateWorldCommand.this.plugin.getServer().createWorld( worldName, 0L, new EmptyWorldGenerator() );
                final SkyBlockWorld sbWorld = new SkyBlockWorld( world, segmentSize );
                CreateWorldCommand.this.plugin.< Config > getConfig().addWorld( sbWorld );
                sbWorld.init( CreateWorldCommand.this.plugin );
                sender.sendMessage( String.format( "Finished creating skyblock world named %s with segments of %d blocks.", worldName, segmentSize ) );
            }
        }.start();
    }

}

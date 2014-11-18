package com.ikkerens.skyblock;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ikkerens.skyblock.events.SkyBlockEntryListener;
import com.ikkerens.skyblock.events.SkyBlockPvPListener;
import com.ikkerens.skyblock.events.UnownedChestListener;
import com.ikkerens.skyblock.events.UnownedSegmentBuildListener;
import com.ikkerens.skyblock.model.SkyBlockWorld;

import com.mbserver.api.Load;
import com.mbserver.api.MBServerPlugin;
import com.mbserver.api.Manifest;
import com.mbserver.api.PluginManager;
import com.mbserver.api.events.EventHandler;
import com.mbserver.api.events.Listener;
import com.mbserver.api.events.WorldSaveEvent;
import com.mbserver.api.game.MBSchematic;
import com.mbserver.api.game.World;

@Manifest( name = "MBSkyBlock", authors = "Ikkerens",
    config = Config.class,
    load = Load.POSTWORLD )
public class SkyBlockPlugin extends MBServerPlugin implements Listener {
    public static final MBSchematic           schematic;

    static {
        // Load the schematic
        try {
            schematic = MBSchematic.loadFromJar( SkyBlockPlugin.class, "/skyblock.mbschem" );
        } catch ( final IOException e ) {
            Logger.getLogger( "Minebuilder" ).log( Level.SEVERE, "Could not load skyblock schematic from plugin, corrupted jar?" );
            throw new RuntimeException( e );
        }
    }

    private final Map< World, SkyBlockWorld > worlds;

    public SkyBlockPlugin() {
        this.worlds = new HashMap< World, SkyBlockWorld >();
    }

    @Override
    public void onEnable() {
        // Load the worlds
        final Config config = this.getConfig();
        config.init( this );

        final PluginManager pm = this.getPluginManager();

        // Save world data
        pm.registerEventHandler( this );

        // Register the command
        pm.registerCommand( "skyblockcreate", new CreateWorldCommand( this ) );

        // Register entry listener
        final SkyBlockEntryListener entry = new SkyBlockEntryListener( this );
        pm.registerEventHandler( entry );
        pm.registerCommand( "skyblock", entry );

        if ( !config.allowBuildingInUnownedSegments() )
            pm.registerEventHandler( new UnownedSegmentBuildListener( this ) );

        if ( !config.allowChestStealing() )
            pm.registerEventHandler( new UnownedChestListener( this ) );

        if ( !config.allowPvP() )
            pm.registerEventHandler( new SkyBlockPvPListener( this ) );
    }

    @Override
    public void onDisable() {
        this.< Config > getConfig().save( this );
        this.saveConfig();
    }

    @EventHandler
    public void onSave( final WorldSaveEvent event ) {
        final SkyBlockWorld world = this.worlds.get( event.getWorld() );
        if ( world != null )
            world.save( this );

        if ( event.getWorld() == this.getServer().getMainWorld() )
            this.saveConfig();
    }

    public void linkWorld( final World world, final SkyBlockWorld sbWorld ) {
        this.worlds.put( world, sbWorld );
    }

    public World getMainWorld() {
        // If we only have 1 skyblock world, that is the main world, or else we return null
        if ( this.worlds.size() == 1 )
            return this.worlds.keySet().iterator().next();
        else
            return null;
    }

    public SkyBlockWorld getWorld( final World world ) {
        return this.worlds.get( world );
    }
}

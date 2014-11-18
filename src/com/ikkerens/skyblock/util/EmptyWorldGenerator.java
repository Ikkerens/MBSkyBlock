package com.ikkerens.skyblock.util;

import com.mbserver.api.dynamic.WorldGenerator;
import com.mbserver.api.game.Chunk;

public class EmptyWorldGenerator extends WorldGenerator {

    @Override
    public void setSeed( final long seed ) {
    }

    @Override
    public void fillChunk( final Chunk chunk ) {
        // World is empty, skyblock segments are post-generated when a player needs them.
    }

}

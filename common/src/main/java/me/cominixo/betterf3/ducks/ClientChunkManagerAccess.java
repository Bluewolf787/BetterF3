package me.cominixo.betterf3.ducks;

import net.minecraft.client.multiplayer.ClientChunkCache;

/**
 * The Chunk Cache Accessor.
 */
@SuppressWarnings("checkstyle:MethodName")
public interface ClientChunkManagerAccess {

    /**
     * Gets the chunk cache storage.
     *
     * @return The storage.
     */
    ClientChunkCache.Storage getChunks();
}

package me.cominixo.betterf3.ducks;

import java.util.Queue;

/**
 * Access the Chunk Builder.
 */
@SuppressWarnings("checkstyle:MethodName")
public interface ChunkBuilderAccess {
  /**
   * Gets the chunk batch count.
   *
   * @return The chunk batch.
   */
  int betterF3$getQueuedTaskCount();

  /**
   * Gets the chunks to upload.
   *
   * @return The amount of chunks to upload.
   */
  Queue<Runnable> betterF3$getUploadQueue();

  /**
   * Gets the free buffer count.
   *
   * @return The free buffer count.
   */
  int betterF3$getBufferCount();

}

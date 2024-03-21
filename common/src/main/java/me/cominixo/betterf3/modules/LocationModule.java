package me.cominixo.betterf3.modules;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import me.cominixo.betterf3.utils.DebugLine;
import me.cominixo.betterf3.utils.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.jetbrains.annotations.Nullable;

/**
 * The Location module.
 */
public class LocationModule extends BaseModule {

  /**
   * Instantiates a new Location module.
   */
  public LocationModule() {
    this.defaultNameColor = TextColor.fromLegacyFormat(ChatFormatting.DARK_GREEN);
    this.defaultValueColor = TextColor.fromLegacyFormat(ChatFormatting.AQUA);

    this.nameColor = defaultNameColor;
    this.valueColor = defaultValueColor;

    lines.add(new DebugLine("dimension"));
    lines.add(new DebugLine("facing"));
    lines.add(new DebugLine("rotation"));
    lines.add(new DebugLine("light"));
    lines.add(new DebugLine("light_server"));
    lines.add(new DebugLine("highest_block"));
    lines.add(new DebugLine("highest_block_server"));
    lines.add(new DebugLine("biome"));
    lines.add(new DebugLine("local_difficulty"));
    lines.add(new DebugLine("day_ticks"));
    lines.add(new DebugLine("days_played"));
    lines.add(new DebugLine("slime_chunk"));
  }

  @Nullable
  private CompletableFuture<LevelChunk> chunkFuture;

  /**
   * Updates the Location module.
   *
   * @param client the Minecraft client
   */
  public void update(final Minecraft client) {
    final Entity cameraEntity = client.getCameraEntity();

    final IntegratedServer integratedServer = client.getSingleplayerServer();

    String chunkLightString = "";
    String chunkLightServerString = "";
    String localDifficultyString = "";
    String slimeChunkString = "";
    final StringBuilder highestBlock = new StringBuilder();
    final StringBuilder highestBlockServer = new StringBuilder();

    final Level serverWorld;
    if (client.level != null) {
      assert cameraEntity != null;
      final BlockPos blockPos = cameraEntity.blockPosition();
      final ChunkPos chunkPos = new ChunkPos(blockPos);

      // Biome
      lines.get(7).value(client.level.registryAccess().registryOrThrow(Registries.BIOME).getKey(client.level.getBiome(blockPos).value()));

      serverWorld = integratedServer != null ? integratedServer.getLevel(client.level.dimension()) : client.level;
      final LevelChunk clientChunk = client.level.getChunk(chunkPos.x, chunkPos.z);
      if (clientChunk.isEmpty()) {
        chunkLightString = I18n.get("text.betterf3.line.waiting_chunk");
      } else if (serverWorld != null) {

        // Client Chunk Lights
        final int totalLight = client.level.getChunkSource().getLightEngine().getRawBrightness(blockPos, 0);
        final int skyLight = client.level.getBrightness(LightLayer.SKY, blockPos);
        final int blockLight = client.level.getBrightness(LightLayer.BLOCK, blockPos);
        chunkLightString = I18n.get("format.betterf3.chunklight", totalLight, skyLight, blockLight);

        // Server Chunk Lights
        final LevelLightEngine lightingProvider = serverWorld.getChunkSource().getLightEngine();

        final int skyLightServer = lightingProvider.getLayerListener(LightLayer.SKY).getLightValue(blockPos);
        final int blockLightServer = lightingProvider.getLayerListener(LightLayer.BLOCK).getLightValue(blockPos);

        chunkLightServerString = I18n.get("format.betterf3.chunklight_server", skyLightServer, blockLightServer);

        // Heightmap stuff (Find the highest block)
        final Heightmap.Types[] heightmapTypes = Heightmap.Types.values();

        LevelChunk serverChunk;

        if (serverWorld instanceof ServerLevel) {
          if (this.chunkFuture == null) {
            this.chunkFuture =
            ((ServerLevel) serverWorld).getChunkSource().getChunkFuture(chunkPos.x, chunkPos.z, ChunkStatus.FULL, false)
              .thenApply((chunkResult) -> (LevelChunk) chunkResult.orElse(null));
          }

          if (this.chunkFuture == null) {
            this.chunkFuture = CompletableFuture.completedFuture(clientChunk);
          }

          serverChunk = this.chunkFuture.getNow(null);
        } else {
          serverChunk = clientChunk;
        }

        for (final Heightmap.Types type : heightmapTypes) {

          // Client
          if (type.sendToClient()) {
            final String typeString = DebugScreenOverlay.HEIGHTMAP_NAMES.get(type);
            final int blockY = clientChunk.getHeight(type, blockPos.getX(), blockPos.getZ());
            if (blockY > -1) {
              highestBlock.append("  ").append(typeString).append(": ").append(blockY);
            }
          }

          // Server
          if (type.keepAfterWorldgen() && serverWorld instanceof ServerLevel) {
            if (serverChunk == null) {
              serverChunk = clientChunk;
            }

            final String typeString = DebugScreenOverlay.HEIGHTMAP_NAMES.get(type);

            final int blockY = serverChunk.getHeight(type, blockPos.getX(), blockPos.getZ());
            if (blockY > -1) {
              highestBlockServer.append("  ").append(typeString).append(": ").append(blockY);
            }
          }
        }

        // Local Difficulty
        if (blockPos.getY() >= 0 && blockPos.getY() < 256) {
          final float moonSize;
          final long inhabitedTime;

          moonSize = serverWorld.getMoonBrightness();

          inhabitedTime = Objects.requireNonNullElse(serverChunk, clientChunk).getInhabitedTime();

          final DifficultyInstance localDifficulty = new DifficultyInstance(serverWorld.getDifficulty(), serverWorld.getDayTime(), inhabitedTime, moonSize);
          localDifficultyString = String.format("%.2f  " + I18n.get("text.betterf3.line.clamped") + ": %.2f", localDifficulty.getEffectiveDifficulty(), localDifficulty.getSpecialMultiplier());
        }

        if (integratedServer != null) {
          final RandomSource slimeChunk = WorldgenRandom.seedSlimeChunk(chunkPos.x, chunkPos.z, ((WorldGenLevel) serverWorld).getSeed(), 0x3ad8025fL);
          slimeChunkString = String.format("%s", I18n.get((slimeChunk.nextInt(10) == 0 ) ? "text.betterf3.line.slime_chunk.true" : "text.betterf3.line.slime_chunk.false"));
        } else {
          slimeChunkString = String.format("%s", I18n.get("text.betterf3.line.slime_chunk.unknown"));
        }
      }
    }

    // Dimension
    if (client.level != null) {
      lines.get(0).value(client.level.dimension().location());
    }

    if (cameraEntity != null) {
      final Direction facing = cameraEntity.getDirection();

      final String facingString = Utils.facingString(facing);
      // Facing
      lines.get(1).value(String.format("%s (%s)", I18n.get("text.betterf3.line." + facing.toString().toLowerCase()), facingString));
      // Rotation
      final String yaw = String.format("%.1f", Mth.wrapDegrees(cameraEntity.getYRot()));
      final String pitch = String.format("%.1f", Mth.wrapDegrees(cameraEntity.getXRot()));
      lines.get(2).value(I18n.get("format.betterf3.rotation", yaw, pitch));
    }

    // Client Light
    lines.get(3).value(chunkLightString);
    // Server Light
    lines.get(4).value(chunkLightServerString);
    // Highest Block
    lines.get(5).value(highestBlock.toString().trim());
    // Highest Block (Server)
    lines.get(6).value(highestBlockServer.toString().trim());

    // Local Difficulty
    lines.get(8).value(localDifficultyString);
    // Ticks in the day
    lines.get(9).value(Long.valueOf(client.level.getDayTime() % 24000L).intValue());
    // Days played
    lines.get(10).value(client.level.getDayTime() / 24000L);

    // Slime chunk
    lines.get(11).value(slimeChunkString.trim());
  }
}

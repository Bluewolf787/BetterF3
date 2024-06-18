package me.cominixo.betterf3.utils;

import com.google.common.base.Strings;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.ArrayList;
import java.util.List;
import me.cominixo.betterf3.config.GeneralOptions;
import me.cominixo.betterf3.modules.BaseModule;
import me.cominixo.betterf3.modules.MiscLeftModule;
import me.cominixo.betterf3.modules.MiscRightModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import static me.cominixo.betterf3.utils.Utils.xPos;

/**
 * Universal render methods to render the debug screen.
 */
public final class DebugRenderer {

  private DebugRenderer() {
    // Do nothing
  }

  /**
   * Lets us draw in batches.
   *
   * @param minecraft The Minecraft instance
   * @param font The font renderer
   * @param pos The position
   * @param list The list of Text
   * @param matrixStack The MatrixStack
   * @return VertexConsumerProvider
   */
  public static MultiBufferSource.BufferSource immediate(final Minecraft minecraft, final Font font, final PositionEnum pos,
                                                         final List<Component> list, final PoseStack matrixStack) {

    final float f = (float) (GeneralOptions.backgroundColor >> 24 & 255) / 255.0F;
    final float g = (float) (GeneralOptions.backgroundColor >> 16 & 255) / 255.0F;
    final float h = (float) (GeneralOptions.backgroundColor >> 8 & 255) / 255.0F;
    final float k = (float) (GeneralOptions.backgroundColor & 255) / 255.0F;
    RenderSystem.setShader(GameRenderer::getPositionColorShader);
    final BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();

    for (int i = 0; i < list.size(); i++) {
      final int height = 9;
      final int width = font.width(list.get(i).getString());
      if (width == 0) {
        continue;
      }
      final int y = 2 + height * i;

      int x1;
      int x2;
      int y1;
      int y2;
      int j;

      int windowWidth;
      if (pos == PositionEnum.RIGHT) {
        windowWidth = (int) (minecraft.getWindow().getGuiScaledWidth() / GeneralOptions.fontScale) - 2 - width;
        if (GeneralOptions.enableAnimations) {
          windowWidth += xPos;
        }

        x1 = windowWidth - 1;
        x2 = windowWidth + width + 1;
      } else {
        windowWidth = 2;

        if (GeneralOptions.enableAnimations) {
          windowWidth -= xPos;
        }
        x1 = windowWidth - 1;
        x2 = width + 1 + windowWidth;
      }
      y1 = y - 1;
      y2 = y + height - 1;

      final Matrix4f matrix = matrixStack.last().pose();

      if (x1 < x2) {
        j = x1;
        x1 = x2;
        x2 = j;
      }

      if (y1 < y2) {
        j = y1;
        y1 = y2;
        y2 = j;
      }

      bufferBuilder.addVertex(matrix, (float) x1, (float) y2, 0.0F).setColor(g, h, k, f);
      bufferBuilder.addVertex(matrix, (float) x2, (float) y2, 0.0F).setColor(g, h, k, f);
      bufferBuilder.addVertex(matrix, (float) x2, (float) y1, 0.0F).setColor(g, h, k, f);
      bufferBuilder.addVertex(matrix, (float) x1, (float) y1, 0.0F).setColor(g, h, k, f);

    }
    try {
      BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
    } catch (final IllegalStateException ignored) {
      // Ignore
    }
    RenderSystem.disableBlend();

    return Minecraft.getInstance().renderBuffers().bufferSource();

  }

  /**
   * Renders the right side text.
   *
   * @param list the list of {@link Component}s to draw
   * @param context Draw Context
   * @param minecraft Minecraft Client
   * @param font the Font Renderer
   * @param additional Additional text to draw
   */
  public static void drawRightText(final List<Component> list,
                                   final GuiGraphics context, final Minecraft minecraft,
                                   final Font font, @Nullable final List<String> additional) {

    if (additional != null) {
      additional.forEach((text) -> list.add(Component.nullToEmpty(text)));
    }

    final MultiBufferSource.BufferSource immediate = immediate(minecraft, font, PositionEnum.RIGHT, list, context.pose());

    for (int i = 0; i < list.size(); i++) {

      if (!Strings.isNullOrEmpty(list.get(i).getString())) {
        final int height = 9;
        final int width = font.width(list.get(i).getString());
        int windowWidth = (int) (minecraft.getWindow().getGuiScaledWidth() / GeneralOptions.fontScale) - 2 - width;
        if (GeneralOptions.enableAnimations) {
          windowWidth += xPos;
        }
        final int y = 2 + height * i;

        font.drawInBatch(list.get(i), windowWidth, y, 0xE0E0E0, GeneralOptions.shadowText, context.pose().last().pose(), immediate, Font.DisplayMode.NORMAL, 0, 15728880);
      }
    }
    immediate.endBatch();

    context.pose().popPose();
  }

  /**
   * Renders the left side text.
   *
   * @param list the list of {@link Component}s to draw
   * @param context Draw Context
   * @param minecraft Minecraft Client
   * @param font the Font Renderer
   * @param additional Additional text to draw
   */
  public static void drawLeftText(final List<Component> list,
                                  final GuiGraphics context, final Minecraft minecraft,
                                  final Font font, @Nullable final List<String> additional) {
    if (additional != null) {
      additional.forEach((text) -> list.add(Component.nullToEmpty(text)));
    }
    final MultiBufferSource.BufferSource immediate = immediate(minecraft, font, PositionEnum.LEFT, list, context.pose());

    for (int i = 0; i < list.size(); i++) {

      if (!Strings.isNullOrEmpty(list.get(i).getString())) {

        final int height = 9;
        final int y = 2 + height * i;
        int xPosLeft = 2;

        if (GeneralOptions.enableAnimations) {
          xPosLeft -= xPos;
        }

        font.drawInBatch(list.get(i), xPosLeft, y, 0xE0E0E0, GeneralOptions.shadowText, context.pose().last().pose(), immediate, Font.DisplayMode.NORMAL, 0, 15728880);
      }
    }
    immediate.endBatch();
  }

  /**
   * Gets a list of {@link Component}s from modules for either the left or right side of the screen.
   *
   * @param minecraft The Minecraft instance
   * @param left Whether the modules are on the left or right
   * @param gameInformation The game information string list
   * @param systemInformation The system information string list
   * @return the right side modules
   */
  public static List<Component> newText(final Minecraft minecraft, final boolean left, final List<String> gameInformation,
                                      final List<String> systemInformation) {

    final List<Component> list = new ArrayList<>();

    for (final BaseModule module : left ? BaseModule.modules : BaseModule.modulesRight) {
      if (!module.enabled) {
        continue;
      }
      if (module instanceof MiscRightModule) {
        ((MiscRightModule) module).update(systemInformation);
      } else if (module instanceof MiscLeftModule) {
        ((MiscLeftModule) module).update(gameInformation);
      } else {
        module.update(minecraft);
      }

      list.addAll(module.linesFormatted(minecraft.showOnlyReducedInfo()));
      if (GeneralOptions.spaceEveryModule) {
        list.add(Component.nullToEmpty(""));
      }
    }

    return list;
  }
}

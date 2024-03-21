package me.cominixo.betterf3.modules;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import me.cominixo.betterf3.utils.DebugLine;
import me.cominixo.betterf3.utils.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;

/**
 * The Server module.
 */
public class ServerModule extends BaseModule {

  /**
   * Instantiates a new Server module.
   */
  public ServerModule() {
    this.defaultNameColor = TextColor.fromLegacyFormat(ChatFormatting.GRAY);
    this.defaultValueColor = TextColor.fromLegacyFormat(ChatFormatting.YELLOW);

    this.nameColor = defaultNameColor;
    this.valueColor = defaultValueColor;

    lines.add(new DebugLine("server_tick", "format.betterf3.server_tick", true));
    lines.add(new DebugLine("packets_sent"));
    lines.add(new DebugLine("packets_received"));

    for (final DebugLine line : lines) {
      line.inReducedDebug = true;
    }
  }

  /**
   * Updates the Server module.
   *
   * @param client the Minecraft client
   */
  public void update(final Minecraft client) {
    final IntegratedServer integratedServer = client.getSingleplayerServer();

    String serverString = "";
    if (integratedServer != null) {
      serverString = I18n.get("text.betterf3.line.integrated_server");
    } else if (client.player != null) {
      serverString = Objects.requireNonNull(client.getConnection()).serverBrand();
    }

    if (client.getConnection() != null) {
      final Connection clientConnection = client.getConnection().getConnection();
      final float packetsSent = clientConnection.getAverageSentPackets();
      final float packetsReceived = clientConnection.getAverageReceivedPackets();

      lines.get(1).value(Math.round(packetsSent));
      lines.get(2).value(Math.round(packetsReceived));
    }
    String tickString = "";
    if (integratedServer != null) {
      tickString = Integer.toString(Math.round(integratedServer.getCurrentSmoothedTickTime()));
    }

    final List<MutableComponent> serverStringList = new LinkedList<>(Arrays.asList(Utils.styledText(serverString, nameColor), Utils.styledText(tickString, nameColor)));

    if (tickString.isEmpty()) {
      lines.get(0).format("format.betterf3.no_format");
      serverStringList.remove(1);
    }

    lines.get(0).value(serverStringList);
  }
}

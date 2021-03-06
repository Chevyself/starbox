package me.googas.starbox.compatibilities.viaversion.channels;

import java.util.UUID;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.googas.starbox.modules.channels.Channel;
import me.googas.starbox.modules.channels.PlayerChannel;
import me.googas.starbox.utility.Versions;

/**
 * Represents a {@link PlayerChannel} which methods may change due to being in a different protocol
 * version.
 */
public class ProtocolPlayerChannel implements PlayerChannel {

  @NonNull @Getter @Setter private Versions.Player version;
  @NonNull private final UUID uniqueId;

  /**
   * Start the channel.
   *
   * @param uuid the unique id of the player
   */
  protected ProtocolPlayerChannel(@NonNull UUID uuid) {
    this.uniqueId = uuid;
  }

  @Override
  public @NonNull UUID getUniqueId() {
    return uniqueId;
  }

  @Override
  public @NonNull ProtocolPlayerChannel sendTitle(
      String title, String subtitle, int fadeIn, int stay, int fadeOut) {
    if (this.version.getBukkit() < 8) {
      if (title != null) this.send(title);
      if (title != null) this.send(subtitle);
      return this;
    } else {
      return (ProtocolPlayerChannel)
          PlayerChannel.super.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }
  }

  @Override
  public @NonNull Channel setTabList(String header, String bottom) {
    if (this.version.getBukkit() >= 8) {
      return PlayerChannel.super.setTabList(header, bottom);
    }
    return this;
  }
}

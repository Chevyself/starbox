package me.googas.starbox.modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

/**
 * The module registry takes care of modules: engaging, disengaging, getting modules etc.
 *
 * <p>Starbox has a default module registry but this can also be implemented by any plugin
 */
public class ModuleRegistry {

  @NonNull @Getter private final Plugin plugin;
  @NonNull @Getter private final List<Module> engaged = new ArrayList<>();

  /**
   * Create the module registry.
   *
   * @param plugin the plugin that registers the modules
   */
  public ModuleRegistry(@NonNull Plugin plugin) {
    this.plugin = plugin;
  }

  /**
   * Get a module by its class.
   *
   * @param clazz the class of the module to get
   * @param <T> the type of the module
   * @return a {@link Optional} holding the nullable module
   */
  public <T extends Module> Optional<T> get(@NonNull Class<T> clazz) {
    return this.engaged.stream()
        .filter(module -> clazz.isAssignableFrom(module.getClass()))
        .map(clazz::cast)
        .findFirst();
  }

  /**
   * Engage a collection of modules.
   *
   * @param modules the modules to engage
   * @return true if at least one module was engaged
   */
  public boolean engage(@NonNull Collection<? extends Module> modules) {
    boolean added = false;
    for (Module module : modules) {
      if (this.engage(module)) added = true;
    }
    return added;
  }

  /**
   * Disengage a collection of modules.
   *
   * @param modules the modules to disengage
   * @return true if at least one module was disengaged
   */
  public boolean disengage(@NonNull Collection<? extends Module> modules) {
    boolean removed = false;
    for (Module module : modules) {
      if (this.disengage(module)) removed = true;
    }
    return removed;
  }

  /**
   * Engage an array of modules.
   *
   * @param modules the modules to engage
   * @return true if at least one module was engaged
   */
  public boolean engage(@NonNull Module... modules) {
    return this.engage(Arrays.asList(modules));
  }

  /**
   * Disengage an array of modules.
   *
   * @param modules the modules to disengage
   * @return true if at least one module was disengaged
   */
  public boolean disengage(@NonNull Module... modules) {
    return this.disengage(Arrays.asList(modules));
  }

  /**
   * Engage a module. This will get the plugin manager registering events with the module and {@link
   * #plugin}
   *
   * @param module the module to engage
   * @return true if the module was engaged
   */
  public boolean engage(@NonNull Module module) {
    this.engaged.add(module);
    Bukkit.getServer().getPluginManager().registerEvents(module, this.plugin);
    module.onEnable();
    return true;
  }

  /**
   * Disengage a module. This will unregister every listener from the handler list
   *
   * @param module the module to disengage
   * @return true if the module was disengaged
   */
  public boolean disengage(@NonNull Module module) {
    if (this.engaged.contains(module) && this.engaged.remove(module)) {
      HandlerList.unregisterAll(module);
      module.onDisable();
      return true;
    }
    return false;
  }

  /**
   * Get a module by its class and require that it is not null.
   *
   * @param typeOfT the class of module to get
   * @param <T> the type of the module
   * @return the required module
   * @throws NullPointerException if the module is not found
   */
  @NonNull
  public <T extends Module> T require(@NonNull Class<T> typeOfT) {
    return this.get(typeOfT)
        .orElseThrow(() -> new NullPointerException(typeOfT + " module has not been engaged"));
  }

  /** Disengage all engaged modules. */
  public void disengageAll() {
    new ArrayList<>(this.engaged).forEach(this::disengage);
  }
}

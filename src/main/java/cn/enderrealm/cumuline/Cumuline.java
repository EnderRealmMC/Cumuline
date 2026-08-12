package cn.enderrealm.cumuline;

import cn.enderrealm.cumuline.api.CumulineApi;
import cn.enderrealm.cumuline.internal.CumulineProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin entry point for Cumuline.
 */
public final class Cumuline extends JavaPlugin {
    private CumulineProvider provider;

    /**
     * Registers the public Cumuline service.
     */
    @Override
    public void onEnable() {
        provider = new CumulineProvider();
        getServer().getServicesManager().register(
                CumulineApi.class,
                provider,
                this,
                org.bukkit.plugin.ServicePriority.Normal
        );
    }

    /**
     * Unregisters the public Cumuline service.
     */
    @Override
    public void onDisable() {
        getServer().getServicesManager().unregister(CumulineApi.class, provider);
        provider = null;
    }
}

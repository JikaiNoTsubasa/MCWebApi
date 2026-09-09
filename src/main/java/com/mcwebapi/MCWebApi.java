package com.mcwebapi;

import com.mcwebapi.config.Config;
import com.mcwebapi.web.WebServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(MCWebApi.MODID)
public class MCWebApi {
    public static final String MODID = "mcwebapi";
    private static final Logger LOGGER = LoggerFactory.getLogger(MCWebApi.class);
    private static WebServer webServer;

    public MCWebApi() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);

        Config.load();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("MCWebApi mod initialized");
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        LOGGER.info("Starting web server on port {}", Config.getPort());
        try {
            webServer = new WebServer(Config.getPort(), event.getServer());
            webServer.start();
            LOGGER.info("Web API server started successfully on port {}", Config.getPort());
        } catch (Exception e) {
            LOGGER.error("Failed to start web server", e);
        }
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        if (webServer != null) {
            LOGGER.info("Stopping web server");
            webServer.stop();
            webServer = null;
        }
    }
}

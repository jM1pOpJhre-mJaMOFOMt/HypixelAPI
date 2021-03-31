package com.nur.hypixelapi;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import net.hypixel.api.HypixelAPI;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import java.util.UUID;


@Mod(modid = HypixelAPIMod.MODID, version = HypixelAPIMod.VERSION)
public class HypixelAPIMod {
    public static final String MODID = "hypixelapi";
    public static final String VERSION = "1.0";

    public static String apiKey = "";

    public static HypixelAPI api = null;

    public static Gson gson = new Gson();

    public static final JsonParser parser = new JsonParser();

    @EventHandler
    public void init(FMLInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new SetAPIKeyCommand());
        ClientCommandHandler.instance.registerCommand(new BedwarsStatsCommand());
        ClientCommandHandler.instance.registerCommand(new PartyGamesStatsCommand());
        MinecraftForge.EVENT_BUS.register(new HypixelListener());
        apiKey = ConfigHandler.getString("api", "key", "");
        if (!"".equals(apiKey)) api = new HypixelAPI(UUID.fromString(apiKey));
    }

    public static void setAPIKey(String key) {
        HypixelAPIMod.api = new HypixelAPI(UUID.fromString(key.toLowerCase()));
        HypixelAPIMod.apiKey = key.toLowerCase();
        ConfigHandler.setString("api", "key", key);
    }
}

package com.nur.hypixelapi;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
        apiKey = ConfigHandler.getString("api","key","");
        if(!"".equals(apiKey))api = new HypixelAPI(UUID.fromString(apiKey));
    }

    public static void setAPIKey(String key) {
        HypixelAPIMod.api = new HypixelAPI(UUID.fromString(key.toLowerCase()));
        HypixelAPIMod.apiKey = key.toLowerCase();
        ConfigHandler.setString("api","key", key);
    }

    private static final JsonObject triggerJson = new JsonParser().parse("{ \"triggers_format\": \"2\",  \"servers\": { \"^(?:.+\\\\.)?hypixel\\\\.(?:net|io)$\": { \"gg_triggers\": { \"triggers\": [ \"^ +1st Killer - ?\\\\[?\\\\w*\\\\+*\\\\]? \\\\w+ - \\\\d+(?: Kills?)?$\", \"^ *1st (?:Place ?)?(?:-|:)? ?\\\\[?\\\\w*\\\\+*\\\\]? \\\\w+(?: : \\\\d+| - \\\\d+(?: Points?)?| - \\\\d+(?: x .)?| \\\\(\\\\w+ .{1,6}\\\\) - \\\\d+ Kills?|: \\\\d+:\\\\d+| - \\\\d+ (?:Zombie )?(?:Kills?|Blocks? Destroyed)| - \\\\[LINK\\\\])?$\", \"^ +Winn(?:er #1 \\\\(\\\\d+ Kills\\\\): \\\\w+ \\\\(\\\\w+\\\\)|er(?::| - )(?:Hiders|Seekers|Defenders|Attackers|PLAYERS?|MURDERERS?|Red|Blue|RED|BLU|\\\\w+)(?: Team)?|ers?: ?\\\\[?\\\\w*\\\\+*\\\\]? \\\\w+(?:, ?\\\\[?\\\\w*\\\\+*\\\\]? \\\\w+)?|ing Team ?[\\\\:-] (?:Animals|Hunters|Red|Green|Blue|Yellow|RED|BLU|Survivors|Vampires))$\", \"^ +Alpha Infected: \\\\w+ \\\\(\\\\d+ infections?\\\\)$\", \"^ +Murderer: \\\\w+ \\\\(\\\\d+ Kills?\\\\)$\", \"^ +You survived \\\\d+ rounds!$\", \"^ +(?:UHC|SkyWars|The Bridge|Sumo|Classic|OP|MegaWalls|Bow|NoDebuff|Blitz|Combo|Bow Spleef) (?:Duel|Doubles|Teams|Deathmatch|2v2v2v2|3v3v3v3)? ?- \\\\d+:\\\\d+$\", \"^ +They captured all wools!$\", \"^ +Game over!$\", \"^ +[\\\\d\\\\.]+k?/[\\\\d\\\\.]+k? \\\\w+$\", \"^ +(?:Criminal|Cop)s won the game!$\", \"^ +\\\\[?\\\\w*\\\\+*\\\\]? \\\\w+ - \\\\d+ Final Kills$\", \"^ +Zombies - \\\\d*:?\\\\d+:\\\\d+ \\\\(Round \\\\d+\\\\)$\", \"^ +. YOUR STATISTICS .$\" ], \"casual_triggers\": [ \"^MINOR EVENT! .+ in .+ ended$\", \"^DRAGON EGG OVER! Earned [\\\\d,]+XP [\\\\d,]g clicking the egg \\\\d+ times$\", \"^GIANT CAKE! Event ended! Cake's gone!$\", \"^PIT EVENT ENDED: .+ \\\\[INFO\\\\]$\", \"^\\\\[?\\\\w*\\\\+*\\\\]? ?\\\\w+ caught ?a?n? .+! .*$\" ] }, \"other\": { \"msg\": \"/ac \" } } } }").getAsJsonObject();

}

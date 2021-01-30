package com.nur.hypixelapi;

import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class BedwarsStatsCommand implements ICommand {
    @Override
    public String getCommandName() {
        return "bwstats";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "bwstats";
    }

    @Override
    public List<String> getCommandAliases() {
        List<String> aliases = new ArrayList<String>();
        aliases.add("bwstat");
        aliases.add("bedwarsstat");
        aliases.add("bedwarsstats");
        return aliases;
    }


    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if("".equals(HypixelAPIMod.apiKey)){
            sender.addChatMessage(new ChatComponentText(""+EnumChatFormatting.RED+ EnumChatFormatting.BOLD+"(!) "+EnumChatFormatting.RED+"API Key not set! Set with /setapikey"));
            return;
        }

        List<String> playersToCheck = new ArrayList<String>();

        if(args.length==0) {
            Collection<NetworkPlayerInfo> onlinePlayers = Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap();
            for (NetworkPlayerInfo player : onlinePlayers) {
                playersToCheck.add(player.getGameProfile().getName());
            }
        } else if (args[0].matches("^\\w{2,16}$")) playersToCheck.add(args[0]);
        else {
            sender.addChatMessage(new ChatComponentText(""+EnumChatFormatting.RED+ EnumChatFormatting.BOLD+"(!) "+EnumChatFormatting.RED+args[0]+" is not a valid IGN!"));
            return;
        }

        for(final String player : playersToCheck) {
            new Thread(new Runnable() {
                final String playerName = player;
                final ChatStyle style = new ChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://plancke.io/hypixel/player/stats/"+playerName));
                public void run() {
                    try {
                        URL url = new URL("https://api.hypixel.net/player?key=" + HypixelAPIMod.apiKey + "&name=" + playerName);
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setRequestMethod("GET");
                        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        String inputLine;
                        StringBuilder response = new StringBuilder();
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();
                        JsonObject j = HypixelAPIMod.parser.parse(response.toString()).getAsJsonObject();

                        if(!j.has("player") || j.get("player").isJsonNull() || !j.get("player").getAsJsonObject().has("stats") || !j.get("player").getAsJsonObject().get("stats").getAsJsonObject().has("Bedwars")) {
                            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("" + EnumChatFormatting.RED + EnumChatFormatting.BOLD + "(!) " + EnumChatFormatting.RED + "Error while looking up " + playerName+"'s Bedwars stats.").setChatStyle(style));
                            return;
                        }

                        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("" + EnumChatFormatting.WHITE + EnumChatFormatting.BOLD + "(!) " + EnumChatFormatting.WHITE + "Player Information: " + (j.get("player").getAsJsonObject().has("achievements")&&j.get("player").getAsJsonObject().get("achievements").getAsJsonObject().has("bedwars_level")?EnumChatFormatting.RED+"\u272B"+j.get("player").getAsJsonObject().get("achievements").getAsJsonObject().get("bedwars_level").getAsInt()+" ":"") + EnumChatFormatting.RED + playerName).setChatStyle(style));

                        JsonObject bedwarsStats = j.get("player").getAsJsonObject().get("stats").getAsJsonObject().get("Bedwars").getAsJsonObject();


                        int gamesPlayed = 0;

                        DecimalFormat df = new DecimalFormat("0.00");

                        if(bedwarsStats.has("games_played_bedwars")) gamesPlayed = bedwarsStats.get("games_played_bedwars").getAsInt();
                        if(gamesPlayed!=0)Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("" + EnumChatFormatting.WHITE + EnumChatFormatting.BOLD + " * " + EnumChatFormatting.GRAY + "Bedwars Games Played: " + EnumChatFormatting.RED + bedwarsStats.get("games_played_bedwars").getAsInt()).setChatStyle(style));
                        if(bedwarsStats.has("final_kills_bedwars")) {
                            int finalKills = bedwarsStats.get("final_kills_bedwars").getAsInt();
                            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("" + EnumChatFormatting.WHITE + EnumChatFormatting.BOLD + " * " + EnumChatFormatting.GRAY + "Final Kills: " + EnumChatFormatting.RED + finalKills+(gamesPlayed!=0?EnumChatFormatting.GRAY+" ("+EnumChatFormatting.RED+df.format((double)finalKills/(double)gamesPlayed)+EnumChatFormatting.GRAY+" avg.)":"")).setChatStyle(style));
                        }
                        if(bedwarsStats.has("beds_broken_bedwars")){
                            int bedsBroken = bedwarsStats.get("beds_broken_bedwars").getAsInt();
                            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("" + EnumChatFormatting.WHITE + EnumChatFormatting.BOLD + " * " + EnumChatFormatting.GRAY + "Beds Broken: " + EnumChatFormatting.RED + bedsBroken +(gamesPlayed!=0?EnumChatFormatting.GRAY+" ("+EnumChatFormatting.RED+df.format((double)bedsBroken/(double)gamesPlayed)+EnumChatFormatting.GRAY+" avg.)":"")).setChatStyle(style));
                        }
                        if(bedwarsStats.has("winstreak")) Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("" + EnumChatFormatting.WHITE + EnumChatFormatting.BOLD + " * " + EnumChatFormatting.GRAY + "Winstreak: " + EnumChatFormatting.RED + bedwarsStats.getAsJsonObject().get("winstreak").getAsString()).setChatStyle(style));
                        if(bedwarsStats.has("emerald_resources_collected_bedwars")) Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("" + EnumChatFormatting.WHITE + EnumChatFormatting.BOLD + " * " + EnumChatFormatting.GRAY + "Collected: " + EnumChatFormatting.GREEN + bedwarsStats.get("emerald_resources_collected_bedwars").getAsInt()+EnumChatFormatting.RED+" / " + EnumChatFormatting.AQUA+ bedwarsStats.get("diamond_resources_collected_bedwars").getAsInt()+EnumChatFormatting.RED+" / "+EnumChatFormatting.GOLD+bedwarsStats.get("gold_resources_collected_bedwars").getAsInt()+EnumChatFormatting.RED+" / "+EnumChatFormatting.GRAY+bedwarsStats.get("iron_resources_collected_bedwars").getAsInt()).setChatStyle(style));


                        //Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("" + EnumChatFormatting.WHITE + EnumChatFormatting.BOLD + " * " + EnumChatFormatting.GRAY + "ISP: " + EnumChatFormatting.RED + j.get("isp").getAsString()).setChatStyle(style));
                    } catch (IOException e) {
                        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("" + EnumChatFormatting.RED + EnumChatFormatting.BOLD + "(!) " + EnumChatFormatting.RED + "Error while looking up " + playerName+"'s stats. Try /setapikey").setChatStyle(style));
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(ICommand o) {
        return 0;
    }
}

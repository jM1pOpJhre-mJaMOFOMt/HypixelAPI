package com.nur.hypixelapi;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HypixelListener {
    @SubscribeEvent
    public void onPlayerJoinedServer(EntityJoinWorldEvent event) {
        if (event.entity != Minecraft.getMinecraft().thePlayer) return;
        if (Minecraft.getMinecraft().getCurrentServerData() != null && !Minecraft.getMinecraft().getCurrentServerData().serverIP.toLowerCase().matches("^(?:.+\\.)?hypixel\\.(?:net|io)$")) return;
        if ("".equals(HypixelAPIMod.apiKey)) Minecraft.getMinecraft().thePlayer.sendChatMessage("/api new");
    }


    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        String msg = event.message.getUnformattedText();
        String regex = "^Your new API key is ([a-fA-F0-9]{8}-[a-fA-F0-9]{4}-4[a-fA-F0-9]{3}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12})$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(msg);
        if (matcher.find()) {
            HypixelAPIMod.setAPIKey(matcher.group(1));
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("" + EnumChatFormatting.GREEN + EnumChatFormatting.BOLD + "(!) " + EnumChatFormatting.GREEN + "Set API key to " + matcher.group(1) + "!"));
        } else return;
    }
}

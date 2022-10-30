package net.herobrine.clashroyale.beta;

import com.google.common.base.Strings;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class Colorize {

    public static void message(String message, CommandSender sender){

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static String color(String message){

        String split = ChatColor.translateAlternateColorCodes('&', message).replace("%%bold%%", ChatColor.BOLD.toString());

        split.split("\n");


        return split;
    }



    public static void actionBar(String text, Player player){
        PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + ChatColor.translateAlternateColorCodes('&', text) + "\"}"),(byte)2);

        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
    }
    //    public static void title(String text, Player player, String textSub){
//        PacketPlayOutTitle packet = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + ChatColor.translateAlternateColorCodes('&', text) + "\"}"));
//        PacketPlayOutTitle packetSub = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + ChatColor.translateAlternateColorCodes('&', textSub) + "\"}"));
//
//
//        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
//        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packetSub);
//
//    }
//    public static void title(String text, String textSub){
//        for(Player player : Bukkit.getOnlinePlayers()) {
//            PacketPlayOutTitle packet = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + ChatColor.translateAlternateColorCodes('&', text) + "\"}"));
//            PacketPlayOutTitle packetSub = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + ChatColor.translateAlternateColorCodes('&', textSub) + "\"}"));
//
//
//            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
//            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetSub);
//        }
//    }
    public static void sendErrorMessage(CommandSender player, ErrorMessage message){
        player.sendMessage(message.getMessage());
    }

    public static String getProgressBar(int current, int max, int totalBars, String nonCompletedSymbol, String completedSymbol, ChatColor completedColor,
                                        ChatColor notCompletedColor) {
        float percent = (float) current / max;
        int progressBars = (int) (totalBars * percent);

        return Strings.repeat("" + completedColor + completedSymbol, progressBars)
                + Strings.repeat("" + notCompletedColor + nonCompletedSymbol, totalBars - progressBars);
    }

    public static List<String> colorizeList(List<String> lore){




        return lore.stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList());
    }
    public static List<String> symbolizeList(List<String> lore){




        return lore.stream().map(s -> ChatColor.translateAlternateColorCodes('&', s.replace("%speed%","&f✦ Speed"))).collect(Collectors.toList());
    }
    static String[] colors = new String[]{"&c", "&e", "&a", "&3", "&9"};

    public static String rainbowString(String s){

        String text = "";

        for(char l : s.toCharArray()){

            String ab = Character.toString(l);

            if(!ab.equalsIgnoreCase(" ")){

                text = colors[0] + ab;


                s = text;
            }
        }


        return s;
    }


}

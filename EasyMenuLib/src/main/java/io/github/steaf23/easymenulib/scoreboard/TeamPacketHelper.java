package io.github.steaf23.easymenulib.scoreboard;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import io.github.steaf23.easymenulib.EasyMenuLibrary;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class TeamPacketHelper
{
    public static void createTeamVisibleToPlayer(Player player, String identifier, Component displayName, Component prefix, Component suffix, Collection<String> entries) {
        WrapperPlayServerTeams.ScoreBoardTeamInfo info = new WrapperPlayServerTeams.ScoreBoardTeamInfo(
                displayName,
                prefix,
                suffix,
                WrapperPlayServerTeams.NameTagVisibility.ALWAYS,
                WrapperPlayServerTeams.CollisionRule.ALWAYS,
                null,
                WrapperPlayServerTeams.OptionData.NONE
        );
        PacketWrapper<WrapperPlayServerTeams> packet = new WrapperPlayServerTeams(identifier, WrapperPlayServerTeams.TeamMode.CREATE, info, entries);
        EasyMenuLibrary.sendPlayerPacket(player, packet);
    }

    public static void removeTeamVisibleToPlayer(Player player, String identifier) {
        PacketWrapper<WrapperPlayServerTeams> packet = new WrapperPlayServerTeams(identifier, WrapperPlayServerTeams.TeamMode.REMOVE, Optional.empty());
        EasyMenuLibrary.sendPlayerPacket(player, packet);
    }
}

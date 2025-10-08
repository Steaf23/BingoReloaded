package io.github.steaf23.bingoreloaded.api;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandlePaper;
import net.kyori.adventure.text.Component;

import java.util.Collection;

public class TeamPacketHelper
{
    public static void createTeamVisibleToPlayer(PlayerHandle player, String identifier, Component displayName, Component prefix, Component suffix, Collection<String> entries) {
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
        PacketEvents.getAPI().getPlayerManager().sendPacket(((PlayerHandlePaper) player).handle(), packet);
    }

    public static void removeTeamVisibleToPlayer(PlayerHandle player, String identifier) {
        PacketWrapper<WrapperPlayServerTeams> packet = new WrapperPlayServerTeams(identifier, WrapperPlayServerTeams.TeamMode.REMOVE, (WrapperPlayServerTeams.ScoreBoardTeamInfo)null);
        PacketEvents.getAPI().getPlayerManager().sendPacket(((PlayerHandlePaper) player).handle(), packet);
    }
}

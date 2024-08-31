package io.github.steaf23.bingoreloaded.settings;

import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.data.core.node.BranchNode;
import io.github.steaf23.bingoreloaded.data.core.node.NodeBuilder;
import io.github.steaf23.bingoreloaded.data.core.node.datatype.NodeDataType;
import io.github.steaf23.bingoreloaded.data.core.node.NodeSerializer;
import io.github.steaf23.bingoreloaded.gui.inventory.EffectOptionFlags;

import java.util.EnumSet;

public record BingoSettings(String card,
                            BingoGamemode mode,
                            CardSize size,
                            int seed,
                            PlayerKit kit,
                            EnumSet<EffectOptionFlags> effects,
                            int maxTeamSize,
                            boolean enableCountdown,
                            int countdownDuration,
                            int hotswapGoal) implements NodeSerializer
{
    public BingoSettings(BranchNode node) {
        this(
                node.getString("card"),
                BingoGamemode.fromDataString(node.getString("mode")),
                CardSize.fromWidth(node.getInt("size")),
                node.getInt("seed"),
                PlayerKit.fromConfig(node.getString("kit")),
                NodeBuilder.enumSetFromList(EffectOptionFlags.class, node.getList("effects", NodeDataType.STRING)),
                node.getInt("team_size"),
                node.getBoolean("countdown", false),
                node.getInt("duration"),
                node.getInt("hotswap_goal", 10)
        );
    }

    @Override
    public BranchNode toNode() {
        return new NodeBuilder()
                .withString("card", card)
                .withString("mode", mode.getDataName())
                .withInt("size", size.size)
                .withInt("seed", seed)
                .withString("kit", kit.configName)
                .withList("effects", NodeDataType.STRING, NodeBuilder.enumSetToList(effects))
                .withInt("team_size", maxTeamSize)
                .withInt("duration", countdownDuration)
                .withBoolean("countdown", enableCountdown)
                .withInt("hotswap_goal", hotswapGoal)
                .getNode();
    }
}

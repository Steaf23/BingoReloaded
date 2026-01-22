package io.github.steaf23.bingoreloaded;

import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.events.AllWorldsLoadedEvent;
import io.github.steaf23.bingoreloaded.action.AutoBingoAction;
import io.github.steaf23.bingoreloaded.action.BingoAction;
import io.github.steaf23.bingoreloaded.action.BingoConfigAction;
import io.github.steaf23.bingoreloaded.api.CardDisplayInfo;
import io.github.steaf23.bingoreloaded.api.CardMenu;
import io.github.steaf23.bingoreloaded.api.TeamDisplay;
import io.github.steaf23.bingoreloaded.api.network.BingoClientManager;
import io.github.steaf23.bingoreloaded.data.config.BingoConfigurationData;
import io.github.steaf23.bingoreloaded.gameloop.BingoInteraction;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.gameloop.phase.PregameLobby;
import io.github.steaf23.bingoreloaded.interaction.GameItemInteraction;
import io.github.steaf23.bingoreloaded.item.GameItem;
import io.github.steaf23.bingoreloaded.lib.action.ActionTree;
import io.github.steaf23.bingoreloaded.lib.api.BingoReloadedRuntime;
import io.github.steaf23.bingoreloaded.lib.api.EntityType;
import io.github.steaf23.bingoreloaded.lib.api.HytaleServerSoftware;
import io.github.steaf23.bingoreloaded.lib.api.PlatformResolver;
import io.github.steaf23.bingoreloaded.lib.api.PlayerInput;
import io.github.steaf23.bingoreloaded.lib.api.ServerSoftware;
import io.github.steaf23.bingoreloaded.lib.api.WorldHandle;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.EmptyDisplay;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.SharedDisplay;
import io.github.steaf23.bingoreloaded.lib.data.core.DataAccessor;
import io.github.steaf23.bingoreloaded.lib.data.core.SnakeYamlDataAccessor;
import io.github.steaf23.bingoreloaded.lib.event.HytaleEventListener;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.ui.card.TextCardMenu;
import net.kyori.adventure.key.Key;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * This class serves as the entrypoint for your plugin. Use the setup method to register into game registries or add
 * event listeners.
 */
public class BingoReloadedHytale extends JavaPlugin implements BingoReloadedRuntime {

    private HytaleServerSoftware platform;

    private BingoReloaded bingo;

    public static HytaleEventListener EVENT_LISTENER;

    public BingoReloadedHytale(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        this.platform = new HytaleServerSoftware(this);
        PlatformResolver.set(platform);

        getEventRegistry().registerGlobal(AllWorldsLoadedEvent.class, event -> {
            bingo.serverReady();
            EVENT_LISTENER = new HytaleEventListener(getEventRegistry(), bingo.getGameManager().eventListener());
        });

        this.getCodecRegistry(Interaction.CODEC).register("game_item_secondary", GameItemInteraction.class, GameItemInteraction.CODEC);

        ConsoleMessenger.log("Let the Bingo Begin!");
        bingo = new BingoReloaded(this);

        bingo.load();
        bingo.enable();
    }

    @Override
    public DataAccessor getConfigData() {
        return new SnakeYamlDataAccessor("config");
    }

    @Override
    public Collection<DataAccessor> getDataToRegister() {
        return List.of(
                new SnakeYamlDataAccessor("scoreboards"),
                new SnakeYamlDataAccessor("placeholders"),
                new SnakeYamlDataAccessor("sounds"));
    }

    @Override
    public void setupConfig() {

    }

    @Override
    public Set<EntityType> getValidEntityTypesForStatistics() {
        return Set.of();
    }

    @Override
    public LanguageData getLanguageData(String language) {
        var lang = new SnakeYamlDataAccessor(language);
        var fallback = new SnakeYamlDataAccessor("languages/en_us");

        BingoReloaded.addDataAccessor(lang);
        BingoReloaded.addDataAccessor(fallback);

        return new LanguageData(lang, fallback);
    }

    @Override
    public void onLanguageUpdated() {

    }

    @Override
    public void onConfigReloaded(BingoConfigurationData config) {

    }

    @Override
    public void registerActions(BingoConfigurationData config) {
        registerCommand(new BingoAction(bingo, config, bingo.getGameManager()), "Basic bingo command, can be used to join the game or change settings, as well as general purpose command related to Bingo.");
        registerCommand(new AutoBingoAction(platform, bingo.getGameManager()), "Advanced bingo command, can be used to set up the game from the console.");
        registerCommand(new BingoConfigAction(config), "Edit config options in-game, no need to open a config file.");
    }

    @Override
    public WorldHandle createBingoWorld(String worldName, Key generationOptions) {
        return null;
    }

    @Override
    public ServerSoftware getServerSoftware() {
        return platform;
    }

    @Override
    public CardMenu createMenu(boolean textured, CardDisplayInfo displayInfo) {
        return new TextCardMenu(displayInfo);
    }

    @Override
    public void setPlayerUpForGame(BingoGame game, BingoParticipant participant) {
    }

    @Override
    public void openBingoMenu(PlayerHandle player, BingoSession session) {

    }

    @Override
    public void openTeamEditor(PlayerHandle player) {

    }

    @Override
    public void openBingoCreator(PlayerHandle player) {

    }

    @Override
    public void openTeamCardSelect(PlayerHandle player, BingoSession session) {
        BingoParticipant participant = session.teamManager.getPlayerAsParticipant(player);
        participant.showCard(null);
    }

    @Override
    public void openTeamSelector(PlayerHandle player, BingoSession session) {

    }

    @Override
    public void openVoteMenu(PlayerHandle player, PregameLobby lobby) {

    }

    @Override
    public TeamDisplay createTeamDisplay(BingoSession session) {
        return TeamDisplay.DUMMY_DISPLAY;
    }

    @Override
    public SharedDisplay gameDisplay() {
        return new EmptyDisplay();
    }

    @Override
    public SharedDisplay settingsDisplay() {
        return new EmptyDisplay();
    }

    @Override
    public BingoClientManager getClientManager() {
        return new BingoClientManager.DisabledClientManager();
    }

    @Override
    public StackHandle defaultStack(GameItem item) {
        return null;
    }

    @Override
    public void playerJoinedLobby(BingoSession session, PlayerHandle player) {

    }

    @Override
    public void droppedItemsOnDeath(BingoSession session, PlayerHandle player, Collection<StackHandle> items) {

    }

    @Override
    public boolean canItemBeUsedForInteraction(BingoSession session, PlayerHandle player, BingoInteraction interaction, StackHandle stack, PlayerInput input) {
        return false;
    }

    @Override
    public boolean canItemBeUsedInKit(StackHandle stack) {
        return true;
    }

    public void registerCommand(ActionTree tree, String description) {
        this.getCommandRegistry().registerCommand(new HytaleCommand(tree, description));
    }
}
package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.data.TeamData;
import io.github.steaf23.bingoreloaded.gui.base.*;
import io.github.steaf23.bingoreloaded.util.Message;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TeamEditorMenu extends PaginatedSelectionMenu
{
    private final TeamData teamData;

    private static final TeamData.TeamTemplate DEFAULT_NEW_TEAM = new TeamData.TeamTemplate("MyTeam", ChatColor.of("#808080"));

    private static final MenuItem RESTORE_DEFAULT = new MenuItem(2, 5, Material.TNT,
            "" + ChatColor.RED + ChatColor.BOLD + "Restore Default Teams",
            "This option will remove all created teams!");

    private static final MenuItem CREATE_TEAM = new MenuItem(6, 5, Material.EMERALD,
            "" + ChatColor.GREEN + ChatColor.BOLD + "Create New Team");

    public TeamEditorMenu(MenuManager manager) {
        super(manager, "Edit Teams", new ArrayList<>(), FilterType.DISPLAY_NAME);
        this.teamData = new TeamData();

        addAction(RESTORE_DEFAULT, p -> {
            teamData.reset();
            updateDisplay();
        });
        addAction(CREATE_TEAM, p -> {
            createTeamEditor("").open(p);
        });
    }

    public void updateDisplay() {
        clearFilter();
        clearItems();
        List<MenuItem> items = new ArrayList<>();

        var teamMap = teamData.getTeams();
        for (String key : teamMap.keySet()) {
            TeamData.TeamTemplate template = teamMap.get(key);
            items.add(MenuItem.createColoredLeather(template.color(), Material.LEATHER_HELMET)
                    .setName("" + ChatColor.RESET + template.color() + ChatColor.BOLD + template.name())
                    .setDescription("" + ChatColor.GRAY + ChatColor.ITALIC + key)
                    .setCompareKey(key));
        }
        addItemsToSelect(items.toArray(new MenuItem[]{}));
    }

    public BasicMenu createTeamEditor(String teamKey) {
        return new BasicMenu(TeamEditorMenu.this.getMenuManager(), "Edit Team", 3)
        {
            private TeamData.TeamTemplate templateToEdit = null;

            @Override
            public void beforeOpening(HumanEntity player) {
                if (templateToEdit == null) {
                    Map<String, TeamData.TeamTemplate> teams = teamData.getTeams();
                    if (teams.containsKey(teamKey)) {
                        templateToEdit = teams.get(teamKey);
                    } else {
                        templateToEdit = DEFAULT_NEW_TEAM;
                    }
                }

                // Add action to change the team's name.
                addAction(new MenuItem(2, 1, Material.WRITABLE_BOOK, templateToEdit.name()), (p) -> {
                    new UserInputMenu(getMenuManager(), "Edit team name", (result) -> {
                        // Update template
                        templateToEdit = new TeamData.TeamTemplate(result, templateToEdit.color());

                        // Update menu item
                        this.updateActionItem(new MenuItem(2, 1, Material.WRITABLE_BOOK, templateToEdit.name()));
                    }, p, templateToEdit.name());
                });
                // Add action to change the team's color.
                addAction(MenuItem.createColoredLeather(templateToEdit.color(), Material.LEATHER_CHESTPLATE)
                        .setName("" + templateToEdit.color() + ChatColor.BOLD + "Color")
                        .setSlot(MenuItem.slotFromXY(4, 1)), (p) -> {
                    new ColorPickerMenu(getMenuManager(), "Pick team color", (result) -> {
                        // Update template
                        templateToEdit = new TeamData.TeamTemplate(templateToEdit.name(), result);

                        // Update menu item
                        MenuItem newItem = MenuItem.createColoredLeather(templateToEdit.color(), Material.LEATHER_CHESTPLATE)
                                .setName("" + templateToEdit.color() + ChatColor.BOLD + "Color")
                                .setSlot(MenuItem.slotFromXY(4, 1));
                        this.updateActionItem(newItem);
                    }).open(p);
                });
                addCloseAction(new MenuItem(6, 1, Material.BARRIER,
                        "" + ChatColor.RED + ChatColor.BOLD + BingoTranslation.MENU_EXIT.translate()));
                super.beforeOpening(player);
            }

            @Override
            public void beforeClosing(HumanEntity player) {
                teamData.addTeam(teamKey.isEmpty() ? teamData.getNewTeamId() : teamKey, templateToEdit);
                super.beforeClosing(player);
            }
        };
    }

    @Override
    public void onOptionClickedDelegate(InventoryClickEvent event, MenuItem clickedOption, HumanEntity player) {
        String key = clickedOption.getCompareKey();
        if (event.getClick() == ClickType.RIGHT) {
            teamData.removeTeam(key);
            updateDisplay();
        } else {
            createTeamEditor(key).open(player);
        }
    }

    @Override
    public void beforeOpening(HumanEntity player) {
        updateDisplay();
        super.beforeOpening(player);
    }
}

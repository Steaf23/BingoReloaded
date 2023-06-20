package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.gui.base.*;
import io.github.steaf23.bingoreloaded.gui.base.MenuItem;
import io.github.steaf23.bingoreloaded.gui.base.MenuManager;
import io.github.steaf23.bingoreloaded.gui.base.PaginatedSelectionMenu;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;

public class TeamEditorMenu extends PaginatedSelectionMenu
{
    public TeamEditorMenu(MenuManager manager) {
        super(manager, "Edit Teams", new ArrayList<>(), FilterType.DISPLAY_NAME);
    }

    @Override
    public void onOptionClickedDelegate(InventoryClickEvent event, MenuItem clickedOption, HumanEntity player) {

    }
//    private final TeamData teamData;
//
//    private static final MenuItem RESTORE_DEFAULT = new MenuItem(2, 5, Material.TNT,
//            "" + ChatColor.RED + ChatColor.BOLD + "Restore Default Teams",
//            "This option will remove all created teams!").setCompareKey("reset");
//
//    private static final MenuItem CREATE_TEAM = new MenuItem(6, 5, Material.EMERALD,
//            "" + ChatColor.GREEN + ChatColor.BOLD + "Create New Team").setCompareKey("create");
//
//    public TeamEditorMenu(MenuInventory parent) {
//        super(new ArrayList<>(), "Edit Teams", parent, FilterType.DISPLAY_NAME);
//        this.teamData = new TeamData();
//
//        addItems(RESTORE_DEFAULT, CREATE_TEAM);
//    }
//
//    @Override
//    public void onItemClicked(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType) {
//        if (RESTORE_DEFAULT.isCompareKeyEqual(event.getCurrentItem())) {
//            teamData.reset();
//            updateDisplay();
//            return;
//        } else if (CREATE_TEAM.isCompareKeyEqual(event.getCurrentItem())) {
//            openTeamEditor("", player);
//        }
//
//        super.onItemClicked(event, slotClicked, player, clickType);
//    }
//
//    @Override
//    public void handleOpen(InventoryOpenEvent event) {
//        updateDisplay();
//
//        super.handleOpen(event);
//    }
//
//    @Override
//    public void onOptionClickedDelegate(InventoryClickEvent event, MenuItem clickedOption, Player player) {
//        String key = clickedOption.getCompareKey();
//        if (event.getClick() == ClickType.RIGHT) {
//            teamData.removeTeam(key);
//            updateDisplay();
//            return;
//        }
//
//        openTeamEditor(key, player);
//    }
//
//    public void openTeamEditor(String teamKey, Player player) {
//        Map<String, TeamData.TeamTemplate> teams = teamData.getTeams();
//        TeamData.TeamTemplate templateToEdit;
//        if (teams.containsKey(teamKey)) {
//            templateToEdit = teams.get(teamKey);
//        } else {
//            templateToEdit = new TeamData.TeamTemplate("MyTeam", ChatColor.of("#808080"));
//        }
//        Message.log("EDITING: " + templateToEdit);
//
//        ActionMenu menu = new ActionMenu(27, "Edit Team", this);
//        menu.addAction(new MenuItem(Material.WRITABLE_BOOK, templateToEdit.name()), (p) -> {
//            UserInputMenu.open("Edit team name", (result) -> {
//                //TODO: Fix this functional action shit
//            }, p, this);
//        });
//        menu.addAction(MenuItem.createColoredLeather(templateToEdit.color(), Material.LEATHER_CHESTPLATE).setName("Color"), (p) -> {
//            ColorPickerMenu.open("Pick team color", (result) -> {
//                //TODO: Fix this functional action shit
//            }, p, this);
//        });
//        menu.addCloseAction(MenuItem.slotFromXY(7, 1));
//        menu.open(player);
//    }
//
//    public void updateDisplay() {
//        clearFilter();
//        clearItems();
//        List<MenuItem> items = new ArrayList<>();
//
//        var teamMap = teamData.getTeams();
//        for (String key : teamMap.keySet()) {
//            TeamData.TeamTemplate template = teamMap.get(key);
//            items.add(MenuItem.createColoredLeather(template.color(), Material.LEATHER_HELMET)
//                    .setName("" + ChatColor.RESET + template.color() + ChatColor.BOLD + template.name())
//                    .setDescription("" + ChatColor.GRAY + ChatColor.ITALIC + key)
//                    .setCompareKey(key));
//        }
//        addPickerContents(items.toArray(new MenuItem[]{}));
//    }
}

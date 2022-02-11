package me.steven.bingoreloaded.GUIInventories;

public class GUIBuilder5x9
{
    public enum OptionPositions
    {
        ONE_CENTER(new int[]{22}),
        ONE_TOP(new int[]{13}),
        ONE_BOTTOM(new int[]{31}),
        TWO_HORIZONTAL(new int[]{21, 23}),
        TWO_VERTICAL(new int[]{13, 31}),
        TWO_HORIZONTAL_WIDE(new int[]{20, 24}),
        THREE_LEFT(new int[]{14, 21, 32}),
        THREE_RIGHT(new int[]{12, 23, 30}),
        THREE_BOTTOM(new int[]{12, 14, 31}),
        THREE_TOP(new int[]{13, 30, 32}),
        FOUR_CENTER1(new int[]{12, 14, 30, 32}),
        FOUR_CENTER2(new int[]{13, 20, 24, 31}),
        FOUR_BOTTOM(new int[]{11, 13, 15, 31}),
        FOUR_TOP(new int[]{13, 29, 31, 33}),
        FIVE_CENTER(new int[]{4, 20, 22, 24, 40}),
        FIVE_TOP(new int[]{12, 14, 29, 31, 33}),
        FIVE_BOTTOM(new int[]{11, 13, 15, 30, 32}),
        FIVE_TOP_WIDE(new int[]{13, 19, 25, 30, 32}),
        FIVE_BOTTOM_WIDE(new int[]{12, 14, 19, 25, 31}),
        SIX_CENTER1(new int[]{11, 13, 15, 29, 31, 33}),
        SIX_CENTER2(new int[]{3, 5, 20, 24, 39, 41}),
        SIX_CENTER3(new int[]{4, 11, 15, 29, 33, 40}),
        SIX_BOTTOM(new int[]{3, 5, 20, 22, 24, 40}),
        SIX_TOP(new int[]{4, 20, 22, 24, 39, 41}),
        SIX_CENTER_WIDE(new int[]{12, 14, 19, 25, 30, 32}),
        SIX_LEFT(new int[]{4, 15, 20, 22, 33, 40}),
        SIX_RIGHT(new int[]{4, 11, 22, 24, 29, 40}),
        SEVEN_CENTER1(new int[]{4, 11, 15, 22, 29, 33, 40}),
        SEVEN_CENTER2(new int[]{3, 5, 20, 22, 24, 39, 41}),
        SEVEN_CENTER_WIDE(new int[]{3, 5, 19, 22, 25, 39, 41}),
        ;

        public final int[] positions;

        OptionPositions(int[] positions)
        {
            this.positions = positions;
        }
    }

//    public static AbstractGUIInventory createUI(OptionPositions optionPoses, List<CustomItem> options, String title)
//    {
//        int[] slots = optionPoses.positions;
//        if (options.size() != slots.length)
//        {
//            Bukkit.getLogger().log(Level.SEVERE, "Cannot create GUI, size of options (" + options.size() + ") and positions (" + slots.length + ") arent equal!");
//        }
//
//        return new AbstractGUIInventory(45, title)
//        {
//            @Override
//            public void delegateClick(InventoryClickEvent event, ItemStack itemClicked, Player player)
//            {
//
//            }
//        };
//    }
}

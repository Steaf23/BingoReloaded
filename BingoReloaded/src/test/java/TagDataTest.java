import be.seeseemelk.mockbukkit.MockBukkit;
import io.github.steaf23.bingoreloaded.data.TeamData;
import io.github.steaf23.bingoreloaded.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.data.core.DataStorageSerializerRegistry;
import io.github.steaf23.bingoreloaded.data.core.helper.ResourceFileHelper;
import io.github.steaf23.bingoreloaded.data.core.helper.SerializablePlayer;
import io.github.steaf23.bingoreloaded.data.core.tag.TagDataAccessor;
import io.github.steaf23.bingoreloaded.data.core.tag.TagDataStorage;
import io.github.steaf23.bingoreloaded.data.core.tag.TagDataType;
import io.github.steaf23.bingoreloaded.data.core.tag.TagTree;
import io.github.steaf23.bingoreloaded.data.serializers.BingoSettingsStorageSerializer;
import io.github.steaf23.bingoreloaded.data.serializers.BingoStatisticStorageSerializer;
import io.github.steaf23.bingoreloaded.data.serializers.CustomKitStorageSerializer;
import io.github.steaf23.bingoreloaded.data.serializers.ItemStorageSerializer;
import io.github.steaf23.bingoreloaded.data.serializers.PlayerStorageSerializer;
import io.github.steaf23.bingoreloaded.data.serializers.TaskStorageSerializer;
import io.github.steaf23.bingoreloaded.data.serializers.TeamTemplateStorageSerializer;
import io.github.steaf23.bingoreloaded.gui.inventory.item.SerializableItem;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.settings.CustomKit;
import io.github.steaf23.bingoreloaded.tasks.AdvancementTask;
import io.github.steaf23.bingoreloaded.tasks.BingoStatistic;
import io.github.steaf23.bingoreloaded.tasks.ItemTask;
import io.github.steaf23.bingoreloaded.tasks.StatisticTask;
import io.github.steaf23.bingoreloaded.tasks.TaskData;
import io.github.steaf23.playerdisplay.inventory.item.ItemTemplate;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TagDataTest
{
    @BeforeAll
    public static void before() {
        //TODO: figure out why constructing BingoStatistic first time is laggy AF
        BingoStatistic stat = new BingoStatistic(Statistic.BEACON_INTERACTION);

        DataStorageSerializerRegistry.addSerializer(new CustomKitStorageSerializer(), CustomKit.class);
        DataStorageSerializerRegistry.addSerializer(new TaskStorageSerializer(), TaskData.class);
        DataStorageSerializerRegistry.addSerializer(new PlayerStorageSerializer(), SerializablePlayer.class);
        DataStorageSerializerRegistry.addSerializer(new TeamTemplateStorageSerializer(), TeamData.TeamTemplate.class);
        DataStorageSerializerRegistry.addSerializer(new BingoSettingsStorageSerializer(), BingoSettings.class);
        DataStorageSerializerRegistry.addSerializer(new BingoStatisticStorageSerializer(), BingoStatistic.class);
        DataStorageSerializerRegistry.addSerializer(new ItemStorageSerializer(), SerializableItem.class);

        try {
            Files.createDirectory(new File("test").toPath());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @AfterAll
    public static void after() {
        ResourceFileHelper.deleteFolderRecurse("test");
    }

    @Test
    public void storeString() {
        String test = "yeetus";
        DataStorage node = new TagDataStorage();
        node.setString("test_path", test);
        assertEquals(test, node.getString("test_path", "def"));
    }

    @Test
    public void storeStringToFile() {
        String test = "\uE031";
        TagDataStorage node = new TagDataStorage();
        node.setString("test_path", test);
        assertEquals(test, node.getString("test_path", "def"));
        writeToFile(node, "test_nodes_uid2");

        TagDataStorage accessor1 = readFromFile("test_nodes_uid2");
        assertEquals(accessor1.getString("test_path", "def"), test);
    }

    @Test
    public void storeInt() {
        int test = 13;
        DataStorage node = new TagDataStorage();
        node.setInt("test_path", test);
        assertEquals(test, node.getInt("test_path", -1));
    }

    @Test
    public void storeIntArray() {
        List<Integer> test = List.of(1432423, 434222254, 44324218);
        DataStorage node = new TagDataStorage();
        node.setList("test_path",TagDataType.INT, test);
        assertEquals(test, node.getList("test_path", TagDataType.INT));
    }

    @Test
    public void storeUUID() {
        UUID id = UUID.randomUUID();
        DataStorage node = new TagDataStorage();
        node.setUUID("test_path", id);
        assertEquals(id, node.getUUID("test_path"));
    }

    @Test
    public void storeSerializable() {
        StatisticTask test = new StatisticTask( new BingoStatistic(org.bukkit.Statistic.CRAFT_ITEM, Material.CRAFTING_TABLE));
        DataStorage node = new TagDataStorage();
        node.setSerializable("test_path", TaskData.class, test);
        assertEquals(test, node.getSerializable("test_path", TaskData.class));
    }

    @Test
    public void storeStringNBTDeep() {
        String test = "yeetus";
        DataStorage node = new TagDataStorage();
        node.setString("longer_path.test_path.extra_extra.another.whatevevrver", test);
        assertEquals(test, node.getString("longer_path.test_path.extra_extra.another.whatevevrver", "def"));

        TagDataStorage accessor = new TagDataStorage();
        accessor.setStorage("hey", node);
        writeToFile(accessor, "test_nodes4.bingo");
    }

    @Test
    public void storeNewString() {
        String test = "yeetus";
        String test2  ="Creepus";
        DataStorage node = new TagDataStorage();
        node.setString("test_path", test);
        node.setString("test_path", test2);
        assertEquals(test2, node.getString("test_path", ""));
    }

    @Test
    public void storeComplex() {
        String testStr = "yeetus";
        int testInt = 13;
        boolean testBool = false;
        boolean testBool2 = true;
        List<Double> doubles = List.of(0.23D, 120.0D, 334.2D, 2378.1D, 25246756.548752D);
        UUID testId = UUID.randomUUID();

        DataStorage node = new TagDataStorage();
        node.setString("node0.test_path", testStr);
        node.setString("node1.test_path", "haha");
        node.setString("node1.test_path", testStr);
        node.setInt("node1.test_int", testInt);
        node.setBoolean("node1.test_bool", testBool);
        node.setBoolean("node0.test_bool", testBool2);
        node.setUUID("test.node0.id", testId);
        node.setList("node0.test_path.list", TagDataType.DOUBLE, doubles);

        assertEquals(testStr, node.getString("node1.test_path", ""));
        assertEquals(testInt, node.getInt("node1.test_int", -1));
        assertEquals(testBool, node.getBoolean("node1.test_bool"));
        assertEquals(testBool2, node.getBoolean("node0.test_bool"));
        assertEquals(testId, node.getUUID("test.node0.id"));
        assertEquals(doubles, node.getList("node0.test_path.list", TagDataType.DOUBLE));
    }

    @Test
    public void serializeToNBTFile() {
        String testStr = "yeetus";
        int testInt = 13;
        boolean testBool = false;
        boolean testBool2 = true;
        List<Double> doubles = List.of(0.23D, 120.0D, 334.2D, 2378.1D, 25246756.548752D);
        UUID testId = UUID.randomUUID();

        TagDataStorage accessor = new TagDataStorage();
        accessor.setString("node0.test_path", testStr);
        accessor.setString("node1.test_path", "haha");
        accessor.setString("node1.test_path", testStr);
        accessor.setInt("node1.test_int", testInt);
        accessor.setBoolean("node1.test_bool", testBool);
        accessor.setBoolean("node0.test_bool", testBool2);
        accessor.setUUID("test.n.id", testId);
        accessor.setList("node0.test_path.list", TagDataType.DOUBLE, doubles);

        writeToFile(accessor, "test_nodes");


        TagDataStorage accessor1 = readFromFile("test_nodes");
        assertEquals(testStr, accessor1.getString("node1.test_path", ""), testStr);
        assertEquals(testInt, accessor1.getInt("node1.test_int", 0), testInt);
        assertEquals(testBool, accessor1.getBoolean("node1.test_bool"));
        assertEquals(testBool2, accessor1.getBoolean("node0.test_bool"));
        assertEquals(testId, accessor1.getUUID("test.n.id"));
        assertEquals(doubles, accessor1.getList("node0.test_path.list", TagDataType.DOUBLE));

        assertTrue(accessor1.contains("node1"));
        assertTrue(accessor1.contains("node1.test_path"));
        assertFalse(accessor1.contains("test.node0.test_path"));
        assertFalse(accessor1.contains("test_path"));

        DataStorage someNode = accessor1.getStorage("node1");
        assertNotNull(someNode);
        assertTrue(someNode.contains("test_path"));
        assertFalse(someNode.contains("node1"));
        assertEquals(testStr, accessor1.getString("node1.test_path", ""), testStr);
    }

    @Test
    public void serializeToNBTFileSimple() {
        String testStr = "yeetus";

        List<Double> doubles = List.of(0.23D, 120.0D, 334.2D, 2378.1D, 25246756.548752D);
        TagDataStorage accessor = new TagDataStorage();
        accessor.setString("node0.test_path", testStr);
        accessor.setList("node0.test_path.list", TagDataType.DOUBLE, doubles);

//        for (int i = 0; i < 10000; i++) {
//            accessor.setString("node_" + i, "some value to inflate the file size, to ballpark which method is more memory efficient..");
//        }
        writeToFile(accessor, "test_nodes3");
    }

    /**
     * Also tests create and erase functions from BranchNode, as well as get node
     */
    @Test
    public void serializeComplex() {
        String testStr = "yeetus";
        int testInt = 13;
        boolean testBool = false;
        boolean testBool2 = true;
        List<Double> doubles = List.of(0.23D, 120.0D, 334.2D, 2378.1D, 25246756.548752D);
        UUID testId = UUID.randomUUID();

        TagDataStorage node = new TagDataStorage();
        node.setString("node0.test_path", testStr);
        node.setString("node1.test_path", "haha");
        node.setString("node1.test_path", testStr);
        node.setInt("node1.test_int", testInt);
        node.setBoolean("node1.test_bool", testBool);
        node.setBoolean("node0.test_bool", testBool2);
        node.setUUID("test.node0.id", testId);
        node.setList("node0.test_path.list", TagDataType.DOUBLE, doubles);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        TagDataType.COMPOUND.writeBytes(node.getTree(), stream);

        ByteArrayInputStream input = new ByteArrayInputStream(stream.toByteArray());
        TagTree tree = TagDataType.COMPOUND.readBytes(input);
        DataStorage result = new TagDataStorage(tree);
        assertEquals(testStr, result.getString("node1.test_path", "def"), testStr);
        assertEquals(testInt, result.getInt("node1.test_int", -1), testInt);
        assertEquals(testBool, result.getBoolean("node1.test_bool"));
        assertEquals(testBool2, result.getBoolean("node0.test_bool"));
        assertEquals(testId, result.getUUID("test.node0.id"));
        assertEquals(doubles, result.getList("node0.test_path.list", TagDataType.DOUBLE));

        assertTrue(result.contains("node1"));
        assertTrue(result.contains("node1.test_path"));
        assertFalse(result.contains("test.node0.test_path"));
        assertFalse(result.contains("test_path"));

        DataStorage someNode = result.getStorage("node1");
        assertNotNull(someNode);
        assertTrue(someNode.contains("test_path"));
        assertFalse(someNode.contains("node1"));
        assertEquals(testStr, result.getString("node1.test_path", "def"), testStr);
    }

    @Test
    public void storeStringList() {
        List<String> test = List.of("Yeetus", "DSDGHGHA", "001d");
        DataStorage node = new TagDataStorage();
        node.setList("test_path", TagDataType.STRING, test);
        assertEquals(node.getList("test_path", TagDataType.STRING), test);
    }

    @Test
    public void storeSerializableList() {
        List<TaskData> test = List.of(
                new StatisticTask(new BingoStatistic(org.bukkit.Statistic.CRAFT_ITEM, Material.CRAFTING_TABLE)),
                new AdvancementTask(null),
                new ItemTask(Material.BEDROCK, 13)
        );
        DataStorage node = new TagDataStorage();
        node.setSerializableList("test_path", TaskData.class, test);
        assertEquals(test, node.getSerializableList("test_path", TaskData.class));
    }

    @Test
    public void storeUUIDListToFile() {
        List<UUID> test = List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),UUID.randomUUID(), UUID.randomUUID());
        TagDataStorage node = new TagDataStorage();
        node.setList("test_path", TagDataType.UUID, test);
        assertEquals(node.getList("test_path", TagDataType.UUID), test);
        writeToFile(node, "test_nodes_uid2");

        TagDataStorage accessor1 = readFromFile("test_nodes_uid2");
        assertEquals(accessor1.getList("test_path", TagDataType.UUID), test);
    }

    @Test
    public void eraseString() {
        String test = "yeetus";
        DataStorage node = new TagDataStorage();
        node.setString("test_path", test);
        assertEquals(test, node.getString("test_path", "def"), test);
        node.erase("test_path");
        assertEquals("def", node.getString("test_path", "def"));
        assertEquals("def2", node.getString("lmao.ww", "def2"));

        int testInt = 12878941;
        node.setInt("test_path.longer.path.eyo", testInt);
        assertEquals(testInt, node.getInt("test_path.longer.path.eyo", 1256));
        node.erase("test_path.longer.path.eyo");
        assertEquals(1256, node.getInt("test_path.longer.path.eyo", 1256));
    }

    @Test
    public void serializeInt() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        int test = 125673217;
        TagDataStorage node = new TagDataStorage();
        node.setInt("test_path", test);
        TagDataType.COMPOUND.writeBytes(node.getTree(), stream);

        ByteArrayInputStream input = new ByteArrayInputStream(stream.toByteArray());
        TagTree tree = TagDataType.COMPOUND.readBytes(input);
        TagDataStorage result = new TagDataStorage(tree);
        assertEquals(test, result.getInt("test_path", 0));
    }

    @Test
    public void serializeString() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        String test = "yeetus";
        TagDataStorage node = new TagDataStorage();
        node.setString("test_path", test);
        TagDataType.COMPOUND.writeBytes(node.getTree(), stream);

        ByteArrayInputStream input = new ByteArrayInputStream(stream.toByteArray());
        TagTree tree = TagDataType.COMPOUND.readBytes(input);
        TagDataStorage result = new TagDataStorage(tree);
        assertEquals(test, result.getString("test_path", "def"));
    }

    @Test
    public void serializeIntArray() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        List<Integer> test = List.of(1432423, 434222254, 44324218);
        TagDataStorage node = new TagDataStorage();
        node.setList("test_path", TagDataType.INT, test);
        TagDataType.COMPOUND.writeBytes(node.getTree(), stream);
        assertEquals(test, node.getList("test_path", TagDataType.INT));

        ByteArrayInputStream input = new ByteArrayInputStream(stream.toByteArray());
        TagTree tree = TagDataType.COMPOUND.readBytes(input);
        DataStorage result = new TagDataStorage(tree);
        assertEquals(test, result.getList("test_path", TagDataType.INT));
    }

    @Test
    public void serializeUUID() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        UUID test = UUID.randomUUID();
        TagDataStorage node = new TagDataStorage();
        node.setUUID("test_path", test);
        TagDataType.COMPOUND.writeBytes(node.getTree(), stream);

        ByteArrayInputStream input = new ByteArrayInputStream(stream.toByteArray());
        TagTree tree = TagDataType.COMPOUND.readBytes(input);
        DataStorage result = new TagDataStorage(tree);
        assertEquals(test, result.getUUID("test_path"));
    }

    @Test
    public void serializeUUIDToFile() {
        UUID test = UUID.randomUUID();

        TagDataStorage accessor = new TagDataStorage();
        accessor.setUUID("test_path", test);
        writeToFile(accessor, "test_nodes_uid");
    }

    @Test
    public void serializeStringList() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        List<String> test = List.of("Yeetus", "DSDGHGHA", "001d");
        TagDataStorage node = new TagDataStorage();
        node.setList("test_path", TagDataType.STRING, test);
        TagDataType.COMPOUND.writeBytes(node.getTree(), stream);

        ByteArrayInputStream input = new ByteArrayInputStream(stream.toByteArray());
        TagTree tree = TagDataType.COMPOUND.readBytes(input);
        DataStorage result = new TagDataStorage(tree);
        assertEquals(test, result.getList("test_path", TagDataType.STRING));
    }

    @Test
    public void serializeSerializable() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        StatisticTask test = new StatisticTask( new BingoStatistic(org.bukkit.Statistic.CRAFT_ITEM, Material.CRAFTING_TABLE));
        TagDataStorage node = new TagDataStorage();
        node.setSerializable("test_path", TaskData.class, test);
        TagDataType.COMPOUND.writeBytes(node.getTree(), stream);

        ByteArrayInputStream input = new ByteArrayInputStream(stream.toByteArray());
        TagTree tree = TagDataType.COMPOUND.readBytes(input);
        DataStorage result = new TagDataStorage(tree);
        assertEquals(test, result.getSerializable("test_path", TaskData.class));
    }

    @Test
    public void serializeSerializableList() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        List<TaskData> test = List.of(
                new ItemTask(Material.TOTEM_OF_UNDYING, 1),
                new StatisticTask(new BingoStatistic(org.bukkit.Statistic.CRAFT_ITEM, Material.CRAFTING_TABLE)),
                new ItemTask(Material.BEDROCK, 13)
        );
        TagDataStorage node = new TagDataStorage();
        node.setSerializableList("test_path", TaskData.class, test);
        TagDataType.COMPOUND.writeBytes(node.getTree(), stream);

        ByteArrayInputStream input = new ByteArrayInputStream(stream.toByteArray());
        TagTree tree = TagDataType.COMPOUND.readBytes(input);
        DataStorage result = new TagDataStorage(tree);
        assertEquals(test, result.getSerializableList("test_path", TaskData.class));
    }

    @Test
    public void serializeBooleans() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        List<Boolean> items = List.of(true, true, false, false, true, false, true, false);
        TagDataStorage node = new TagDataStorage();
        node.setList("test_path", TagDataType.BOOLEAN, items);
        TagDataType.COMPOUND.writeBytes(node.getTree(), stream);

        ByteArrayInputStream input = new ByteArrayInputStream(stream.toByteArray());
        TagTree tree = TagDataType.COMPOUND.readBytes(input);
        DataStorage result = new TagDataStorage(tree);
        assertEquals(items, result.getList("test_path", TagDataType.BOOLEAN));
    }

    public TagDataStorage readFromFile(String filename) {
        TagDataStorage data = new TagDataStorage();
        TagDataAccessor.readTagDataFromFile(data, new File("test\\" + filename + ".nbt"));
        return data;
    }

    public void writeToFile(TagDataStorage data, String filename) {
        TagDataAccessor.writeTagDataToFile(data, new File("test\\" + filename + ".nbt"));
    }
}

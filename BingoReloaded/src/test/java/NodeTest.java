import io.github.steaf23.bingoreloaded.data.core.NodeDataAccessor;
import io.github.steaf23.bingoreloaded.data.core.node.BranchNode;
import io.github.steaf23.bingoreloaded.data.core.node.datatype.ItemStackDataType;
import io.github.steaf23.bingoreloaded.data.core.node.datatype.NodeDataType;
import io.github.steaf23.bingoreloaded.tasks.AdvancementTask;
import io.github.steaf23.bingoreloaded.tasks.BingoStatistic;
import io.github.steaf23.bingoreloaded.tasks.ItemTask;
import io.github.steaf23.bingoreloaded.tasks.StatisticTask;
import io.github.steaf23.bingoreloaded.tasks.TaskData;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.advancement.Advancement;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NodeTest
{
    @BeforeAll
    public static void before() {
        //TODO: figure out why constructing BingoStatistic first time is laggy AF
        BingoStatistic stat = new BingoStatistic(Statistic.BEACON_INTERACTION);
    }

    @Test
    public void storeString() {
        String test = "yeetus";
        BranchNode node = new BranchNode();
        node.setString("test_path", test);
        assertEquals(test, node.getString("test_path"));
    }

    @Test
    public void storeSerializable() {
        StatisticTask test = new StatisticTask( new BingoStatistic(org.bukkit.Statistic.CRAFT_ITEM, Material.CRAFTING_TABLE));
        BranchNode node = new BranchNode();
        node.setSerializable("test_path", test);
        assertEquals(test, node.getSerializable("test_path", StatisticTask.class));
    }

    @Test
    public void storeStringDeep() {
        String test = "yeetus";
        BranchNode node = new BranchNode();
        node.setString("longer_path.test_path.extra_extra.another.whatevevrver", test);
        assertEquals(test, node.getString("longer_path.test_path.extra_extra.another.whatevevrver"));

        NodeDataAccessor accessor = new NodeDataAccessor("test_nodes4.bingo");
        accessor.set("hey", node);
        accessor.saveChanges();
    }

    @Test
    public void storeNewString() {
        String test = "yeetus";
        String test2  ="Creepus";
        BranchNode node = new BranchNode();
        node.setString("test_path", test);
        node.setString("test_path", test2);
        assertEquals(test2, node.getString("test_path"));
    }

    @Test
    public void storeComplex() {
        String testStr = "yeetus";
        int testInt = 13;
        boolean testBool = false;
        boolean testBool2 = true;
        List<Double> doubles = List.of(0.23D, 120.0D, 334.2D, 2378.1D, 25246756.548752D);
        UUID testId = UUID.randomUUID();

        BranchNode node = new BranchNode();
        node.setString("node0.test_path", testStr);
        node.setString("node1.test_path", "haha");
        node.setString("node1.test_path", testStr);
        node.setInt("node1.test_int", testInt);
        node.setBoolean("node1.test_bool", testBool);
        node.setBoolean("node0.test_bool", testBool2);
        node.setUUID("test.node0.id", testId);
        node.setList("node0.test_path.list", NodeDataType.DOUBLE, doubles);

        assertEquals(testStr, node.getString("node1.test_path"));
        assertEquals(testInt, node.getInt("node1.test_int"));
        assertEquals(testBool, node.getBoolean("node1.test_bool"));
        assertEquals(testBool2, node.getBoolean("node0.test_bool"));
        assertEquals(testId, node.getUUID("test.node0.id"));
        assertEquals(doubles, node.getList("node0.test_path.list", NodeDataType.DOUBLE));
    }

    @Test
    public void serializeToFile() {
        String testStr = "yeetus";
        int testInt = 13;
        boolean testBool = false;
        boolean testBool2 = true;
        List<Double> doubles = List.of(0.23D, 120.0D, 334.2D, 2378.1D, 25246756.548752D);
        UUID testId = UUID.randomUUID();

        NodeDataAccessor accessor = new NodeDataAccessor("test_nodes.bingo");
        BranchNode node = new BranchNode();
        accessor.setString("node0.test_path", testStr);
        accessor.setString("node1.test_path", "haha");
        accessor.setString("node1.test_path", testStr);
        accessor.setInt("node1.test_int", testInt);
        accessor.setBoolean("node1.test_bool", testBool);
        accessor.setBoolean("node0.test_bool", testBool2);
        accessor.setUUID("test.n.id", testId);
        accessor.setList("node0.test_path.list", NodeDataType.DOUBLE, doubles);

//        for (int i = 0; i < 10000; i++) {
//            accessor.setString("node_" + i, "some value to inflate the file size, to ballpark which method is more memory efficient..");
//        }
        accessor.saveChanges();


        NodeDataAccessor accessor1 = new NodeDataAccessor("test_nodes.bingo");
        accessor1.load();
        assertEquals(testStr, accessor1.getString("node1.test_path"), testStr);
        assertEquals(testInt, accessor1.getInt("node1.test_int"), testInt);
        assertEquals(testBool, accessor1.getBoolean("node1.test_bool"));
        assertEquals(testBool2, accessor1.getBoolean("node0.test_bool"));
        assertEquals(testId, accessor1.getUUID("test.n.id"));
        assertEquals(doubles, accessor1.getList("node0.test_path.list", NodeDataType.DOUBLE));

        assertTrue(accessor1.contains("node1"));
        assertTrue(accessor1.contains("node1.test_path"));
        assertFalse(accessor1.contains("test.node0.test_path"));
        assertFalse(accessor1.contains("test_path"));

        BranchNode someNode = accessor1.getNode("node1", BranchNode.class);
        assertNotNull(someNode);
        assertTrue(someNode.contains("test_path"));
        assertFalse(someNode.contains("node1"));
        assertEquals(testStr, accessor1.getString("node1.test_path"), testStr);
    }

    @Test
    public void serializeToFileSimple() {
        String testStr = "yeetus";

        List<Double> doubles = List.of(0.23D, 120.0D, 334.2D, 2378.1D, 25246756.548752D);
        NodeDataAccessor accessor = new NodeDataAccessor("test_nodes3.bingo");
        accessor.setString("node0.test_path", testStr);
        accessor.setList("node0.test_path.list", NodeDataType.DOUBLE, doubles);

//        for (int i = 0; i < 10000; i++) {
//            accessor.setString("node_" + i, "some value to inflate the file size, to ballpark which method is more memory efficient..");
//        }
        accessor.saveChanges();
    }

    /**
     * Also tests create and erase functions from BranchNode, as well as get node
     */
    @Test
    public void serializeComplex() throws IOException {
        String testStr = "yeetus";
        int testInt = 13;
        boolean testBool = false;
        boolean testBool2 = true;
        List<Double> doubles = List.of(0.23D, 120.0D, 334.2D, 2378.1D, 25246756.548752D);
        UUID testId = UUID.randomUUID();

        BranchNode node = new BranchNode();
        node.setString("node0.test_path", testStr);
        node.setString("node1.test_path", "haha");
        node.setString("node1.test_path", testStr);
        node.setInt("node1.test_int", testInt);
        node.setBoolean("node1.test_bool", testBool);
        node.setBoolean("node0.test_bool", testBool2);
        node.setUUID("test.node0.id", testId);
        node.setList("node0.test_path.list", NodeDataType.DOUBLE, doubles);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        node.serialize(stream);

        ByteArrayInputStream input = new ByteArrayInputStream(stream.toByteArray());
        BranchNode result = new BranchNode(input);
        assertEquals(testStr, result.getString("node1.test_path"), testStr);
        assertEquals(testInt, result.getInt("node1.test_int"), testInt);
        assertEquals(testBool, result.getBoolean("node1.test_bool"));
        assertEquals(testBool2, result.getBoolean("node0.test_bool"));
        assertEquals(testId, result.getUUID("test.node0.id"));
        assertEquals(doubles, result.getList("node0.test_path.list", NodeDataType.DOUBLE));

        assertTrue(result.contains("node1"));
        assertTrue(result.contains("node1.test_path"));
        assertFalse(result.contains("test.node0.test_path"));
        assertFalse(result.contains("test_path"));

        BranchNode someNode = result.getNode("node1", BranchNode.class);
        assertNotNull(someNode);
        assertTrue(someNode.contains("test_path"));
        assertFalse(someNode.contains("node1"));
        assertEquals(testStr, result.getString("node1.test_path"), testStr);
    }

    @Test
    public void storeStringList() {
        List<String> test = List.of("Yeetus", "DSDGHGHA", "001d");
        BranchNode node = new BranchNode();
        node.setList("test_path", NodeDataType.STRING, test);
        assertEquals(node.getList("test_path", NodeDataType.STRING), test);
    }

    @Test
    public void storeSerializableList() {
        List<TaskData> test = List.of(
                new StatisticTask(new BingoStatistic(org.bukkit.Statistic.CRAFT_ITEM, Material.CRAFTING_TABLE)),
                new AdvancementTask((Advancement)null),
                new ItemTask(Material.BEDROCK, 13)
        );
        BranchNode node = new BranchNode();
        node.setList("test_path", test);
        System.out.println(node.getList("test_path", TaskData.class));
        assertEquals(test, node.getList("test_path", TaskData.class));
    }

    @Test
    public void eraseString() {
        String test = "yeetus";
        BranchNode node = new BranchNode();
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
    public void serializeString() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        String test = "yeetus";
        BranchNode node = new BranchNode();
        node.setString("test_path", test);
        node.serialize(stream);

        ByteArrayInputStream input = new ByteArrayInputStream(stream.toByteArray());
        BranchNode result = new BranchNode(input);
        assertEquals(test, result.getString("test_path"));
    }

    @Test
    public void serializeUUID() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        UUID test = UUID.randomUUID();
        BranchNode node = new BranchNode();
        node.setUUID("test_path", test);
        node.serialize(stream);

        ByteArrayInputStream input = new ByteArrayInputStream(stream.toByteArray());
        BranchNode result = new BranchNode(input);
        assertEquals(test, result.getUUID("test_path"));
    }

    @Test
    public void serializeUUIDToFile() {
        UUID test = UUID.randomUUID();
        System.out.println(test);

        NodeDataAccessor accessor = new NodeDataAccessor("test_nodes_uuid.bingo");
        accessor.setUUID("test_path", test);
        accessor.saveChanges();
    }

    @Test
    public void serializeStringList() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        List<String> test = List.of("Yeetus", "DSDGHGHA", "001d");
        BranchNode node = new BranchNode();
        node.setList("test_path", NodeDataType.STRING, test);
        node.serialize(stream);

        ByteArrayInputStream input = new ByteArrayInputStream(stream.toByteArray());
        BranchNode result = new BranchNode(input);
        assertEquals(test, result.getList("test_path", NodeDataType.STRING));
    }

    @Test
    public void serializeSerializable() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        StatisticTask test = new StatisticTask( new BingoStatistic(org.bukkit.Statistic.CRAFT_ITEM, Material.CRAFTING_TABLE));
        BranchNode node = new BranchNode();
        node.setSerializable("test_path", test);
        node.serialize(stream);

        ByteArrayInputStream input = new ByteArrayInputStream(stream.toByteArray());
        BranchNode result = new BranchNode(input);
        assertEquals(test, result.getSerializable("test_path", StatisticTask.class));
    }

    @Test
    public void serializeSerializableList() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        List<TaskData> test = List.of(
                new ItemTask(Material.TOTEM_OF_UNDYING, 1),
                new StatisticTask(new BingoStatistic(org.bukkit.Statistic.CRAFT_ITEM, Material.CRAFTING_TABLE)),
                new ItemTask(Material.BEDROCK, 13)
        );
        BranchNode node = new BranchNode();
        node.setList("test_path", test);
        node.serialize(stream);

        ByteArrayInputStream input = new ByteArrayInputStream(stream.toByteArray());
        BranchNode result = new BranchNode(input);
        System.out.println(node.getList("test_path", TaskData.class));
        System.out.println(result.getList("test_path", TaskData.class));
        assertEquals(test, result.getList("test_path", TaskData.class));
    }
}

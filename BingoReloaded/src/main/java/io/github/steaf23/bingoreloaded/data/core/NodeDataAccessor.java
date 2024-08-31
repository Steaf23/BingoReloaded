package io.github.steaf23.bingoreloaded.data.core;

import io.github.steaf23.bingoreloaded.data.core.node.BranchNode;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class NodeDataAccessor extends BranchNode implements DataAccessor<BranchNode>
{
    private final String filepath;

    public NodeDataAccessor(String filepath) {
        this.filepath = filepath;
    }

    @Override
    public String getLocation() {
        return filepath;
    }

    @Override
    public void load() {
        clear();

        try (InputStream input = new FileInputStream(filepath)) {
            deserialize(input);
        } catch (IOException e) {
            ConsoleMessenger.error("Data file " + filepath + " could not be opened");
        }
    }

    @Override
    public void saveChanges() {
        try (OutputStream output = new FileOutputStream(filepath)) {
            serialize(output);
        } catch (IOException e) {
            ConsoleMessenger.error("Data file " + filepath + " could not be opened");
        }
    }
}

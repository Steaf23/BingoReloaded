package io.github.steaf23.bingoreloaded.data.helper;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class ResourceFileHelper
{
    public static boolean deleteFolderRecurse(String folderPath) {
        File folder = FileUtils.getFile(folderPath);
        try {
            FileUtils.deleteDirectory(folder);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}

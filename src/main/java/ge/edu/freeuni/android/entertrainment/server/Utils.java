package ge.edu.freeuni.android.entertrainment.server;

import ge.edu.freeuni.android.entertrainment.server.model.NameGenerator;

import java.io.File;
import java.net.URL;

/**
 * Created by Nika Doghonadze
 */
public class Utils {
    public static File getFileFromResources(String fileName) {
        ClassLoader classLoader = Utils.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null)
            return null;
        return new File(resource.getFile());
    }
}

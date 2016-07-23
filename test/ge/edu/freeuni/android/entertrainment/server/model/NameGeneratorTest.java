package ge.edu.freeuni.android.entertrainment.server.model;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Nika Doghonadze
 */
public class NameGeneratorTest {
    @Test
    public void generateRandomName() throws Exception {
        System.out.println(NameGenerator.generateRandomName());
        System.out.println(NameGenerator.generateRandomName());
        System.out.println(NameGenerator.generateRandomName());
        System.out.println(NameGenerator.generateRandomName());
    }

}
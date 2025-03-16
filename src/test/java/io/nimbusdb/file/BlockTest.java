package io.nimbusdb.file;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class BlockTest {
    @Test
    void testBlockCreation() {
        Block block = new Block("file1.dat", 123);
        assertEquals("file1.dat", block.fileName());
        assertEquals(123, block.number());
    }

    @Test
    void testEquality() {
        Block block1 = new Block("file1.dat", 123);
        Block block2 = new Block("file1.dat", 123);
        Block block3 = new Block("file2.dat", 123);
        Block block4 = new Block("file1.dat", 456);

        assertEquals(block1, block2);
        assertNotEquals(block1, block3);
        assertNotEquals(block1, block4);
    }

    @Test
    void testHashCodeConsistency() {
        Block block1 = new Block("file1.dat", 123);
        Block block2 = new Block("file1.dat", 123);
        assertEquals(block1.hashCode(), block2.hashCode());
    }

    @Test
    void testToString() {
        Block block = new Block("file1.dat", 123);
        String expected = "BlockId{blknum=123, filename='file1.dat'}";
        assertEquals(expected, block.toString());
    }
}

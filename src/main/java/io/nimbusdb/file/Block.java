package io.nimbusdb.file;

import java.util.Objects;

public class Block {
    private final long blknum;
    private final String filename;

    public Block(final String filename, final long blknum) {
        this.blknum = blknum;
        this.filename = filename;
    }

    public String fileName() {
        return this.filename;
    }

    public long number() {
        return this.blknum;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Block blockId = (Block) o;
        return blknum == blockId.blknum && Objects.equals(filename, blockId.filename);
    }

    @Override
    public int hashCode() {
        return Objects.hash(blknum, filename);
    }

    @Override
    public String toString() {
        return "BlockId{" + "blknum=" + blknum + ", filename='" + filename + '\'' + '}';
    }
}

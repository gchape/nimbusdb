package io.nimbusdb.log;

import io.nimbusdb.file.Block;
import io.nimbusdb.file.FileMgr;
import io.nimbusdb.file.Page;

import java.util.Iterator;

class LogIterator implements Iterator<byte[]> {
    private final Page page;
    private final FileMgr fileMgr;

    private Block block;
    private int position;

    public LogIterator(FileMgr fileMgr, Block block) {
        this.block = block;
        this.fileMgr = fileMgr;
        this.page = new Page(new byte[fileMgr.blockSize()]);

        setPosition(block);
    }

    public boolean hasNext() {
        return position < fileMgr.blockSize() || block.blknum() > 0;
    }

    public byte[] next() {
        if (position == fileMgr.blockSize()) {
            block = new Block(block.fname(), block.blknum() - 1);

            setPosition(block);
        }

        byte[] rec = page.getBytes(position);
        position += Integer.BYTES + rec.length;

        return rec;
    }

    private void setPosition(Block block) {
        fileMgr.read(block, page);

        position = page.getInt(0);
    }
}

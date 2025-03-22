package io.nimbusdb.log;

import io.nimbusdb.file.Block;
import io.nimbusdb.file.FileMgr;
import io.nimbusdb.file.Page;

import java.util.Iterator;

class LogIterator implements Iterator<byte[]> {
    private final Page page;
    private final FileMgr fileMgr;

    private Block block;
    private int logpos;

    public LogIterator(FileMgr fileMgr, Block block) {
        this.block = block;
        this.fileMgr = fileMgr;
        this.page = new Page(new byte[fileMgr.blockSize()]);

        setLogpos(block);
    }

    public boolean hasNext() {
        return logpos < fileMgr.blockSize() || block.blknum() > 0;
    }

    public byte[] next() {
        if (logpos == fileMgr.blockSize()) {
            block = new Block(block.fname(), block.blknum() - 1);

            setLogpos(block);
        }

        byte[] logrec = page.getBytes(logpos);
        logpos += Integer.BYTES + logrec.length;

        return logrec;
    }

    private void setLogpos(Block block) {
        fileMgr.read(block, page);

        logpos = page.getInt(0);
    }
}

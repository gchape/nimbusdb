package io.nimbusdb.buffer;

import io.nimbusdb.file.Block;
import io.nimbusdb.file.FileMgr;
import io.nimbusdb.file.Page;
import io.nimbusdb.log.LogMgr;

public class Buffer {
    private final Meta $;
    private final Page contents;
    private final LogMgr logMgr;
    private final FileMgr fileMgr;

    private Block block;

    public Buffer(FileMgr fileMgr, LogMgr logMgr) {
        this.$ = new Meta();

        this.logMgr = logMgr;
        this.fileMgr = fileMgr;
        this.contents = new Page(fileMgr.blockSize());
    }

    public Page contents() {
        return contents;
    }

    public Block block() {
        return block;
    }

    public void setModified(int txId, int lsn) {
        $.txId = txId;
        if (lsn >= 0) $.lsn = lsn;
    }

    public int modifyingTx() {
        return $.txId;
    }

    public boolean isPinned() {
        return $.pins > 0;
    }

    void assign(Block block) {
        flush();

        fileMgr.read(block, contents);

        $.pins = 0;

        this.block = block;
    }

    void flush() {
        if ($.txId > 0) {
            logMgr.flush($.lsn);

            fileMgr.write(block, contents);

            $.txId = 0;
        }
    }

    void pin() {
        $.pins++;
    }

    void unpin() {
        $.pins--;
    }

    private class Meta {
        private int lsn = -1;
        private int txId = -1;
        private int pins = 0;
    }
}

package io.nimbusdb.buffer;

import io.nimbusdb.file.Block;
import io.nimbusdb.file.FileMgr;
import io.nimbusdb.file.Page;
import io.nimbusdb.log.LogMgr;

public class Buffer {
    private final Meta m;
    private final Page contents;
    private final LogMgr logMgr;
    private final FileMgr fileMgr;

    private Block block;

    public Buffer(FileMgr fileMgr, LogMgr logMgr, Page contents) {
        this.logMgr = logMgr;
        this.fileMgr = fileMgr;
        this.contents = contents;

        this.m = new Meta();
    }

    public Page contents() {
        return contents;
    }

    public Block block() {
        return block;
    }

    public void setModified(int txId, int lsn) {
        m.setModified(txId, lsn);
    }

    public int modifyingTx() {
        return m.modifyingTx();
    }

    public boolean isPinned() {
        return m.isPinned();
    }

    void assign(Block block) {
        flush();

        fileMgr.read(block, contents);

        m.pinCount = 0;

        this.block = block;
    }

    void flush() {
        if (m.txId > 0) {
            logMgr.flush(m.lsn);

            fileMgr.write(block, contents);

            m.txId = 0;
        }
    }

    void pin() {
        m.pin();
    }

    void unpin() {
        m.unpin();
    }

    private class Meta {
        private int lsn = -1;
        private int txId = -1;
        private int pinCount = 0;

        private void pin() {
            pinCount++;
        }

        private void unpin() {
            pinCount--;
        }

        private void setModified(int txId, int lsn) {
            this.txId = txId;
            if (lsn >= 0) this.lsn = lsn;
        }

        private int modifyingTx() {
            return txId;
        }

        private boolean isPinned() {
            return pinCount > 0;
        }
    }
}

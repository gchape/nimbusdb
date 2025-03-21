package io.nimbusdb.log;

import io.nimbusdb.file.Block;
import io.nimbusdb.file.FileMgr;
import io.nimbusdb.file.Page;

public class LogMgr {
    private final Page logpage;
    private final FileMgr fileMgr;
    private final String logfile;

    private int latestLSN;
    private int lastSavedLSN;
    private Block currentBlock;

    public LogMgr(FileMgr fileMgr, String filename) {
        this.fileMgr = fileMgr;
        this.logfile = filename;
        this.logpage = new Page(new byte[fileMgr.blockSize()]);

        int blockCount = fileMgr.size(filename);
        if (blockCount == 0) currentBlock = append();
        else {
            currentBlock = new Block(filename, blockCount - 1);
            fileMgr.read(currentBlock, logpage);
        }
    }

    public synchronized int append(byte[] logrec) {
        int capacity = logpage.getInt(0);
        int logsize = logrec.length + Integer.BYTES;

        if (capacity - logsize < Integer.BYTES) {
            flush();
            currentBlock = append();
            capacity = logpage.getInt(0);
        }

        int logpos = capacity - logsize;
        logpage.setBytes(logpos, logrec);
        logpage.setInt(0, logpos);
        latestLSN += 1;

        return latestLSN;
    }

    private void flush() {
        fileMgr.write(currentBlock, logpage);

        lastSavedLSN = latestLSN;
    }

    private Block append() {
        var block = fileMgr.append(logfile);
        logpage.setInt(0, fileMgr.blockSize());
        fileMgr.write(block, logpage);

        return block;
    }

    public void flush(int lsn) {
        if (lsn >= lastSavedLSN) {
            flush();
        }
    }
}

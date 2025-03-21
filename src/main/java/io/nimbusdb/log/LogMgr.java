package io.nimbusdb.log;

import io.nimbusdb.file.Block;
import io.nimbusdb.file.FileMgr;
import io.nimbusdb.file.Page;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class LogMgr {
    private final Page logpage;
    private final String logfile;
    private final FileMgr fileMgr;

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

    public synchronized int append(byte[] log) {
        int capacity = logpage.getInt(0);
        int logsize = log.length + Integer.BYTES;

        if (capacity - logsize < Integer.BYTES) {
            flush();
            currentBlock = append();
            capacity = logpage.getInt(0);
        }

        int logPosition = capacity - logsize;
        logpage.setInt(0, logPosition);
        logpage.setBytes(logPosition, log);
        latestLSN += 1;

        return latestLSN;
    }

    public void flush(int lsn) {
        if (lsn >= lastSavedLSN) {
            flush();
        }
    }

    public Stream<byte[]> stream() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(), Spliterator.IMMUTABLE), false);
    }

    private Iterator<byte[]> iterator() {
        flush();

        return new LogIterator(fileMgr, currentBlock);
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
}

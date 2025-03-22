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
    private final Meta $;
    private final Page logpage;
    private final String logfile;
    private final FileMgr fileMgr;

    public LogMgr(FileMgr fileMgr, String filename) {
        this.$ = new Meta();
        this.fileMgr = fileMgr;
        this.logfile = filename;
        this.logpage = new Page(new byte[fileMgr.blockSize()]);

        int blockCount = fileMgr.size(filename);
        if (blockCount == 0) $.block = append();
        else {
            $.block = new Block(filename, blockCount - 1);
            fileMgr.read($.block, logpage);
        }

    }

    public synchronized int append(byte[] logrec) {
        int capacity = logpage.getInt(0);
        int logsize = logrec.length + Integer.BYTES;

        if (capacity - Integer.BYTES < logsize) {
            flush();
            $.block = append();
            capacity = logpage.getInt(0);
        }

        int logPosition = capacity - logsize;
        logpage.setInt(0, logPosition);
        logpage.setBytes(logPosition, logrec);
        $.latestLSN += 1;

        return $.latestLSN;
    }

    public void flush(int lsn) {
        if (lsn >= $.savedLSN) {
            flush();
        }
    }

    public Stream<byte[]> stream() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(), Spliterator.IMMUTABLE), false);
    }

    private Iterator<byte[]> iterator() {
        flush();

        return new LogIterator(fileMgr, $.block);
    }

    private void flush() {
        fileMgr.write($.block, logpage);

        $.savedLSN = $.latestLSN;
    }

    private Block append() {
        var block = fileMgr.append(logfile);
        logpage.setInt(0, fileMgr.blockSize());
        fileMgr.write(block, logpage);

        return block;
    }

    private class Meta {
        private int latestLSN;
        private int savedLSN;
        private Block block;
    }
}

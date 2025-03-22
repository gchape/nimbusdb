package io.nimbusdb.buffer;

import io.nimbusdb.file.Block;
import io.nimbusdb.file.FileMgr;
import io.nimbusdb.log.LogMgr;

public class BufferMgr {
    private static final long MAX_WAIT_TIME = 10000;
    private final Buffer[] bufferPool;

    private int freeFrames;

    public BufferMgr(FileMgr fm, LogMgr lm, int poolSize) {
        bufferPool = new Buffer[poolSize];
        freeFrames = poolSize;

        for (int i = 0; i < poolSize; i++)
            bufferPool[i] = new Buffer(fm, lm);
    }

    public synchronized int freeFrames() {
        return freeFrames;
    }

    public synchronized void flushAll(int txId) {
        for (Buffer buff : bufferPool)
            if (buff.modifyingTx() == txId) buff.flush();
    }

    public synchronized void unpin(Buffer buff) {
        buff.unpin();

        if (buff.isPinned()) {
            freeFrames++;

            notifyAll();
        }
    }

    public synchronized Buffer pin(Block block) throws BufferAbortException {
        try {
            long timestamp = System.currentTimeMillis();
            Buffer buff = tryToPin(block);

            while (buff == null && !waitingTooLong(timestamp)) {
                wait(MAX_WAIT_TIME);
                buff = tryToPin(block);
            }

            if (buff == null) throw new BufferAbortException();

            return buff;
        } catch (InterruptedException e) {
            throw new BufferAbortException();
        }
    }

    private boolean waitingTooLong(long startTime) {
        return System.currentTimeMillis() - startTime > MAX_WAIT_TIME;
    }

    private Buffer tryToPin(Block blk) {
        Buffer buffer = findExistingBuffer(blk);

        if (buffer == null) {
            buffer = chooseUnpinnedBuffer();

            if (buffer == null) return null;

            buffer.assign(blk);
        }

        if (buffer.isPinned()) freeFrames--;
        buffer.pin();

        return buffer;
    }

    private Buffer findExistingBuffer(Block block) {
        for (Buffer buffer : bufferPool) {

            Block b = buffer.block();
            if (b != null && b.equals(block)) return buffer;
        }

        return null;
    }

    private Buffer chooseUnpinnedBuffer() {
        for (Buffer buffer : bufferPool)
            if (buffer.isPinned()) return buffer;

        return null;
    }
}

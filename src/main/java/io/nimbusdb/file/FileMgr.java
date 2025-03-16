package io.nimbusdb.file;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FileMgr {
    private final boolean isNew;
    private final int blocksize;
    private final Path dbDirectory;
    private final Map<String, FileChannel> openFiles = new HashMap<>();

    public FileMgr(final Path dbDirectory, final int blocksize) throws IOException {
        this.blocksize = blocksize;
        this.dbDirectory = dbDirectory;

        isNew = !Files.exists(dbDirectory);
        if (isNew) {
            Files.createDirectory(dbDirectory);
        }

        deleteTempFiles(dbDirectory);
    }

    private static void deleteTempFiles(Path dbDirectory) throws IOException {
        try (var entries = Files.list(dbDirectory)) {
            entries.filter(e -> String.valueOf(e.getFileName()).startsWith("temp")).forEach(tempFile -> {
                try {
                    Files.delete(tempFile);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
        }
    }

    public synchronized void read(Block blk, Page p) {
        try {
            FileChannel fc = getFile(blk.fileName());

            fc.position(blk.number() * blocksize);
            fc.read(p.contents());
        } catch (IOException e) {
            throw new RuntimeException("cannot read block " + blk);
        }
    }

    public synchronized void write(Block blk, Page p) {
        try {
            FileChannel fc = getFile(blk.fileName());

            fc.position(blk.number() * blocksize);
            fc.write(p.contents());
        } catch (IOException e) {
            throw new RuntimeException("cannot write block" + blk);
        }
    }

    public synchronized Block append(String filename) {
        int newBlknum = blockCount(filename);

        Block blk = new Block(filename, newBlknum);
        ByteBuffer emptyBlock = ByteBuffer.allocate(blocksize);

        try {
            FileChannel fc = getFile(blk.fileName());

            fc.position(blk.number() * blocksize);
            fc.write(emptyBlock);
        } catch (IOException e) {
            throw new RuntimeException("cannot append block" + blk);
        }

        return blk;
    }

    public int blockCount(String filename) {
        try {
            FileChannel fc = getFile(filename);

            return (int) (fc.size() / blocksize);
        } catch (IOException e) {
            throw new RuntimeException("cannot access " + filename);
        }
    }

    private FileChannel getFile(String filename) throws IOException {
        FileChannel fc = openFiles.get(filename);

        if (fc == null) {
            Path dbTable = dbDirectory.resolveSibling(filename);

            fc = FileChannel.open(dbTable, Set.of(StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.SYNC));
            openFiles.put(filename, fc);
        }
        return fc;
    }

    public boolean isNew() {
        return isNew;
    }

    public int blockSize() {
        return this.blocksize;
    }
}

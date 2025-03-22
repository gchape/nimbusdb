package io.nimbusdb.file;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FileMgr {
    public static final Set<OpenOption> FILE_OPEN_OPTIONS = Set.of(
            StandardOpenOption.CREATE,
            StandardOpenOption.READ,
            StandardOpenOption.WRITE,
            StandardOpenOption.SYNC);

    private final int blocksize;
    private final boolean isNew;
    private final Path dbDirectory;
    private final Map<String, FileChannel> channelPool;

    public FileMgr(final Path dbDirectory, final int blocksize) throws IOException {
        this.blocksize = blocksize;
        this.dbDirectory = dbDirectory;
        this.channelPool = new HashMap<>();

        isNew = !Files.exists(dbDirectory);
        if (isNew) {
            Files.createDirectory(dbDirectory);
        }

        deleteTempFiles(dbDirectory);
    }

    private void deleteTempFiles(Path dbDirectory) throws IOException {
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

    public synchronized void read(Block block, Page p) {
        try {
            FileChannel fc = getFile(block.fname());

            fc.position((long) block.blknum() * blocksize);
            fc.read(p.contents());
        } catch (IOException e) {
            throw new RuntimeException("cannot read block " + block);
        }
    }

    public synchronized void write(Block block, Page p) {
        try {
            FileChannel fc = getFile(block.fname());

            fc.position((long) block.blknum() * blocksize);
            fc.write(p.contents());
        } catch (IOException e) {
            throw new RuntimeException("cannot write block" + block);
        }
    }

    public synchronized Block append(String filename) {
        int blockCount = size(filename);

        try {
            FileChannel fc = getFile(filename);

            fc.position((long) blockCount * blocksize);
            fc.write(ByteBuffer.allocate(blocksize));
        } catch (IOException e) {
            throw new RuntimeException("cannot append block");
        }

        return new Block(filename, blockCount);
    }

    public int size(String filename) {
        try {
            FileChannel fc = getFile(filename);

            return (int) (fc.size() / blocksize);
        } catch (IOException e) {
            throw new RuntimeException("cannot access " + filename);
        }
    }

    private FileChannel getFile(String filename) throws IOException {
        Path dbTable = dbDirectory.resolveSibling(filename);
        FileChannel fileChannel = channelPool.putIfAbsent(filename, FileChannel.open(dbTable, FILE_OPEN_OPTIONS));

        if (fileChannel == null) {
            fileChannel = channelPool.get(filename);
        }

        return fileChannel;
    }

    public boolean isNew() {
        return isNew;
    }

    public int blockSize() {
        return this.blocksize;
    }
}

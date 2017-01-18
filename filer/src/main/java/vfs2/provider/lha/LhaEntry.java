package vfs2.provider.lha;

import jp.gr.java_conf.dangan.util.lha.LhaHeader;

public class LhaEntry {
    private LhaHeader header;

    public LhaEntry(LhaHeader header) {
        this.header = header;
    }

    public LhaHeader getHeader() {
        return header;
    }

    public String getName() {
        return header.getPath();
    }

    public long getSize() {
        return header.getOriginalSize();
    }

    public long getTime() {
        return header.getLastModified().getTime();
    }

    public boolean isDirectory() {
        // TODO:
        return header.getOriginalSize() == 0;
    }
}

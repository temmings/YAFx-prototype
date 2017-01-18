package vfs2.provider.lha;

import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.provider.AbstractFileName;
import org.apache.commons.vfs2.provider.AbstractFileObject;

import java.io.InputStream;
import java.util.HashSet;

/**
 * A file in a LHA file system.
 */
public class LhaFileObject extends AbstractFileObject<LhaFileSystem> {
    private LhaEntry entry;
    private final HashSet<String> children = new HashSet<>();

    private FileType type;

    protected LhaFileObject(final AbstractFileName name,
                            final LhaEntry entry,
                            final LhaFileSystem fs,
                            final boolean containerExists) throws FileSystemException {
        super(name, fs);
        setLhaEntry(entry);
        if (!containerExists) {
            type = FileType.IMAGINARY;
        }
    }

    /**
     * Sets the details for this file object.
     *
     * @param entry LHA information related to this file.
     */
    protected void setLhaEntry(final LhaEntry entry) {
        if (this.entry != null) {
            return;
        }

        if (entry == null || entry.isDirectory()) {
            type = FileType.FOLDER;
        } else {
            type = FileType.FILE;
        }

        this.entry = entry;
    }

    /**
     * Attaches a child.
     * <p>
     * TODO: Shouldn't this method have package-only visibility?
     * Cannot change this without breaking binary compatibility.
     *
     * @param childName The name of the child.
     */
    public void attachChild(final FileName childName) {
        children.add(childName.getBaseName());
    }

    /**
     * Determines if this file can be written to.
     *
     * @return {@code true} if this file is writeable, {@code false} if not.
     * @throws FileSystemException if an error occurs.
     */
    @Override
    public boolean isWriteable() throws FileSystemException {
        return false;
    }

    /**
     * Returns the file's type.
     */
    @Override
    protected FileType doGetType() {
        return type;
    }

    /**
     * Lists the children of the file.
     */
    @Override
    protected String[] doListChildren() {
        try {
            if (!getType().hasChildren()) {
                return null;
            }
        } catch (final FileSystemException e) {
            // should not happen as the type has already been cached.
            throw new RuntimeException(e);
        }

        return children.toArray(new String[children.size()]);
    }

    /**
     * Returns the size of the file content (in bytes).  Is only called if
     * {@link #doGetType} returns {@link FileType#FILE}.
     */
    @Override
    protected long doGetContentSize() {
        return entry.getSize();
    }

    /**
     * Returns the last modified time of this file.
     */
    @Override
    protected long doGetLastModifiedTime() throws Exception {
        return entry.getTime();
    }

    /**
     * Creates an input stream to read the file content from.  Is only called
     * if  {@link #doGetType} returns {@link FileType#FILE}.  The input stream
     * returned by this method is guaranteed to be closed before this
     * method is called again.
     */
    @Override
    protected InputStream doGetInputStream() throws Exception {
        // VFS-210: zip allows to gather an input stream even from a directory and will
        // return -1 on the first read. getType should not be expensive and keeps the tests
        // running
        if (!getType().hasContent()) {
            throw new FileSystemException("vfs.provider/read-not-file.error", getName());
        }

        return getAbstractFileSystem().getLhaFile().getInputStream(entry.getHeader());
    }
}

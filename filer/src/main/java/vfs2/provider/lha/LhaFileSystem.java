package vfs2.provider.lha;

import jp.gr.java_conf.dangan.util.lha.LhaFile;
import jp.gr.java_conf.dangan.util.lha.LhaHeader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.*;
import org.apache.commons.vfs2.provider.AbstractFileName;
import org.apache.commons.vfs2.provider.AbstractFileSystem;
import org.apache.commons.vfs2.provider.UriParser;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * A read-only file system for LHA files.
 */
public class LhaFileSystem extends AbstractFileSystem {
    private static final Log LOG = LogFactory.getLog(LhaFileSystem.class);

    private final File file;
    private LhaFile lhaFile;

    /**
     * Cache doesn't need to be synchronized since it is read-only.
     */
    private final Map<FileName, FileObject> cache = new HashMap<>();

    public LhaFileSystem(final AbstractFileName rootName,
                         final FileObject parentLayer,
                         final FileSystemOptions fileSystemOptions)
            throws FileSystemException {
        super(rootName, parentLayer, fileSystemOptions);

        // Make a local copy of the file
        file = parentLayer.getFileSystem().replicateFile(parentLayer, Selectors.SELECT_SELF);

        // Open the LHA file
        if (!file.exists()) {
            // Don't need to do anything
            lhaFile = null;
            return;
        }

        // lhaFile = createLhaFile(this.file);
    }

    @Override
    public void init() throws FileSystemException {
        super.init();

        try {
            // Build the index
            final List<LhaFileObject> strongRef = new ArrayList<>(getLhaFile().size());
            final Enumeration<? extends LhaHeader> entries = getLhaFile().entries();
            while (entries.hasMoreElements()) {
                final LhaEntry entry = new LhaEntry(entries.nextElement());
                final AbstractFileName name = (AbstractFileName) getFileSystemManager().resolveName(getRootName(),
                        UriParser.encode(entry.getName()));

                // Create the file
                LhaFileObject fileObj;
                if (entry.isDirectory() && getFileFromCache(name) != null) {
                    fileObj = (LhaFileObject) getFileFromCache(name);
                    fileObj.setLhaEntry(entry);
                    continue;
                }

                fileObj = createLhaFileObject(name, entry);
                putFileToCache(fileObj);
                strongRef.add(fileObj);
                fileObj.holdObject(strongRef);

                // Make sure all ancestors exist
                // TODO - create these on demand
                LhaFileObject parent;
                for (AbstractFileName parentName = (AbstractFileName) name.getParent();
                     parentName != null;
                     fileObj = parent, parentName = (AbstractFileName) parentName.getParent()) {
                    // Locate the parent
                    parent = (LhaFileObject) getFileFromCache(parentName);
                    if (parent == null) {
                        parent = createLhaFileObject(parentName, null);
                        putFileToCache(parent);
                        strongRef.add(parent);
                        parent.holdObject(strongRef);
                    }

                    // Attach child to parent
                    parent.attachChild(fileObj.getName());
                }
            }
        } finally {
            closeCommunicationLink();
        }
    }

    protected LhaFile getLhaFile() throws FileSystemException {
        if (lhaFile == null && this.file.exists()) {
            final LhaFile lhaFile = createLhaFile(this.file);

            this.lhaFile = lhaFile;
        }

        return lhaFile;
    }

    protected LhaFileObject createLhaFileObject(final AbstractFileName name,
                                                final LhaEntry entry) throws FileSystemException {
        return new LhaFileObject(name, entry, this, true);
    }

    protected LhaFile createLhaFile(final File file) throws FileSystemException {
        try {
            return new LhaFile(file);
        } catch (final IOException ioe) {
            throw new FileSystemException("vfs.provider.lha/open-lha-file.error", file, ioe);
        }
    }

    @Override
    protected void doCloseCommunicationLink() {
        // Release the lha file
        try {
            if (lhaFile != null) {
                lhaFile.close();
                lhaFile = null;
            }
        } catch (final IOException e) {
            // getLogger().warn("vfs.provider.lha/close-lha-file.error :" + file, e);
            VfsLog.warn(getLogger(), LOG, "vfs.provider.lha/close-lha-file.error :" + file, e);
        }
    }

    /**
     * Returns the capabilities of this file system.
     */
    @Override
    protected void addCapabilities(final Collection<Capability> caps) {
        caps.addAll(LhaFileProvider.capabilities);
    }

    /**
     * Creates a file object.
     */
    @Override
    protected FileObject createFile(final AbstractFileName name) throws FileSystemException {
        // This is only called for files which do not exist in the LHA file
        return new LhaFileObject(name, null, this, false);
    }

    /**
     * Adds a file object to the cache.
     */
    @Override
    protected void putFileToCache(final FileObject file) {
        cache.put(file.getName(), file);
    }

    /**
     * Returns a cached file.
     */
    @Override
    protected FileObject getFileFromCache(final FileName name) {
        return cache.get(name);
    }

    /**
     * remove a cached file.
     */
    @Override
    protected void removeFileFromCache(final FileName name) {
        cache.remove(name);
    }

    /**
     * will be called after all file-objects closed their streams.
     protected void notifyAllStreamsClosed()
     {
     closeCommunicationLink();
     }
     */
}

package vfs2.provider.lha;

import org.apache.commons.vfs2.*;
import org.apache.commons.vfs2.provider.AbstractFileName;
import org.apache.commons.vfs2.provider.AbstractLayeredFileProvider;
import org.apache.commons.vfs2.provider.FileProvider;
import org.apache.commons.vfs2.provider.LayeredFileName;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * A file system provider for LHA files.  Provides read-only file systems.
 */
public class LhaFileProvider extends AbstractLayeredFileProvider implements FileProvider {
    /**
     * The list of capabilities this provider supports
     */
    static final Collection<Capability> capabilities =
            Collections.unmodifiableCollection(Arrays.asList(
                    Capability.GET_LAST_MODIFIED,
                    Capability.GET_TYPE,
                    Capability.LIST_CHILDREN,
                    Capability.READ_CONTENT,
                    Capability.URI,
                    Capability.COMPRESS,
                    Capability.VIRTUAL));

    public LhaFileProvider() {
        super();
    }

    /**
     * Creates a layered file system.  This method is called if the file system
     * is not cached.
     *
     * @param scheme The URI scheme.
     * @param file   The file to create the file system on top of.
     * @return The file system.
     */
    @Override
    protected FileSystem doCreateFileSystem(final String scheme,
                                            final FileObject file,
                                            final FileSystemOptions fileSystemOptions)
            throws FileSystemException {
        final AbstractFileName rootName =
                new LayeredFileName(scheme, file.getName(), FileName.ROOT_PATH, FileType.FOLDER);
        return new LhaFileSystem(rootName, file, fileSystemOptions);
    }

    @Override
    public Collection<Capability> getCapabilities() {
        return capabilities;
    }
}

package Utils;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.WString;

import java.io.File;
import java.io.IOException;

public class NativeUtils {
    /* http://stackoverflow.com/questions/3249117/cross-platform-way-to-detect-a-symbolic-link-junction-point/3286732 */
    interface Kernel32 extends Library {
        public int GetFileAttributesW(WString fileName);
    }

    static Kernel32 lib = null;
    public static int getWin32FileAttributes(File f) throws IOException {
        if (lib == null) {
            synchronized (Kernel32.class) {
                lib = (Kernel32) Native.loadLibrary("kernel32", Kernel32.class);
            }
        }
        return lib.GetFileAttributesW(new WString(f.getCanonicalPath()));
    }

    public static boolean isJunctionOrSymlink(File f) throws IOException {
        if (!f.exists()) { return false; }
        int attributes = getWin32FileAttributes(f);
        if (-1 == attributes) { return false; }
        return ((0x400 & attributes) != 0);
    }
}

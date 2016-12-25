package Util;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.WString;

import java.io.File;
import java.io.IOException;

public class Win32Util {
    /* http://stackoverflow.com/questions/3249117/cross-platform-way-to-detect-a-symbolic-link-junction-point/3286732 */
    interface Kernel32 extends Library {
        int GetFileAttributesW(WString fileName);
    }

    private static Kernel32 lib = null;
    private static int getWin32FileAttributes(File f) throws IOException {
        if (lib == null) {
            synchronized (Kernel32.class) {
                lib = (Kernel32) Native.loadLibrary("kernel32", Kernel32.class);
            }
        }
        return lib.GetFileAttributesW(new WString(f.getCanonicalPath()));
    }

    public static boolean isJunctionOrSymlink(File f) throws IOException {
        if (!f.exists()) return false;
        int attributes = getWin32FileAttributes(f);
        return -1 != attributes && ((0x400 & attributes) != 0);
    }
}

package com.icatchtek.baseutil.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class SHFile {
    public static boolean createDirectory( String dirPath ) {
        if( dirPath != null ) {
            File dir = new File( dirPath );
            if( !dir.exists() ) {
                return dir.mkdirs();
            }
        }

        return false;
    }

    public static boolean createFile( String dirPath, String filename ) {
        boolean ret = createDirectory( dirPath );
        if( !ret )
            return ret;

        File file = new File( dirPath + File.pathSeparator + filename );
        if( !file.exists() ) {
            try {
                file.createNewFile();
            } catch( IOException e ) {
                return false;
            }
        }
        return true;
    }
}

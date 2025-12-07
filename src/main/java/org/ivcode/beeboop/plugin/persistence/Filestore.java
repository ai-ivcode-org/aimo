package org.ivcode.beeboop.plugin.persistence;

import java.io.IOException;
import java.io.InputStream;

public interface Filestore {
    void writeFile(String path, InputStream inputStream) throws IOException;

    InputStream readFile(String path) throws IOException;

    void deleteFile(String path) throws IOException;
}

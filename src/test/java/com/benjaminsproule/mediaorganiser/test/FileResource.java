package com.benjaminsproule.mediaorganiser.test;

import java.io.File;
import java.net.URISyntaxException;

public class FileResource {
    public static File getFile(String fileName) throws URISyntaxException {
        return new File(FileResource.class.getClassLoader().getResource(fileName).toURI());
    }
}

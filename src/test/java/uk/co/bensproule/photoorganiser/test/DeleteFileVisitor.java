package uk.co.bensproule.photoorganiser.test;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.FileVisitResult.CONTINUE;

public class DeleteFileVisitor extends SimpleFileVisitor<Path> {

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
        try {
            if (file.startsWith("test")) {
                Files.delete(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
        try {
            if (dir.toString().endsWith("directory")) {
                Files.delete(dir);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return CONTINUE;
    }
}

package kr.co.seoultel.message.mt.mms.direct.infrastructure.cache.storage.fileIoHandler;


import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class FileIOHandler<E> {

    public abstract void write(String filePath, E e) throws Exception;
    public abstract void write(String filePath, Collection<E> c) throws Exception;

    public abstract void append(E e);
    public abstract void append(Collection<E> c);

    public abstract Collection<E> read(String filePath) throws Exception;
    public abstract Collection<E> read(String filePath, Type type) throws Exception;

    public List<Path> getSubDirectoryPaths(String path) throws Exception {
        Path directoryPath = Paths.get(path);

        boolean isDirectory = Files.exists(directoryPath) && Files.isDirectory(directoryPath);
        assert isDirectory;

        try (Stream<Path> stream = Files.list(directoryPath)) {
            return stream.filter(Files::isDirectory).collect(Collectors.toList());
        }
    }

    public String readFileAsString(String filePath) throws IOException {
        return Files.readString(Path.of(filePath));
    }

    public byte[] readFileAsByteArray(String filePath) throws IOException {
        return Files.readAllBytes(Path.of(filePath));
    }


    public void createFile(String filePath) throws IOException {
        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
    }

    public void createFile(String filePath, FileAttribute<?>... attrs) throws IOException {
        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            Files.createFile(path, attrs);
        }
    }


    public void deleteFileIfExists(String filePath) throws IOException {
        Files.deleteIfExists(Path.of(filePath));
    }
}

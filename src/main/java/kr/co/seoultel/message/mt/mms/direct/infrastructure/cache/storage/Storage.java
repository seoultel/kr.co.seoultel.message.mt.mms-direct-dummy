package kr.co.seoultel.message.mt.mms.direct.infrastructure.cache.storage;


import kr.co.seoultel.message.mt.mms.direct.infrastructure.cache.storage.fileIoHandler.FileIOHandler;
import kr.co.seoultel.message.mt.mms.direct.infrastructure.cache.storage.fileIoHandler.JsonFileIOHandler;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public abstract class Storage<E> {


    protected boolean isSynchronized = true;


    protected final String filePath;
    protected final FileIOHandler<E> fileIOHandler;

    protected Supplier<Collection<E>> destroyBySupplier = this::snapshot;

    protected Storage(String filePath) {
        this.filePath = filePath;
        this.fileIOHandler = new JsonFileIOHandler<E>();
    }

    protected Storage(String filePath, FileIOHandler<E> fileIOHandler) {
        this.filePath = filePath;
        this.fileIOHandler = fileIOHandler;
    }

    public void setSynchronized(boolean aSynchronized) {
        isSynchronized = aSynchronized;
    }

    abstract Collection<E> snapshot();

    public void createFile() throws IOException {
        fileIOHandler.createFile(filePath);
    }

    public void createFile(FileAttribute<?>... attrs) throws IOException {
        fileIOHandler.createFile(filePath, attrs);
    }

    public Storage<E> createFileAnd() throws IOException {
        fileIOHandler.createFile(filePath);
        return this;
    }

    public Storage<E> createFileAnd(FileAttribute<?>... attrs) throws IOException {
        fileIOHandler.createFile(filePath, attrs);
        return this;
    }



    public Storage<E> deleteFileIfExists() throws IOException {
        fileIOHandler.deleteFileIfExists(filePath);
        return this;
    }


    public Optional<List<Path>> getSubDirectoryPaths() throws Exception {
         return Optional.of(fileIOHandler.getSubDirectoryPaths(filePath));
    }

    public Optional<Collection<E>> readFileAsCollection(Type type) throws Exception {
        return Optional.ofNullable(fileIOHandler.read(filePath, type));
    }


    public Optional<Collection<E>> readFileAsCollection() throws Exception {
        return Optional.ofNullable(fileIOHandler.read(filePath));
    }

    public String readFileAsString() throws IOException {
        return fileIOHandler.readFileAsString(filePath);
    }

    public byte[] readFileAsByteArray() throws IOException {
        return fileIOHandler.readFileAsByteArray(filePath);
    }



    public Storage<E> destroyBy(Supplier<Collection<E>> destroyBySupplier) throws IOException {
        this.destroyBySupplier = destroyBySupplier;
        return this;
    }

    protected void destroy() throws Exception {
        if (!isSynchronized) return;

        Collection<E> c = destroyBySupplier.get();
        fileIOHandler.write(filePath, c);
    }

}

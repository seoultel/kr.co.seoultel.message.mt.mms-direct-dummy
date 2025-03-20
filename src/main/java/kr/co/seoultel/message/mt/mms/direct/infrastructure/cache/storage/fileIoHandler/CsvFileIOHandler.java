package kr.co.seoultel.message.mt.mms.direct.infrastructure.cache.storage.fileIoHandler;//package kr.co.seoultel.storiko.fileIoHandler;
//
//import com.opencsv.CSVReader;
//import com.opencsv.CSVReaderBuilder;
//import com.opencsv.bean.CsvToBeanBuilder;
//import com.opencsv.exceptions.CsvException;
//
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.IOException;
//import java.lang.reflect.Type;
//import java.util.Collection;
//
//public class CsvFileIOHandler<E> extends FileIOHandler<E> {
//
//    private final Class<E> cls;
//    public CsvFileIOHandler(Class<E> cls) {
//        this.cls = cls;
//    }
//
//    @Override
//    public void write(String filePath, E e) throws IOException {
//
//    }
//
//    @Override
//    public void write(String filePath, Collection<E> c) throws IOException {
//
//    }
//
//    @Override
//    public Collection<E> read(String filePath) throws IOException, CsvException {
//        Collection<E> collection = new CsvToBeanBuilder<E>(getFileReader(filePath))
//                                            .build()
//                                            .parse();
//
//        return collection;
//    }
//
//    @Override
//    public Collection<E> read(String filePath, Type type) throws IOException {
//        Collection<Type> collection = new CsvToBeanBuilder<Type>(getFileReader(filePath))
//                                            .withType(type.getClass())
//                                            .build()
//                                            .parse();
//
//        return collection;
//    }
//
//    /* TODO : E 타입 데이터를 JSON 형태로 직렬화하여 파일에 append 하는 코드가 당장에 필요하지 않아 추후 작성 */
//    @Override
//    @Deprecated
//    public void append(E e) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    /* TODO : E 타입 데이터를 JSON 형태로 직렬화하여 파일에 append 하는 코드가 당장에 필요하지 않아 추후 작성 */
//    @Override
//    @Deprecated
//    public void append(Collection<E> c) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    private FileReader getFileReader(String filePath) throws FileNotFoundException {
//        return new FileReader(filePath);
//    }
//
//
//}

package kr.co.seoultel.message.mt.mms.direct.infrastructure.cache.storage;



import kr.co.seoultel.message.mt.mms.direct.infrastructure.cache.storage.fileIoHandler.FileIOHandler;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SetStorage<E> extends Storage<E> {

    private final Set<E> set = new HashSet<E>();

    public SetStorage(String filePath) {
        super(filePath);
    }

    public SetStorage(String filePath, FileIOHandler<E> fileIOHandler) {
        super(filePath, fileIOHandler);
    }


    public int size() {
        return set.size();
    }

    public boolean isEmpty() {
        return set.isEmpty();
    }


    public boolean contains(E e) {
        return set.contains(e);
    }

    public Iterator<E> iterator() {
        return set.iterator();
    }

    public Object[] toArray() {
        return set.toArray();
    }

    public boolean add(E e) {
        return set.add(e);
    }

    public boolean remove(E e) {
        return set.remove(e);
    }

    boolean containsAll(Collection<E> c) {
        return set.containsAll(c);
    }

    public boolean addAll(Collection<? extends E> c) {
        return set.addAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return set.retainAll(c);
    }


    public boolean removeAll(Collection<?> c) {
        return set.removeAll(c);
    }

    public void clear() {
        set.clear();
    }



    @Override
    public Collection<E> snapshot() {
        return set;
    }
}

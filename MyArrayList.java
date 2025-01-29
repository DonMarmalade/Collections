// https://docs.oracle.com/javase/8/docs/api/?java/util/ArrayList.html

package Collections;
import java.io.Serializable;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.*;

@SuppressWarnings("unused") // pentru List<T> ca deja <T> e dat de clasa parinte
public class MyArrayList<T> extends AbstractList<T> implements List<T>, RandomAccess, Cloneable, Serializable{
    
    // Variabile
    private static final int DEFAULT_CAPACITY = 10;
    private static final Object[] EMPTY_ELEMENTDATA = {}; // lista goala
    private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {}; // pentru liste ce nu au elemente
    
    transient Object[] elementData; // transient -> ?
    private int size; // numar elemente in lista

    // Constructore
    MyArrayList(int initialCapacity) {
        if(initialCapacity > 0) {
            elementData = new Object[initialCapacity];
        } else if(initialCapacity == 0) {
            elementData = EMPTY_ELEMENTDATA;
        } else {
            throw new IllegalArgumentException("Capacitatea gresita: " + initialCapacity);
        }
    }

    MyArrayList() {
        elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
    }

    // c - colectia de elemente ce va fi pusa in lista
    MyArrayList(Collection<? extends T> c) {
        Object[] _obj = c.toArray();
        if((size = _obj.length) != 0) {
            if(c.getClass() == MyArrayList.class) {
                elementData = _obj;
            } else {
                elementData = Arrays.copyOf(_obj, size, Object[].class);
            }
        } else {
            elementData = EMPTY_ELEMENTDATA;
        }
    }

    //Metode
    public void trimToSize() { // Micsoreaza capacitatea alocata listei pana numarul de campuri deja ocupate
        modCount++; //variabila care raspunde de cate ori a fost modificata lista
        if(size < elementData.length) {
            elementData = (size == 0) ? EMPTY_ELEMENTDATA : Arrays.copyOf(elementData, size);
        }
    }

    //minCapacity - capacitatea minima a listei
    public void ensureCapacity(int minCapacity) { // cat spatiu dorim sa dam la lista
        if (minCapacity > elementData.length && !(elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA && minCapacity <= DEFAULT_CAPACITY)) {
            modCount++;
            grow(minCapacity); // metoda urmeaza    
        }
    }

    private Object[] grow(int minCapacity) {  // cu cat sa creasca lista
        int oldCapacity = elementData.length;
        if(oldCapacity > 0 || elementData != DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
            int newCapacity = MyNewLength(oldCapacity, minCapacity - oldCapacity, oldCapacity >> 1);
            return elementData = Arrays.copyOf(elementData, newCapacity);
        } else {
            return elementData = new Object[Math.max(DEFAULT_CAPACITY, minCapacity)];
        }
    }

    private Object[] grow() { // creste lista cu un spatiu
        return grow(size + 1);
    }

    int SOFT_MAX_ARRAY_LENGTH = Integer.MAX_VALUE - 8; // sa fie

    private int MyNewLength(int oldLength, int minGrowth, int prefGrowth) { // pentru grow
        int prefLength = oldLength + Math.max(minGrowth, prefGrowth);
        if(0 < prefLength && prefLength <= SOFT_MAX_ARRAY_LENGTH) {
            return prefLength;
        } else {
            return MyHugeLength(oldLength, minGrowth);
        }
    }

    private int MyHugeLength(int oldLength, int minGrowth) { // in caz daca lungimea la array nou e pea mare
        int minLength = oldLength + minGrowth;
        if(minLength < 0) {
            throw new OutOfMemoryError("Dimensiunea array-ului trebuita " + oldLength + " + " + minGrowth + " este prea mare");
        } else if (minLength <= SOFT_MAX_ARRAY_LENGTH) {
            return SOFT_MAX_ARRAY_LENGTH;
        } else {
            return minLength;
        }
    }

    public int size() { // verificam cate elemente are lista
        return size;
    }

    public boolean isEmpty() { // verificam daca lista e goala
        return size == 0;
    }

    public boolean contains(Object obj) { // verificam daca macar odata apare elementul in lista
        return indexOf(obj) >= 0; // sau obj == null ? element == null // false : obj.equals(element); //true
    }

    public int indexOf(Object obj) { // va returna indexul la element(cand prima data il vede de la inceput). in caz daca nu este element va returna -1
        return indexOfRange(obj, 0, size);
    }

    int indexOfRange(Object obj, int start, int end) { // primeste pozitia din lista, si inceputul si sfarsitul listei. Cauta elementul in lista (poate pozitia sa fie goala)
        Object[] temp = elementData;
        if(obj == null) { // in caz daca pozitia nu are element
            for(int i = start; i < end; i++) {
                if(temp[i] == null) {
                    return i;
                }
            }
        } else {
            for(int i = start; i < end; i++) {
                if(obj.equals(temp[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    public int lastIndexOf(Object obj) { // va returna indexul la element(cand prima data il vede de la sfarsit). in caz daca nu este element va returna -1
        return lastIndexOfRange(obj, 0, size);
    }

    int lastIndexOfRange(Object obj, int start, int end) { // tot aceasi ce si IndexOfRange dar parcurge for-ul de la coada
        Object[] temp = elementData;
        if(obj == null) { // in caz daca pozitia nu are element
            for(int i = end - 1; i >= start; i--) {
                if(temp[i] == null) {
                    return i;
                }
            }
        } else {
            for(int i = end - 1; i >= start; i--) {
                if(obj.equals(temp[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    public Object clone() { // returneaza copie la lista fara a copia elemente din el
        try {
            MyArrayList<?> v = (MyArrayList<?>) super.clone(); // face clone de la parinte sau din obiect
            v.elementData = Arrays.copyOf(elementData, size);
            v.modCount = 0; // resetam numarul de modificari
            return v;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    public Object[] toArray() { // transforma lista in array (cu dimensiunea arrayului egala cu cate elemente are lista)
        return Arrays.copyOf(elementData, size);
    }

    @SuppressWarnings("unchecked") // sa fie
    public <U> U[] toArray(U[] arr) { // transforma lista in array (cu dimensiunea arrayului egala cu cate spatii au fost alocate pentru lista, locuri ce mai raman sunt inlocuite cu null)
        if(arr.length < size) return (U[]) Arrays.copyOf(elementData, size, arr.getClass());
        System.arraycopy(elementData, 0, arr, 0, size); // pricolnia
        if(arr.length > size) arr[size] = null;
        return arr;
    }

    // Positional Access Operations sau Pentru a accesa elemente din lista

    @SuppressWarnings("unchecked")
    T elementData(int index) {
        return (T) elementData[index];
    }

    @SuppressWarnings("unchecked")
    static <T> T elementAt(Object[] temp, int index) {
        return (T) temp[index];
    }

    // continuam cu metode :)

    public T get(int index) { // primim elementul de pe pozitie
        Objects.checkIndex(index, size);
        return elementData(index); // returneaza elementul din lista
    }

    public T getFirst() { // returneaza primul element din lista
        if (size == 0) {
            throw new NoSuchElementException();
        } else {
            return elementData(0);
        }
    }

    public T getLast() { // returneaza ultimul element din lista
        int last = size - 1;
        if(last < 0) {
            throw new NoSuchElementException();
        } else {
            return elementData(last);
        }
    }

    public T set(int index, T element) { // inlocuim elementul de pe o pozitie si returnam pozitia asta
        Objects.checkIndex(index, size); // verifica daca index este in range
        T oldValue = elementData(index); // gasim pozitia la element ce schimbam
        elementData[index] = element; // schimbam elementul cu elementul nou
        return oldValue; // returnam pozitia cu elementul inlocuit
    }

    private void add(T el, Object[] elementData, int s) { // metoda ajutatoare pentru add, in caz cand adaugam nou element el sa adauge si spatiu pentru el (daca nu era alocat)
        if (s == elementData.length) elementData = grow(); // aici si folosim grow() ca sa creasca lista cu un spatiu
        elementData[s] = el;
        size = s + 1;
    }

    private void rangeCheckForAdd(int index) { // veriunea a rangecheck speciala pentru add si addAll
        if( index > size || index < 0) {
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
        }
    }

    private String outOfBoundsMsg(int index) { // mesaj pentru exceptia din rangeCheckForAdd
        return "Index: "+index+", Dimeansiunea: "+size;
    }

    public boolean add(T e) { // adaugam element la sfarsitul listei
        modCount++;
        add(e, elementData, size);
        return true;
    }

    public void add(int index, T element) { // adaugam element pe o pozitie anumita
        rangeCheckForAdd(index);
        modCount++;
        final int s = size;
        Object[] elementData = this.elementData;
        if(s == elementData.length) elementData = grow(); // POSIBIL AICI SA APARA GRESALA
        System.arraycopy(elementData, index, elementData, index + 1, s - index);
        elementData[index] = element;
        size = s + 1;
    }

    public void addFirst(T element) { // adaugam element la inceputul istei
        add(0, element);
    }

    public void addLast(T element) { // adaugam element la sfarsitul istei
        add(element);
    }

    private void fastRemove(Object[] temp, int i) { // functie ajutatoare pentru remove, sterge elementul fara al returna
        modCount++;
        final int newSize = size - 1;
        if(newSize > i) {
            System.arraycopy(temp, i+1, temp, i, newSize - i); // //se genereaza array nou cu un element mai putin
        }
        temp[size = newSize] = null; // umplem ultimul spatiu cu null
    }

    public T remove(int index) { // stergem element de pe pozitia specifica fara a sterge pozitia, doar umplem ultimul spatiu/pozitie cu null
        Objects.checkIndex(index, size); // verificam daca indexul este in lista
        final Object[] temp = elementData;
        @SuppressWarnings("unchecked")
        T oldValue = (T) temp[index];
        fastRemove(temp, index); // stergem elementul
        return oldValue; // intoarcem elementul sters
    }

    public T removeFirst() { // stergem primul element din lista
        if(size == 0) {
            throw new NoSuchElementException();
        } else {
            Object[] temp = elementData;
            @SuppressWarnings("unchecked")
            T oldValue = (T) temp[0];
            fastRemove(temp, 0);
            return oldValue;
        }
    }

    public T removeLast() { // stergem ultimul element din lista
        int last = size - 1;
        if (last < 0) {
            throw new NoSuchElementException();
        } else {
            Object[] temp = elementData;
            elementData.equals(temp);
            @SuppressWarnings("unchecked")
            T oldValue = (T) temp[last];
            fastRemove(temp, last);
            return oldValue;
        }
    }

    // Pentru a verifica doua arrayliste, apare in remove cu obiect <--- !!! --->

    public boolean equals(Object obj) { // verificam daca obiectul este egal cu alt obiect (sau cu tot el)
        if(obj == this) {
            return true;
        }

        if(!(obj instanceof List)) {
            return false;
        }

        final int expectedModCount = modCount;
        boolean equal = (obj.getClass() == MyArrayList.class) ? equalsArrayList((MyArrayList<?>) obj) : equalsRange((List<?>) obj, 0, size);
        checkForComodification(expectedModCount);
        return equal;
    }

    boolean equalsRange(List<?> other, int from, int to) { // verifica elemente din arraylist de la o valoare (from) pana alta (to) prin iterator
        final Object[] temp = elementData;
        if(to > temp.length) {
            throw new ConcurrentModificationException();
        }
        var oit = other.iterator(); // ceva cu iterator
        for(; from < to; from++) {
            if(!oit.hasNext() || !Objects.equals(temp[from], oit.next())) {
                return false;
            }
        }
        return !oit.hasNext();
    }

    private boolean equalsArrayList(MyArrayList<?> other) { // verifica daca doua arraylist-uri sunt egale
        final int otherModCount = other.modCount;
        final int s = size;
        boolean equal;
        if(equal = (s == other.size)) { // verifica dimensiunea lor
            final Object[] otherTemp = other.elementData;
            final Object[] temp = elementData;
            if(s > temp.length || s > otherTemp.length) {
                throw new ConcurrentModificationException();
            }
            for (int i = 0; i < s; i++) { // verifica fiecare element aparte
                if(!Objects.equals(temp[i], otherTemp[i])) {
                    equal = false;
                    break;
                }
            }
        }
        other.checkForComodification(otherModCount);
        return equal;
    }

    private void checkForComodification(final int expectedModCount) { // in caz daca numar de modificari nou nu coincide cu cel vechi, arunca gresala
        if(modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
    }

    // Ceva ce exista pentru SubList cu hashCode <--- !!! --->

    public int hashCode() { // presupun pentru a rescrie hashCode din clasa Objects, se mai foloseste la equals
        int expectedModCount = modCount;
        int hash = hashCodeRange(0, size);
        checkForComodification(expectedModCount);
        return hash;
    }

    int hashCodeRange(int from, int to) {
        final Object[] temp = elementData;
        if(to > temp.length) {
            throw new ConcurrentModificationException();
        }
        int hashCode = 1;
        for (int i = from; i < to; i++) {
            Object el = temp[i];
            hashCode = 31 * hashCode + (el == null ? 0 : el.hashCode());
        }
        return hashCode;
    }

    // Ne intoarcem iarasi la metode :)

    public boolean remove(Object obj) { // sterge primul element gasit din lista care e asemenea la ce introducem, daca nu este asa element atunci lista nu se schimba. Returneaza true sau false
        final Object[] temp = elementData;
        final int size = this.size;
        int i = 0;
        found: { // tipa "goto" din java sau prin alte cuvinte asta e un bloc de cod cu denumiea "found" 
            if(obj == null) { // daca valoarea cautata este null
                for(; i < size; i++) {
                    if (temp[i] == null) break found; // iesim din blocul de cod "found" daca am gasit valoarea
                }
            } else { // daca valoarea cautata nu este null
                for(; i < size; i++) {
                    if(obj.equals(temp[i])) break found; // iesim din bloc daca am gasit valoarea
                }
            }
            return false; // daca nu gasim valoarea returnam false
        }
        fastRemove(temp, i); // stergem valoaea
        return true; // daca am gasit returnam true
    }

    public void clear() { // inlocuim toate spatiio cu null sau curatam lista de toate elemente
        modCount++;
        final Object[] temp = elementData;
        for (int to = size, i = size = 0; i < to; i++) {
            temp[i] = null;
        }
    }

    public boolean addAll(Collection<? extends T> c) { // adaugam toate elemente din alta lista in lista noastra (la sfarsitul ei si respecta ordinea controlata de iterator)
        Object[] arr = c.toArray();
        modCount++;
        int numNew = arr.length;
        if(numNew == 0) return false;
        Object[] elementData = this.elementData;
        final int s = size;
        if (numNew > elementData.length - s) elementData = grow(s + numNew); // POSIBIL EROARE // adugam atat loc cat este in lista ce am adaugat
        System.arraycopy(arr, 0, elementData, s, numNew); // copiem lista (din pozitia 0 din lista care copiem si punem in lista noastra din ultima pozitie ocupata a ei)
        size = s + numNew; // dimensiunea nou (sau cate elemente sunt acum ocupate)
        return true;
    }

    public boolean addAll(int index, Collection<? extends T> c) { // adaugam toate elemente din alta lista in lista noastra (din indexul specificat si respecta ordinea controlata de iterator)
        rangeCheckForAdd(index);

        Object[] arr = c.toArray();
        modCount++;
        int numNew = arr.length;
        if(numNew == 0) return false;
        Object[] elementData = this.elementData;
        final int s = size;
        if(numNew > elementData.length - s) elementData = grow(s + numNew); // POSIBIL EROARE

        int numMoved = s - index;
        if(numMoved > 0) System.arraycopy(elementData, index, elementData, index + numNew, numMoved); // motam/miscam(copiem) toate elemente din lista nostra din pozitia din care dorim sa introducem elemente noi, cu atatea pozitii cate elemente vom adauga(sau sunt in lista pe care o adaugam)
        System.arraycopy(arr, 0, elementData, index, numNew); // in pozitii eliberate introducem lista adaugata
        size = s + numNew;
        return true;
    }

    protected void removeRange(int fromIndex, int toIndex) { // eliminam toate elemente dintre doua indexuri, apoi miscam toate elemente (ramase in dreapata) spre stanga
        if(fromIndex > toIndex) {
            throw new IndexOutOfBoundsException(outOfBoundsMsg(fromIndex, toIndex));
        }
        modCount++;
        shiftTailOverGap(elementData, fromIndex, toIndex);
    }

    private void shiftTailOverGap(Object[] temp, int fromIndex, int toIndex) { // Metoda ajutatoare pentru removeRange. 
        System.arraycopy(temp, toIndex, temp, fromIndex, size - toIndex); // Copiem toate elemente de dupa ultimul index (pana la care index am dorit sa stergem) si le amplasam la indexul din care stergem
        for(int to = size, i = (size -= toIndex - fromIndex); i < to; i++) temp[i] = null; // Toate elemente dupa ultimul index le facem null
    }

    private static String outOfBoundsMsg(int fromIndex, int toIndex) { // mesaj pentru removeRange in caz daca introducem index in afara listei
        return "Din Index: " + fromIndex + " > Pana Index: " + toIndex;
    }

    public boolean removeAll(Collection<?> c) { // sterge toate elemente ce se afla in colectia specifica
        return batchRemove(c, false, 0, size);
    }

    public boolean retainAll(Collection<?> c) { //  sterge toate elemente ce nu se afla in colectia specifica
        return batchRemove(c, true, 0, size);
    }

    // Aici nu am intales nimic <--- !!! --->
    boolean batchRemove(Collection<?> c, boolean complement, final int from, final int end) { // metoda ajutatoare pentru removeAll si retainAll. Sterge toate elemente din/in afara de colectie specifica
        Objects.requireNonNull(c); // verifica daca referinta e null
        final Object[] temp = elementData;
        int r;
        // Ceva optimizare, pentru ca codul sa incearca sa ruleze numai din punct ce acest poate face schimbari
        for(r = from;; r++) {
            if(r == end) return false;
            if(c.contains(temp[r]) != complement) break;
        }
        int w = r++;

        try {
            for(Object el; r < end; r++) {
                if(c.contains(el = temp[r]) == complement) temp[w++] = el;
            }
        } catch (Throwable ex) { // va prinde exceptie chiar daca c.contains() arunce gresala
            System.arraycopy(temp, r, temp, w, end - r);
            w += end - r;
            throw ex;
        } finally {
            modCount += end - w;
            shiftTailOverGap(temp, w, end);
        }

        return true;
    }

    // Iteratori )::):):::)):):)

    public ListIterator<T> listIterator(int index) { // returneaza iteratorul a listei iar index este elementul ce va urma in iterator (se afla in next). In secventa a listei corecta(ca un element merge dupa altul)
        rangeCheckForAdd(index);
        return new ListItr(index);
    }

    public ListIterator<T> listIterator() { // returneaza iteratorul a listei. In secventa a listei corecta(ca un element merge dupa altul)
        return new ListItr(0);
    }

    public Iterator<T> iterator() { // returneaza iteratorul. In secventa a listei corecta(ca un element merge dupa altul)
        return new Itr();
    }

    // calse pentru iteratori
    private class Itr implements Iterator<T> { // clasa pentru iterator
        int cursor; // indexul la urmator element pentru returnare
        int lastRet = -1; // indexul la ultimul element returnat (-1 daca asa element nu este)
        int expectedModCount = modCount;

        Itr() {} // sa nu fie creat constructor fara parametrii

        public boolean hasNext() { // verific daca am ajuns la sfarsitul listei
            return cursor != size;
        }

        @SuppressWarnings("unchecked")
        public T next() { // trecem la urmator element
            checkForComodification();
            int i = cursor;
            if(i >= size) throw new NoSuchElementException();
            Object[] elementData = MyArrayList.this.elementData;
            if (i >= elementData.length) throw new ConcurrentModificationException();
            cursor = i + 1;
            return (T) elementData[lastRet = i];
        }

        public void remove() {
            if(lastRet < 0) throw new IllegalStateException();
            checkForComodification();

            try {
                MyArrayList.this.remove(lastRet);
                cursor = lastRet;
                lastRet = -1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        @Override // la ceva?
        public void forEachRemaining(Consumer<? super T> action) { // Consumer -> ??????? // performa operatie pentru toate elemente ce au ramas
            Objects.requireNonNull(action);
            final int size = MyArrayList.this.size;
            int i = cursor;
            if (i < size) {
                final Object[] temp = elementData;
                if (i >= temp.length) throw new ConcurrentModificationException();
                for(; i < size && modCount == expectedModCount; i++) {
                    action.accept(elementAt(temp, i)); // accept -> permite sa faca operatie asupra la element
                }
                cursor = i;
                lastRet = i - 1;
                checkForComodification();
            }
        }

        final void checkForComodification() {
            if(modCount != expectedModCount) throw new ConcurrentModificationException();
        }
    }

    private class ListItr extends Itr implements ListIterator<T> {  // clasa pentru iterator al listei
        ListItr(int index) {
            super();
            cursor = index;
        }

        public boolean hasPrevious() { // verifica daca exista precendent
            return cursor != 0;
        }

        public int nextIndex() { // next (urmator)
            return cursor;
        }

        public int previousIndex() { // previous (precedent)
            return cursor - 1;
        }

        @SuppressWarnings("unchecked")
        public T previous() { // mova pozitia la iterator (cursorul) cu o pozitie in urma
            checkForComodification();
            int i = cursor - 1;
            if(i < 0) throw new NoSuchElementException();
            Object[] elemetData = MyArrayList.this.elementData;
            if(i >= elemetData.length) throw new ConcurrentModificationException();
            cursor = i;
            return (T) elementData[lastRet = i];
        }

        public void set(T el) { // schimba ultimul element iterat (next sau previous) cu alt element
            if(lastRet < 0) throw new IllegalStateException();
            checkForComodification();

            try {
                MyArrayList.this.set(lastRet, el);
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        public void add(T el) { // introduce ul element la pozitia cursorului (iteratorului). In caz daca lista e goala adauga element la lista sa nu mai fie goala.
            checkForComodification();

            try {
                int i = cursor;
                MyArrayList.this.add(i, el);
                cursor = i + 1;
                lastRet = -1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }
    }

    // SubLista

    static void subListRangeCheck(int fromIndex, int toIndex, int size) { // am introdus metoda din AbstractList ca nu dorea s-o importeze de acolo
        if (fromIndex < 0)
            throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
        if (toIndex > size)
            throw new IndexOutOfBoundsException("toIndex = " + toIndex);
        if (fromIndex > toIndex)
            throw new IllegalArgumentException("fromIndex(" + fromIndex +
                                               ") > toIndex(" + toIndex + ")");
    }

        public List<T> subList(int fromIndex, int toIndex) { // o sublista din elemente asupra carei pot lucra metode arraylistului si sunt prezente schimbari non-structurale(care nu afecteaza lista si navigarea prin ea)
        subListRangeCheck(fromIndex, toIndex, size);
        return new SubList<>(this, fromIndex, toIndex);
    }

    private static class SubList<T> extends AbstractList<T> implements RandomAccess { // clasa cu sublista
        private final MyArrayList<T> root;
        private final SubList<T> parent;
        private final int offset;
        private int size;

        public SubList(MyArrayList<T> root, int fromIndex, int toIndex) { // constructor din arraylist
            this.root = root;
            this.parent = null;
            this.offset = fromIndex;
            this.size = toIndex - fromIndex;
            this.modCount = root.modCount;
        }

        private SubList(SubList<T> parent, int fromIndex, int toIndex) { // constructor din alt sublist
            this.root = parent.root;
            this.parent = parent;
            this.offset = parent.offset + fromIndex;
            this.size = toIndex - fromIndex;
            this.modCount = parent.modCount;
        }

        public T set(int index, T element) {
            Objects.checkIndex(index, size);
            checkForComodification();
            T oldValue = root.elementData(offset + index);
            root.elementData[offset + index] = element;
            return oldValue;
        }

        public T get(int index) {
            Objects.checkIndex(index, size);
            checkForComodification();
            return root.elementData(offset + index);
        }

        public int size() {
            checkForComodification();
            return size;
        }

        public void add(int index, T element) {
            rangeCheckForAdd(index);
            checkForComodification();
            root.add(offset + index, element);
            updateSizeAndModCount(1);
        }

        public T remove(int index) {
            Objects.checkIndex(index, size);
            checkForComodification();
            T result = root.remove(offset + index);
            updateSizeAndModCount(-1);
            return result;
        }

        protected void removeRange(int fromIndex, int toIndex) {
            checkForComodification();
            root.removeRange(offset + fromIndex, offset + toIndex);
            updateSizeAndModCount(fromIndex - toIndex);
        }

        public boolean addAll(Collection<? extends T> c) {
            return addAll(this.size, c);
        }

        public boolean addAll(int index, Collection<? extends T> c) {
            rangeCheckForAdd(index);
            int cSize = c.size();
            if (cSize == 0) return false;
            checkForComodification();
            root.addAll(offset + index, c);
            updateSizeAndModCount(cSize);
            return true;
        }

        public void replaceAll(UnaryOperator<T> operator) {
            root.replaceAllRange(operator, offset, offset + size);
        }

        public boolean removeAll(Collection<?> c) {
            return batchRemove(c, false);
        }

        public boolean retainAll(Collection<?> c) {
            return batchRemove(c, true);
        }

        private boolean batchRemove(Collection<?> c, boolean complement) {
            checkForComodification();
            int oldSize = root.size;
            boolean modified = root.batchRemove(c, complement, offset, offset + size);
            if(modified) updateSizeAndModCount(root.size - oldSize);
            return modified;
        }

        public boolean removeIf(Predicate<? super T> filter) {
            checkForComodification();
            int oldSize = root.size;
            boolean modified = root.removeIf(filter, offset, offset + size);
            if(modified) updateSizeAndModCount(root.size - oldSize);
            return modified;
        }

        public Object[] toArray() {
            checkForComodification();
            return Arrays.copyOfRange(root.elementData, offset, offset + size);
        }

        @SuppressWarnings("unchecked")
        public <U> U[] toArray(U[] arr) {
            checkForComodification();
            if(arr.length < size) return (U[]) Arrays.copyOfRange(root.elementData, offset, offset + size, arr.getClass());
            System.arraycopy(root.elementData, offset, arr, 0, size);
            if(arr.length > size) arr[size] = null;
            return arr;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }

            if(!(obj instanceof List)) {
                return false;
            }

            boolean equal = root.equalsRange((List<?>)obj, offset, offset + size);
            checkForComodification();
            return equal;
        }

        public int hashCode() {
            int hash = root.hashCodeRange(offset, offset + size);
            checkForComodification();
            return hash;
        }

        public int indexOf(Object obj) {
            int index = root.indexOfRange(obj, offset, offset + size);
            checkForComodification();
            return index >= 0 ? index - offset : -1;
        }

        public int lastIndexOf(Object obj) {
            int index = root.lastIndexOfRange(obj, offset, offset + size);
            checkForComodification();
            return index >= 0 ? index - offset : -1;
        }

        public boolean contains(Object obj) {
            return indexOf(obj) >= 0;
        }

        public Iterator<T> iterator() {
            return listIterator();
        }

        public ListIterator<T> listIterator(int index) {
            checkForComodification();
            rangeCheckForAdd(index);

            return new ListIterator<T>() {
                int cursor = index;
                int lastRet = -1;
                int expectedModCount = SubList.this.modCount;

                public boolean hasNext() {
                    return cursor != SubList.this.size;
                }

                @SuppressWarnings("unchecked")
				public T next() {
                    checkForComodification();
                    int i = cursor;
                    if (i >= SubList.this.size) throw new NoSuchElementException();
                    Object[] elementData = root.elementData;
                    if(offset + 1 >= elementData.length) throw new ConcurrentModificationException();
                    cursor = i + 1;
                    return (T) elementData[offset + (lastRet = i)];
                }

                public boolean hasPrevious() {
                    return cursor != 0;
                }

                @SuppressWarnings("unchecked")
                public T previous() {
                    checkForComodification();
                    int i = cursor - 1;
                    if(i < 0) throw new NoSuchElementException();
                    Object[] elementData = root.elementData;
                    if (offset + i >= elementData.length) throw new ConcurrentModificationException();
                    cursor = i;
                    return (T) elementData[offset + (lastRet = i)]; 
                }

                public void forEachRemaining(Consumer<? super T> action) {
                    Objects.requireNonNull(action);
                    final int size = SubList.this.size;
                    int i = cursor;
                    if(i<size) {
                        final Object[] temp = root.elementData;
                        if(offset + i >= temp.length) throw new ConcurrentModificationException();
                        for(; i < size && root.modCount == expectedModCount; i++) {
                            action.accept(elementAt(temp, offset + i));
                        }
                        cursor = i;
                        lastRet = i - 1;
                        checkForComodification();
                    }
                }

                public int nextIndex() {
                    return cursor;
                }

                public int previousIndex() {
                    return cursor - 1;
                }

                public void remove() {
                    if(lastRet < 0) throw new IllegalStateException();
                    checkForComodification();

                    try {
                        SubList.this.remove(lastRet);
                        cursor = lastRet;
                        lastRet = -1;
                        expectedModCount = SubList.this.modCount;
                    } catch (IndexOutOfBoundsException ex) {
                        throw new ConcurrentModificationException();
                    }
                }

                public void set(T e) {
                    if (lastRet < 0) throw new IllegalStateException();
                    checkForComodification();

                    try {
                        root.set(offset + lastRet, e);
                    } catch (IndexOutOfBoundsException ex) {
                        throw new ConcurrentModificationException();
                    }
                }

                public void add(T e) {
                    checkForComodification();

                    try {
                        int i = cursor;
                        SubList.this.add(i, e);
                        cursor = i + 1;
                        lastRet = -1;
                        expectedModCount = SubList.this.modCount;
                    } catch (IndexOutOfBoundsException ex) {
                        throw new ConcurrentModificationException();
                    }
                }

                final void checkForComodification() {
                    if(root.modCount != expectedModCount) throw new ConcurrentModificationException();
                }
            };
        }

        public List<T> subList(int fromIndex, int toIndex) {
            subListRangeCheck(fromIndex, toIndex, size);
            return new SubList<>(this, fromIndex, toIndex);
        }

        private void rangeCheckForAdd(int index) {
            if (index < 0 || index > this.size) throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
        }

        private String outOfBoundsMsg(int index) {
            return "Index: " + index + ", Dimensiunea: " + this.size;
        }

        private void checkForComodification() {
            if(root.modCount != modCount) throw new ConcurrentModificationException();
        }

        private void updateSizeAndModCount(int sizeChange) {
            SubList<T> slist = this;
            do {
                slist.size += sizeChange;
                slist.modCount = root.modCount;
                slist = slist.parent;
            } while(slist != null);
        }

        public Spliterator<T> spliterator() { // spliterator pentru sublista
            checkForComodification();

            return new Spliterator<T>() {
                private int index = offset;
                private int fence = -1;
                private int expectedModCount;

                private int getFence() {
                    int hi = fence;
                    if (hi < 0) { //POSIBIL EROARE
                        expectedModCount = modCount;
                        hi = fence = offset + size;
                    }
                    return hi;
                }

                public MyArrayList<T>.ArrayListSpliterator trySplit() {
                    int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
                    return (lo >= mid) ? null : root.new ArrayListSpliterator(lo, index = mid, expectedModCount);
                }

                public boolean tryAdvance(Consumer<? super T> action) {
                    Objects.requireNonNull(action);
                    int hi = getFence(), i = index;
                    if(i < hi) {
                        index = i + 1;
                        @SuppressWarnings("unchecked")
                        T e = (T)root.elementData[i];
                        action.accept(e);
                        if (root.modCount != expectedModCount) throw new ConcurrentModificationException();
                        return true;
                    }
                    return false;
                }

                public void forEachRemaining(Consumer<? super T> action) {
                    Objects.requireNonNull(action);
                    int i, hi, mc;
                    MyArrayList<T> lst = root;
                    Object[] a = lst.elementData;
                    if(a != null) { //POSIBIL PROBLEMA
                        if ((hi = fence) < 0) {
                            mc = modCount;
                            hi = offset + size;
                        } else {
                            mc = expectedModCount;
                        }
                        if((i = index) >= 0 && (index = hi) <= a.length) {
                            for(; i<hi;++i) {
                                @SuppressWarnings("unchecked")
                                T e = (T) a[i];
                                action.accept(e);
                            }
                            if(lst.modCount == mc) return;
                        }
                    }
                    throw new ConcurrentModificationException();
                }

                public long estimateSize() {
                    return getFence() - index;
                }

                public int characteristics() {
                    return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED;
                }
            };
        }
    }

    // NU MORE SUBLIST >>> FINALLY
    // continuam cu metode
    
    public void forEach(Consumer<? super T> action) { // override din interface iterable. Trece prin fiecare element
        Objects.requireNonNull(action);
        final int expectedModCount = modCount;
        final Object[] temp = elementData;
        final int size = this.size;
        for (int i = 0; modCount == expectedModCount && i < size; i++) {
            action.accept(elementAt(temp, i));
        }
        if (modCount != expectedModCount) throw new ConcurrentModificationException();
    }

    public Spliterator<T> spliterator() {
        return new ArrayListSpliterator(0, -1, 0);
    }

    final class ArrayListSpliterator implements Spliterator<T> { // pentru a verifa lista? <--- !!! --->
        private int index;
        private int fence;
        private int expectedModCount;

        ArrayListSpliterator(int origin, int fence, int expectedModCount) {
            this.index = origin;
            this.fence = fence;
            this.expectedModCount = expectedModCount;
        }

        private int getFence() { //initializam gardul
            int hi;
            if((hi = fence) < 0) {
                expectedModCount = modCount;
                hi = fence = size;
            }
             return hi;
        }

        public ArrayListSpliterator trySplit() {
            int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
            return (lo >= mid) ? null : new ArrayListSpliterator(lo, index = mid, expectedModCount);
        }

        public boolean tryAdvance(Consumer<? super T> action) {
            if(action == null) throw new NullPointerException();
            int hi = getFence(), i = index;
            if(i < hi) {
                index = i + 1;
                @SuppressWarnings("unchecked")
                T e = (T)elementData[i];
                action.accept(e);
                if(modCount != expectedModCount) throw new ConcurrentModificationException();
                return true;
            }
            return false;
        }

        public void forEachRemaining(Consumer<? super T> action) {
            int i, hi, mc;
            Object[] a = elementData;
            if(action == null) throw new NullPointerException();
            if(a != null) {
                if((hi = fence) < 0) {
                    mc = modCount;
                    hi = size;
                } else {
                    mc = expectedModCount;
                }
                if((i = index) >= 0 && (index = hi) <= a.length) {
                    for(; i < hi; ++i) {
                        @SuppressWarnings("unchecked")
                        T e = (T) a[i];
                        action.accept(e);
                    }
                    if(modCount == mc) return;
                }
            }
            throw new ConcurrentModificationException();
        }

        public long estimateSize() {
            return getFence() - index;
        }

        public int characteristics() {
            return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED;
        }
    }

    //ceva random cu biti (pentru removeIf)

    private static long[] nBits(int n) {
        return new long[((n-1) >> 6) + 1];
    }

    private static void setBit(long[] bits, int i) { // <--- !!! --->
        bits[i >> 6] |= 1L << i;
    }

    private static boolean isClear(long[] bits, int i) {
        return (bits[i >> 6] & (1L << i)) == 0;
    }

    public boolean removeIf(Predicate<? super T> filter) { // override din interfata colectii. Sterge toate elemente ce satisfac conditia
        return removeIf(filter, 0, size);
    }

    boolean removeIf(Predicate<? super T> filter, int i, final int end) { // i - din ce camp cautam
        Objects.requireNonNull(filter);
        int expectedModCount = modCount;
        final Object[] temp = elementData;

        for(; i < end && !filter.test(elementAt(temp, i)); i++); // test - evalueaza predicatul <--- !!! --->
        if(i < end) {
            final int beg = i;
            final long[] deathRow = nBits(end - beg);
            deathRow[0] = 1L;
            for(i = beg + 1; i < end; i++) {
                if(filter.test(elementAt(temp, i))) setBit(deathRow, i - beg);
            }
            if(modCount != expectedModCount) throw new ConcurrentModificationException();
            modCount++;
            int w = beg;
            for(i = beg; i < end; i++) {
                if(isClear(deathRow, i - beg)) temp[w++] = temp[i];
            }
            shiftTailOverGap(temp, w, end);
            return true;
        } else {
            if(modCount != expectedModCount) throw new ConcurrentModificationException();
            return false;
        }
    }

    public void replaceAll(UnaryOperator<T> operator) { //inlocuieste fiecare element cu rezultatul operatiei cu acel element
        replaceAllRange(operator, 0, size);
        modCount++;
    }

    private void replaceAllRange(UnaryOperator<T> operator, int i, int end) { // functie ajutatoare pentru replaceAll (+spoate de dat range anuimit, comod pentru subliste)
        Objects.requireNonNull(operator);
        final int expectedModCount = modCount;
        final Object[] temp = elementData;
        for(; modCount == expectedModCount && i < end; i++) {
            temp[i] = operator.apply(elementAt(temp, i));
        }
        if(modCount != expectedModCount) throw new ConcurrentModificationException();
    }
    

    @SuppressWarnings("unchecked")
    public void sort(Comparator<? super T> c) { // sorteaza lista dupa specific comparator
        final int expectedModCount = modCount;
        Arrays.sort((T[]) elementData, 0, size, c);
        if(modCount != expectedModCount) throw new ConcurrentModificationException();
        modCount++;
    }
}


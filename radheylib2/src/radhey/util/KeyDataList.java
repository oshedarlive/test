/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package radhey.util;

import java.util.*;
import javax.swing.event.*;

/**
 *
 * @author hoshi
 */
public class KeyDataList<E> extends AbstractList<E>
        implements List<E>, RandomAccess, Cloneable, java.io.Serializable {

    private boolean clearListWhenLastListenerIsRemoved=false;
    protected EventListenerList listenerList = new EventListenerList();

    protected short blockSize = 40;
    /**
     * The array buffer into which the elements of the ArrayList are stored.
     * The capacity of the ArrayList is the length of this array buffer.
     */
    protected transient Object[][] blocks;
    protected transient Object[][] keyBlocks;
    /**
     * The size of the ArrayList (the number of elements it contains).
     *
     * @serial
     */
    protected int size;

    private boolean ignoreCaseOfKey=false;

    /**
     * Constructs an empty list with an initial capacity of ten.
     */
    public KeyDataList() {
        blocks = new Object[5][];
        keyBlocks = new Object[5][];
        size=0;
    }

    public KeyDataList(int incrementBy) {
        this();
        if(incrementBy>=5)
            this.blockSize=(short)incrementBy;
        else
            this.blockSize=5;
    }

    public KeyDataList(boolean ignoreCaseOfKey){
        this();
        this.ignoreCaseOfKey=ignoreCaseOfKey;
    }

    public KeyDataList(boolean ignoreCaseOfKey,int incrementBy){
        this(incrementBy);
        this.ignoreCaseOfKey=ignoreCaseOfKey;
    }


    /**
     * Returns the number of elements in this list.
     *
     * @return the number of elements in this list
     */
    public int size() {
        return size;
    }

    /**
     * Returns <tt>true</tt> if this list contains no elements.
     *
     * @return <tt>true</tt> if this list contains no elements
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns <tt>true</tt> if this list contains the specified element.
     * More formally, returns <tt>true</tt> if and only if this list contains
     * at least one element <tt>e</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>.
     *
     * @param o element whose presence in this list is to be tested
     * @return <tt>true</tt> if this list contains the specified element
     */
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    /**
     * Returns the index of the first occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * More formally, returns the lowest index <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
     * or -1 if there is no such index.
     */
    public int indexOf(Object o) {
        if (o == null) {
            for (int i = 0; i < size; i++) {
                if (blocks[(i+blockSize)/blockSize-1][(i+blockSize) % blockSize] == null) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (o.equals(blocks[(i+blockSize)/blockSize-1][(i+blockSize) % blockSize])) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Returns the index of the first occurrence of the specified key
     * in this list, or -1 if this list does not contain the key.
     * More formally, returns the lowest index <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
     * or -1 if there is no such index.
     */
    public int indexOfKey(Object key) {
        if (key == null) {
            for (int i = 0; i < size; i++) {
                if (keyBlocks[(i+blockSize)/blockSize-1][(i+blockSize) % blockSize] == null) {
                    return i;
                }
            }
        } else {
            if(ignoreCaseOfKey && key instanceof String){
                String sKey=(String) key;
                Object keyValue;
                for (int i = 0; i < size; i++) {
                    keyValue=keyBlocks[(i+blockSize)/blockSize-1][(i+blockSize) % blockSize];
                    if(keyValue!=null && keyValue instanceof String && sKey.equalsIgnoreCase((String)keyValue))
                        return i;
                }
            }
            else{
                for (int i = 0; i < size; i++) {
                    if (key.equals(keyBlocks[(i+blockSize)/blockSize-1][(i+blockSize) % blockSize])) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Returns the index of the last occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * More formally, returns the highest index <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
     * or -1 if there is no such index.
     */
    public int lastIndexOf(Object o) {
        if (o == null) {
            for (int i = size - 1; i >= 0; i--) {
                if (blocks[(i+blockSize)/blockSize-1][(i+blockSize) % blockSize] == null) {
                    return i;
                }
            }
        } else {
            for (int i = size - 1; i >= 0; i--) {
                if (o.equals(blocks[(i+blockSize)/blockSize-1][(i+blockSize) % blockSize])) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Returns a shallow copy of this <tt>ArrayList</tt> instance.  (The
     * elements themselves are not copied.)
     *
     * @return a clone of this <tt>ArrayList</tt> instance
     */
    public Object clone() {
        try {
            KeyDataList<E> v = (KeyDataList<E>) super.clone();
            for(int i=0;i<v.blocks.length;i++){
                if(v.blocks[i]==null)
                    break;
                v.blocks[i]=new Object[blockSize];
                v.keyBlocks[i]=new Object[blockSize];
            }
            for(int i=0;i<size;i++){
                v.blocks[(i+blockSize)/blockSize-1][(i+blockSize) % blockSize]=blocks[(i+blockSize)/blockSize-1][(i+blockSize) % blockSize];
                v.keyBlocks[(i+blockSize)/blockSize-1][(i+blockSize) % blockSize]=keyBlocks[(i+blockSize)/blockSize-1][(i+blockSize) % blockSize];
            }
            v.modCount = 0;
            return v;
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }

    /**
     * Returns an array containing all of the elements in this list
     * in proper sequence (from first to last element).
     *
     * <p>The returned array will be "safe" in that no references to it are
     * maintained by this list.  (In other words, this method must allocate
     * a new array).  The caller is thus free to modify the returned array.
     *
     * <p>This method acts as bridge between array-based and collection-based
     * APIs.
     *
     * @return an array containing all of the elements in this list in
     *         proper sequence
     */
    public Object[] toArray() {
        Object[] array=new Object[size];
        for(int i=0;i<size;i++)
            array[i]=blocks[(i+blockSize)/blockSize-1][(i+blockSize) % blockSize];
        return array;
    }

    /**
     * Returns an array containing all of the elements in this list in proper
     * sequence (from first to last element); the runtime type of the returned
     * array is that of the specified array.  If the list fits in the
     * specified array, it is returned therein.  Otherwise, a new array is
     * allocated with the runtime type of the specified array and the size of
     * this list.
     *
     * <p>If the list fits in the specified array with room to spare
     * (i.e., the array has more elements than the list), the element in
     * the array immediately following the end of the collection is set to
     * <tt>null</tt>.  (This is useful in determining the length of the
     * list <i>only</i> if the caller knows that the list does not contain
     * any null elements.)
     *
     * @param a the array into which the elements of the list are to
     *          be stored, if it is big enough; otherwise, a new array of the
     *          same runtime type is allocated for this purpose.
     * @return an array containing the elements of the list
     * @throws ArrayStoreException if the runtime type of the specified array
     *         is not a supertype of the runtime type of every element in
     *         this list
     * @throws NullPointerException if the specified array is null
     */
    public <T> T[] toArray(T[] array) {
        if (array.length < size) // Make a new array of a's runtime type, of length size
            array=(T[]) new Object[size];
        for(int i=0;i<size;i++)
            array[i]=(T)blocks[(i+blockSize)/blockSize-1][(i+blockSize) % blockSize];
        if (array.length > size) {
            array[size] = null;
        }
        return array;
    }

    // Positional Access Operations
    /**
     * Returns the element at the specified position in this list.
     *
     * @param  index index of the element to return
     * @return the element at the specified position in this list
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public E get(int index) {
        if (index<0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        return (E) blocks[(index+blockSize)/blockSize-1][(index+blockSize) % blockSize];
    }

    /**
     * Returns the value for the specified key in the list
     *
     * @param  key key of the value to return
     * @return the value for the specified key in this list
     */
    public E get(Object key){
        int index=indexOfKey(key);
        if(index<0)
            return null;
        return get(index);
    }

    /**
     * Returns the key at the specified position in this list.
     *
     * @param  index index of the key to return
     * @return the key at the specified position in this list
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public Object getKey(int index) {
        if (index<0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        return keyBlocks[(index+blockSize)/blockSize-1][(index+blockSize) % blockSize];
    }

    /**
     * Replaces the element at the specified position in this list with
     * the specified element.
     *
     * @param index index of the element to replace
     * @param element element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public E set(int index, E element) {
        if (index<0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        E oldValue = (E) blocks[(index+blockSize)/blockSize-1][(index+blockSize) % blockSize];
        blocks[(index+blockSize)/blockSize-1][(index+blockSize) % blockSize] = element;
        fireContentsChanged(this, index, index);
        return oldValue;
    }

    public E set(Object key, E element){
        int index=indexOfKey(key);
        if(index<0)
            return null;
        return set(index, element);
    }

    /**
     * Replaces the element at the specified position in this list with
     * the specified element.
     *
     * @param index index of the key to replace
     * @param key key to be stored at the specified position
     * @return the key previously at the specified position
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public Object setKey(int index, Object key) {
        if (index<0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        Object oldValue = keyBlocks[(index+blockSize)/blockSize-1][(index+blockSize) % blockSize];
        keyBlocks[(index+blockSize)/blockSize-1][(index+blockSize) % blockSize] = key;
        return oldValue;
    }

    public boolean add(E e){
        throw new UnsupportedOperationException();
    }

    /**
     * Appends the specified key and element to the end of this list.
     *
     * @param key key to be appended to this list
     * @param e element to be appended to this list
     * @return <tt>true</tt> (as specified by {@link Collection#add})
     */
    public boolean add(Object key,E e) {
        modCount++;//used by iterator to check  if list is perturbed in such a fashion that iterations in progress may yield incorrect results.
        int requiredBlockIndex=(size+blockSize)/blockSize-1;
        if(requiredBlockIndex>=blocks.length){
            Object[][] blocks2=new Object[blocks.length+10][];
            System.arraycopy(blocks, 0, blocks2, 0, blocks.length);
            Object[][] keyBlocks2=new Object[blocks.length+10][];
            System.arraycopy(keyBlocks, 0, keyBlocks2, 0, blocks.length);
            blocks=blocks2;
            keyBlocks=keyBlocks2;
        }
        if(blocks[requiredBlockIndex]==null){
            blocks[requiredBlockIndex]=new Object[blockSize];
            keyBlocks[requiredBlockIndex]=new Object[blockSize];
        }
        int posInBlock=(size+blockSize) % blockSize;
        blocks[requiredBlockIndex][posInBlock]=e;
        keyBlocks[requiredBlockIndex][posInBlock]=key;
        size++;
        fireIntervalAdded(this, size-1, size-1);
        return true;
    }

    /**
     * Inserts the specified key and element at the specified position in this
     * list. Shifts the element currently at that position (if any) and
     * any subsequent elements to the right (adds one to their indices).
     *
     * @param index index at which the specified element is to be inserted
     * @param key key to be inserted
     * @param element element to be inserted
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public void add(int index, Object key, E element) {
        if (index > size || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        modCount++;//used by iterator to check  if list is perturbed in such a fashion that iterations in progress may yield incorrect results.
        ensureCapacity(size+1);
        if(index<size)
            fastPushDown(index, 1);
        blocks[(index+blockSize)/blockSize-1][(index+blockSize) % blockSize]=element;
        keyBlocks[(index+blockSize)/blockSize-1][(index+blockSize) % blockSize]=key;
        size++;
        fireIntervalAdded(this, index, index);
    }

    //does not call ensureCapacity as capacity is already ensured also does not check if index is correct
    protected void fastPushDown(int fromIndex,int qty){
        int ctr;
        ctr=size-1;
        for(;ctr>=fromIndex;ctr--){
            blocks[(ctr+qty+blockSize)/blockSize-1][(ctr+qty+blockSize) % blockSize]=blocks[(ctr+blockSize)/blockSize-1][(ctr+blockSize) % blockSize];
            keyBlocks[(ctr+qty+blockSize)/blockSize-1][(ctr+qty+blockSize) % blockSize]=keyBlocks[(ctr+blockSize)/blockSize-1][(ctr+blockSize) % blockSize];
        }
    }

    /**
     * Removes the element at the specified position in this list.
     * Shifts any subsequent elements to the left (subtracts one from their
     * indices).
     *
     * @param index the index of the element to be removed
     * @return the element that was removed from the list
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public E remove(int index) {
        if(index<0 && index>=size)
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        modCount++;//used by iterator to check  if list is perturbed in such a fashion that iterations in progress may yield incorrect results.
        Object removedObject=blocks[(index+blockSize)/blockSize-1][(index+blockSize) % blockSize];
        //push up
        int ctr;
        for(ctr=index+1;ctr<size;ctr++){
            blocks[(ctr-1+blockSize)/blockSize-1][(ctr-1+blockSize) % blockSize]=blocks[(ctr+blockSize)/blockSize-1][(ctr+blockSize) % blockSize];
            keyBlocks[(ctr-1+blockSize)/blockSize-1][(ctr-1+blockSize) % blockSize]=keyBlocks[(ctr+blockSize)/blockSize-1][(ctr+blockSize) % blockSize];
        }
         blocks[(ctr-1+blockSize)/blockSize-1][(ctr-1+blockSize) % blockSize]=null;
         keyBlocks[(ctr-1+blockSize)/blockSize-1][(ctr-1+blockSize) % blockSize]=null;
         size--;
         if(size%blockSize==0){
             ctr=size/blockSize+1;
             if(ctr<blocks.length){
                 blocks[ctr]=null;
                 keyBlocks[ctr]=null;
             }
        }
         fireIntervalRemoved(this, index, index);
         return (E)removedObject;
    }

    public E removeKey(Object key){
        int index=indexOfKey(key);
        if(index<0)
            return null;
        return remove(index);
    }

    private void fastRemove(int index){
        modCount++;//used by iterator to check  if list is perturbed in such a fashion that iterations in progress may yield incorrect results.
        //push up
        int ctr;
        for(ctr=index+1;ctr<size;ctr++){
            blocks[(ctr-1+blockSize)/blockSize-1][(ctr-1+blockSize) % blockSize]=blocks[(ctr+blockSize)/blockSize-1][(ctr+blockSize) % blockSize];
            keyBlocks[(ctr-1+blockSize)/blockSize-1][(ctr-1+blockSize) % blockSize]=keyBlocks[(ctr+blockSize)/blockSize-1][(ctr+blockSize) % blockSize];
        }
         blocks[(ctr-1+blockSize)/blockSize-1][(ctr-1+blockSize) % blockSize]=null;
         keyBlocks[(ctr-1+blockSize)/blockSize-1][(ctr-1+blockSize) % blockSize]=null;
         size--;
         if(size%blockSize==0){
             ctr=size/blockSize+1;
             if(ctr<blocks.length){
                 blocks[ctr]=null;
                 keyBlocks[ctr]=null;
             }
        }
    }

    /**
     * Removes the first occurrence of the specified element from this list,
     * if it is present.  If the list does not contain the element, it is
     * unchanged.  More formally, removes the element with the lowest index
     * <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>
     * (if such an element exists).  Returns <tt>true</tt> if this list
     * contained the specified element (or equivalently, if this list
     * changed as a result of the call).
     *
     * @param o element to be removed from this list, if present
     * @return <tt>true</tt> if this list contained the specified element
     */
    public boolean remove(Object o) {
        int index=indexOf(o);
        if(index>=0){
            fastRemove(index);
            fireIntervalRemoved(this, index, index);
            return true;
        }
        return false;
    }


    /**
     * Removes all of the elements from this list.  The list will
     * be empty after this call returns.
     */
    public void clear() {
        modCount++;

        // Let gc do its work
        for (int i = 0; i < size; i++) {
            blocks[(i+blockSize)/blockSize-1][(i+blockSize) % blockSize]=null;
            keyBlocks[(i+blockSize)/blockSize-1][(i+blockSize) % blockSize]=null;
        }
        for(int i=0;i<blocks.length;i++){
            blocks[i]=null;
            keyBlocks[i]=null;
        }
        if(blocks.length>5){
            blocks=new Object[5][];
            keyBlocks=new Object[5][];
        }
        int oldSize=size;
        size=0;
        if(oldSize>0)
            fireIntervalRemoved(this, 0, oldSize-1);
    }

    /**
     * Removes all of the elements from this list - but maintains capacity if maintainCapacity is true.
     * The list will be empty after this call returns.
     */
    public void clear(boolean maintainCapacity) {
        modCount++;

        // Let gc do its work
        for (int i = 0; i < size; i++) {
            blocks[(i+blockSize)/blockSize-1][(i+blockSize) % blockSize]=null;
            keyBlocks[(i+blockSize)/blockSize-1][(i+blockSize) % blockSize]=null;
        }
        if(!maintainCapacity){
            for(int i=0;i<blocks.length;i++){
                blocks[i]=null;
                keyBlocks[i]=null;
            }
            if(blocks.length>10){
                blocks=new Object[10][];
                keyBlocks=new Object[10][];
            }
        }
        int oldSize=size;
        size=0;
        if(oldSize>0)
            fireIntervalRemoved(this, 0, oldSize-1);
    }

    /**
     * This operation is Unsupported as of now
     * Appends all of the elements in the specified collection to the end of
     * this list, in the order that they are returned by the
     * specified collection's Iterator.  The behavior of this operation is
     * undefined if the specified collection is modified while the operation
     * is in progress.  (This implies that the behavior of this call is
     * undefined if the specified collection is this list, and this
     * list is nonempty.)
     *
     * @param c collection containing elements to be added to this list
     * @return <tt>true</tt> if this list changed as a result of the call
     * @throws NullPointerException if the specified collection is null
     */
    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    protected void ensureCapacity(int capacity){
        int requiredBlocks=(capacity-1+blockSize)/blockSize;
        int lastBlockIndex=(size-1+blockSize)/blockSize-1;
        if(requiredBlocks>blocks.length){
            Object[][] blocks2=new Object[requiredBlocks+8][];
            System.arraycopy(blocks, 0, blocks2, 0, blocks.length);
            Object[][] keyBlocks2=new Object[requiredBlocks+8][];
            System.arraycopy(keyBlocks, 0, keyBlocks2, 0, blocks.length);
            blocks=blocks2;
            keyBlocks=keyBlocks2;
        }
        if(lastBlockIndex<0)
            lastBlockIndex=0;
        for(;lastBlockIndex<requiredBlocks;lastBlockIndex++){
            if(blocks[lastBlockIndex]==null){
                blocks[lastBlockIndex]=new Object[blockSize];
                keyBlocks[lastBlockIndex]=new Object[blockSize];
            }
        }
    }

    /**
     * This operation is Unsupported as of now
     * Inserts all of the elements in the specified collection into this
     * list, starting at the specified position.  Shifts the element
     * currently at that position (if any) and any subsequent elements to
     * the right (increases their indices).  The new elements will appear
     * in the list in the order that they are returned by the
     * specified collection's iterator.
     *
     * @param index index at which to insert the first element from the
     *              specified collection
     * @param c collection containing elements to be added to this list
     * @return <tt>true</tt> if this list changed as a result of the call
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @throws NullPointerException if the specified collection is null
     */
    public boolean addAll(int index, Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * Removes from this list all of the elements whose index is between
     * <tt>fromIndex</tt>, inclusive, and <tt>toIndex</tt>, exclusive.
     * Shifts any succeeding elements to the left (reduces their index).
     * This call shortens the list by <tt>(toIndex - fromIndex)</tt> elements.
     * (If <tt>toIndex==fromIndex</tt>, this operation has no effect.)
     *
     * @param fromIndex index of first element to be removed
     * @param toIndex index after last element to be removed
     * @throws IndexOutOfBoundsException if fromIndex or toIndex out of
     *              range (fromIndex &lt; 0 || fromIndex &gt;= size() || toIndex
     *              &gt; size() || toIndex &lt; fromIndex)
     */
    protected void removeRange(int fromIndex, int toIndex) {
        if(fromIndex<0 || fromIndex>=size)
            throw new IndexOutOfBoundsException("Index: "+fromIndex+", Size: "+size);
        if(toIndex<0 || toIndex>=size)
            throw new IndexOutOfBoundsException("Index: "+toIndex+", Size: "+size);
        int temp;
        if(fromIndex>toIndex){//swap
            temp=fromIndex;
            fromIndex=toIndex;
            toIndex=temp;
        }
        int numRemoved=toIndex-fromIndex + 1;
        int ctr;
        if(numRemoved<1)
            return;
        modCount++;//used by iterator to check  if list is perturbed in such a fashion that iterations in progress may yield incorrect results.
        for(ctr=fromIndex;ctr<size-numRemoved;ctr++){
            blocks[(ctr+blockSize)/blockSize-1][(ctr+blockSize) % blockSize]=blocks[(ctr+numRemoved+blockSize)/blockSize-1][(ctr+numRemoved+blockSize) % blockSize];
            keyBlocks[(ctr+blockSize)/blockSize-1][(ctr+blockSize) % blockSize]=keyBlocks[(ctr+numRemoved+blockSize)/blockSize-1][(ctr+numRemoved+blockSize) % blockSize];
        }
        size-=numRemoved;
        trimToSize();
        fireIntervalRemoved(this, fromIndex, toIndex);
    }

    /**
     * Trims the capacity of this <tt>ArrayList</tt> instance to be the
     * list's current size.  An application can use this operation to minimize
     * the storage of an <tt>ArrayList</tt> instance.
     */
    public void trimToSize() {
        int lastBlockIndex=(size-1+blockSize)/blockSize-1;
        int ctr,i;
        for(ctr=blocks.length-1;ctr>lastBlockIndex;ctr--){
            if(blocks[ctr]!=null){
                for(i=0;i<blockSize;i++){
                    blocks[ctr][i]=null;
                    keyBlocks[ctr][i]=null;
                }
            }
            blocks[ctr]=null;
            keyBlocks[ctr]=null;
        }
        //make remaining items in last block null
        if(blocks[ctr]!=null){
            i=(size + blockSize)%blockSize;//position of last element in block + 1
            for(;i<blockSize;i++){
                blocks[ctr][i]=null;
                keyBlocks[ctr][i]=null;
            }
        }
        if(size==0)
            blocks[0]=null;
        if(blocks.length-1>lastBlockIndex){//remove extra blocks
            blocks=Arrays.copyOf(blocks, lastBlockIndex+1);
            keyBlocks=Arrays.copyOf(keyBlocks, lastBlockIndex+1);
        }
    }

    /**
     * Save the state of the <tt>ArrayList</tt> instance to a stream (that
     * is, serialize it).
     *
     * @serialData The length of the array backing the <tt>ArrayList</tt>
     *             instance is emitted (int), followed by all of its elements
     *             (each an <tt>Object</tt>) in the proper order.
     */
    private void writeObject(java.io.ObjectOutputStream s)
            throws java.io.IOException {
        // Write out size, and any hidden stuff
        int expectedModCount = modCount;
        s.defaultWriteObject();

        // Write out all elements in the proper order.
        for (int i = 0; i < size; i++) {
            s.writeObject(blocks[(i+blockSize)/blockSize-1][(i+blockSize) % blockSize]);
            s.writeObject(keyBlocks[(i+blockSize)/blockSize-1][(i+blockSize) % blockSize]);
        }

        if (modCount++ != expectedModCount) {
            throw new ConcurrentModificationException();
        }

    }

    /**
     * Reconstitute the <tt>ArrayList</tt> instance from a stream (that is,
     * deserialize it).
     */
    private void readObject(java.io.ObjectInputStream s)
            throws java.io.IOException, ClassNotFoundException {
        // Read in size, and any hidden stuff
        s.defaultReadObject();

        blocks = new Object[10][];
        blocks[0] = new Object[blockSize];
        keyBlocks = new Object[10][];
        keyBlocks[0] = new Object[blockSize];
        int numNew=size;
        size=0;
        if(numNew==0)
            return;
        ensureCapacity(numNew);
        int ctr;
        // Read in all elements in the proper order.
        for(ctr=0;ctr<numNew;ctr++){
            blocks[(ctr+blockSize)/blockSize-1][(ctr+blockSize) % blockSize]=s.readObject();
            keyBlocks[(ctr+blockSize)/blockSize-1][(ctr+blockSize) % blockSize]=s.readObject();
        }
        size=numNew;
    }

    /**
     * Adds a listener to the list that's notified each time a change
     * to the data model occurs.
     *
     * @param l the <code>ListDataListener</code> to be added
     */
    public void addListDataListener(ListDataListener l) {
	listenerList.add(ListDataListener.class, l);
    }


    /**
     * Removes a listener from the list that's notified each time a
     * change to the data model occurs.
     *
     * @param l the <code>ListDataListener</code> to be removed
     */
    public void removeListDataListener(ListDataListener l) {
    	listenerList.remove(ListDataListener.class, l);
        if(clearListWhenLastListenerIsRemoved && listenerList.getListenerCount()<1)
            clear();
    }


    /**
     * Returns an array of all the list data listeners
     * registered on this <code>AbstractListModel</code>.
     *
     * @return all of this model's <code>ListDataListener</code>s,
     *         or an empty array if no list data listeners
     *         are currently registered
     *
     * @see #addListDataListener
     * @see #removeListDataListener
     *
     * @since 1.4
     */
    public ListDataListener[] getListDataListeners() {
        return (ListDataListener[])listenerList.getListeners(
                ListDataListener.class);
    }


    /**
     * <code>AbstractListModel</code> subclasses must call this method
     * <b>after</b>
     * one or more elements of the list change.  The changed elements
     * are specified by the closed interval index0, index1 -- the endpoints
     * are included.  Note that
     * index0 need not be less than or equal to index1.
     *
     * @param source the <code>ListModel</code> that changed, typically "this"
     * @param index0 one end of the new interval
     * @param index1 the other end of the new interval
     * @see EventListenerList
     * @see DefaultListModel
     */
    protected void fireContentsChanged(Object source, int index0, int index1)
    {
	Object[] listeners = listenerList.getListenerList();
	ListDataEvent e = null;

	for (int i = listeners.length - 2; i >= 0; i -= 2) {
	    if (listeners[i] == ListDataListener.class) {
		if (e == null) {
		    e = new ListDataEvent(source, ListDataEvent.CONTENTS_CHANGED, index0, index1);
		}
		((ListDataListener)listeners[i+1]).contentsChanged(e);
	    }
	}
    }


    /**
     * <code>AbstractListModel</code> subclasses must call this method
     * <b>after</b>
     * one or more elements are added to the model.  The new elements
     * are specified by a closed interval index0, index1 -- the enpoints
     * are included.  Note that
     * index0 need not be less than or equal to index1.
     *
     * @param source the <code>ListModel</code> that changed, typically "this"
     * @param index0 one end of the new interval
     * @param index1 the other end of the new interval
     * @see EventListenerList
     * @see DefaultListModel
     */
    protected void fireIntervalAdded(Object source, int index0, int index1)
    {
	Object[] listeners = listenerList.getListenerList();
	ListDataEvent e = null;

	for (int i = listeners.length - 2; i >= 0; i -= 2) {
	    if (listeners[i] == ListDataListener.class) {
		if (e == null) {
		    e = new ListDataEvent(source, ListDataEvent.INTERVAL_ADDED, index0, index1);
		}
		((ListDataListener)listeners[i+1]).intervalAdded(e);
	    }
	}
    }


    /**
     * <code>AbstractListModel</code> subclasses must call this method
     * <b>after</b> one or more elements are removed from the model.
     * <code>index0</code> and <code>index1</code> are the end points
     * of the interval that's been removed.  Note that <code>index0</code>
     * need not be less than or equal to <code>index1</code>.
     *
     * @param source the <code>ListModel</code> that changed, typically "this"
     * @param index0 one end of the removed interval,
     *               including <code>index0</code>
     * @param index1 the other end of the removed interval,
     *               including <code>index1</code>
     * @see EventListenerList
     * @see DefaultListModel
     */
    protected void fireIntervalRemoved(Object source, int index0, int index1)
    {
	Object[] listeners = listenerList.getListenerList();
	ListDataEvent e = null;

	for (int i = listeners.length - 2; i >= 0; i -= 2) {
	    if (listeners[i] == ListDataListener.class) {
		if (e == null) {
		    e = new ListDataEvent(source, ListDataEvent.INTERVAL_REMOVED, index0, index1);
		}
		((ListDataListener)listeners[i+1]).intervalRemoved(e);
	    }
	}
    }

    /**
     * Returns an array of all the objects currently registered as
     * <code><em>Foo</em>Listener</code>s
     * upon this model.
     * <code><em>Foo</em>Listener</code>s
     * are registered using the <code>add<em>Foo</em>Listener</code> method.
     * <p>
     * You can specify the <code>listenerType</code> argument
     * with a class literal, such as <code><em>Foo</em>Listener.class</code>.
     * For example, you can query a list model
     * <code>m</code>
     * for its list data listeners
     * with the following code:
     *
     * <pre>ListDataListener[] ldls = (ListDataListener[])(m.getListeners(ListDataListener.class));</pre>
     *
     * If no such listeners exist,
     * this method returns an empty array.
     *
     * @param listenerType  the type of listeners requested;
     *          this parameter should specify an interface
     *          that descends from <code>java.util.EventListener</code>
     * @return an array of all objects registered as
     *          <code><em>Foo</em>Listener</code>s
     *          on this model,
     *          or an empty array if no such
     *          listeners have been added
     * @exception ClassCastException if <code>listenerType</code> doesn't
     *          specify a class or interface that implements
     *          <code>java.util.EventListener</code>
     *
     * @see #getListDataListeners
     *
     * @since 1.3
     */
    public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
    	return listenerList.getListeners(listenerType);
    }

    /**
     * @return the clearListWhenLastListenerIsRemoved
     */
    public boolean isClearListWhenLastListenerIsRemoved() {
        return clearListWhenLastListenerIsRemoved;
    }

    /**
     * @param clearListWhenLastListenerIsRemoved the clearListWhenLastListenerIsRemoved to set
     */
    public void setClearListWhenLastListenerIsRemoved(boolean clearListWhenLastListenerIsRemoved) {
        this.clearListWhenLastListenerIsRemoved = clearListWhenLastListenerIsRemoved;
    }

    public int indexOfStartsWith(String s){
        Object item;
        if(s==null || size==0)
            return -1;
        s=s.toUpperCase();
        for(int i=0;i<size;i++){
            item=blocks[(i+blockSize)/blockSize-1][(i+blockSize) % blockSize];
            if(item==null)
                continue;
            if(item instanceof String){
                if(((String)item).toUpperCase().startsWith(s))
                    return i;
            }
            else if(item.toString().toUpperCase().startsWith(s))
               return i;
        }
        return -1;
    }
}

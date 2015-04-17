/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package radhey.util;
import java.util.*;

/**
 *
 * @author hoshi
 */
public class SortedFastGrowingList2 <E> extends FastGrowingList<E> {
    private Comparator<E> comparator;

    public SortedFastGrowingList2(Comparator<E> comparator){
        if(comparator==null)
            throw new NullPointerException("comparator cannot be null");
        this.comparator=comparator;
    }

    public SortedFastGrowingList2(Comparator<E> comparator,int incrementBy){
        super(incrementBy);
        if(comparator==null)
            throw new NullPointerException("comparator cannot be null");
        this.comparator=comparator;
    }

    public SortedFastGrowingList2(Collection<? extends E> c, Comparator<E> comparator) {
        if(comparator==null)
            throw new NullPointerException("comparator cannot be null");
        this.comparator=comparator;
        if(c instanceof FastGrowingList){
            FastGrowingList c2=(FastGrowingList)c;
            if(c2.size==0)
                return;
            int ctr;
            if(c2 instanceof SortedFastGrowingList2 && ((SortedFastGrowingList2)c2).comparator.getClass()==comparator.getClass()){
                ensureCapacity(size + c2.size);
                for(ctr=0;ctr<c2.size;ctr++)
                    blocks[(ctr+blockSize)/blockSize-1][(ctr+blockSize) % blockSize]=c2.blocks[(ctr+blockSize)/blockSize-1][(ctr+blockSize) % blockSize];
                size=c2.size;
            }
            else{
                for(ctr=0;ctr<c2.size;ctr++)
                    add((E)c2.blocks[(ctr+blockSize)/blockSize-1][(ctr+blockSize) % blockSize]);
            }
        }
        else{
            Object[] a = c.toArray();
            int numNew=a.length;
            if(numNew==0)
                return;
            int ctr;
            for(ctr=0;ctr<numNew;ctr++)
               add((E)a[ctr]);
        }
    }

    @Override
    public boolean add(E e) {
        modCount++;//used by iterator to check  if list is perturbed in such a fashion that iterations in progress may yield incorrect results.
        int insertAt=size;
        int requiredBlockIndex=(size+blockSize)/blockSize-1;
        if(requiredBlockIndex>=blocks.length){
            Object[][] blocks2=new Object[blocks.length+10][];
            System.arraycopy(blocks, 0, blocks2, 0, blocks.length);
            blocks=blocks2;
        }
        if(blocks[requiredBlockIndex]==null)
            blocks[requiredBlockIndex]=new Object[blockSize];

        //if size==0 or e >= last element then add element to the end
        if(size==0 || compare(e,blocks[(size-1+blockSize)/blockSize-1][(size-1+blockSize) % blockSize])>=0){
            blocks[requiredBlockIndex][(size+blockSize) % blockSize]=e;

        }
        else{
            //find first element that is greater then e insert before that element
            insertAt=indexOfFirstGreater(e);
            fastPushDown(insertAt, 1);
            blocks[(insertAt+blockSize)/blockSize-1][(insertAt+blockSize) % blockSize]=e;
        }
        size++;
        fireIntervalAdded(this, insertAt, insertAt);
        return true;
    }

    @Override
    public void add(int index, E element) {
        add(element);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        if(c instanceof FastGrowingList){
            FastGrowingList c2=(FastGrowingList)c;
            if(c2.size==0)
                return false;
            int ctr;
            modCount++;
            int blockSize2=c2.blockSize;
            if(size==0 && c2 instanceof SortedFastGrowingList2 && ((SortedFastGrowingList2)c2).comparator.getClass()==comparator.getClass()){
                ensureCapacity(size + c2.size);
                for(ctr=0;ctr<c2.size;ctr++)
                    blocks[(ctr+blockSize)/blockSize-1][(ctr+blockSize) % blockSize]=c2.blocks[(ctr+blockSize2)/blockSize2-1][(ctr+blockSize2) % blockSize2];
                size=c2.size;
                fireIntervalAdded(this, 0, size-1);
            }
            else{
                for(ctr=0;ctr<c2.size;ctr++)
                    add((E)c2.blocks[(ctr+blockSize2)/blockSize2-1][(ctr+blockSize2) % blockSize2]);
            }
        }
        else{
            Object[] a = c.toArray();
            int numNew=a.length;
            if(numNew==0)
                return false;
            int ctr;
            for(ctr=0;ctr<numNew;ctr++)
               add((E)a[ctr]);
        }
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return addAll(c);
    }

    @Override
    public int indexOf(Object item) {
        int low=0,hi=size-1,mid;
        int compareResult,index=-1;

        while(hi>low){
            mid=(low+hi)/2;
            compareResult=compare(item,blocks[(mid+blockSize)/blockSize-1][(mid+blockSize) % blockSize]);
            if(compareResult==0){
                index=mid;
                break;
            }
            else if(compareResult>0)
                low=mid+1;
            else
                hi=mid-1;
        }
        if(hi<=low){
            if(hi==low && compare(item,blocks[(hi+blockSize)/blockSize-1][(hi+blockSize) % blockSize])==0)
                index=hi;
            else
                index=-1;
        }
        if(index==-1)
            return index;
        //keep going up till no duplicate
        while(index>0 && compare(blocks[(index+blockSize)/blockSize-1][(index+blockSize) % blockSize],blocks[(index-1+blockSize)/blockSize-1][(index-1+blockSize) % blockSize])==0)
            index--;

        return index;
    }

    @Override
    public int lastIndexOf(Object item) {
        int low=0,hi=size-1,mid;
        int compareResult,index=-1;

        while(hi>low){
            mid=(low+hi)/2;
            compareResult=compare(item,blocks[(mid+blockSize)/blockSize-1][(mid+blockSize) % blockSize]);
            if(compareResult==0){
                index=mid;
                break;
            }
            else if(compareResult>0)
                low=mid+1;
            else
                hi=mid-1;
        }
        if(hi<=low){
            if(hi==low && compare(item,blocks[(hi+blockSize)/blockSize-1][(hi+blockSize) % blockSize])==0)
                index=hi;
            else
                index=-1;
        }
        if(index==-1)
            return index;
        //keep going down till no duplicate
        while(index<size-1 && compare(blocks[(index+blockSize)/blockSize-1][(index+blockSize) % blockSize],blocks[(index+1+blockSize)/blockSize-1][(index+1+blockSize) % blockSize])==0)
            index++;

        return index;
    }

    @Override
    public E set(int index, E element) {
        if (index<0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        E oldValue = (E) blocks[(index+blockSize)/blockSize-1][(index+blockSize) % blockSize];
        //if element before index is <= element and element after index is >= element then set else remove and add
        Object valueBefore=element,valueAfter=element;
        if(index>0)
            valueBefore=blocks[(index-1+blockSize)/blockSize-1][(index-1+blockSize) % blockSize];
        if(index<size-1)
            valueAfter=blocks[(index+1+blockSize)/blockSize-1][(index+1+blockSize) % blockSize];
        if(compare(valueBefore,element)<=0 && compare(valueAfter,element)>=0){
            blocks[(index+blockSize)/blockSize-1][(index+blockSize) % blockSize]=element;
            fireContentsChanged(this, index, index);
            return oldValue;
        }
        modCount++;
        remove(index);
        add(element);
        return oldValue;
    }

    private int compare(Object o1, Object o2){
        if(o1==o2)
            return 0;
        else if(o1==null)
            return -1;
        else if(o2==null)
            return 1;
        return comparator.compare((E)o1,(E)o2);
    }

    private int indexOfFirstGreater(Object item){
        int low=0,hi=size-1,mid=0;
        while(hi>low){
            mid=(low+hi)/2;
            if(compare(item,blocks[(mid+blockSize)/blockSize-1][(mid+blockSize) % blockSize])>=0)
                low=mid+1;
            else
                hi=mid;
        }
        if(hi<=low && compare(item,blocks[(hi+blockSize)/blockSize-1][(hi+blockSize) % blockSize])<0){
            return hi;
        }
        return hi+1;
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

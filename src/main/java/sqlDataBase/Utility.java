package sqlDataBase;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.SelectQuery.*;


public class Utility{
    
    
    //this search  method is a modified version of the one found in our textbook, Java Software Structures
    //It has been modified from being generic to specifically dealing with Row Objects, and the 
    //potential multiple stages of an orderBy search
    
    //sort the data using mergesort, and the specified orderbys
    public static void mergeSort(Row[] data,OrderBy[] orderBys){
        mergeSort(data,0,data.length-1,orderBys);
    }
    
    //recursively used to mergesort smaller and smaller segments
    private static void mergeSort(Row[] data,int min, int max,OrderBy[] orderBys){      
        if(min<max){
            int mid = (min+max)/2;
            mergeSort(data,min,mid,orderBys);
            mergeSort(data,mid+1,max,orderBys);
            merge(data,min,mid,max,orderBys);
        }        
    }
    //merge to segments into one in the proper sorted order as defined by the orderbys
    private static void merge(Row[] data,int min, int mid, int max,OrderBy[] orderBys){
        Row[] temp = (new Row[data.length]);
        int first1 = min, last1 = mid;
        int first2 = mid+1, last2 = max;
        int index = first1;
        while((first1<=last1) && (first2<=last2)){
            int comparison = 0;
            boolean ascendingOrder = true;            
            //we iterate through the orderBys until we find that order matters, or we run out
            for(OrderBy order: orderBys){
                comparison = data[first1].compareToViaColumn(data[first2],order.getColumnID().getColumnName());
                ascendingOrder = order.isAscending();
                if(comparison != 0){
                    break;
                }
            }            
            //if ascendingOrder is true, compareTo should compare to < 0 
            //otherwise it should be > 0
            if(((comparison>0)&&(ascendingOrder))  || ((comparison<0)&&(!ascendingOrder)) ){
                temp[index] = data[first1];
                first1++;
            }
            else{
                temp[index] = data[first2];
                first2++;                   
            }
            index++;
        }
        // if either first or second array is not empty, copy over its elements into temp
        while(first1<=last1){
            temp[index] = data[first1];
            first1++; 
            index++;
        }
        while(first2<=last2){
            temp[index] = data[first2];
            first2++; 
            index++;
        }
        //copy temp into data
        for(index = min;index<=max;index++){
            data[index] = temp[index];
        }
    }
}
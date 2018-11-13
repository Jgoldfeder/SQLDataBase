package sqlDataBase;

public interface Entry extends Comparable<Entry>{
    //get the entries value
    public Object getValue();
    //get the value of the type the entry stores
    public Class getType();
    //We want entry to be immutable
    //This means that if we set its value to something else, other pointers to the entry
    //will not be effected. This property is made use of in the Table class to create new rows based on changing a pre-initialized set of default entry values
    public Entry setValue(String s);
    //print the entry's value
    public void print();
    @Override 
    public int compareTo(Entry entry);
}
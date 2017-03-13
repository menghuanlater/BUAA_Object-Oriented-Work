package core;

/**
 * Created by ****** on 2017-03-06.
 */
public class ValidTrie {
    int count;
    PolyNodeCol arrays[] = new PolyNodeCol[ProjectBegin.sum];
    public ValidTrie(){
        count = 0;
    }
    public void AddNode(PolyNodeCol target){
        arrays[count++] = target;
    }
    public PolyNodeCol getNodeAt(int position){
        return arrays[position];
    }
    public void setNodeAt(PolyNodeCol target,int position){
        arrays[position] = target;
    }
}

package core;

/**
 * Created by ****** on 2017-03-05.
 */
public class TrieNode {
    static final int ARRAY_LENGTH = 10;
    private boolean flags[] = new boolean[ARRAY_LENGTH];
    private TrieNode linkNodeArray[] = new TrieNode[ARRAY_LENGTH];
    private boolean exist;
    private PolyNodeCol current;
    public TrieNode(){
        for(int i=0;i<ARRAY_LENGTH;i++){
            flags[i] = false;
            linkNodeArray[i] = null;
        }
        exist = false; current = null;
    }
    public boolean isExist(){
        return exist;
    }
    public PolyNodeCol getCurrent(){
        return current;
    }
    public boolean getFlagAt(int position){
        return flags[position];
    }
    public TrieNode getLinkAt(int position){
        return linkNodeArray[position];
    }
    public void createNewLink(int position){
        flags[position] = true;
        linkNodeArray[position] = new TrieNode();
    }
    public void createNewNode(PolyNodeCol current){
        this.exist = true;
        this.current = current;
    }
}

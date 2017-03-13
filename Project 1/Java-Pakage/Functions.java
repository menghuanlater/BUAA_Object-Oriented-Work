package core;

/**
 * Created by ****** on 2017-03-05.
 * 承载构建字典树、快排、计算三个函数的类模块
 */
public class Functions {
    public void buildDictTree(TrieNode trieHead,GetPoly myPoly,int row,int col){
        TrieNode temp = trieHead;
        PolyNodeCol buildNode = myPoly.getNodeAtRow(row).getNodeAtCol(col);
        String target = String.valueOf(buildNode.getPower());
        for(int i=0;i<target.length();i++){
            int position = target.charAt(i)-'0';
            if(!temp.getFlagAt(position))
                temp.createNewLink(position);
            temp = temp.getLinkAt(position);
        }
        if(temp.isExist()){
            temp.getCurrent().addCoefficient(buildNode.getCoefficient());
        }else{
            temp.createNewNode(buildNode);
            ProjectBegin.sum++;
        }
    }
    public void depthFirstSearch(TrieNode trieHead,ValidTrie validTrie){
        if(trieHead.isExist())
            validTrie.AddNode(trieHead.getCurrent());
        for(int i=0;i<TrieNode.ARRAY_LENGTH;i++){
            if(trieHead.getFlagAt(i)){
                depthFirstSearch(trieHead.getLinkAt(i),validTrie);
            }
        }
    }
    public void quickSort(ValidTrie validTrie,int low,int high){
        if(low < high){
            int pivotKey = getPivot(validTrie,low,high);
            quickSort(validTrie,low,pivotKey-1);
            quickSort(validTrie,pivotKey+1,high);
        }
    }
    public int getPivot(ValidTrie validTrie,int low,int high){
        PolyNodeCol pivotKey = validTrie.getNodeAt(low);
        while(low < high){
            while(low < high && validTrie.getNodeAt(high).getPower()>= pivotKey.getPower())
                high--;
            validTrie.setNodeAt(validTrie.getNodeAt(high),low);
            while(low < high && validTrie.getNodeAt(low).getPower()<= pivotKey.getPower())
                low++;
            validTrie.setNodeAt(validTrie.getNodeAt(low),high);
        }
        validTrie.setNodeAt(pivotKey,low);
        return low;
    }
}

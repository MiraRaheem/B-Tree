import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        BTree.CreateIndexFileFile("try1.bin",10,5);
        BTree.DisplayIndexFileContent("try1.bin",10,5);
        BTree.insertIntoNonFull("try1.bin",1,5,100,180,1);
        BTree.insertIntoNonFull("try1.bin",1,5,101,180,1);
        BTree.insertIntoNonFull("try1.bin",1,5,102,180,1);
        BTree.insertIntoNonFull("try1.bin",1,5,104,180,1);
        BTree.insertIntoNonFull("try1.bin",1,5,103,180,1);
        //BTree.Split("try1.bin",1,5,106,180,1);
        System.out.println();
        System.out.println();
        System.out.println();
        BTree.DisplayIndexFileContent("try1.bin",10,5);
        //System.out.println(BTree.isrootNotFull("try1.bin", 5));
        // BTree.knowOffset("try1.bin",3,1,3);
    }
}

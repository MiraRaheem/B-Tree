import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.Key;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class BTree {

    //BTreeNode root; // Pointer to root node
    public static void CreateIndexFileFile (String filename, int numberOfRecords, int m) throws IOException
    {
        RandomAccessFile fileStore = new RandomAccessFile(filename, "rw");
        int index=1;
        for(int i=0; i<numberOfRecords;i++){
            fileStore.writeInt(-1);
            fileStore.writeInt(index);
            for(int j=0; j<(m*2)-1; j++){
                fileStore.writeInt(-1);
            }
            index=index+1;
        }
    }

    public static void DisplayIndexFileContent (String filename,int numberOfRecords, int m) throws IOException
    {
        RandomAccessFile fileStore = new RandomAccessFile(filename, "rw");
        int index=1;
        Vector<Integer> record=new Vector<>();
        for(int i = 0; i < numberOfRecords; i++){
            for(int j = 0; j < (m*2)+1; j++){
                record.add(fileStore.readInt());
            }
            System.out.println(record);
            record.clear();
            index = index + 1;
            //System.out.println("index: " + index);
        }
    }

    public  static void insertIntoNonFull(String filename,int offset,int m, int number, int offsetInFile, int isRoot) throws IOException
    {
        RandomAccessFile fileStore = new RandomAccessFile(filename, "rw");
        int pos=(offset-1)* (m*2+1)*4;
        fileStore.seek(pos);
        fileStore.writeInt(isRoot);
        HashMap<Integer, Integer> record= new HashMap<>();
        for(int j = 0; j < (m*2); j++){
            record.put(fileStore.readInt(),fileStore.readInt());
        }
        record.put(number,offsetInFile);
        record.remove(-1);
        record.remove(offset);
        int takenInRec=record.size()*2;
        int nonTaken= (m*2)-takenInRec;
        //System.out.println("taken "+ takenInRec+" nont "+nonTaken);
        fileStore.seek(pos+4);
        for (Map.Entry<Integer, Integer> entry : record.entrySet()) {
            fileStore.writeInt(entry.getKey());
            fileStore.writeInt(entry.getValue());
        }
        for(int j = 0; j < nonTaken; j++){
            fileStore.writeInt(-1);
            //record.put(fileStore.readInt(),fileStore.readInt());
        }

        //if this insertion fell the node u need to inrcement the empty position
        fileStore.seek(4);
        int emptyposition1=fileStore.readInt();
        if(!isNotFull(filename,offset,m) && (offset-1)==emptyposition1){
            emptyposition1=emptyposition1+1;
            fileStore.seek(4);
            fileStore.writeInt(emptyposition1);
        }

    }

    public  static void Split(String filename,int offsetToBeSplited,int m, int number, int offsetInFile, int isRoot) throws IOException
    {
        RandomAccessFile fileStore = new RandomAccessFile(filename, "rw");
        //read the empty nodes
        fileStore.seek(4);
        int emptyposition1=fileStore.readInt();
        int emptyposition2=emptyposition1+1;
        System.out.println("empty position2 "+emptyposition2);
        //update the empty node
        fileStore.seek(4);
        fileStore.writeInt(emptyposition2+1);

        //read the full record and add the added no.
        int pos=(offsetToBeSplited-1)* (m*2+1)*4;
        fileStore.seek(pos);
        fileStore.writeInt(isRoot);
        HashMap<Integer, Integer> record= new HashMap<>();
        for(int j = 0; j < (m*2); j++){
            record.put(fileStore.readInt(),fileStore.readInt());
        }
        record.remove(-1);
        record.put(number,offsetInFile);
        System.out.println("original record "+record);

        //split the record into 2 smaller ones and create the new record
        int counter=record.size();
        System.out.println("record size "+counter);
        HashMap<Integer, Integer> record1= new HashMap<>();
        HashMap<Integer, Integer> record2= new HashMap<>();
        HashMap<Integer, Integer> replacedRecord= new HashMap<>();
        int counter1=0;
        for (Map.Entry<Integer, Integer> entry : record.entrySet()) {
           if(counter1<(counter/2))
           {
               record1.put(entry.getKey(),entry.getValue());
               counter1++;
           }
           else{
               record2.put(entry.getKey(),entry.getValue());
           }
        }
        Integer key1 = Collections.max(record1.entrySet(), Map.Entry.comparingByKey()).getKey();
        Integer key2 = Collections.max(record2.entrySet(), Map.Entry.comparingByKey()).getKey();
        replacedRecord.put(key1,emptyposition1);
        replacedRecord.put(key2,emptyposition2);
        System.out.println("replace record"+ replacedRecord);

        //put them on file
        //first the splited record
        fileStore.seek(pos);
        System.out.println("pos "+pos);
        int takenInRec=replacedRecord.size()*2;
        int nonTaken= (m*2)-takenInRec;
        //System.out.println("taken "+ takenInRec+" nont "+nonTaken);
        fileStore.seek(pos+4);
        for (Map.Entry<Integer, Integer> entry : replacedRecord.entrySet()) {
            fileStore.writeInt(entry.getKey());
            fileStore.writeInt(entry.getValue());
        }
        for(int j = 0; j < nonTaken; j++){
            fileStore.writeInt(-1);
            //record.put(fileStore.readInt(),fileStore.readInt());
        }

        //the first half og the split
        //ELPOSITION DA 8LT
        pos=(emptyposition1)* (m*2+1)*4;
        System.out.println("pos1 "+pos);
        fileStore.seek(pos);
        takenInRec=record1.size()*2;
        nonTaken= (m*2)-takenInRec;
        //System.out.println("taken "+ takenInRec+" nont "+nonTaken);
        fileStore.seek(pos+4);
        for (Map.Entry<Integer, Integer> entry : record1.entrySet()) {
            fileStore.writeInt(entry.getKey());
            fileStore.writeInt(entry.getValue());
        }
        for(int j = 0; j < nonTaken; j++){
            fileStore.writeInt(-1);
            //record.put(fileStore.readInt(),fileStore.readInt());
        }

        //the second half og the split
        //ELPOSITION DA 8LT

        pos=(emptyposition2)* (m*2+1)*4;
        System.out.println("pos2 "+pos);
        fileStore.seek(pos);
        takenInRec=record2.size()*2;
        nonTaken= (m*2)-takenInRec;
        //System.out.println("taken "+ takenInRec+" nont "+nonTaken);
        fileStore.seek(pos+4);
        for (Map.Entry<Integer, Integer> entry : record2.entrySet()) {
            fileStore.writeInt(entry.getKey());
            fileStore.writeInt(entry.getValue());
        }
        for(int j = 0; j < nonTaken; j++){
            fileStore.writeInt(-1);
            //record.put(fileStore.readInt(),fileStore.readInt());
        }

    }

    public  static int InsertNewRecordAtIndex (String filename, int RecordID, int Reference){


        return -1;
    }

    /*Utlization functions*/
    //go to the node position read the node then check if it has -1 in it if it does then return true
    public static boolean isNotFull(String filename, int offset, int m) throws IOException
    {
        RandomAccessFile fileStore = new RandomAccessFile(filename, "rw");
        int pos=(offset-1)* (m*2+1)*4;
        fileStore.seek(pos);
        Vector<Integer> record=new Vector<>();
            for(int j = 0; j < (m*2)+1; j++){
                record.add(fileStore.readInt());
            }
            if (record.contains(-1)){return true;}
            else {return false;}
    }

    public static boolean isrootNotFull(String filename, int m) throws IOException
    {
        RandomAccessFile fileStore = new RandomAccessFile(filename, "rw");
        int pos=1* (m*2+1)*4;
        fileStore.seek(pos);
        Vector<Integer> record=new Vector<>();
        for(int j = 0; j < (m*2)+1; j++){
            record.add(fileStore.readInt());
        }
        if (record.contains(-1)){return true;}
        else {return false;}
    }

}

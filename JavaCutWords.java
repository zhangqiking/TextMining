/**
 * Created by Administrator on 2017/8/7 0007.
 */
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import java.io.*;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;


public class Test {
    private int textNum;
    private ArrayList labels = new ArrayList();
    private ArrayList tokens = new ArrayList();
    private HashMap<String, Integer> dictionary = new HashMap<String, Integer>();
    private  HashMap<Integer, HashMap<Integer, Double>> train = new HashMap<Integer, HashMap<Integer, Double>>();

    public void readData(String datapath){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(datapath));
            reader.readLine();
            String str;
            int index = 0;
            while((str=reader.readLine()) != null){
                String [] line = str.split(",");
                this.labels.add(line[1]);
                this.textNum++;

                // 对分词结果建立字典
                StringReader sr = new StringReader(line[0]);
                IKSegmenter ik = new IKSegmenter(sr, true);
                Lexeme lex = null;
                ArrayList temp = new ArrayList();

                // 利用IKSegmenter工具进行分词
                while((lex=ik.next())!=null){
                    String word = lex.getLexemeText();
                    temp.add(word);
                    if(!this.dictionary.containsKey(word)){
                        this.dictionary.put(word, index);
                        index++;
                    }
                }
                this.tokens.add(temp);
            }
        } catch(IOException e){
            System.out.println("Handed");
        }
    }

    public void TransformTrain(){
        int index = 0;
        for(int i=0; i<this.labels.size(); i++){
            //int label = Integer.parseInt((String)this.labels.get(i));
            this.train.put(index, new HashMap<Integer,Double>());
            List words = (ArrayList) this.tokens.get(i);
            for(int j=0;j<words.size();j++){
                String s = (String)words.get(j);
                //System.out.println(s);
                int dictionaryindex = this.dictionary.get(s);
                //System.out.println(dictionaryindex);
                this.train.get(index).put(dictionaryindex, 1.0);
            }
            index++;
        }
    }

    public void writeTxt(String filename)throws Exception{
        File file = new File(filename);
        try{
            if(!file.exists()){
                file.createNewFile();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(file),"utf-8");
        BufferedWriter writer = new BufferedWriter(write);
        for(int i=0; i<this.textNum; i++){
            String si = String.valueOf(i);
            writer.write(si);
            writer.write(" ");
            HashMap features = this.train.get(i);
            //System.out.println(features);
            Set set = features.keySet();
            //System.out.println(set);
            for(Iterator iter = set.iterator();iter.hasNext();){
                //System.out.println(iter.next());
                int key = Integer.parseInt(iter.next().toString());
                String sk = String.valueOf(key);
                System.out.println(sk);
                String value = features.get(key).toString();
                System.out.println(value);
                writer.write(sk);
                writer.write(":");
                writer.write(value);
                writer.write(" ");
                //writer.write("收到货居");
            }
            writer.write("\r\n");
        }
        writer.close();
    }

    public static void main(String args[])throws IOException {
        Test T = new Test();
        T.readData("dataclean.csv");
        T.TransformTrain();
        try {
            T.writeTxt("train.txt");
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}

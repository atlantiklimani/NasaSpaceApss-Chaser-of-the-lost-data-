import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import weka.clusterers.*;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.*;

public class AproxData {
    public void ReadCSV() throws IOException {
        Scanner sc = new Scanner(new File("Fireball.csv"));

        File f = new File("notNull.csv");
        FileWriter fw = new FileWriter(f);

        File f1 = new File("null.csv");
        FileWriter fw1 = new FileWriter(f1);

        String value = null;
        while(sc.hasNextLine()) {
            value = sc.nextLine();
            if(hasEmptyAtt(value)) {
                value = new String(value + "\n");
                fw1.write(value );
            }
            else {
                value = new String(value+"\n");
                fw.write(value );
            }
        }
        fw.close();
        fw1.close();
    }
    public boolean hasEmptyAtt(String row){
        boolean answer = false;
        for (int i = 0; i <row.length() ; i++) {
            if (i == 0 && row.charAt(0) == ',') {
                answer = true;
                break;

            }
            if( i != 0 && row.charAt(i-1) == ',' && row.charAt(i) == ',' ) {
                answer = true;
                break;
            }
            if( row.charAt(row.length() - 1) == ',') {
                answer = true;
                break;
            }
        }
        return answer;
    }


    public File fillWithO(File file ) throws IOException {
        Scanner sc = new Scanner(file); ///
        //Scanner sc = new Scanner(new File("near-earth-comets.csv"));
        File f = new File("Filled.csv");
        FileWriter fw = new FileWriter(f);
        String value = null;
        int i = 0;

        while(sc.hasNextLine()) {
            i++;
            value = sc.nextLine();
            fw.write(new String(value.replace('?','0') + "\n") );

        }
        fw.close();
        System.out.println(i);
        return f;
    }
    public boolean [][] createMatrixIndex(File f) throws FileNotFoundException {
        Scanner sc = new Scanner(f);
        String value = sc.nextLine();
        sc.reset();
        int countRow = 1;
        int countCol = 1;
        while (sc.hasNextLine()) {
            sc.nextLine();
            countRow++;
        }

        for (int i = 0; i <value.length() ; i++) {
            if (value.charAt(i) == ',') {
                countCol++;
            }
        }
        boolean [][] rez = new boolean[countRow][countCol];
        int row = 0;
        int col = 0;
        sc.close();

        Scanner sc1 = new Scanner(f);
        sc1.nextLine(); /// e kalo rreshtin e pare
        while (sc1.hasNextLine()) {
            String v = sc1.nextLine();
            for (int i = 0; i < v.length(); i++) {
                if(v.charAt(i) == ','){
                    col++;
                    System.out.println("c" +col);
                }
                else {
                    if (v.charAt(i) == '?') {
                        rez[row][col] = true;
                    }
                }

            }
            col = 0;
            row++;
            System.out.println("row"+ row);
        }
        return rez;
    }


    public double [][] createItemMatrix (File f) throws FileNotFoundException {
        Scanner sc = new Scanner(new File("Filled.csv"));
        String value = sc.nextLine();
        sc.reset();
        int countRow = 0;
        int countCol = 1;
        while (sc.hasNextLine()) {
            sc.nextLine();
            countRow++;
        }

        for (int i = 0; i <value.length() ; i++) {
            if (value.charAt(i) == ',') {
                countCol++;
            }
        }
        double [][] matrica = new double[countRow][countCol];
        int row = 0, col = 0;
        sc.close();

        sc = new Scanner(new File("Filled.csv"));
        sc.nextLine();

        while (sc.hasNextLine()) {
            String v= sc.nextLine();
            String [] arr = v.split(",");
            for (int i = 0; i <arr.length ; i++) {
                matrica[row][i] = new Double(arr[i]).doubleValue();

            }
            row++;
        }

        return matrica;
    }

    public String replaceString(String input, String app){

        if(input.charAt(0)==','){
            input=app+input;
        }
        if(input.charAt(input.length()-1)==','){
            input=input+app;
        }
        for (int i = 1; i <input.length() ; i++) {
            if(input.substring(i-1,i+1).equals("?")){
                input=input.substring(0,i)+app+input.substring(i);
            }

        }
        return  input;


    }

     public double euklidianDistance (double [] a ,double [] b)
     {

       double sum = 0;
       for(int i = 0;i<a.length;i++)
           sum = sum + Math.pow(a[i]-b[i],2);

       double sqrt = Math.sqrt(sum);

       return sqrt;

     }

     public ArrayList<double []> [] cluster (File f ,int n ) throws Exception  // n - numri i grumbujve (clusters)
     {

         File output = new File("output.arff");
         System.out.println(output.createNewFile());
         File source = fillWithO(f);
         convert(source.getPath(),output.getPath());

         BufferedReader read = new BufferedReader(new FileReader(output));
         Instances in = new Instances(read);
         SimpleKMeans kMeans = new SimpleKMeans();
         kMeans.setSeed(30);
         kMeans.setNumClusters(n);
         kMeans.buildClusterer(in);

         Instances centroids = kMeans.getClusterCentroids();
//         System.out.println(centroids.instance(0).numAttributes());



         ArrayList<double []> [] vargu = new ArrayList[n];
         for(int k = 0;k< vargu.length;k++)
         {
             vargu[k] = new ArrayList<double []>();
         }

         double [][] matrica = createItemMatrix(source);
         for (int i = 0; i <matrica.length ; i++) {
             double [] l = new double[n];
             for(int j = 0;j<n;j++)
             {
                 l[j] = euklidianDistance(matrica[i],centroids.instance(j).toDoubleArray());
             }
             int index = 0;
             double min = l[0];
             for (int j = 1; j < n; j++) {
                 if(min>l[j])
                 {
                     min = l[j];
                  index = j;
                 }

             }
             vargu[index].add(matrica[i]);

         }

         return vargu;
     }
    public ArrayList<double []> [] cluster2 (File f ,int n,double [] [] itemMatrix ) throws Exception  // n - numri i grumbujve (clusters)
    {

        File output = new File("output.arff");
        //System.out.println(output.createNewFile());
        //File source = new File("output.arff");
        //convert(source.getPath(),output.getPath());

        BufferedReader read = new BufferedReader(new FileReader(output));
        Instances in = new Instances(read);
        SimpleKMeans kMeans = new SimpleKMeans();
        kMeans.setSeed(30);
        kMeans.setNumClusters(n);
        kMeans.buildClusterer(in);

        Instances centroids = kMeans.getClusterCentroids();
//         System.out.println(centroids.instance(0).numAttributes());



        ArrayList<double []> [] vargu = new ArrayList[n];
        for(int k = 0;k< vargu.length;k++)
        {
            vargu[k] = new ArrayList<double []>();
        }

        double [][] matrica =  itemMatrix;
        for (int i = 0; i <matrica.length ; i++) {
            double [] l = new double[n];
            for(int j = 0;j<n;j++)
            {
                l[j] = euklidianDistance(matrica[i],centroids.instance(j).toDoubleArray());
            }
            int index = 0;
            double min = l[0];
            for (int j = 1; j < n; j++) {
                if(min>l[j])
                {
                    min = l[j];
                    index = j;
                }

            }
            vargu[index].add(matrica[i]);

        }

        return vargu;
    }


     public double logMes(double[][] in, boolean[][] stat,int a, int b, ArrayList<double[]> [] vr){
        double sum = 0;
        int count = 0;
         for (int i = 0; i <in.length ; i++) {
             if(stat[i][b] == false && a == whichCluster(in[i], vr)) {
                 sum += in[i][b];
                 count++;
             }

         }
         return 2.0/3.0* sum / count;
     }
     public double[][] logMat(double[][] in, boolean[][] stat, ArrayList<double[]> [] vr ) {
         for (int i = 0; i < in.length ; i++) {
             for (int j = 0; j < in[0].length ; j++) {
                 if(stat[i][j] == true ) {
                     int index = whichCluster(in[i], vr);
                    in[i][j] = logMes(in, stat, index, j , vr);
                 }
             }
         }
            return in;
     }
     public int whichCluster(double[] v, ArrayList<double []> [] vr) {
        int index = -1;
        for (int i = 0; i < vr.length; i++) {
            if(isInList(vr[i], v)){
                index = i;
            }
         }
         return index;
     }
    public static boolean isInList(
            final ArrayList<double[]> list, final double[] candidate){

        for(final double[] item : list){
            if(Arrays.equals(item, candidate)){
                return true;
            }
        }
        return false;
    }

    public static void convert(String sourcepath,String destpath) throws Exception {
        // load CSV
        CSVLoader loader = new CSVLoader();
        loader.setSource(new File(sourcepath));
        Instances data = loader.getDataSet();

        // save ARFF
        ArffSaver saver = new ArffSaver();
        saver.setInstances(data);
        saver.setFile(new File(destpath));
     ///   saver.setDestination(new File(destpath));  ////
        saver.writeBatch();
    }

    public static void main(String[] args) throws Exception {
        AproxData s = new AproxData();
        
        int numCluster = 2;

        File src = s.fillWithO(new File("Speed.csv"));

        boolean [][] matrix = s.createMatrixIndex(new File("Speed.csv"));
        for (int i = 0; i <matrix.length ; i++) {
            for (int j = 0; j <matrix[0].length ; j++) {
                System.out.print(matrix[i][j] + " | ");
            }
            System.out.println();
        }

        double [][] matrica = s.createItemMatrix(src);
        for (int i = 0; i <matrica.length ; i++) {
            for (int j = 0; j <matrica[0].length ; j++) {
                System.out.print(matrica[i][j] + " | ");
            }
            System.out.println();

        }

        ArrayList<double []> [] vargu = s.cluster(new File("Speed.csv"),numCluster);
        for(int k = 0;k< vargu.length;k++){
            for(int i = 0;i<vargu[k].size();i++)
            {
                System.out.print(k+"  ");
                for(double a : vargu[k].get(i))
                    System.out.print(a +" ");

                System.out.println();
            }

            System.out.println();
        }

      //  System.out.println(" Centroid 0  : "+vargu[0].size());
      //  System.out.println(" Centroid 1  : "+vargu[1].size());
     //   System.out.println(" Centroid 2  : "+vargu[2].size());

        double [][] matricaa = s.logMat( matrica, matrix, vargu);
        for (int i = 0; i <matricaa.length ; i++) {
            for (int j = 0; j <matricaa[0].length ; j++) {
                System.out.print(matricaa[i][j] + " ");
            }
            System.out.println();

        }
        System.out.println();
        System.out.println();
        System.out.println("-----------------------------------------------------------------------");
        ArrayList<double []> [] vargu2 = s.cluster2(new File("output.arff"),numCluster,matricaa);
        double [][] matricaa2 = s.logMat( matrica, matrix, vargu);
        for (int i = 0; i <matricaa2.length ; i++) {
            for (int j = 0; j <matricaa2[0].length ; j++) {
                System.out.print(matricaa2[i][j] + " ");
            }
            System.out.println();

        }

       File l = new File("ReZzultati.txt");
       System.out.println(l.createNewFile());
       FileWriter fw1 = new FileWriter(l);
       for(double [] v1 : matricaa2){
        for(double a1 : v1)
         fw1.write(a1 + "   ");
         
        fw1.write("\n") ;
        
       }
       fw1.close();

    }
}
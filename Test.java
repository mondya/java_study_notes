import java.util.Arrays;
import java.util.*;
public class Test{
    public static void main(String[] args) {
        /*
        int[] ints = {23,4,56,19,5,100,22,23,45,34};
        insertSort(ints);
        System.out.println(Arrays.toString(ints));
        */
        Date date = new Date();
        System.out.println(date);
        System.out.println(date.getTime());
    }

    //插入排序
    public static void insertSort(int[] ints) {
        for(int i =1;i<ints.length;i++){
            int index = i;
            int value = ints[i];
            while(index>0 && value<ints[index-1]){
                ints[index] = ints[index-1];
                index--;
            }
            ints[index] = value;
        }
    }


    //直接选择排序
    public static void selectSort(int[] ints){
        for(int i=0;i<ints.length-1;i++){
            int maxIndex = i;
            for(int j=i;j<ints.length;j++){
                if(ints[maxIndex] < ints[j]){
                    maxIndex = j;
                }
            }
            if(!(maxIndex == i)){
                int temp = ints[maxIndex];
                ints[maxIndex] = ints[i];
                ints[i] = temp;
            }
        }
    }


    //冒泡排序
    public static void bubbleSort(int[] ints) {
        //从小到大排列顺序
        for(int j=0;j<10;j++){
            boolean flag = false;
            for(int i = ints.length-1;i>0;i--){
                if(ints[i]<ints[i-1]){
                    int temp = ints[i];
                    ints[i] = ints[i-1];
                    ints[i-1] = temp;
                    flag = true;
                }
            }
            if(flag == false){
                break;
            }
        }
    }
}
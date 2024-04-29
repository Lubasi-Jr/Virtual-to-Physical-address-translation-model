//LUBASI MILUPI
//MLPLUB001
//OS1 Assigment 2024
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.math.BigInteger;

public class OS1Assignment {

    public String filename;

    public String path;

    public OS1Assignment(){
        this.filename= "OS1sequence";
    }

    public OS1Assignment(String fname){
        this.filename=  fname;
    }

    public static String binToHex(String physical){
        BigInteger binBigInt = new BigInteger(physical,2); //Convert from binary to decimal

        String hex = binBigInt.toString(16); //Convert from decimal to hexadecimal
        return hex.toUpperCase(); //Return in upper case just in case the hex letters are in lower case

    }

    public static String conversion(String addy){
        String offset = addy.substring(9); //Get offset
        String virt_page = addy.substring(0,9); //Virtual page number

        int virt_page_decimal = Integer.parseInt(virt_page,2); //Convert the virtual page to a decimal to properly map it using the page table

        if( virt_page_decimal >= 1 && virt_page_decimal <= 7){//If there is a page to map
            int[] page_table = new int[]{2,4,1,7,3,5,6};
            int physical_page = page_table[virt_page_decimal]; //Mapping

            String physical_page_binary = Integer.toBinaryString(physical_page); //After mapping, convert to binary. Then attach it with offset

            //Binary number must be 9 bits. Since 9 + 7 page offset bits = 16
            if (physical_page_binary.length() != 9){
                int diff = 9-physical_page_binary.length();
                String filler = "0".repeat(diff);
                physical_page_binary = filler+physical_page_binary;
            }

            //Physical page in binary
            String final_physical_page = physical_page_binary+offset;

            return final_physical_page;
        }
        else{
            return addy;
        }

    }

    public static String hexToBin(String hexString){
        //Convert the string to a decimal, then convert it to a binary
        int decimal_value = Integer.parseInt(hexString,16);
        String binString = Integer.toBinaryString(decimal_value);

        //Pad the binary string with 0's so that it has 16 bits
        if (binString.length() != 16){
            int diff = 16-binString.length();
            String filler = "0".repeat(diff);
            binString = filler+binString;
        }
        return binString;

    }

    public static String reverse(String virtual){
        ArrayList<String> storage = new ArrayList<>(); //We need 4  chunks of 2 bytes each to be stored in the array
        int end = 2;
        String reversed_string = "";
        for (int i = 0;i<virtual.length(); i+=2){ //Get a chunk of two bytes
            storage.add(virtual.substring(i,end));
            end += 2;
        }
        for (int j = storage.size()-1; j>=0; j--){ //Reverse the string by getting the last chunk in the array (Which is meant to be the first).
            reversed_string += storage.get(j);
        }
        return reversed_string;

    }



    public static void main(String[] args) {
        String path = "";
        if(args.length > 0){ //If Terminal is used to run the program and file path is specified
            path = args[0];
        }
        else { //No file path specified from the terminal, or program has been run from the IDE. Therefor default constructor called.
            OS1Assignment obj1 = new OS1Assignment();
            path = obj1.filename;

        }


        FileInputStream fis = null;
        DataInputStream dis = null;
        String virtual_init = "";
        String virtual_final = "";
        String virtual_binary = "";
        String physical_page = "";
        String output_filename = "output-OS1.txt";

        try {
            fis = new FileInputStream(path);
            dis = new DataInputStream(fis);
            BufferedWriter writer = new BufferedWriter(new FileWriter(output_filename));

            while (dis.available() > 0) {
                long c = dis.readLong(); //Read 8 bytes at each iteration
                virtual_init = String.format("%02X",c); //Convert the read to its hexadecimal string representation. However, this string is backwards.

                if(virtual_init.length() != 16){ // Pad the string with 0's to avoid string manipulation errors when the functions are being called
                    int diff = 16-virtual_init.length();
                    String filler = "0".repeat(diff);
                    
                    virtual_init = virtual_init+filler;
                }



                virtual_final = reverse(virtual_init); //Since it is backwards, we call the reverse function. Store the value in virtual final.


                virtual_binary = hexToBin(virtual_final); //Convert the virtual address to binary, in order to start performing the translation

                physical_page = conversion(virtual_binary);//Function to obtain the translation from virtual page to physical page (in binary)


                //Translate the physical page(in binary) to a hexadecimal
                String hexadecimal = binToHex(physical_page);

                //System.out.println(hexadecimal);

                //Write the address into the output file. Also writing a new line after each iteration
                writer.write(hexadecimal);
                writer.newLine();
                writer.flush();


            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis!= null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dis!= null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Translation complete and output file created");
    }
}
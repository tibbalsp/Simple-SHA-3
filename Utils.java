//Authors:   Patrick Tibbals, Iam McLean


import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class Utils {


    String keccakInput(String N,String S,int w){
        String hex = textToHexString(S);
        String hex1 = textToHexString(N);
        String z = left_encode((hex.length()/2)*8)+hex;
        String y = left_encode((hex1.length()/2)*8)+hex1;
        String output = bytepad(y+z,w);
        return output;
    }

    long[] hexToLong(String hex){
        long[] L = new long[25];
        Arrays.fill(L,0x000000000L);

        for (int i = 0; i*16 < hex.length(); i++) {
            L[i] = new BigInteger(hex.substring(i*16, i*16+16),16).longValue();
        }

        return L;
    }

    String padInput(String text) {

        if (text.length() > 272) {
            return text;
        }
        String paded = text + "04";
        while (paded.length() < 270) {
            paded += "00";
        }
        return paded + "80";

    }

    String byteArrToHex(byte[] bytes){
        String out = "";
        for (byte b : bytes) {
            out += Integer.toHexString(Integer.valueOf(b));
        }
        return out;
    }

    String longToHex(long[] input){
        String hex ="";
        for (int i = 0; i < input.length; i++) {
            byte[] bytes = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(input[i]).array();
            for (int j = 0; j < 8; j++) {
                String temp = (Long.toHexString(bytes[j] & 0xff));

                if(temp.length()<2){
                    temp = "0"+temp;
                }
                hex += temp;
            }
        }

        return hex;
    }

    String textToHexString(String text){
        StringBuffer hex = new StringBuffer();
        char ch[] = text.toCharArray();
        for(int i = 0; i < ch.length; i++) {
            String temp = Integer.toHexString(ch[i]);
            if(Character.isDigit(ch[i])){
                temp = "0"+ch[i];
            }

            if(temp.length()<2){
                temp = "0"+temp;
            }
            hex.append(temp);
        }
        return hex.toString();
    }

    void printHex(String hex) {
        System.out.println("");
        hex = hex.toUpperCase();
        int c = 0;
        for (int i = 0; i < hex.length()-1; i=i+2) {
            System.out.print(hex.charAt(i)+""+hex.charAt(i+1)+" ");
            c++;
            if(c%16==0){
                System.out.println("");
                c=0;
            }
        }
        System.out.println("");
    }

    String bytepad(String X, int w) {
        String z = "";
        String y = "";
        if(w > 0){
            z = left_encode(w)+X;

            while ((z.length()*4+y.length()) % 8 != 0){
                y += "0";
            }
            for (int i = 0; i < y.length(); i=i+4) {
                z+="0";
            }

            while ((z.length()*4/8) % w != 0){
                z += "00";
            }
        }
        return z;
    }


    String encode_string(String input){
        if (input.length()*4 < 22040){
            return left_encode(input.length()*4) + input;
        }
        return "";
    }
    String[] intToHex(int X){
        String hex[] = new String[2];
        hex[0] = Integer.toHexString(X);
        if(hex[0].length()%2 != 0){
            hex[0] = "0"+hex[0];
        }
        hex[1] = Integer.toHexString(hex[0].length()/2);
        if(hex[1].length()%2 != 0){
            hex[1] = "0"+hex[1];
        }
        return hex;
    }
    String right_encode(int X){
        String[] encoded = intToHex(X);
        return encoded[0]+encoded[1];
    }
    String left_encode(int X){
        String[] encoded = intToHex(X);
        return encoded[1]+encoded[0];
    }

    String XORhex(String hex1, String hex2){
        String xord = "";
        int loopSize = 0;

        hex1 = appendHex(hex1);
        hex2 = appendHex(hex2);


        // hex1 longer than hex2
        if(hex1.length() > hex2.length()){
            loopSize = hex2.length();
            xord = hex1.substring(loopSize);
            // hex2 is longest
        }else{
            loopSize = hex1.length();
            xord = hex2.substring(loopSize);
        }

        String str = "";
        while(loopSize > 0){
            String[] temp = {"0x"+hex1.substring(0,2),"0x"+hex2.substring(0,2)};
            hex1 = hex1.substring(2);
            hex2 = hex2.substring(2);
            String hex = Integer.toHexString(Integer.decode(temp[0]) ^ Integer.decode(temp[1]));
            if(hex.length()<2){
                hex = "0"+hex;
            }
            loopSize -= 2;
            str += hex;
        }

        xord = str + xord;

        return xord;
    }
    String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }

    String appendHex(String hex){
        if(hex.length()%2 != 0){
            hex += "0";
        }
        return hex;
    }

}
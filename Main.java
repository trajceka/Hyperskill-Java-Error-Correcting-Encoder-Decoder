package correcter;
import java.io.*;
import java.math.BigInteger;
import java.util.Scanner;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        //while (scanner.hasNext()) {
            System.out.print("Write a mode: ");
            String modestr = scanner.nextLine();
            System.out.println();
            //if ("q".equals(modestr)) {
                //break;
            //}
            if ("encode".equals(modestr)) {
                encode();
            }
            if ("send".equals(modestr)) {
                send();
            }
            if ("decode".equals(modestr)) {
                decode();
            }
        //}
    }

    static void encode() {
        File filesend = new File("send.txt");
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filesend));
             BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("encoded.txt"))) {
            int firstsize = (int) filesend.length();
            byte[] firstbytes = new byte[firstsize];
            bis.read(firstbytes);
            System.out.println("send.txt:");
            printtext(firstbytes);
            printhexa(firstbytes);
            printbinary(firstbytes);
            StringBuilder sb = binarysb(firstbytes);
            byte[] secondbytes = strbtobytear(sb);
            printhexa(secondbytes);
            if (secondbytes[0] == 0) {
                bos.write(secondbytes,1,secondbytes.length -1);
            } else {
                bos.write(secondbytes);
            }
        } catch (IOException e) {
            System.out.println("Error" + e.getMessage());
        }
    }

    static void send() {
        File filesend = new File("encoded.txt");
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filesend));
             BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("received.txt"))) {
            int firstsize = (int) filesend.length();
            byte[] firstbytes = new byte[firstsize];
            bis.read(firstbytes);
            System.out.println("encoded.txt:");
            printhexa(firstbytes);
            printbinary(firstbytes);
            System.out.println("\nreceived.txt:");
            byte[] secondbytes = sending(firstbytes);
            printhexa(secondbytes);
            printbinary(secondbytes);
            bos.write(secondbytes);

        } catch (IOException e) {
            System.out.println("Error" + e.getMessage());
        }
    }

    static void decode() {
        File filesend = new File("received.txt");
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filesend));
             BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("decoded.txt"))) {
            int firstsize = (int) filesend.length();
            byte[] firstbytes = new byte[firstsize];
            bis.read(firstbytes);
            System.out.println("received.txt:");
            printhexa(firstbytes);
            printbinary(firstbytes);
            System.out.println("\ndecoded.txt:");
            byte[] corrected = correcting(firstbytes);
            printbinaryc(corrected);
            byte[] decoded = decoding(corrected);
            printbinaryd(decoded);
            // byte[] removed = removing(decoded);
            // printbinaryr(removed);
            printhexa(decoded);
            printtext(decoded);
            bos.write(decoded);

        } catch (IOException e) {
            System.out.println("Error" + e.getMessage());
        }
    }

    static void printtext(byte... bytes) {
        System.out.println("text view: " + new String(bytes));
    }

    static void printhexa(byte... bytes) {
        String hexstr = "";
        for (byte bb : bytes) {
            hexstr += String.format("%02X ", bb);
        }
        System.out.println("hex view: " + hexstr);
    }


    static void printbinary(byte... bytes) {
        System.out.print("bin view: ");
        for (byte bb : bytes) {
            System.out.print(String.format("%8s", Integer.toBinaryString(bb & 0xFF)).replace(' ', '0') + " ");
        }
        System.out.println();
    }

    static void printbinaryc(byte... bytes) {
        System.out.print("correct: ");
        for (byte bb : bytes) {
            System.out.print(String.format("%8s", Integer.toBinaryString(bb & 0xFF)).replace(' ', '0') + " ");
        }
        System.out.println();
    }

    static void printbinaryd(byte... bytes) {
        System.out.print("decode: ");
        for (byte bb : bytes) {
            System.out.print(String.format("%8s", Integer.toBinaryString(bb & 0xFF)).replace(' ', '0') + " ");
        }
        System.out.println();
    }

    static void printbinaryr(byte... bytes) {
        System.out.print("remove: ");
        for (byte bb : bytes) {
            System.out.print(String.format("%8s", Integer.toBinaryString(bb & 0xFF)).replace(' ', '0') + " ");
        }
        System.out.println();
    }

    static StringBuilder binarysb(byte... bytes) {
        StringBuilder fb = new StringBuilder();
        System.out.println("\nencoded.txt:");
        System.out.print("expand: ");
        for (byte bb : bytes) {
            fb.append(String.format("%8s", Integer.toBinaryString(bb & 0xFF)).replace(' ', '0'));
        }
        char[] bits = new char[8];
        StringBuilder sb = new StringBuilder();
        for (int i = 3; i < fb.length(); i += 4) {
            System.out.print("..");
            bits[2] = fb.charAt(i - 3);
            System.out.print(bits[2]);
            System.out.print(".");
            bits[4] = fb.charAt(i - 2);
            System.out.print(bits[4]);
            bits[5] = fb.charAt(i - 1);
            System.out.print(bits[5]);
            bits[6] = fb.charAt(i);
            System.out.print(bits[6]);
            bits[7] = '0';
            System.out.print(". ");
            bits[0] = ((bits[2] != bits[4]) ^ ('1' == bits[6])) ? '1' : '0';
            bits[1] = ((bits[2] != bits[5]) ^ ('1' == bits[6])) ? '1' : '0';
            bits[3] = ((bits[4] != bits[5]) ^ ('1' == bits[6])) ? '1' : '0';
            sb.append(bits);
        }
        System.out.println();
        System.out.print("parity:");
        for (int i = 0; i < sb.length(); i++) {
            if (i % 8 == 0) {
                System.out.print(" ");
            }
            System.out.print(sb.charAt(i));
        }

        System.out.println();

        return sb;
    }

    static byte[] sending(byte... pbyte) {
        byte[] rbyte = new byte[pbyte.length];
        for (int i = 0; i < rbyte.length; i++) {
            rbyte[i] = (byte) Integer.parseInt(changeonebit(String.format("%8s", Integer.toBinaryString(pbyte[i] & 0xFF)).replace(' ', '0')), 2);
        }
        return rbyte;
    }

    static String changeonebit(String cstr) {
        Random rnd = new Random();
        int index = rnd.nextInt(cstr.length());
        char ch = cstr.charAt(index) == '0' ? '1' : '0';
        return cstr.substring(0, index) + ch + cstr.substring(index + 1);
    }

    static byte[] correcting(byte... pbyte) {
        byte[] rbyte = new byte[pbyte.length];
        for (int i = 0; i < rbyte.length; i++) {
            rbyte[i] = (byte) Integer.parseInt(corre(String.format("%8s", Integer.toBinaryString(pbyte[i] & 0xFF)).replace(' ', '0')), 2);
        }
        return rbyte;
    }

    static String corre(String cstr) {
        char[] bits = cstr.toCharArray();
        boolean bit0 = (bits[0] != bits[2]) ^ (bits[4] != bits[6]);
        boolean bit1 = (bits[1] != bits[2]) ^ (bits[5] != bits[6]);
        boolean bit3 = (bits[3] != bits[4]) ^ (bits[5] != bits[6]);
        if (bit0 && bit1 && bit3) {
            bits[6] = (bits[6] == '0') ? '1' : '0';
        } else if (bit0 && bit1) {
            bits[2] = (bits[2] == '0') ? '1' : '0';
        } else if (bit0 && bit3) {
            bits[4] = (bits[4] == '0') ? '1' : '0';
        } else if (bit1 && bit3) {
            bits[5] = (bits[5] == '0') ? '1' : '0';
        }

        return new String(bits);
    }


    static byte[] decoding(byte... pbyte) {
        StringBuilder fb = new StringBuilder();
        for (int i = 0; i < pbyte.length; i++) {
            fb.append(decod(String.format("%8s", Integer.toBinaryString(pbyte[i] & 0xFF)).replace(' ', '0')));
        }
//        if (pbyte[pbyte.length - 1] == 51) {
//            fb.append(decodlast(String.format("%8s", Integer.toBinaryString(pbyte[pbyte.length - 1] & 0xFF)).replace(' ', '0')));
//        } else {
//            fb.append(decod(String.format("%8s", Integer.toBinaryString(pbyte[pbyte.length - 1] & 0xFF)).replace(' ', '0')));
//        }

         return new BigInteger(fb.toString(), 2).toByteArray();
    }

    static String decod(String cstr) {
        return cstr.charAt(2) + "" + cstr.charAt(4) + "" + cstr.charAt(5)+ "" + cstr.charAt(6);
    }

//    static String decodlast(String cstr) {
//        String rstr = "";
//        if (cstr.charAt(2) == '0' && cstr.charAt(4) == '0') {
//            rstr += cstr.charAt(0);
//        } else if (cstr.charAt(4) == '0') {
//            rstr += cstr.charAt(0) + "" +cstr.charAt(2);
//        } else {
//            rstr += cstr.charAt(0) + "" + cstr.charAt(2) + "" + cstr.charAt(4);
//        }
//        return rstr;
//    }

//    static byte[] removing(byte... pbyte) {
//        if (pbyte[pbyte.length - 1] == 0) {
//            byte[] bytes = new byte[pbyte.length - 1];
//            for (int i = 0; i < pbyte.length - 1; i++) {
//                bytes[i] = pbyte[i];
//            }
//            return bytes;
//        } else {
//            return pbyte;
//        }
//    }

    static byte[] strbtobytear(StringBuilder sb) {
        return new BigInteger(sb.toString(), 2).toByteArray();
    }

}

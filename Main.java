import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.Scanner;

//Authors:   Patrick Tibbals, Iam McLean


class Main {
    final static Utils utils = new Utils();
    final static Keccak keccak = new Keccak();
    final static int rate = 256;
    final static int capacity = 576;

    final static KMACXOF256 k = new KMACXOF256();

    static String[] endcodedFile = null;
    static boolean run = true;
    public static void main(String[] args) {
 
        while (run) {
            menuPrompt(new Scanner(System.in));
        }



    }


    private static void menuPrompt(Scanner s) {
        while (run) {
            System.out.println("""
                    Select the service you would like:
                        A) Compute a plain cryptographic hash
                        B) Compute an authentication tag (MAC)
                        C) Encrypt a given data file
                        D) Decrypt a given symmetric cryptogram
                        E) EXIT
                    """);
            String choice = s.nextLine();
            switch (choice.toLowerCase()) {
                case "a":
                    plainHash();
                    return;
                case "b":
                    authenticationTag();
                    return;
                case "c":
                    encryption();
                    return;
                case "d":
                    decryption();
                    return;
                case "e":
                    System.out.println("Good Bye");
                    run = false;
                    break;
                default:
                    System.out.println("That is not a service try again");
            }
        }
    }

    private static void plainHash() {
        Scanner s = new Scanner(System.in);
        System.out.println("Choose what you would like to hash: \n" +
                "   A) file input\n   B) user input");
        String choice = s.nextLine();
        if (choice.equalsIgnoreCase("A")) {
            String data = gettingFileInfo(s);
            System.out.println("Please enter a Customization String(optional): ");
            String cStr = s.nextLine();
            System.out.println( new SHAKE256().Sponge(utils.textToHexString(data), "", cStr, 512 / 2));
        } else if (choice.equalsIgnoreCase("B")) {
            System.out.println("Enter the phrase you want to hash: ");
            String data = s.nextLine();
            System.out.println("Please enter a Customization String(optional): ");
            String cStr = s.nextLine();
            System.out.println( new SHAKE256().Sponge(utils.textToHexString(data), "", cStr, 512 / 2));
        } else {
            System.out.println("That is not a service try again: ");
            plainHash();
        }
    }

    private static void authenticationTag() {
        Scanner s = new Scanner(System.in);
        String X = null;
        System.out.println("Choose what you would like to hash: \n" +
                "   A) file input\n   B) user input");
        String choice = s.nextLine();
        if (choice.equalsIgnoreCase("A")) {
            X = gettingFileInfo(s);
        } else if (choice.equalsIgnoreCase("B")) {
            System.out.println("Enter the phrase you want to hash: ");
            X = s.nextLine();

        } else {
            System.out.println("That is not a service try again: ");
            authenticationTag();
        }
        
        System.out.println("Please enter a passphrase: ");
        String K = s.nextLine();
        System.out.println("Please enter a Customization String(optional): ");
        String S = s.nextLine();
        utils.printHex(k.KMACJOB(K,X,S,256));

    }

    private static void encryption() {
        Scanner s = new Scanner(System.in);
        byte[] values = new byte[64];
        SecureRandom sr = new SecureRandom();
        sr.nextBytes(values);
        String z = bytesToHex(values);

        String m = utils.textToHexString(gettingFileInfo(s));
        System.out.println("Please enter a passphrase");
        String pw = s.nextLine();


        String keka = k.KMACJOB(z + pw, "", "S", 1024 / 4);
        String ke = keka.substring(0, keka.length() / 2);
        String ka = keka.substring(keka.length() / 2);

        String c = utils.XORhex(k.KMACJOB(ke, "", "SKE", m.length()), m);
        String t = k.KMACJOB(ka, m, "SKA", 512 / 2);
        endcodedFile = new String[]{z,c,t};
        System.out.println("Encoded successfully");
    }

    private static void decryption() {
        if(endcodedFile==null){
            System.out.println("You need to encrypt a file first!");
            return;
        }
        try (Scanner s = new Scanner(System.in)) {
            System.out.println("Please enter the password: ");
            String pw = s.nextLine();


            String keka = k.KMACJOB(endcodedFile[0] + pw, "", "S", 1024 / 4);
            String ke = keka.substring(0, keka.length() / 2);
            String ka = keka.substring(keka.length() / 2);

            String m = utils.XORhex(k.KMACJOB(ke, "", "SKE", endcodedFile[1].length()), endcodedFile[1]);
            String tPrime = k.KMACJOB(ka, m, "SKA", 512 / 2);

            if (endcodedFile[2].equals(tPrime)) {
                System.out.println("Accepted Input");
            } else {
                System.out.println("Incorrect t != t'");
            }
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }

    public static String gettingFileInfo(Scanner s) {
        boolean done = false;
        String theString = null;
        while (!done) {
            System.out.println("Please enter the full path of the file:");
            File f = new File(s.nextLine());
            if (f.exists()) {
                try {
                    theString = new String(Files.readAllBytes(f.getAbsoluteFile().toPath()));
                    done = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("ERROR: File doesn't exist. try again: ");
            }
        }
        return theString;
    }
}

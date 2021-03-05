import java.io.*;

import net.fornwall.jelf.ElfFile;
import org.jsoup.Jsoup;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.List;
import java.nio.file.Path;

class DisassembledElement {
    public String valueToPrint;
    public int numberOfBytes;

    public DisassembledElement() {
    }

    public void setValueToPrint(String valueToPrint) {
        this.valueToPrint = valueToPrint;
    }

    public void setNumberOfBytes(int numberOfBytes) {
        this.numberOfBytes = numberOfBytes;
    }

}

public class Main extends Helper{

    // to run:
    // compile in here first
    // mvn compile
    // mvn exec:java -Dexec.mainClass=Main

    public static void main(String[] args) {

        Map<String, String[]> map = constructHashMap();

        //printHashMap(map);

        try {
            String x = binaryFileToHexString("/Users/kris/Desktop/Personal/DePaul/SE526/disassembler/main");

            for (int i = 0; i < x.length() - 1; i+=2) {
                //System.out.println(x.substring(i, i+2));
            }
            doDisassembly(x, map);
            readBytes(x);

        } catch (Exception e){

        }

    }

    private static void readBytes(String x) {
        try {
            File file = new File("/Users/kris/Desktop/Personal/DePaul/SE526/disassembler/mybinaryfile");

            InputStream insputStream = new FileInputStream(file);
            long length = file.length();

            byte[] bytes = new byte[(int) length];

            insputStream.read(bytes);
            insputStream.close();

            //int numBytesToPrint = 100;
            int start = 15776;
            int numBytesToPrint = bytes.length - start;

            for (int i = start; i < numBytesToPrint-3; i+=4) {
                String bytesToPrint = String.format("%02x", bytes[i]) + " " +
                        String.format("%02x", bytes[i+1]) + " " +
                String.format("%02x", bytes[i+2]) + " " +
                String.format("%02x", bytes[i+3]);
                System.out.println(bytesToPrint);
            }
            String s = new String(bytes);
            //Print the byte data into string format
            for (int i = 0; i < s.length(); i++ ) {
                //System.out.print(String.format("%c", s.charAt(i)));
            }
            //System.out.println(new String(new byte[]{ (byte)0x63 }, "US-ASCII"));

        } catch (Exception e) {

        }
    }

    private static void doDisassembly(String x, Map<String, String[]> map) {
        int c = 0;
        DisassembledElement de;

        try {
            FileWriter myFile = new FileWriter("disassembly.txt", false);

            //int pc = 0;
            int pc = 15776;
            while (pc < x.length()) {
                de = disassemble(x, pc, map);
                pc += de.numberOfBytes;
                myFile.write(de.valueToPrint + "\n");
                c++;
            }

            myFile.close();


        } catch (Exception e) {
            e.printStackTrace();
        }


        System.out.println("number of bytes processed: " + c);
    }

    private static DisassembledElement disassemble(String x, int pc, Map<String, String[]> map) {
        // make sure we have two bytes to get
        DisassembledElement toReturn = new DisassembledElement();
        if (pc >= x.length() - 1) {
            toReturn.setNumberOfBytes(1);
            return toReturn;
        } else {
            String memoryAddress = String.format("%08x", pc);
            String opcode = x.substring(pc, pc+2);
            String opcodeName = map.get(opcode)[0];
            String opcodeArgs = map.get(opcode)[1];
            toReturn.setNumberOfBytes(getNumberOfBytesForOpcode(opcode) * 2);
            toReturn.setValueToPrint(memoryAddress + "      " + opcodeName + "   " + opcodeArgs);
        }

        return toReturn;
    }

    private static int getNumberOfArgumentsInOpcode(String opCodeArgs) {

        String args = opCodeArgs.trim();
        System.out.println("args is " + args);

        if (args.length() > 0) {
            String[] parts = args.split(" ");

            return parts.length;
        }

        return 0;
    }

    private static int getNumberOfBytesForOpcode(String opCode) {

        if (opCode.charAt(0) == '0') {
            return 1;
        }
        return 2;

    }

}


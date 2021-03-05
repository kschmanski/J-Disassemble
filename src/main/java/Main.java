import java.io.*;

import org.jsoup.Jsoup;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.List;

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

            doDisassembly(x, map);


        } catch (Exception e){

        }
    }

    private static void doDisassembly(String x, Map<String, String[]> map) {
        int c = 0;
        DisassembledElement de = new DisassembledElement();

        try {
            FileWriter myFile = new FileWriter("disassembly.txt", false);
            //myFile.write("H2!");

            int pc = 0;
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


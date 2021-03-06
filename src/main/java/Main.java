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

public class Main extends Helper {

    // to run:
    // compile in here first
    // mvn compile
    // mvn exec:java -Dexec.mainClass=Main

    public static void main(String[] args) {

        Map<String, String[]> map = constructHashMap();

        //printHashMap(map);

        try {
            String x = binaryFileToHexString("/Users/kris/Desktop/Personal/DePaul/SE526/disassembler/mybinaryfile");

            int entryPoint = getEntrypoint("/Users/kris/Desktop/Personal/DePaul/SE526/disassembler/mybinaryfile");

            System.out.println("entrypoint: " + entryPoint);
            System.out.println("expected: 15776");

            doDisassembly(x, map, entryPoint);
            //readBytes(x);

        } catch (Exception e){
            e.printStackTrace();

        }

    }

    /**
     * Hacky function used to get the entrypoint of a file.
     * Will probably only work for MacOS files...
     *
     * @param x
     * @return
     */
    private static int getEntrypoint(String x) {
        int entryPoint = 0;
        ProcessBuilder processBuilder = new ProcessBuilder();

        // -- Linux --

        // Run a shell command
        processBuilder.command("bash", "-c", "otool -l /Users/kris/Desktop/Personal/DePaul/SE526/disassembler/mybinaryfile | fgrep -B1 -A3 LC_MAIN");


        try {

            Process process = processBuilder.start();

            StringBuilder output = new StringBuilder();
            Map<String, String> map = new HashMap<String, String>();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
                String[] t = line.trim().split(" ");
                map.put(t[0], t[1]);
            }

            int exitVal = process.waitFor();
            if (exitVal == 0) {
                //System.out.println("Success!");
                //System.out.println(output);
                for (String key : map.keySet()) {
                    if (key.equals("entryoff")) {
                        entryPoint = Integer.parseInt(map.get(key));
                    }
                }
            } else {
                //abnormal...
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return entryPoint;
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

    private static void doDisassembly(String x, Map<String, String[]> map, int entryPoint) {
        int c = 0;
        DisassembledElement de;
        File file = new File("/Users/kris/Desktop/Personal/DePaul/SE526/disassembler/mybinaryfile");

        try {
            FileWriter myFile = new FileWriter("disassembly.txt", false);
            InputStream insputStream = new FileInputStream(file);
            long length = file.length();

            byte[] bytes = new byte[(int) length];

            insputStream.read(bytes);
            insputStream.close();



            //int pc = 0;
            int pc = entryPoint;
            int numBytesToPrint = bytes.length - pc;

            //while (pc < 15800) {
            while (pc < numBytesToPrint - 3) {
                de = disassemble(x, pc, map, bytes);
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

    private static DisassembledElement disassemble(String x, int pc, Map<String, String[]> map, byte[] bytes) {
        // make sure we have two bytes to get
        DisassembledElement toReturn = new DisassembledElement();
        if (pc >= bytes.length - 1) {
            toReturn.setNumberOfBytes(1);
            return toReturn;
        } else {
            String memoryAddress = String.format("%08x", pc);
            //String opcode = x.substring(pc, pc+2);
            String opcode = String.format("%02x", bytes[pc]).toUpperCase();
            System.out.println("opcode is: " + opcode);
            System.out.println("Name is: " + map.get(opcode)[0]);
            String opcodeName = map.get(opcode)[0];
            String opcodeArgs = map.get(opcode)[1];
            toReturn.setNumberOfBytes(getNumberOfBytesForOpcode(opcode) * 2);
            toReturn.setValueToPrint(memoryAddress + "      " + opcodeName + "   " + opcodeArgs);
        }

        return toReturn;
    }

    private static DisassembledElement d2(String x, int pc, Map<String, String[]> map) {
        File file = new File("/Users/kris/Desktop/Personal/DePaul/SE526/disassembler/mybinaryfile");
        DisassembledElement toReturn = new DisassembledElement();

        try {
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


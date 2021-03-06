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

public class Disassembler extends Helper {


    // to run:
    // compile in here first
    // mvn compile
    // mvn exec:java -Dexec.mainClass=Main

    // Binary file to disassemble
    String filepath = "/Users/kris/Desktop/Personal/DePaul/SE526/disassembler/mybinaryfile";
    Map<String, String[]> map;
    int entryPoint = 0;

    public static void main(String[] args) {

        Disassembler d = new Disassembler();
        d.map = constructHashMap();

        //printHashMap(map);

        try {
            d.entryPoint = getEntrypoint(d.filepath);

            System.out.println("entrypoint: " + d.entryPoint);
            System.out.println("expected: 15776");

            doDisassembly(d);

        } catch (Exception e){
            e.printStackTrace();

        }

    }

    /**
     * Hacky function used to get the entrypoint of a file.
     * Will probably only work for MacOS files.
     *
     * @param filepath String
     *
     * @return
     */
    private static int getEntrypoint(String filepath) {
        int entryPoint = 0;
        ProcessBuilder processBuilder = new ProcessBuilder();

        String shell_command = "otool -l " + filepath + " | fgrep -B1 -A3 LC_MAIN";

        // Run a shell command
        processBuilder.command("bash", "-c", shell_command);


        try {

            Process process = processBuilder.start();

            StringBuilder output = new StringBuilder();
            Map<String, String> map = new HashMap<String, String>();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
                String[] t = line.trim().split(" ");
                map.put(t[0], t[1]);
            }

            int exitVal = process.waitFor();
            if (exitVal == 0) {
                for (String key : map.keySet()) {
                    if (key.equals("entryoff")) {
                        entryPoint = Integer.parseInt(map.get(key));
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return entryPoint;
    }

    private static void doDisassembly(Disassembler d) {
        int c = 0;
        DisassembledElement de;
        File file = new File(d.filepath);

        try {
            FileWriter myFile = new FileWriter("disassembly.txt", false);
            InputStream insputStream = new FileInputStream(file);
            long length = file.length();

            byte[] bytes = new byte[(int) length];

            insputStream.read(bytes);
            insputStream.close();

            int program_counter = d.entryPoint;
            int numBytesToPrint = bytes.length - program_counter;

            while (program_counter < numBytesToPrint - 1) {
                de = disassemble(program_counter, d.map, bytes);
                program_counter += de.numberOfBytes;
                myFile.write(de.valueToPrint + "\n");
                c++;
            }

            myFile.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static DisassembledElement disassemble(int pc, Map<String, String[]> map, byte[] bytes) {
        // make sure we have two bytes to get
        DisassembledElement toReturn = new DisassembledElement();
        if (pc >= bytes.length - 1) {
            toReturn.setNumberOfBytes(1);
            return toReturn;
        } else {
            String memoryAddress = String.format("%08x", pc);
            //String opcode = x.substring(pc, pc+2);
            String opcode = String.format("%02x", bytes[pc]).toUpperCase();
            //System.out.println("opcode is: " + opcode);
            //System.out.println("Name is: " + map.get(opcode)[0]);
            String opcodeName = map.get(opcode)[0];
            String opcodeArgs = map.get(opcode)[1];
            toReturn.setNumberOfBytes(getNumberOfBytesForOpcode(opcode) * 2);
            toReturn.setValueToPrint(memoryAddress + "      " + opcodeName + "   " + opcodeArgs);
        }

        return toReturn;
    }

    private static int getNumberOfBytesForOpcode(String opCode) {

        if (opCode.charAt(0) == '0') {
            return 1;
        }
        return 2;

    }

}


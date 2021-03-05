import java.io.*;

import org.jsoup.Jsoup;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.List;

public class Main {

    // to run:
    // compile in here first
    // mvn compile
    // mvn exec:java -Dexec.mainClass=Main

    public static void main(String[] args) {

        Map<String, String[]> map = constructHashMapCoder32();

        //printHashMap(map);

        try {
            String x = binaryFileToHexString("/Users/kris/Desktop/Personal/DePaul/SE526/disassembler/main");
            //System.out.println(x);

            int pc = 0;
            int c = 0;
            while (pc < x.length()) {
                pc += disassemble(x, pc, map);
                c++;
            }
            System.out.println("number of bytes processed: " + c);

        } catch (Exception e){

        }
    }

    private static int disassemble(String x, int pc, Map<String, String[]> map) {
        // make sure we have two bytes to get
        if (pc >= x.length() - 1) {
            return 1;
        }

        String memoryAddress = String.format("%08x", pc);
        String opcode = x.substring(pc, pc+2);
        String opcodeName = map.get(opcode)[0];
        String opcodeArgs = map.get(opcode)[1];

        System.out.println("Opcode: " + opcode);
        System.out.println(memoryAddress + "      " + opcodeName + "   " + opcodeArgs);

        return getNumberOfBytesForOpcode(opcode) * 2;
    }

    //TODO - rework this function, docblock, change name, variable names
    public static String binaryFileToHexString(final String path)
            throws FileNotFoundException, IOException
    {
        final int bufferSize = 512;
        final byte[] buffer = new byte[bufferSize];
        final StringBuilder sb = new StringBuilder();

        // open the file
        FileInputStream stream = new FileInputStream(path);
        int bytesRead;

        // read a block
        while ((bytesRead = stream.read(buffer)) > 0)
        {
            // append the block as hex
            for (int i = 0; i < bytesRead; i++)
            {
                sb.append(String.format("%02X", buffer[i]));
            }
        }
        stream.close();

        return sb.toString();
    }

    //TODO - better function name, create helper functions and docblocks
    private static Map<String, String[]> constructHashMap() {

        //TODO - better variable names
        Map map = new HashMap<String, String>();


        try {
            ClassLoader classLoader = Main.class.getClassLoader();
            File input = new File(classLoader.getResource("opcodes.html").getFile());
            Document doc = Jsoup.parse(input, "UTF-8", "http://sparksandflames.com");

            Element table = doc.select("table").get(0); //select the first table.
            Elements rows = table.select("tr");

            //TODO - refactor both of these loops to be a foreach (look up the Java syntax for it)
            for (int i = 0; i < rows.size(); i++) {
                Element row = rows.get(i);
                Elements cols = row.select("td");

                for (int j = 0; j < cols.size(); j++) {
                    Elements opCode = cols.get(j).select("i");
                    Elements opCodeName = cols.get(j).select("b");

                    String opCodetext = opCode.text();
                    String opCodeNameText = opCodeName.text();
                    String opCodeArgs = cols.get(j).ownText();

                    String stringArray[] = new String[2];
                    stringArray[0] = opCodeNameText;
                    stringArray[1] = opCodeArgs;

                    map.put(opCodetext, stringArray);
                    System.out.println(opCodetext);
                    System.out.println(Arrays.toString((String[])map.get(opCodetext)));
                    System.out.println(getNumberOfArgumentsInOpcode(opCodeArgs));
                    System.out.println("number of bytes is " + getNumberOfBytesForOpcode(opCodetext));
                    System.out.println("\n\n");
                }

            }
            //TODO - do some better exception handling here
        } catch (Exception e) {

        }

        return map;

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


    //TODO - better function name, create helper functions and docblocks
    private static Map<String, String[]> constructHashMapCoder32() {

        //TODO - better variable names
        Map map = new HashMap<String, String>();


        try {
            ClassLoader classLoader = Main.class.getClassLoader();
            File input = new File(classLoader.getResource("coder32_opcodes.html").getFile());
            System.out.println("got table");

            Document doc = Jsoup.parse(input, "UTF-8", "http://sparksandflames.com");

            Element table = doc.select("table").get(0); //select the first table.
            Elements rows = table.select("tr");

            Elements headers = rows.first().select("th");


            for (int i = 0; i < rows.size(); i++) {
                Element row = rows.get(i);
                Elements cols = row.select("td");
                //System.out.println(row.text());
                String opcode = rows.get(i).child(2).text();
                String opcodeMnemonic = rows.get(i).child(10).text();
                String arg1 = rows.get(i).child(11).text();
                String arg2 = rows.get(i).child(12).text();
                //System.out.println("and value for opcode is " + opcode);
                //System.out.println("and name for opcode is " + opcodeMnemonic);
                //System.out.println("and name for arg1 is " + arg1);
                //System.out.println("and name for arg2 is " + arg2);

                StringBuilder opcodeArgs = new StringBuilder();
                opcodeArgs.append(arg1.trim());
                opcodeArgs.append(",");
                opcodeArgs.append(arg2.trim());

                //System.out.println("args is " + opcodeArgs.toString());


                String stringArray[] = new String[2];
                stringArray[0] = opcodeMnemonic;
                stringArray[1] = opcodeArgs.toString();

                map.put(opcode, stringArray);

            }
            //TODO - do some better exception handling here
        } catch (Exception e) {

        }

        map = fillInMissingValues(map);

        return map;

    }

    private static Map<String, String[]> fillInMissingValues(Map<String, String[]> map) {
        for (char i = 0; i < 256; i++) {
            if (map.get(String.format("%02x", (int)i)) != null) {
                //System.out.println("map already has value " + String.format("%02x", (int)i));
            } else {
                String arr[] = new String[2];
                arr[0] = "NOP";
                map.put(String.format("%02x", (int)i).toUpperCase(), arr);
                //System.out.println("put NOP for " + String.format("%02x", (int)i));
            }
            //System.out.println(String.format("%04x", (int)i));
        }
        return map;

    }

    private static void printHashMap(Map<String, String[]> map) {

        for (String key: map.keySet()) {
            System.out.println(key + " : ");
            for (String element : map.get(key)) {
                System.out.println(element);
            }
        }

    }

}


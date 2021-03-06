import com.sun.tools.javac.util.ArrayUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Helper {

    /**
     * Constructs a hash map of opcodes to their mnemonics and arguments.
     *
     * Hashmap keys are Strings of their numberic value.
     * Values are an array consisting of [Mnemonic, Arguments] where Arguments are comma-separated.
     *
     * @return Map<String, String[]>
     */
    public static Map<String, String[]> constructHashMap() {

        Map map = new HashMap<String, String>();

        try {
            ClassLoader classLoader = Disassembler.class.getClassLoader();
            // Get the HTML file from which to construct the hashmap
            File input = new File(classLoader.getResource("coder32_opcodes_round2.html").getFile());

            Document doc = Jsoup.parse(input, "UTF-8", "http://sparksandflames.com");
            Element table = doc.select("table").get(0);
            Elements rows = table.select("tr");

            for (int i = 0; i < rows.size(); i++) {
                Element row = rows.get(i);

                // Get the opcode, the mnemonic, and the arguments.
                // Not super robust but it works
                String opcode = rows.get(i).child(2).text();
                String opcodeMnemonic = rows.get(i).child(10).text();
                String arg1 = rows.get(i).child(11).text();
                String arg2 = rows.get(i).child(12).text();

                StringBuilder opcodeArgs = new StringBuilder();
                opcodeArgs.append(arg1.trim());
                opcodeArgs.append(",");
                opcodeArgs.append(arg2.trim());

                String stringArray[] = new String[2];
                stringArray[0] = opcodeMnemonic;
                stringArray[1] = opcodeArgs.toString();

                map.put(opcode, stringArray);

                // If the row we're processing has multiple entries, we need to skip at least one
                if (row.select("td").hasAttr("rowspan")) {
                    int rowspan = Integer.parseInt(row.select("td").attr("rowspan"));
                    i+= rowspan - 1;
                }
            }
        } catch (Exception e) {

        }

        map = fillInMissingValues(map);

        return map;

    }

    /**
     * Hacky function which fills in the missing values from the Opcodes table.
     * Need to fill in 40-59 since the opcodes table only lists "40+r", "48+r" etc.
     * If I had more time to refactor this method to make it look nicer and less clunky,
     * I absolutely would.
     *
     * @param map Hashmap of opcodes.
     *
     * @return map new Hashmap with missing values included.
     */
    public static Map<String, String[]> fillInMissingValues(Map<String, String[]> map) {
        String[] incs = new String[]{ "40", "41", "42", "43", "44", "45", "46", "47" };
        String[] decs = new String[]{ "48", "49" };
        String[] pushes = new String[]{ "50", "51", "52", "53", "54", "55", "56", "57" };
        String[] pops = new String[]{ "58", "59" };

        for (char i = 0; i < 256; i++) {
            if (map.get(String.format("%02x", (int)i)) != null) {
            } else {
                String arr[] = new String[2];
                arr[0] = "NOP";

                if (arrayContainsKey(incs, String.format("%02x", (int)i))) {
                        arr[0] = "INC";
                        arr[1] = "r16/32,";
                }

                if (arrayContainsKey(decs, String.format("%02x", (int)i))) {
                    arr[0] = "DEC";
                    arr[1] = "r16/32,";
                }

                if (arrayContainsKey(pushes, String.format("%02x", (int)i))) {
                    arr[0] = "PUSH";
                    arr[1] = "r16/32,";
                }

                if (arrayContainsKey(pops, String.format("%02x", (int)i))) {
                    arr[0] = "POP";
                    arr[1] = "r16/32,";
                }


                map.put(String.format("%02x", (int)i).toUpperCase(), arr);
            }
        }
        return map;

    }

    /**
     * Useful function to print the Hashmap. Used for debugging purposes.
     *
     * @param map
     *
     * @return void
     */
    public static void printHashMap(Map<String, String[]> map) {

        for (String key: map.keySet()) {
            System.out.println(key + " : ");
            for (String element : map.get(key)) {
                System.out.println(element);
            }
        }

    }

    /**
     * Returns true if the array of strings contains the key.
     *
     * @param array
     * @param key
     *
     * @return boolean
     */
    private static boolean arrayContainsKey(String[] array, String key) {
        for (String i : array
             ) {
            if (key.equals(i)) {
                return true;
            }

        }
        return false;
    }

    /**
     * Useful function to read the bytes of a string and print them out one at a time.
     *
     * @return void
     */
    public static void readBytes() {
        try {
            File file = new File("/Users/kris/Desktop/Personal/DePaul/SE526/disassembler/mybinaryfile");

            InputStream inputStream = new FileInputStream(file);
            long length = file.length();

            byte[] bytes = new byte[(int) length];

            inputStream.read(bytes);
            inputStream.close();

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
        } catch (Exception e) {
        }
    }
}

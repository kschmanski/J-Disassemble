import com.sun.tools.javac.util.ArrayUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Helper {

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
    public static Map<String, String[]> constructHashMap() {

        //TODO - better variable names
        Map map = new HashMap<String, String>();


        try {
            ClassLoader classLoader = Main.class.getClassLoader();
            File input = new File(classLoader.getResource("coder32_opcodes_round2.html").getFile());
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
//                System.out.println("and value for opcode is " + opcode);
//                System.out.println("and name for opcode is " + opcodeMnemonic);
//                System.out.println("and name for arg1 is " + arg1);
//                System.out.println("and name for arg2 is " + arg2);

                StringBuilder opcodeArgs = new StringBuilder();
                opcodeArgs.append(arg1.trim());
                opcodeArgs.append(",");
                opcodeArgs.append(arg2.trim());

                //System.out.println("args is " + opcodeArgs.toString());


                String stringArray[] = new String[2];
                stringArray[0] = opcodeMnemonic;
                stringArray[1] = opcodeArgs.toString();

                map.put(opcode, stringArray);



                if (row.select("td").hasAttr("rowspan")) {
                     int rowspan = Integer.parseInt(row.select("td").attr("rowspan"));
                    //System.out.println("rowspan 2");
                    i+= rowspan - 1 ;
                }


            }
            //TODO - do some better exception handling here
        } catch (Exception e) {

        }

        map = fillInMissingValues(map);

        return map;

    }

    public static Map<String, String[]> fillInMissingValues(Map<String, String[]> map) {
        String[] incs = new String[]{ "40", "41", "42", "43", "44", "45", "46", "47" };
        String[] decs = new String[]{ "48", "49" };
        String[] pushes = new String[]{ "50", "51", "52", "53", "54", "55", "56", "57" };
        String[] pops = new String[]{ "58", "59" };

        for (char i = 0; i < 256; i++) {
            if (map.get(String.format("%02x", (int)i)) != null) {
                //System.out.println("map already has value " + String.format("%02x", (int)i));
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
                //System.out.println("put NOP for " + String.format("%02x", (int)i));
            }
            //System.out.println(String.format("%04x", (int)i));
        }
        return map;

    }

    public static void printHashMap(Map<String, String[]> map) {

        for (String key: map.keySet()) {
            System.out.println(key + " : ");
            for (String element : map.get(key)) {
                System.out.println(element);
            }
        }

    }

    private static boolean arrayContainsKey(String[] array, String key) {
        for (String i : array
             ) {
            if (key.equals(i)) {
                return true;
            }

        }
        return false;
    }

}

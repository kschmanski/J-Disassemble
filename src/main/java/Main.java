import java.io.*;

import org.jsoup.Jsoup;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {

        Map<String, String> map = constructHashMap();

        System.out.println(Arrays.asList(map));
    }

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

    private static Map<String, String> constructHashMap() {

        Map map = new HashMap<String, String>();


        try {
            ClassLoader classLoader = Main.class.getClassLoader();
            File input = new File(classLoader.getResource("opcodes.html").getFile());
            Document doc = Jsoup.parse(input, "UTF-8", "http://sparksandflames.com");

            Element table = doc.select("table").get(0); //select the first table.
            Elements rows = table.select("tr");

            for (int i = 0; i < rows.size(); i++) {
                Element row = rows.get(i);
                Elements cols = row.select("td");

                for (int j = 0; j < cols.size(); j++) {
                    Elements opCode = cols.get(j).select("i");
                    Elements opCodeName = cols.get(j).select("b");

                    String opCodetext = opCode.text();
                    String opCodeNameText = opCodeName.text();

                    map.put(opCodetext, opCodeNameText);
                }

            }

        } catch (Exception e) {

        }

        return map;

    }

}


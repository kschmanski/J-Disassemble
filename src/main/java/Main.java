import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
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

        System.out.println(Arrays.asList(map)); // method 1

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
            Document doc = Jsoup.connect("http://sparksandflames.com/files/x86InstructionChart.html").get();

            Element table = doc.select("table").get(0); //select the first table.
            Elements rows = table.select("tr");


            for (int i = 0; i < rows.size(); i++) {
                Element row = rows.get(i);
                Elements cols = row.select("td");

                for (int j = 0; j < cols.size(); j++) {
                    //System.out.println(cols.get(j));

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


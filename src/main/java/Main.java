import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import org.jsoup.Jsoup;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import java.util.ArrayList;
public class Main {

    public static void main(String[] args) {
        System.out.println("hello!");


        // write your code here

//        try {
//            //String x = Main.binaryFileToHexString("/Users/kris/Desktop/Personal/DePaul/SE526/disassembler/invaders.h");
//            //String x = Main.binaryFileToHexString("/Users/kris/Desktop/Personal/DePaul/SE526/disassembler/main");
//            //System.out.println(" X is: " + x + "\n");
//
//
//
//        } catch (Exception e) {
//
//            System.out.println("something bad happened: " + e);
//
//        }

        String html = "<html><head><title>First parse</title></head>"
                + "<body><p>Parsed HTML into a doc.</p></body></html>";
        //Document doc = Jsoup.parse(html);
        ArrayList<String> downServers = new ArrayList<String>();
        try {
            Document doc = Jsoup.connect("http://sparksandflames.com/files/x86InstructionChart.html").get();

            Element table = doc.select("table").get(0); //select the first table.
            Elements rows = table.select("tr");


            for (int i = 1; i < rows.size(); i++) { //first row is the col names so skip it.
                Element row = rows.get(i);
                Elements cols = row.select("td");

                System.out.println(cols.get(0));

            }

        } catch (Exception e) {

        }

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

    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }

}


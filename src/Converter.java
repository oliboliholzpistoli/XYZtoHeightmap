import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Converter {
    private File baseDir;
    private File outputDir;
    public Converter(File baseDir, File outputDir) {
        this.baseDir = baseDir;
        this.outputDir = outputDir;
    }

    public void convert() {

        try {
            Path fileName = Path.of(baseDir.getPath());
            String content = Files.readString(fileName);
            String[] splitString = content.split("\\r?\\n");
            double maxX = 0;
            double minX = -1;
            double maxY = 0;
            double minY = -1;
            double maxZ = 0;
            double minZ = -1;
            int i = 0;

            HashMap<String,Double> imageContent = new HashMap<>();

            for (String s:splitString){
                String[] ss = s.split(" ");
                double x = Double.parseDouble(ss[0]);
                double y = Double.parseDouble(ss[1]);
                double z = Double.parseDouble(ss[2]);

                maxX = maxX > x ? maxX : x;
                minX = minX < x && minX !=-1 ? minX: x;
                maxY = maxY > y? maxY : y;
                minY = minY < y && minY !=-1 ? minY: y;
                maxZ = maxZ > z ? maxZ : z;
                minZ = minZ < z && minZ !=-1 ? minZ: z;
                System.out.println(i+"\t"+x+"|"+y +"|"+z);
                i++;
                imageContent.put(x+"|"+y,z);
            }
            System.out.println("X MAX: " + maxX + " X MIN: " + minX);
            System.out.println("Y MAX: " + maxY + " Y MIN: " + minY);
            System.out.println("Z MAX: " + maxZ + " Z MIN: " + minZ);

            BufferedImage heightMap = new BufferedImage((int)(maxX - minX)*2,(int)(maxY - minY)*2,BufferedImage.TYPE_USHORT_GRAY);
            for (int x = 0; x < heightMap.getWidth(); x++) {
                for (int y = 0; y < heightMap.getHeight(); y++) {
                    Double mapKey = imageContent.get(((x/2)+minX)+"|"+((y/2)+minY));
                    if (mapKey != null) {
                        short singleRGBValue = (short) (32768 * ((mapKey-minZ) / (maxZ-minZ)));
                        System.out.println(new Color(singleRGBValue,singleRGBValue,singleRGBValue).getRGB());
                        heightMap.setRGB(x, y,new Color(singleRGBValue,singleRGBValue,singleRGBValue).getRGB());
                    }
                }
            }

            File outputfile = new File(outputDir + "/" + baseDir.toString().substring(baseDir.toString().lastIndexOf('\\'),baseDir.toString().length()-4)+".png");
            ImageIO.write(heightMap, "png", outputfile);
            System.out.println("Done!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
class FileMatrixComparator implements Comparator<ArrayList<String>> {
    @Override
    public int compare(ArrayList<String> o1, ArrayList<String> o2) {
        return Double.compare((Double.parseDouble(o1.get(2))+Double.parseDouble(o1.get(1))), Double.parseDouble(o2.get(2))+Double.parseDouble(o2.get(1)));
    }
}

import javax.imageio.ImageIO;
import javax.xml.crypto.Data;
import java.awt.*;
import java.awt.image.*;
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

    private int[] convertToRGB(int grayScaleValue){
        int remainder = grayScaleValue;
        int[] rgbVals = {0,0,0};
        for (int i = 2; i >= 0; i--){
            int powerValue =(int)(remainder / Math.pow(255,i));
            remainder -= Math.pow(255,i) * powerValue;
            rgbVals[i] = powerValue;
        }

        return rgbVals;
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

                maxX = Math.max(maxX, x);
                minX = minX < x && minX !=-1 ? minX: x;
                maxY = Math.max(maxY, y);
                minY = minY < y && minY !=-1 ? minY: y;
                maxZ = Math.max(maxZ, z);
                minZ = minZ < z && minZ !=-1 ? minZ: z;
                System.out.println(i+"\t"+x+"|"+y +"|"+z);
                i++;
                imageContent.put(x+"|"+y,z);
            }
            System.out.println("X MAX: " + maxX + " X MIN: " + minX);
            System.out.println("Y MAX: " + maxY + " Y MIN: " + minY);
            System.out.println("Z MAX: " + maxZ + " Z MIN: " + minZ);

            BufferedImage heightMap = new BufferedImage((int)(maxX - minX)*2,(int)(maxY - minY)*2,BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < heightMap.getWidth(); x++) {
                for (int y = 0; y < heightMap.getHeight(); y++) {
                    Double mapKey = imageContent.get(((x/2)+minX)+"|"+((y/2)+minY));
                    if (mapKey != null) {
                        int singleGreyValue = (int) (16581375 * ((mapKey-minZ) / (maxZ-minZ)));
                        int[] rgbVals = convertToRGB(singleGreyValue);
                        System.out.println("Red:" + rgbVals[2] + " Blue:" + rgbVals[1] + " Green:" + rgbVals[0]);
                        //System.out.println(new Color(rgbVals[2],rgbVals[1],rgbVals[0]).getRGB());
                        heightMap.setRGB(x, y,new Color(rgbVals[2],rgbVals[1],rgbVals[0]).getRGB());
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

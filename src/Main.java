import javax.swing.*;
import javax.swing.filechooser.FileSystemView;

public class Main {
    public static void main(String[] args) {
        JFileChooser jFileChooserBase = new JFileChooser(System.getProperty("user.dir") + "/3d2r");
        jFileChooserBase.setDialogTitle("Choose your XYZ File");
        jFileChooserBase.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnValueBaseDir = jFileChooserBase.showOpenDialog(null);

        JFileChooser jFileChooserTarget = new JFileChooser(System.getProperty("user.dir"));
        jFileChooserTarget.setDialogTitle("Choose the target directory for the height map image");
        jFileChooserTarget.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnValueTargetDir = jFileChooserTarget.showOpenDialog(null);

        if (returnValueBaseDir == JFileChooser.APPROVE_OPTION && returnValueTargetDir == JFileChooser.APPROVE_OPTION){
            Converter converter = new Converter(jFileChooserBase.getSelectedFile(),jFileChooserTarget.getSelectedFile());
            converter.convert();
        }
    }
}

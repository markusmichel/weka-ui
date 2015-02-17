package wekaui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import weka.core.Instances;

/**
 *
 * @author markus
 */
public class ArffFile {
    private File file;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
    
    public boolean isArffFileValid() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(getFile()));
            Instances data = new Instances(reader);
            reader.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ArffFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            // Throwed by new Instances
            System.out.println("Arff file is invalid");
            return false;
        }
        System.out.println("arff file is valid");
        return true;
    }
    
    /**
     * If the Arff file exists and is valid,
     * saves a new Arff file to the files system with the same class
     * and attribute annotaions, but with no content.
     * @param location Location of the new empty arff file.
     * @throws IOException If failed to write file on disk.
     */
    public void saveEmptyArffFile(File location) throws IOException {
        
        StringBuilder builder = new StringBuilder();
        String line;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(getFile()));
            while((line = reader.readLine()) != null) {
                if(line.startsWith("@")) {
                    builder.append(line);
                    builder.append("\n");
                }
            }
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(ArffFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        FileUtils.writeStringToFile(location, builder.toString());
    }
}

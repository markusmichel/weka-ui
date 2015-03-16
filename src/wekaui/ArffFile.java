package wekaui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import wekaui.logic.MyInstances;

/**
 *
 * @author markus
 */
public class ArffFile extends File {
    private File file;

    public ArffFile(String pathname) {
        super(pathname);
    }

    public ArffFile(String parent, String child) {
        super(parent, child);
    }

    public ArffFile(File parent, String child) {
        super(parent, child);
    }

    public ArffFile(URI uri) {
        super(uri);
    }

    public void setFile(File file) {
        this.file = file;
    }
    
    public MyInstances getInstances() throws ArffFileInvalidException {
        MyInstances instances = null;
        try {
            DataSource source = new DataSource(this.getAbsolutePath());
            Instances data = source.getDataSet();
            instances = new MyInstances(data, this);
        } catch (Exception ex) {
            throw new ArffFileInvalidException();
        }
        
        return instances;
    }
    
    /**
     * If the Arff file exists and is valid,
     * saves a new Arff file to the files system with the same class
     * and attribute annotaions, but with no content.
     * @param location Location of the new empty arff file.
     * @throws IOException If failed to write file on disk.
     */
    public ArffFile saveEmptyArffFile(File location, String modelName) throws IOException {
        
        StringBuilder builder = new StringBuilder();
        String line;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(this));
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
        
        //@TODO: alternative filepath?!
        String modelNameWithOutExt = FilenameUtils.removeExtension(modelName);
        ArffFile fileToSave = new ArffFile("empty_arff_file_for_" + modelNameWithOutExt + "_model.arff");
        
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave));
        writer.write(builder.toString());
        writer.flush();
        writer.close();
        
        return fileToSave;
        //FileUtils.writeStringToFile(fileToSave, builder.toString());
    }
    
    /**
     * Returns the content of the Arff-File as a String
     * @return String which contains the content.
     */
    public String getArffFileContent(){
        String content = "";
        
        StringBuilder builder = new StringBuilder();
        String line;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(this));            
            while((line = reader.readLine()) != null) {                
                content += line + "\n";                
            }
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(ArffFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        return content;
    }

    public static class ArffFileInvalidException extends Exception {

        public ArffFileInvalidException() {
        }
    }
}

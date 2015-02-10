/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekaui.logic.arffconverter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 *
 * @author Noodlewood
 */
public class ArffConverter {

    private DataToArff dataToArff;

    public File makeArffFile(List<File> data) {
        List<File> arffFiles  = null;
        
        for (File orgFile : data) {
            try {
                String type = Files.probeContentType(orgFile.toPath());
                if (type == null) {
                    System.err.format("'%s' has an" + " unknown filetype.%n", data);
                } else {
                    System.out.println("file is type " + type);
                    dataToArff = new TxtToArff();
                    arffFiles.add(dataToArff.convertToArff(orgFile));
                }
            } catch (IOException x) {
                System.err.println(x);
            }
        }

        return mergeArffFiles(arffFiles);
    }
    
    private File mergeArffFiles(List<File> arffFiles) {
        File mergedArff = null;
        /*
        for (File arffFile : arffFiles) {
            
        }
        */
        return mergedArff;
    }
}

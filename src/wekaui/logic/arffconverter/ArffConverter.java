/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekaui.logic.arffconverter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 *
 * @author Noodlewood
 */
public class ArffConverter {
    private DataToArff dataToArff;

    public File convert(File data) {
        try {
            String type = Files.probeContentType(data.toPath());
            if (type == null) {
                System.err.format("'%s' has an" + " unknown filetype.%n", data);
            } else {
                System.out.println("file is type " + type);
                dataToArff = new TxtToArff();
            }
        } catch (IOException x) {
            System.err.println(x);
        }
        
        return dataToArff.convertToArff(data);
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekaui.logic.arffconverter;

import java.io.File;

/**
 *
 * @author Noodlewood
 */
public class TxtToArff implements wekaui.logic.arffconverter.DataToArff {
    @Override
    public File convertToArff(File data) {
        return new File("path");
    }
}
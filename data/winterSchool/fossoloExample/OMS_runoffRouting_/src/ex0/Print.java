/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ex0;

import java.time.Instant;
import oms3.annotations.Execute;
import oms3.annotations.InNode;
import org.altervista.growworkinghard.jswmm.dataStructure.SWMMobject;

/**
 *
 * @author sidereus
 */
public class Print {
    
    @InNode
    public SWMMobject datastructure;
    
    @Execute
    public void exec() {
        // print dimension of the pipes
        for (int i=11; i<=20; i++) {
            System.out.print(i);
            System.out.println(datastructure.getConduit(String.valueOf(i)).getCrossSectionType().getMainDimension());
        }
    }
}

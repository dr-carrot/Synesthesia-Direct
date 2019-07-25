/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gradient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javafx.scene.paint.Color;

/**
 *
 * @author 'Aaron Lomba'
 */
public class FullGradient {

    private List<Gradient> gradients = new ArrayList<>();

    public Color getColorAt(double val) {
        double lowBound = 0;
        Gradient gr = null;
        double rem = -1;
        int x = 0;

        if(val == 1){
            return gradients.get(gradients.size() - 1).getGradColor(1);
        }
        
        for (Gradient g : gradients) {
            lowBound += g.getSize();
            //System.out.println("evaluating for " + x + ": " + g.getSize());
            if (val < lowBound) {
                gr = g;
                rem = (val - (lowBound - g.getSize()))/g.getSize();
                //System.out.println("val: " + rem);
                return gr.getGradColor(rem);
            }

        }
        return null;
        
    }
    
    public void swap(Gradient g1, Gradient g2){
        Gradient gt = g1;
        int a = gradients.indexOf(g1);
        int b = gradients.indexOf(g2);
        gradients.set(a, g2);
        gradients.set(b, gt);
    }
    
    public void remove(Gradient g){
        gradients.remove(g);
    }

    public Gradient gradientAt(double d) {
        double lowBound = 0;
        for (Gradient g : gradients) {
            lowBound += g.getSize();
            if (d < lowBound) {
                return g;
            }

        }
        return null;
    }

    public List<Gradient> getGradients() {
        return Collections.unmodifiableList(gradients);
    }

    /**
     * adds the gradient. Modifies the length of the added gradient to a fraction of the previous size
     * @param gr 
     */
    public void appendGrad(Gradient gr) {
        if (gradients.isEmpty()) {
            gr.setSize(1);
            gradients.add(gr);
        } else {
            double scl = 1/(1 + gr.getSize());
            
            for(Gradient g : gradients){
                g.setSize(g.getSize() * scl);
            }
            gr.setSize(gr.getSize() * scl);
            gradients.add(gr);
        }
    }
    
    public void appendSizedGrad(Gradient gr){
        if (gr.getSize() == 0)
            throw new IllegalArgumentException("The gradient cannot have size 0!");
        gradients.add(gr);
    }

}

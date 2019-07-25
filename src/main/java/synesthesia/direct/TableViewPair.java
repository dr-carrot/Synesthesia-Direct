/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package synesthesia.direct;

import javafx.scene.control.Tooltip;

/**
 *
 * @author 'Aaron Lomba'
 */
public class TableViewPair {

    
    
    public String val1;
    public String val2;
    private String description;
    private Tooltip tooltip = new Tooltip("No description available :(");
    private Float confidence = null;

    /*
     * public TableViewPair(String val1, String val2) {
     * this.val1 = (val1 == null) ? "NA" : val1;
     * this.val2 = (val2 == null) ? "NA" : val2;
     * }
     * public TableViewPair(String val1, String val2, String desc) {
     * this.val1 = (val1 == null) ? "NA" : val1;
     * this.val2 = (val2 == null) ? "NA" : val2;
     * }
     */
    public TableViewPair(Object val1, Float val2) {
        this.val1 = (val1 == null) ? "NA" : val1.toString();
        this.val2 = (val2 == null) ? "NA" : Float.toString(Math.round(val2 * 1000) / 1000.0f);
//        updateTooltip();
    }

    public TableViewPair(Object val1, Float val2, String desc) {
        this(val1, val2);
        description = desc;
    }

    public TableViewPair(Object val1, Object val2) {
        this.val1 = (val1 == null) ? "NA" : val1.toString();
        this.val2 = (val2 == null) ? "NA" : val2.toString();
        //updateTooltip();
    }

    public TableViewPair(Object val1, Object val2, String desc) {
        this(val1, val2);
        description = desc;
    }

    public TableViewPair(Object val1, Object val2, String desc, float confidence) {
        this(val1, val2, desc);
        this.confidence = confidence;
    }

    public TableViewPair(Object val1, Float val2, String desc, float confidence) {
        this(val1, val2, desc);
        this.confidence = confidence;
    }
    public void updateTooltip() {
        if (confidence == null) {
            tooltip.setText(val1 + ":\n" + description);
        }else{
            tooltip.setText(val1 + ":\n" + description + "\nConfidence: " + confidence);
        }
    }

    /**
     * @return the val1
     */
    public String getVal1() {
        return val1;
    }

    /**
     * @param val1 the val1 to set
     */
    public void setVal1(Object val1) {
        this.val1 = val1.toString();
    }

    /**
     * @param val1 the val1 to set
     */
    public void setVal1(String val1) {
        this.val1 = val1;
    }

    /**
     * @return the val2
     */
    public String getVal2() {
        return val2;
    }

    /**
     * @param val2 the val2 to set
     */
    public void setVal2(String val2) {
        this.val2 = val2;
    }

    /**
     * @param val2 the val2 to set
     */
    public void setVal2(Object val2) {
        this.val2 = val2.toString();
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the tooltip
     */
    public Tooltip getTooltip() {
        updateTooltip();
        return tooltip;
    }

}

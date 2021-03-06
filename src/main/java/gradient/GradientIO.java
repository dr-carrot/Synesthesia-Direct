/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gradient;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;
import javafx.scene.paint.Color;
import settings.Settings;

/**
 *
 * @author 'Aaron Lomba'
 */
public class GradientIO {

    private static String clrStr(String c) {
        return c.length() == 1 ? "0" + c : c;
    }

    public static void restoreFromAppData(File source, File dest) throws IOException {
        Files.copy(source.toPath(), dest.toPath());
    }

    public static void saveGradient(FullGradient bigGrad, File f) throws IOException {
        if (f.exists()) {
            f.delete();
        }
        
        FileWriter fw = new FileWriter(f);
        

        for (Gradient g : bigGrad.getGradients()) {
            //System.out.println("wrote a grad section");
            String re = Integer.toHexString((int) (g.getColor1().getRed() * 255));
            String gr = Integer.toHexString((int) (g.getColor1().getGreen() * 255));
            String bl = Integer.toHexString((int) (g.getColor1().getBlue() * 255));
            String al = Integer.toHexString((int) (g.getColor1().getOpacity() * 255));
            //System.out.println(re + gr + bl + al);
            fw.append("<gr>" + Settings.LN);
            fw.append("\t<start>" + "#" + clrStr(re) + clrStr(gr) + clrStr(bl) + clrStr(al) + Settings.LN);
            re = Integer.toHexString((int) (g.getColor2().getRed() * 255));
            gr = Integer.toHexString((int) (g.getColor2().getGreen() * 255));
            bl = Integer.toHexString((int) (g.getColor2().getBlue() * 255));
            al = Integer.toHexString((int) (g.getColor2().getOpacity() * 255));
            fw.append("\t<end>" + "#" + clrStr(re) + clrStr(gr) + clrStr(bl) + clrStr(al) + Settings.LN);
            //fw.append("\t<start>" + "#" + Integer.toHexString(g.getColor1().hashCode()) + Settings.LN);
            //fw.append("\t<end>" + "#" + Integer.toHexString(g.getColor2().hashCode()) + Settings.LN);
            fw.append("\t<size>" + g.getSize() + Settings.LN);
            fw.append("</gr>" + Settings.LN);

        }
        fw.close();
    }

    public static FullGradient loadGradient(File f) throws FileNotFoundException {
        FullGradient fg = new FullGradient();
        String ln;
        Scanner s = new Scanner(f);
        Color st = null;
        Color en = null;
        double sz = -1;
        while (s.hasNext()) {
            ln = s.nextLine().trim();
            if (ln.trim().equals("<gr>")) {
                while (s.hasNext()) {
                    ln = s.nextLine().trim();
                    //System.out.println(ln);
                    if (ln.equals("</gr>")) {
                        break;
                    } else if (ln.contains("<start>")) {
                        //System.out.println("color: " + ln.substring(ln.indexOf("#")));
                        st = Color.web(ln.substring(ln.indexOf("#")));
                    } else if (ln.contains("<end>")) {
                        en = Color.web(ln.substring(ln.indexOf("#")));
                    } else if (ln.contains("<size>")) {
                        sz = Double.parseDouble(ln.substring(ln.indexOf(">") + 1));
                    }

                }
                if (st == null || en == null || sz == -1) {
                    s.close();
                    throw new NullPointerException("Colors couldn't be initialized");
                }

                fg.appendSizedGrad(new Gradient(st, en, sz));

            }
        }
        s.close();
        return fg;
    }

}

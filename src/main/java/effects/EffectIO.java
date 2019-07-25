/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package effects;

import gradient.FullGradient;
import gradient.Gradient;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import javafx.scene.paint.Color;
import settings.Settings;

/**
 *
 * @author 'Aaron Lomba'
 */
public class EffectIO {

    public static void writeFX(FX fx, File f) throws IOException {
        if (f.exists()) {
            f.delete();
        }
        FileWriter writer = new FileWriter(f);
        writer.append("<fx>");
        writer.append("\t<FX name>" + fx.getName());
        writer.append("\t<Binding>" + fx.getBinding());
        writer.append("\t<Factor>" + fx.getFactorValue());
        writer.append("\t<Jumper>" + fx.getJumperValue());
        writer.append("\t<Smoother>" + fx.getSmootherValue());
        writer.append("\t<Profile>" + fx.getProfile());
        if (true) {
            for (Gradient g : fx.getGradient().getGradients()) {
                System.out.println("wrote a grad section");
                String re = Integer.toHexString((int) (g.getColor1().getRed() * 255));
                String gr = Integer.toHexString((int) (g.getColor1().getGreen() * 255));
                String bl = Integer.toHexString((int) (g.getColor1().getBlue() * 255));
                String al = Integer.toHexString((int) (g.getColor1().getOpacity() * 255));
                //System.out.println(re + gr + bl + al);
                writer.append("\t<gr>" + Settings.LN);
                writer.append("\t\t<start>" + "#" + clrStr(re) + clrStr(gr) + clrStr(bl) + clrStr(al) + Settings.LN);
                re = Integer.toHexString((int) (g.getColor2().getRed() * 255));
                gr = Integer.toHexString((int) (g.getColor2().getGreen() * 255));
                bl = Integer.toHexString((int) (g.getColor2().getBlue() * 255));
                al = Integer.toHexString((int) (g.getColor2().getOpacity() * 255));
                writer.append("\t\t<end>" + "#" + clrStr(re) + clrStr(gr) + clrStr(bl) + clrStr(al) + Settings.LN);
                //fw.append("\t<start>" + "#" + Integer.toHexString(g.getColor1().hashCode()) + Settings.LN);
                //fw.append("\t<end>" + "#" + Integer.toHexString(g.getColor2().hashCode()) + Settings.LN);
                writer.append("\t\t<size>" + g.getSize() + Settings.LN);
                writer.append("\t</gr>" + Settings.LN);

            }
        }
        writer.append("</fx>");
        writer.close();
    }

    public static FX readFX(File f) throws FileNotFoundException {
        String ln;
        FX fx = new FX();
        Scanner s = new Scanner(f);
        Color st = null;
        Color en = null;
        FullGradient fg = new FullGradient();
        double sz = -1;
        while (s.hasNext()) {
            ln = s.nextLine().trim();
            if (ln.trim().equals("<fx>")) {
                while (s.hasNext()) {
                    ln = s.nextLine().trim();
                    //System.out.println(ln);

                    //TODO replace contains with equals expression
                    if (ln.equals("</fx>")) {
                        break;
                    } else if (ln.contains("<FX name>")) {
                        //System.out.println("color: " + ln.substring(ln.indexOf("#")));
                        fx.setName(ln.substring(ln.indexOf(">") + 1));
                    } else if (ln.contains("<Binding>")) {
                        fx.setBinding(ln.substring(ln.indexOf(">") + 1));
                    } else if (ln.contains("<size>")) {
                        fx.setFactorValue(Double.parseDouble(ln.substring(ln.indexOf(">") + 1)));
                    } else if (ln.contains("<Jumper>")) {
                        fx.setJumperValue(Integer.parseInt(ln.substring(ln.indexOf(">") + 1)));
                    } else if (ln.contains("<Smother>")) {
                        fx.setSmootherValue(Integer.parseInt(ln.substring(ln.indexOf(">") + 1)));
                    } else if (ln.contains("<Profile>")) {
                        fx.setProfile(Integer.parseInt(ln.substring(ln.indexOf(">") + 1)));
                    } else if (ln.substring(0, ln.indexOf('>')).contains("gr")) {
                        while (s.hasNext()) {
                            ln = s.nextLine().trim();
                            //System.out.println(ln);
                            if (ln.equals("</gr>")) {
                                break;
                            } else if (ln.contains("<start>")) {
                                System.out.println("color: " + ln.substring(ln.indexOf("#")));
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

            }
        }
        s.close();
        fx.setGradient(fg);
        return fx;
    }

    private static String clrStr(String c) {
        return c.length() == 1 ? "0" + c : c;
    }
}

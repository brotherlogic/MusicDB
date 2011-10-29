package uk.co.brotherlogic.mdb;

import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;

public class Settings
{
   private static String DEF = "uk.co.brotherlogic.mdb.";

   public static File getCDFileOutputDirectory()
   {
      File outputDir = null;

      // Try and retrieve the settings
      Preferences prefs = getPrefs();

      String outputdirstr = prefs.get(DEF + "cdoutputpath", "notfound");
      if (outputdirstr.equals("notfound"))
      {
         JFileChooser chooser = new JFileChooser();
         chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
         chooser.showOpenDialog(null);
         File f = chooser.getSelectedFile();
         prefs.put(DEF + "cdoutputpath", f.getAbsolutePath());
         outputdirstr = f.getAbsolutePath();
      }

      outputDir = new File(outputdirstr);
      if (outputDir.exists())
         return outputDir;
      else
         return null;
   }

   private static Preferences getPrefs()
   {
      return Preferences.userRoot();
   }
}

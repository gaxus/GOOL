package gool.test;

import gool.GOOLCompiler;
import gool.Settings;
import gool.generator.python.PythonPlatform;
import gool.parser.java.JavaParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;

import logger.Log;

import org.junit.Assert;
import org.junit.Test;

public class GoolTestTranslationFromJavaToPython {
	@Test
	public void SimpleForTranslationTest(){
		try {
			String javain = Settings.get("java_in_dir");

			String s ="public class SimpleFor {public static void main(String[] args) {int total = 0;for(int i = 0; i < 4; i++){total++;}System.out.println(total);}}";			
			FileManager.write(javain+"SimpleFor.java", s);

			runGoolCompiler();
		} catch(Exception e){
			e.printStackTrace();
		}

		String reference = Settings.get("python_ref_dir")+"SimpleFor.py";
		String output = Settings.get("python_out_dir")+"SimpleFor.py";
		try{
			Assert.assertTrue(FileManager.compareFile(reference, output));
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	@Test
	public void SimpleWhileTranslationTest(){
		try {
			String javain = Settings.get("java_in_dir");

			String s ="public class SimpleWhile {public static void main(String[] args) {int i = 0; int total = 0;	while(i < 4){total++;i++;}System.out.println(total);}}";			
			FileManager.write(javain+"SimpleWhile.java", s);

			runGoolCompiler();
		} catch(Exception e){
			e.printStackTrace();
		}
		
		String reference = Settings.get("python_ref_dir")+"SimpleWhile.py";
		String output = Settings.get("python_out_dir")+"SimpleWhile.py";
		try{
			Assert.assertTrue(FileManager.compareFile(reference, output));
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	@Test
	public void SimpleAddTranslationTest(){
		try {
			String javain = Settings.get("java_in_dir");

			String s ="public class SimpleAdd {	public static void main(String[] args) {		int n = 2+2;		System.out.println(n);	}}";			
			FileManager.write(javain+"SimpleAdd.java", s);

			runGoolCompiler();
		} catch(Exception e){
			e.printStackTrace();
		}
		
		String reference = Settings.get("python_ref_dir")+"SimpleAdd.py";
		String output = Settings.get("python_out_dir")+"SimpleAdd.py";
		try{
			Assert.assertTrue(FileManager.compareFile(reference, output));
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	@Test
	public void SimpleSubTranslationTest(){
		try {
			String javain = Settings.get("java_in_dir");

			String s ="public class SimpleSub {	public static void main(String[] args) {		int n = 6-2;		System.out.println(n);	}}";			
			FileManager.write(javain+"SimpleSub.java", s);

			runGoolCompiler();
		} catch(Exception e){
			e.printStackTrace();
		}
		
		String reference = Settings.get("python_ref_dir")+"SimpleSub.py";
		String output = Settings.get("python_out_dir")+"SimpleSub.py";
		try{
			Assert.assertTrue(FileManager.compareFile(reference, output));
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	@Test
	public void SimpleDivTranslationTest(){
		try {
			String javain = Settings.get("java_in_dir");

			String s ="public class SimpleDiv {	public static void main(String[] args) {	int n = 8/2;		System.out.println(n);	}}";			
			FileManager.write(javain+"SimpleDiv.java", s);

			runGoolCompiler();
		} catch(Exception e){
			e.printStackTrace();
		}
		
		String reference = Settings.get("python_ref_dir")+"SimpleDiv.py";
		String output = Settings.get("python_out_dir")+"SimpleDiv.py";
		try{
			Assert.assertTrue(FileManager.compareFile(reference, output));
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	@Test
	public void SimpleMultTranslationTest(){
		try {
			String javain = Settings.get("java_in_dir");

			String s ="public class SimpleMult {	public static void main(String[] args) {	int n = 2*2;		System.out.println(n);	}}";			
			FileManager.write(javain+"SimpleMult.java", s);

			runGoolCompiler();
		} catch(Exception e){
			e.printStackTrace();
		}
		
		String reference = Settings.get("python_ref_dir")+"SimpleMult.py";
		String output = Settings.get("python_out_dir")+"SimpleMult.py";
		try{
			Assert.assertTrue(FileManager.compareFile(reference, output));
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	@Test
	public void SimpleModTranslationTest(){
		try {
			String javain = Settings.get("java_in_dir");

			String s ="public class SimpleMod {	public static void main(String[] args) {int n = 4%5;	System.out.println(n);	}}";			
			FileManager.write(javain+"SimpleMod.java", s);

			runGoolCompiler();
		} catch(Exception e){
			e.printStackTrace();
		}
		
		String reference = Settings.get("python_ref_dir")+"SimpleMod.py";
		String output = Settings.get("python_out_dir")+"SimpleMod.py";
		try{
			Assert.assertTrue(FileManager.compareFile(reference, output));
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public void runGoolCompiler(){
		try {
			String javain = Settings.get("java_in_dir");
			File folder = new File(javain);


			Collection<File> files = getFilesInFolder(folder, "java");
			ArrayList<String> extToNCopy = new ArrayList<String>();

			BufferedReader g = null;
			try {
				File t = new File(javain + File.separator
						+ ".goolIgnore");
				FileReader f = new FileReader(t);
				g = new BufferedReader(f);
				String ligne;
				while ((ligne = g.readLine()) != null)
					extToNCopy.add(ligne);
			} catch (Exception e) {
				Log.e(e);
			} finally{
				g.close();
			}

			Collection<File> filesNonChange = getFilesInFolderNonExe(folder,
					extToNCopy);

			GOOLCompiler gc=new GOOLCompiler();

			// JAVA input -> PYTHON output
			gc.runGOOLCompiler(new JavaParser(), PythonPlatform.getInstance(filesNonChange), files);

			for(File f : files){
				f.delete();
			}
		} catch (Exception e) {
			Log.e(e);
		}
	}

	public static Collection<File> getFilesInFolder(File folder, String ext) {
		Collection<File> files = new ArrayList<File>();
		for (File f : folder.listFiles()) {
			if (f.isDirectory()) {
				files.addAll(getFilesInFolder(f, ext));
			} else if (f.getName().endsWith(ext)) {
				files.add(f);
			}
		}
		return files;
	}

	private static Collection<File> getFilesInFolderNonExe(File folder,
			ArrayList<String> ext) {

		Collection<File> files = new ArrayList<File>();

		for (File f : folder.listFiles()) {
			if (f.isDirectory()) {
				files.addAll(getFilesInFolderNonExe(f, ext));
			} else {
				boolean trouve = false;
				for (String s : ext) {
					if (f.getName().endsWith(s))
						trouve = true;
				}
				if (!trouve)
					files.add(f);
				trouve = false;

			}
		}
		return files;
	}
}

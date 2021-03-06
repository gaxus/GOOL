package gool.parser.cpp;

import gool.ParseGOOL;
import gool.Settings;
import gool.ast.core.ClassDef;
import gool.generator.common.Platform;
import gool.recognizer.cpp.CppRecognizer;
import gool.recognizer.cpp.CppRecognizerImport;
import gool.recognizer.cpp.ast.ASTCppNode;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.gnu.cpp.GPPLanguage;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.core.parser.DefaultLogService;
import org.eclipse.cdt.core.parser.FileContent;
import org.eclipse.cdt.core.parser.IParserLogService;
import org.eclipse.cdt.core.parser.IScannerInfo;
import org.eclipse.cdt.core.parser.IncludeFileContentProvider;
import org.eclipse.cdt.core.parser.ScannerInfo;

/**
 * This class parses concrete Cpp into abstract GOOL. For this purpose it
 * relies on Eclipse's CDT parser.
 */
public class CppParser extends ParseGOOL {

	/**
	 * The real parser GOOL, which transforms a C++ AST into a GOOL AST.
	 * @param defaultPlatform
	 * 		: The default platform.
	 * @param translationUnits
	 * 		: The collection of transition units which can be transformed into GOOL AST.
	 * @param dependencies
	 * 		: List of dependencies files.
	 * @param visitor
	 * 		: The visitor, which create the GOOL AST (ie. the Recognizer).
	 * @return
	 * 		The GOOL AST associated to the transition units.
	 * @throws Exception
	 */
	public static Collection<ClassDef> parseGool(Platform defaultPlatform,
			Collection<IASTTranslationUnit> translationUnits,
			List<File> dependencies, CppRecognizer visitor) throws Exception {
		if (visitor == null) {
			throw new IllegalArgumentException("The gool visitor is null.");
		}
		
		visitor.setDefaultPlatform(defaultPlatform);
		
		for(IASTTranslationUnit translationUnit : translationUnits){
			ASTCppNode.transforme(translationUnit).accept(visitor,null);
		}
		
		for (ClassDef classDef : visitor.getGoolClasses()) {
			classDef.getPlatform().registerCustomDependency(
					classDef.getName(), new ClassDef(classDef.getName() + ".h"));
		}
		
		return visitor.getGoolClasses();
	}
	
	
	@Override
	public Collection<ClassDef> parseGool(Platform defaultPlatform, String input)
			throws Exception {
		Collection<IASTTranslationUnit> translationUnits = new ArrayList<IASTTranslationUnit>();
		IASTTranslationUnit tu = creatIASTTranslationUnit(input) ;

		// First step : indirect input usage case.
		CppRecognizerImport visitImport = new CppRecognizerImport() ;
		ASTCppNode.transforme(tu).accept(visitImport,null);
		translationUnits.add(tu);
		for(String dep : visitImport.getFilesAdd())
			translationUnits.add(creatIASTTranslationUnit(
					FileContent.createForExternalFileLocation(new File(dep).getPath())
					));
		
		// transformed into a GOOL AST : second step. Parse files in folder cpp_in_tmp.
		return parseGool(defaultPlatform, translationUnits, null, new CppRecognizer());
	}

	@Override
	public Collection<ClassDef> parseGool(Platform defaultPlatform,
			Collection<? extends File> inputFiles) throws Exception {
		Collection<IASTTranslationUnit> translationUnits = new ArrayList<IASTTranslationUnit>();

		// First step : indirect input usage case.
		for(File input : inputFiles)
		{
			IASTTranslationUnit tu = creatIASTTranslationUnit(
					FileContent.createForExternalFileLocation(input.getPath())
					);
			
			CppRecognizerImport visitImport = new CppRecognizerImport() ;
			ASTCppNode.transforme(tu).accept(visitImport,null);
			translationUnits.add(tu);
			for(String dep : visitImport.getFilesAdd())
				translationUnits.add(creatIASTTranslationUnit(
						FileContent.createForExternalFileLocation(new File(dep).getPath())
			));
		}
		
		// transformed into a GOOL AST : second step. Parse files in folder cpp_in_tmp.
		return parseGool(defaultPlatform, translationUnits, null, new CppRecognizer());
	}
	
	/**
	 * To parse C++ as a file.
	 * @param input
	 * 		: The code C++ in input as a file.
	 * @return
	 * 		A transition unit about the input file.
	 * @throws Exception
	 */
	public static IASTTranslationUnit creatIASTTranslationUnit(FileContent input) throws Exception {
		Map<String, String> macroDefinitions = new HashMap<String, String>();
		String[] includeSearchPaths = Settings.get("cpp_in_libraries").split(" ");
		IScannerInfo si = new ScannerInfo(macroDefinitions, includeSearchPaths);
		IncludeFileContentProvider ifcp = IncludeFileContentProvider.getEmptyFilesProvider();
		IIndex idx = null;
		int options = ILanguage.OPTION_IS_SOURCE_UNIT;
		IParserLogService log = new DefaultLogService();
		return GPPLanguage.getDefault().getASTTranslationUnit(input, si, ifcp, idx, options, log);
	}
	
	/**
	 * To parse C++ as string.
	 * @param input
	 * 		: The code C++ in input as string.
	 * @return
	 * 		A transition unit about the input string.
	 * @throws Exception
	 */
	public static IASTTranslationUnit creatIASTTranslationUnit(String input) throws Exception {
		FileContent fc = FileContent.create("Test.cpp", input.toCharArray());
		return creatIASTTranslationUnit(fc);
	}

}
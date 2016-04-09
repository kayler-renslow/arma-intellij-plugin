package com.kaylerrenslow.a3plugin.lang.shared.stringtable;

import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.indexing.FileBasedIndex;
import com.intellij.util.xml.DomManager;
import com.kaylerrenslow.a3plugin.Plugin;
import com.kaylerrenslow.a3plugin.lang.shared.stringtable.dom.Container;
import com.kaylerrenslow.a3plugin.lang.shared.stringtable.dom.Key;
import com.kaylerrenslow.a3plugin.lang.shared.stringtable.dom.Package;
import com.kaylerrenslow.a3plugin.lang.shared.stringtable.dom.StringtableProject;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Kayler
 * Stringtable object to fetch data inside stringtable.xml
 * Created on 04/08/2016.
 */
public class Stringtable {
	private final VirtualFile virtualFile;

	private Stringtable(VirtualFile virtualFile){
		this.virtualFile = virtualFile;
	}

	public VirtualFile getVirtualFile(){
		return this.virtualFile;
	}

	/** Get the root tag*/
	private StringtableProject getStringtableProject(Project project){
		XmlFile xmlFile = (XmlFile) PsiManager.getInstance(project).findFile(this.virtualFile).getOriginalFile();
		DomManager manager = DomManager.getDomManager(project);

		StringtableProject stringtableProject = (StringtableProject) manager.getFileElement(xmlFile).getRootElement();
		return stringtableProject;
	}

	/** Gets all keys inside stringtable.xml
	 * @param project project
	 * @return array of all keys
	 */
	public StringtableKey[] getAllKeysValues(Project project){
		StringtableProject stringtableProject = getStringtableProject(project);
		List<StringtableKey> keysVals = new ArrayList<>();

		List<Package> packages = stringtableProject.getPackages();

		for (Package stringtablePackage : packages) {
			List<Container> containers = stringtablePackage.getContainers();
			for (Container container : containers) {
				List<Key> keys = container.getKeys();
				for(Key key : keys){
					keysVals.add(new StringtableKey(key, container));
				}
			}
		}

		return keysVals.toArray(new StringtableKey[keysVals.size()]);
	}

	/** Get documentation html for the given stringtable key
	 * @param xmlKey key
	 * @return html showing all languages' values
	 */
	@Nullable
	public static String getKeyDoc(XmlTag xmlKey) {
		if(!xmlKey.getName().equalsIgnoreCase("key")){
			return null;
		}
		XmlTag[] childTags = xmlKey.getSubTags();

		String format = Plugin.resources.getString("lang.shared.stringtable.language_documentation_format");
		String doc = "";
		for(XmlTag childTag: childTags){
			doc+= String.format(format, childTag.getName(), childTag.getText());
		}
		return doc;
	}

	/** Creates and returns a new Stringtable instance
	 * @param module module
	 * @return new instance
	 * @throws FileNotFoundException when stringtable.xml doens't exist
	 */
	public static Stringtable load(Module module) throws FileNotFoundException {
		VirtualFile stringtableXmlVf = null;
		Collection<VirtualFile> files = FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, XmlFileType.INSTANCE, module.getModuleContentScope());
		for (VirtualFile file : files) {
			if (file.getName().equalsIgnoreCase("stringtable.xml")) {
				stringtableXmlVf = file;
			}
		}
		if (stringtableXmlVf == null) {
			throw new FileNotFoundException("stringtable.xml wasn't found");
		}
		Stringtable instance = new Stringtable(stringtableXmlVf);
		return instance;
	}
}

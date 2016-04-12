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
	private final Module module;

	private Stringtable(VirtualFile virtualFile, Module module){
		this.virtualFile = virtualFile;
		this.module = module;
	}

	public VirtualFile getVirtualFile(){
		return this.virtualFile;
	}

	/** Get the root tag*/
	private StringtableProject getStringtableProject(){
		XmlFile xmlFile = (XmlFile) PsiManager.getInstance(module.getProject()).findFile(this.virtualFile).getOriginalFile();
		DomManager manager = DomManager.getDomManager(module.getProject());

		StringtableProject stringtableProject = (StringtableProject) manager.getFileElement(xmlFile).getRootElement();
		return stringtableProject;
	}

	/** Gets all keys inside stringtable.xml
	 * @return array of all keys
	 */
	public StringtableKey[] getAllKeysValues(){
		StringtableProject stringtableProject = getStringtableProject();
		List<StringtableKey> keysVals = new ArrayList<>();

		List<Package> packages = stringtableProject.getPackages();
		List<Key> keys = stringtableProject.getKeys();
		addKeys(keysVals, keys, null);
		for (Package stringtablePackage : packages) {
			List<Container> containers = stringtablePackage.getContainers();
			keys = stringtablePackage.getKeys();
			addKeys(keysVals, keys, null);
			for (Container container : containers) {
				keys = container.getKeys();
				addKeys(keysVals, keys, container);
			}
		}

		return keysVals.toArray(new StringtableKey[keysVals.size()]);
	}

	private void addKeys(List<StringtableKey> keysVals, List<Key> keys, Container container) {
		for(Key key : keys){
			keysVals.add(new StringtableKey(key, container));
		}
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
		Stringtable instance = new Stringtable(stringtableXmlVf, module);
		return instance;
	}
}

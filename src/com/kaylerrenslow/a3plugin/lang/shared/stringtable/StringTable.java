package com.kaylerrenslow.a3plugin.lang.shared.stringtable;

import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomManager;
import com.kaylerrenslow.a3plugin.Plugin;
import com.kaylerrenslow.a3plugin.lang.shared.stringtable.dom.Container;
import com.kaylerrenslow.a3plugin.lang.shared.stringtable.dom.Key;
import com.kaylerrenslow.a3plugin.lang.shared.stringtable.dom.Package;
import com.kaylerrenslow.a3plugin.lang.shared.stringtable.dom.StringTableProject;
import com.kaylerrenslow.a3plugin.util.PluginUtil;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * StringTable object to fetch data inside stringtable.xml
 *
 * @author Kayler
 * @since 04/08/2016
 */
public class StringTable {
	private final VirtualFile virtualFile;
	private final Module module;

	private StringTable(VirtualFile virtualFile, Module module) {
		this.virtualFile = virtualFile;
		this.module = module;
	}

	public VirtualFile getVirtualFile() {
		return this.virtualFile;
	}

	/**
	 * Get the root tag
	 */
	@Nullable
	private StringTableProject getStringTableProject() {
		PsiFile file = PsiManager.getInstance(module.getProject()).findFile(this.virtualFile);
		if (file == null) {
			return null;
		}
		XmlFile xmlFile = (XmlFile) file.getOriginalFile();
		DomManager manager = DomManager.getDomManager(module.getProject());

		return (StringTableProject) manager.getFileElement(xmlFile).getRootElement();
	}

	/**
	 * Gets all keys inside stringtable.xml
	 *
	 * @return array of all keys
	 */
	public StringtableKey[] getAllKeysValues() {
		StringTableProject stringtableProject = getStringTableProject();
		if (stringtableProject == null) {
			return StringtableKey.EMPTY;
		}
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
		for (Key key : keys) {
			keysVals.add(new StringtableKey(key, container));
		}
	}

	/**
	 * Get documentation html for the given stringtable key
	 *
	 * @param xmlKey key
	 * @return html showing all languages' values
	 */
	@Nullable
	public static String getKeyDoc(XmlTag xmlKey) {
		if (!xmlKey.getName().equalsIgnoreCase("key")) {
			return null;
		}
		XmlTag[] childTags = xmlKey.getSubTags();

		String format = Plugin.resources.getString("lang.shared.stringtable.language_documentation_format");
		String doc = "";
		for (XmlTag childTag : childTags) {
			doc += String.format(format, childTag.getName(), childTag.getText());
		}
		return doc;
	}

	/**
	 * Creates and returns a new StringTable instance
	 *
	 * @param module module
	 * @return new instance
	 * @throws FileNotFoundException when stringtable.xml doens't exist
	 */
	public static StringTable load(Module module) throws FileNotFoundException {
		VirtualFile stringtableXmlVf = PluginUtil.findFileInModuleByName("stringtable.xml", module, XmlFileType.INSTANCE, true);
		if (stringtableXmlVf == null) {
			throw new FileNotFoundException("stringtable.xml wasn't found");
		}
		return new StringTable(stringtableXmlVf, module);
	}
}

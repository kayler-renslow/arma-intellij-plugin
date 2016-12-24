package com.kaylerrenslow.a3plugin.lang.shared.stringtable.psi;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.Key;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.*;
import com.intellij.util.xml.reflect.AbstractDomChildrenDescription;
import com.intellij.util.xml.reflect.DomGenericInfo;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFArrayEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author Kayler
 * @since 06/26/2016
 */
public class SQFArrayEntryDomValue implements GenericDomValue<com.kaylerrenslow.a3plugin.lang.shared.stringtable.dom.Key> {
	private final SQFArrayEntry entry;

	public SQFArrayEntryDomValue(SQFArrayEntry entry) {
		this.entry = entry;
	}

	@NotNull
	@Override
	public Converter<com.kaylerrenslow.a3plugin.lang.shared.stringtable.dom.Key> getConverter() {
		return new DomResolveConverter<>(com.kaylerrenslow.a3plugin.lang.shared.stringtable.dom.Key.class);
	}

	@Override
	public void setStringValue(String value) {

	}

	@Override
	public void setValue(com.kaylerrenslow.a3plugin.lang.shared.stringtable.dom.Key value) {

	}

	@Override
	public String getRawText() {
		return null;
	}

	@Override
	public XmlTag getXmlTag() {
		return null;
	}

	@Nullable
	@Override
	public XmlElement getXmlElement() {
		return null;
	}

	@Override
	public DomElement getParent() {
		return null;
	}

	@Override
	public XmlTag ensureTagExists() {
		return null;
	}

	@Override
	public XmlElement ensureXmlElementExists() {
		return null;
	}

	@Override
	public void undefine() {

	}

	@Override
	public boolean isValid() {
		return false;
	}

	@Override
	public boolean exists() {
		return false;
	}

	@NotNull
	@Override
	public DomGenericInfo getGenericInfo() {
		return null;
	}

	@NotNull
	@Override
	public String getXmlElementName() {
		return null;
	}

	@NotNull
	@Override
	public String getXmlElementNamespace() {
		return null;
	}

	@Nullable
	@Override
	public String getXmlElementNamespaceKey() {
		return null;
	}

	@Override
	public void accept(DomElementVisitor visitor) {

	}

	@Override
	public void acceptChildren(DomElementVisitor visitor) {

	}

	@NotNull
	@Override
	public DomManager getManager() {
		return null;
	}

	@NotNull
	@Override
	public Type getDomElementType() {
		return null;
	}

	@Override
	public AbstractDomChildrenDescription getChildDescription() {
		return null;
	}

	@NotNull
	@Override
	public DomNameStrategy getNameStrategy() {
		return null;
	}

	@NotNull
	@Override
	public ElementPresentation getPresentation() {
		return null;
	}

	@Override
	public GlobalSearchScope getResolveScope() {
		return null;
	}

	@Nullable
	@Override
	public <T extends DomElement> T getParentOfType(Class<T> requiredClass, boolean strict) {
		return null;
	}

	@Nullable
	@Override
	public Module getModule() {
		return null;
	}

	@Override
	public void copyFrom(DomElement other) {

	}

	@Override
	public <T extends DomElement> T createMockCopy(boolean physical) {
		return null;
	}

	@Override
	public <T extends DomElement> T createStableCopy() {
		return null;
	}

	@Nullable
	@Override
	public <T> T getUserData(@NotNull Key<T> key) {
		return null;
	}

	@Override
	public <T> void putUserData(@NotNull Key<T> key, @Nullable T value) {

	}

	@Nullable
	@Override
	public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
		return null;
	}

	@Nullable
	@Override
	public String getStringValue() {
		return null;
	}

	@Nullable
	@Override
	public com.kaylerrenslow.a3plugin.lang.shared.stringtable.dom.Key getValue() {
		return null;
	}
}

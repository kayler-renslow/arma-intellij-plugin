package com.kaylerrenslow.a3plugin.lang.sqf.editor;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.tree.IElementType;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * @author Kayler
 * FoldingBuilder implementation for SQF language
 * Created on 03/20/2016.
 */
public class SQFFoldingBuilder implements FoldingBuilder{
	@NotNull
	@Override
	public FoldingDescriptor[] buildFoldRegions(@NotNull ASTNode node, @NotNull Document document) {
		ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
		collectFoldingDescriptors(node, document, descriptors);
		return descriptors.toArray(new FoldingDescriptor[descriptors.size()]);
	}

	private void collectFoldingDescriptors(ASTNode node, Document document, ArrayList<FoldingDescriptor> descriptors) {
		IElementType type = node.getElementType();
		if ((type == SQFTypes.SWITCH_CASE || type == SQFTypes.CODE_BLOCK || type == SQFTypes.BLOCK_COMMENT || type == SQFTypes.ARRAY_VAL) && spansMultipleLines(node, document)){
			descriptors.add(new FoldingDescriptor(node, node.getTextRange()));
		}
		for (ASTNode child : node.getChildren(null)){
			collectFoldingDescriptors(child, document, descriptors);
		}
	}

	private boolean spansMultipleLines(ASTNode node, Document document) {
		TextRange range = node.getTextRange();
		return document.getLineNumber(range.getStartOffset()) < document.getLineNumber(range.getEndOffset());
	}

	@Nullable
	@Override
	public String getPlaceholderText(@NotNull ASTNode node) {
		IElementType type = node.getElementType();
		if(type == SQFTypes.SWITCH_CASE || type == SQFTypes.CODE_BLOCK){
			return "{...}";
		}
		if(type == SQFTypes.BLOCK_COMMENT){
			return "/*...*/";
		}
		if(type == SQFTypes.ARRAY_VAL){
			return "[...]";
		}
		return "...";
	}

	@Override
	public boolean isCollapsedByDefault(@NotNull ASTNode node) {
		return false;
	}
}

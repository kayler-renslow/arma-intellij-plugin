package com.kaylerrenslow.a3plugin.lang.header.editor;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.tree.IElementType;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Created by Kayler on 03/20/2016.
 */
public class HeaderFoldingBuilder implements FoldingBuilder{
	@NotNull
	@Override
	public FoldingDescriptor[] buildFoldRegions(@NotNull ASTNode node, @NotNull Document document) {
		ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
		collectFoldingDescriptors(node, document, descriptors);
		return descriptors.toArray(new FoldingDescriptor[descriptors.size()]);
	}

	private void collectFoldingDescriptors(ASTNode node, Document document, ArrayList<FoldingDescriptor> descriptors) {
		IElementType type = node.getElementType();
		if ((type == HeaderTypes.ARRAY || type == HeaderTypes.CLASS_CONTENT || type == HeaderTypes.BLOCK_COMMENT) && spansMultipleLines(node, document)){
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
		if(type == HeaderTypes.ARRAY || type == HeaderTypes.CLASS_CONTENT ){
			return "{...}";
		}
		if(type == HeaderTypes.BLOCK_COMMENT){
			return "/*...*/";
		}
		return "...";
	}

	@Override
	public boolean isCollapsedByDefault(@NotNull ASTNode node) {
		return false;
	}
}

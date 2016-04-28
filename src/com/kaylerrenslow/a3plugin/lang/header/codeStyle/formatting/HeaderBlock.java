package com.kaylerrenslow.a3plugin.lang.header.codeStyle.formatting;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.TokenType;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderClassContent;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderClassDeclaration;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderTypes;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderPsiUtilForGrammar;
import com.kaylerrenslow.a3plugin.lang.shared.PsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kayler
 *         AbstractBlock implementation for Header language
 *         Created on 03/18/2016.
 */
class HeaderBlock implements ASTBlock {

	private final CodeStyleSettings settings;

	private final PsiElement myElement;
	private final SpacingBuilder spacingBuilder;

	private Indent myIndent;
	private ASTNode myNode;
	private Wrap myWrap;

	HeaderBlock(@NotNull ASTNode node, @Nullable Wrap wrap, @NotNull Indent indent, CodeStyleSettings settings, @NotNull SpacingBuilder spacingBuilder) {
		this.myNode = node;
		this.myWrap = wrap;
		this.myIndent = indent;
		this.myElement = node.getPsi();

		this.spacingBuilder = spacingBuilder;
		this.settings = settings;

		if (PsiUtil.isOfElementType(node, TokenType.WHITE_SPACE)) {
			System.out.println(node.getText());
		}
	}


	@NotNull
	@Override
	public TextRange getTextRange() {
		return myNode.getTextRange();
	}

	@NotNull
	@Override
	public List<Block> getSubBlocks() {
		List<Block> blocks = new ArrayList<>();

		if (isLeaf()) {
			return blocks;
		}

		ASTNode childNode = this.myNode.getFirstChildNode();
		if (childNode.getElementType() == TokenType.WHITE_SPACE) {
			childNode = PsiUtil.getNextSiblingNotWhitespace(childNode);
		}

		//note to self: for each block, it needs a new instance of indent and wrap

		while (childNode != null) {
			Wrap childWrap = Wrap.createWrap(WrapType.NONE, false);
			Indent childIndent = Indent.getNoneIndent();
			if (myElement instanceof HeaderClassDeclaration) {
				if (!HeaderPsiUtilForGrammar.bracesAreEmpty((HeaderClassDeclaration) myElement)) {
					if (childNode.getElementType() == HeaderTypes.CLASS_CONTENT) {
						childWrap = Wrap.createWrap(WrapType.ALWAYS, true);
					}
				}
			}
			if (myElement instanceof HeaderClassContent) {
				if (childNode.getElementType() == HeaderTypes.FILE_ENTRIES) {
					childIndent = Indent.getNormalIndent();
				}
			}
			if (childNode.getElementType() == HeaderTypes.FILE_ENTRY) {
				childWrap = Wrap.createWrap(WrapType.ALWAYS, true);
			}

			Block b = new HeaderBlock(childNode, childWrap, childIndent, this.settings, this.spacingBuilder);
			blocks.add(b);
			childNode = PsiUtil.getNextSiblingNotWhitespace(childNode);
		}

		return blocks;
	}

	@Nullable
	@Override
	public Wrap getWrap() {
		return myWrap;
	}

	@Override
	public Indent getIndent() {
		return myIndent;
	}

	@Nullable
	@Override
	public Alignment getAlignment() {
		return null;
	}


	@Nullable
	@Override
	public Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
		return spacingBuilder.getSpacing(this, child1, child2);
	}

	@NotNull
	@Override
	public ChildAttributes getChildAttributes(int newChildIndex) {
		return ChildAttributes.DELEGATE_TO_NEXT_CHILD;
	}

	@Override
	public boolean isIncomplete() {
		return false;
	}

	@Override
	public boolean isLeaf() {
		return this.getNode().getFirstChildNode() == null;
	}

	@NotNull
	@Override
	public ASTNode getNode() {
		return this.myNode;
	}
}

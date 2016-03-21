package com.kaylerrenslow.a3plugin.lang.header.codeStyle.formatting;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.TokenType;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.kaylerrenslow.a3plugin.lang.psiUtil.PsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kayler on 03/18/2016.
 */
public class HeaderBlock extends AbstractBlock{

	private SpacingBuilder spacingBuilder;

	protected HeaderBlock(@NotNull ASTNode node, @Nullable Wrap wrap, @Nullable Alignment alignment, SpacingBuilder spacingBuilder) {
		super(node, wrap, alignment);
		this.spacingBuilder = spacingBuilder;
	}

	@Override
	protected List<Block> buildChildren() {
		List<Block> blocks = new ArrayList<>();

		ASTNode childNode = this.myNode.getFirstChildNode();
		while(childNode != null){
			if(!PsiUtil.isOfElementType(childNode, TokenType.WHITE_SPACE)){
				Block b = new HeaderBlock(childNode, Wrap.createWrap(WrapType.NONE, false), Alignment.createAlignment(), spacingBuilder);
				blocks.add(b);
			}
			childNode = childNode.getTreeNext();
		}

		return blocks;
	}

	@Override
	public Indent getIndent() {
		return super.getIndent();
	}

	@Nullable
	@Override
	public Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
		return spacingBuilder.getSpacing(this, child1, child2);
	}

	@Override
	public boolean isLeaf() {
		return this.myNode.getFirstChildNode() == null;
	}
}

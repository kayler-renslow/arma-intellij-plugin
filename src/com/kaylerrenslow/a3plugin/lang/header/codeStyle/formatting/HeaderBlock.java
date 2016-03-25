package com.kaylerrenslow.a3plugin.lang.header.codeStyle.formatting;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.TokenType;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.kaylerrenslow.a3plugin.lang.header.HeaderFileType;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderClassContent;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderClassDeclaration;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderTypes;
import com.kaylerrenslow.a3plugin.lang.shared.PsiUtil;
import com.kaylerrenslow.a3plugin.lang.shared.formatting.CodeStyleUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kayler on 03/18/2016.
 */
public class HeaderBlock extends AbstractBlock{

	private final CodeStyleSettings settings;

	private final Indent myIndent;
	private final Alignment myAlignment;
	private final Wrap myWrap;

	private final SpacingBuilder spacingBuilder;

	private final PsiElement myElement;
	private boolean allowChildrenToIndent;

	private final int tabSize;

	protected HeaderBlock(@NotNull ASTNode node, @Nullable Wrap wrap, @Nullable Alignment alignment, @NotNull Indent indent, CodeStyleSettings settings, @NotNull SpacingBuilder spacingBuilder) {
		this(node, wrap, alignment, indent, true, settings, spacingBuilder);
	}

	protected HeaderBlock(@NotNull ASTNode node, @Nullable Wrap wrap, @Nullable Alignment alignment, @NotNull Indent indent, boolean allowChildrenToIndent, CodeStyleSettings settings, @NotNull SpacingBuilder spacingBuilder) {
		super(node, wrap, alignment);
		this.spacingBuilder = spacingBuilder;
		this.settings = settings;
		this.myIndent = indent;
		this.myAlignment = alignment;
		this.myWrap = wrap;
		this.myElement = node.getPsi();
		this.allowChildrenToIndent = allowChildrenToIndent;
		this.tabSize = settings.getTabSize(HeaderFileType.INSTANCE);
	}

	@Override
	protected List<Block> buildChildren() {
		List<Block> blocks = new ArrayList<>();

		ASTNode childNode = this.myNode.getFirstChildNode();

		Indent childIndent = Indent.getNoneIndent();
		Wrap childWrap = null;

		boolean allowGrandChildrenToIndent = true;
		while(childNode != null){
			boolean childIndentSet = false;
			if(PsiUtil.isOfElementType(childNode, TokenType.WHITE_SPACE)){
				childNode = childNode.getTreeNext();
				continue;
			}

			if (myElement instanceof HeaderClassDeclaration){
				if(PsiUtil.isOfElementType(childNode, HeaderTypes.LBRACE)){
					if(CodeStyleUtil.ClassBraceStyle.endOfLine(settings)){
						childWrap = null;
					}else if(CodeStyleUtil.ClassBraceStyle.nextLine(settings)){
						childWrap = Wrap.createWrap(WrapType.ALWAYS, true);
						childIndentSet = true;
						childIndent = Indent.getNoneIndent();
					}else if(CodeStyleUtil.ClassBraceStyle.nextLineIfWrapped(settings)){
						childWrap = Wrap.createWrap(WrapType.NORMAL, true);
						childIndentSet = true;
						childIndent = Indent.getNoneIndent();
					}else if(CodeStyleUtil.ClassBraceStyle.nextLineShifted(settings)){
						childWrap = Wrap.createWrap(WrapType.ALWAYS, true);
						childIndent = Indent.getSpaceIndent(tabSize);
						allowGrandChildrenToIndent = false;
					}else if(CodeStyleUtil.ClassBraceStyle.nextLineEachShifted(settings)){
						childWrap = Wrap.createWrap(WrapType.ALWAYS, true);
						childIndentSet = true;
						childIndent = Indent.getSpaceIndent(tabSize);
					}
				}
			}
			if(myElement instanceof HeaderClassContent){
				if(!childIndentSet && this.allowChildrenToIndent){ //{ is on next line
					childIndent = Indent.getSpaceIndent(tabSize);
				}
			}

			Block b = new HeaderBlock(childNode, childWrap, this.myAlignment, childIndent, allowGrandChildrenToIndent, this.settings, this.spacingBuilder);
			blocks.add(b);
			childNode = childNode.getTreeNext();
		}

		return blocks;
	}

	@Override
	public Indent getIndent() {
		return myIndent;
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

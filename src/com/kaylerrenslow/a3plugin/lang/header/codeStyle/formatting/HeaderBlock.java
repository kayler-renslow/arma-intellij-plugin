package com.kaylerrenslow.a3plugin.lang.header.codeStyle.formatting;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.TokenType;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.intellij.psi.tree.IElementType;
import com.kaylerrenslow.a3plugin.lang.header.HeaderFileType;
import com.kaylerrenslow.a3plugin.lang.header.psi.*;
import com.kaylerrenslow.a3plugin.lang.shared.PsiUtil;
import com.kaylerrenslow.a3plugin.lang.shared.formatting.BlockFormatter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.kaylerrenslow.a3plugin.lang.shared.formatting.CodeStyleUtil.*;

/**
 * @author Kayler
 * AbstractBlock implementation for Header language
 * Created on 03/18/2016.
 */
public class HeaderBlock extends AbstractBlock{

	private final CodeStyleSettings settings;

	private final Indent myIndent;

	private final SpacingBuilder spacingBuilder;

	private final PsiElement myElement;
	private final Indent childIndent;
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
		this.myElement = node.getPsi();
		this.allowChildrenToIndent = allowChildrenToIndent;
		this.tabSize = settings.getTabSize(HeaderFileType.INSTANCE);

		if (myElement instanceof HeaderClassContent ){ //for when you are inside class content and press enter.
			this.childIndent = Indent.getNormalIndent(); // this will automatically align your cursor to the correct indent
		}else {
			this.childIndent = myIndent;
		}

	}

	@Override
	protected List<Block> buildChildren() {
		List<Block> blocks = new ArrayList<>();

		ASTNode childNode = this.myNode.getFirstChildNode();

		Indent childIndent = null;
		Wrap childWrap = null;
		Alignment childAlignment = null;

		boolean allowGrandChildrenToIndent = true;
		final BlockFormatter classFormatter = new HeaderClassContentBlockFormatter();
		final BlockFormatter arrayFormatter = new ArrayBlockFormatter();
		BlockFormatter f = null;
		int childNum = 0;
		while (childNode != null){
			if (PsiUtil.isOfElementType(childNode, TokenType.WHITE_SPACE)){
				childNode = childNode.getTreeNext();
				continue;
			}

			if (myElement instanceof HeaderClassContent){
				f = classFormatter;
			}
			if(myElement instanceof HeaderArray || myElement instanceof HeaderArrayBody){
				f = arrayFormatter;
			}
			if(f != null){
				f.format(myElement, childNode, childNum, allowChildrenToIndent);
				allowGrandChildrenToIndent = f.allowGrandChildrenToIndent;
				childIndent = f.childIndent;
				childWrap = f.childWrap;
				childAlignment = f.childAlignment;
			}
			if (childIndent == null){
				childIndent = Indent.getNoneIndent();
			}
			if (childNode.getPsi() instanceof HeaderBasicAssignment || childNode.getPsi() instanceof HeaderExpression){
				childWrap = Wrap.createWrap(WrapType.NONE, true);
			}

			Block b = new HeaderBlock(childNode, childWrap, childAlignment, childIndent, allowGrandChildrenToIndent, this.settings, this.spacingBuilder);
			blocks.add(b);
			childNode = childNode.getTreeNext();
			f = null;
			childNum++;
		}

		return blocks;
	}

	@Override
	public Indent getIndent() {
		return myIndent;
	}

	@Nullable
	@Override
	protected Indent getChildIndent() {
		return this.childIndent;
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


	private class HeaderClassContentBlockFormatter extends BlockFormatter{

		@Override
		protected void formatNode(PsiElement currentElement, ASTNode childNode, int childNum, boolean allowChildrenToIndent) {
			if (childNode.getElementType() == HeaderTypes.LBRACE){
				if (ClassBraceStyle.endOfLine(settings)){
					childWrap = null;
				}else if (ClassBraceStyle.nextLine(settings)){
					childWrap = Wrap.createWrap(WrapType.ALWAYS, true);
					childIndent = Indent.getNoneIndent();
				}else if (ClassBraceStyle.nextLineIfWrapped(settings)){
					childWrap = Wrap.createWrap(WrapType.NORMAL, true);
					childIndent = Indent.getNoneIndent();
				}else if (ClassBraceStyle.nextLineShifted(settings)){
					childWrap = Wrap.createWrap(WrapType.ALWAYS, true);
					childIndent = Indent.getNormalIndent();
					allowGrandChildrenToIndent = false;
				}else if (ClassBraceStyle.nextLineEachShifted(settings)){
					childWrap = Wrap.createWrap(WrapType.ALWAYS, true);
					childIndent = Indent.getNormalIndent();
				}
			}else if (childNode.getElementType() == HeaderTypes.RBRACE){
				if (ClassBraceStyle.endOfLine(settings) || ClassBraceStyle.nextLine(settings)){
					childWrap = Wrap.createWrap(WrapType.ALWAYS, true);
					childIndent = Indent.getNoneIndent();
				}else if (ClassBraceStyle.nextLineIfWrapped(settings)){
					childWrap = Wrap.createWrap(WrapType.NORMAL, true);
					childIndent = Indent.getNoneIndent();
				}else if (ClassBraceStyle.nextLineShifted(settings)){
					childWrap = Wrap.createWrap(WrapType.ALWAYS, true);
					childIndent = Indent.getNormalIndent();
					allowGrandChildrenToIndent = false;
				}else if (ClassBraceStyle.nextLineEachShifted(settings)){
					childWrap = Wrap.createWrap(WrapType.ALWAYS, true);
					childIndent = Indent.getNormalIndent();
				}
			}
			if (childIndent == null && allowChildrenToIndent){
				if (ClassBraceStyle.nextLineEachShifted(settings)){
					childIndent = Indent.getSpaceIndent(tabSize * 2);
				}else {
					childIndent = Indent.getNormalIndent();
				}
			}
		}

	}

	private class ArrayBlockFormatter extends BlockFormatter{

		private Wrap nextChildWrap = null;
		private IElementType nextChildWrapType;

		@Override
		protected void formatNode(PsiElement currentElement, ASTNode childNode, int childNum, boolean allowChildrenToIndent) {
			if(currentElement instanceof HeaderArray){
				if(childNode.getElementType() == HeaderTypes.LBRACE){
					if(ArrayInitializerStyle.newLineAfterLBrace(settings)){
						nextChildWrap = Wrap.createWrap(WrapType.ALWAYS, true);
						nextChildWrapType = HeaderTypes.ARRAY_BODY;
						childIndent = Indent.getNoneIndent();
					}
				}else if(childNode.getElementType() == HeaderTypes.RBRACE){
					if(ArrayInitializerStyle.newLineAfterRBrace(settings)){
						childWrap = Wrap.createWrap(WrapType.ALWAYS, true);
						childIndent = Indent.getNoneIndent();
					}
				}
			}
			if(currentElement instanceof HeaderArrayBody && childNode.getElementType() != HeaderTypes.COMMA){
				if(ArrayInitializerStyle.doNotWrap(settings)){
					return;
				}else if(ArrayInitializerStyle.alwaysWrap(settings)){
					if((ArrayInitializerStyle.newLineAfterLBrace(settings) && childNum == 0) || childNum > 0){
						childWrap = Wrap.createWrap(WrapType.ALWAYS, true);
						childIndent = Indent.getNormalIndent();
					}
				}else if(ArrayInitializerStyle.chopDownIfLong(settings)){
					childWrap = Wrap.createWrap(WrapType.CHOP_DOWN_IF_LONG, true);
					childIndent = Indent.getNormalIndent();
				}else if(ArrayInitializerStyle.wrapIfLong(settings)){
					childWrap = Wrap.createWrap(WrapType.NORMAL, true);
					childIndent = Indent.getNormalIndent();
				}
			}
			if(nextChildWrap != null && childNode.getElementType() == nextChildWrapType){
				childWrap = nextChildWrap;
				nextChildWrap = null;
			}

			if(childIndent == null){
				childIndent = Indent.getNormalIndent();
			}
		}
	}
}

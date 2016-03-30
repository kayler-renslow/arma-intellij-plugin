package com.kaylerrenslow.a3plugin.lang.header.editor;

import com.intellij.lang.Commenter;
import org.jetbrains.annotations.Nullable;

/**
 * @author Kayler
 * Commenter implementation for Header language
 * Created on 01/03/2016.
 */
public class HeaderCommenter implements Commenter{
	@Nullable
	@Override
	public String getLineCommentPrefix() {
		return "//";
	}

	@Nullable
	@Override
	public String getBlockCommentPrefix() {
		return "/*";
	}

	@Nullable
	@Override
	public String getBlockCommentSuffix() {
		return "*/";
	}

	@Nullable
	@Override
	public String getCommentedBlockCommentPrefix() {
		return getLineCommentPrefix();
	}

	@Nullable
	@Override
	public String getCommentedBlockCommentSuffix() {
		return getBlockCommentSuffix();
	}
}

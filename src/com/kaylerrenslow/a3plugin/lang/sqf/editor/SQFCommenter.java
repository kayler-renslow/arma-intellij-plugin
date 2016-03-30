package com.kaylerrenslow.a3plugin.lang.sqf.editor;

import com.intellij.lang.Commenter;
import org.jetbrains.annotations.Nullable;

/**
 * @author Kayler
 * Commenter implementation for SQF language
 * Created on 01/03/2016.
 */
public class SQFCommenter implements Commenter{
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

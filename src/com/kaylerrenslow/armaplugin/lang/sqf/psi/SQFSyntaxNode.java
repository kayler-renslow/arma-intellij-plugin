package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.kaylerrenslow.armaplugin.lang.sqf.syntax.CommandDescriptorCluster;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kayler
 * @since 11/14/2017
 */
public interface SQFSyntaxNode {
	@NotNull
	Object accept(@NotNull SQFSyntaxVisitor visitor, @NotNull CommandDescriptorCluster cluster);
}

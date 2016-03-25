package com.kaylerrenslow.a3plugin.lang.sqf.providers;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import com.intellij.usages.UsageView;
import com.intellij.usages.rules.UsageGroupingRule;
import com.intellij.usages.rules.UsageGroupingRuleProvider;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Kayler on 03/23/2016.
 */
public class SQFUsageGroupingProvider implements UsageGroupingRuleProvider{
	@NotNull
	@Override
	public UsageGroupingRule[] getActiveRules(Project project) {

		return new UsageGroupingRule[0];
	}

	@NotNull
	@Override
	public AnAction[] createGroupingActions(UsageView view) {
		return new AnAction[0];
	}
}

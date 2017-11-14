package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.kaylerrenslow.armaplugin.lang.PsiUtil;
import com.kaylerrenslow.armaplugin.lang.sqf.syntax.CommandDescriptor;
import com.kaylerrenslow.armaplugin.lang.sqf.syntax.CommandDescriptorPool;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * @author Kayler
 * @since 11/13/2017
 */
public class SQFCommandSyntaxHelper {
	private static final SQFCommandSyntaxHelper instance = new SQFCommandSyntaxHelper();

	@NotNull
	public static SQFCommandSyntaxHelper getInstance() {
		return instance;
	}

	private final CommandDescriptorPool pool = new CommandDescriptorPool();

	private SQFCommandSyntaxHelper() {
	}

	@Nullable
	public CommandDescriptor getDescriptor(@NotNull String commandName) {
		return pool.get(commandName);
	}

	public void checkSyntax(@NotNull SQFFile file, @NotNull ProblemsHolder problemsHolder) {
		CommandDescriptor[] descriptors = getCommandDescriptors(file);
		List<SQFStatement> statements = file.getFileScope().getChildStatements();
		//todo finish
	}

	@NotNull
	private CommandDescriptor[] getCommandDescriptors(@NotNull SQFFile file) {
		CommandDescriptor[] descriptors;

		HashSet<String> commands = new HashSet<>();
		{ //collect all commands needed to get syntax for
			PsiUtil.traverseBreadthFirstSearch(file.getNode(), astNode -> {
				PsiElement psiElement = astNode.getPsi();
				if (!(psiElement instanceof SQFCommand)) {
					return false;
				}
				SQFCommand command = (SQFCommand) psiElement;
				commands.add(command.getCommandName().toLowerCase());
				//make sure we do commandname.toLowerCase() to guarantee name collisions

				return false;
			});
		}

		if (commands.size() == 0) {
			descriptors = new CommandDescriptor[0];
			return descriptors;
		}

		final String[] first = new String[commands.size() >= 2 ? (commands.size() / 2) : 1];
		final String[] second = new String[commands.size() - first.length];
		{ //split the commands up
			int i = 0;
			int j = 0;
			for (String command : commands) {
				if (i >= first.length) {
					second[j] = command;
					j++;
				} else {
					first[i] = command;
				}
				i++;
			}
		}

		//get syntax's concurrently
		{
			List<CommandDescriptor> finished = Collections.synchronizedList(new ArrayList<>());

			Thread t1 = new Thread(() -> {
				for (String command : first) {
					CommandDescriptor d = getDescriptor(command);
					if (d != null) {
						finished.add(d);
					}
				}
			}, getClass().getName() + " - Worker Thread 1");

			Thread t2 = new Thread(() -> {
				for (String command : second) {
					CommandDescriptor d = getDescriptor(command);
					if (d != null) {
						finished.add(d);
					}
				}
			}, getClass().getName() + " - Worker Thread 2");

			t1.start();
			t2.start();
			try {
				t1.join();
			} catch (InterruptedException ignore) {

			}
			try {
				t2.join();
			} catch (InterruptedException ignore) {

			}

			//convert them into an array to release some memory and
			//because we no longer need a synchronized list
			descriptors = new CommandDescriptor[finished.size()];
			int i = 0;
			for (CommandDescriptor d : finished) {
				descriptors[i++] = d;
			}
		}
		return descriptors;
	}

}

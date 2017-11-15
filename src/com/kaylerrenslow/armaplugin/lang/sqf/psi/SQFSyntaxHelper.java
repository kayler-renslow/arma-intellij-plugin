package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.kaylerrenslow.armaplugin.lang.PsiUtil;
import com.kaylerrenslow.armaplugin.lang.sqf.syntax.CommandDescriptor;
import com.kaylerrenslow.armaplugin.lang.sqf.syntax.CommandDescriptorCluster;
import com.kaylerrenslow.armaplugin.lang.sqf.syntax.CommandDescriptorPool;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author Kayler
 * @since 11/13/2017
 */
public class SQFSyntaxHelper {
	private static final SQFSyntaxHelper instance = new SQFSyntaxHelper();

	@NotNull
	public static SQFSyntaxHelper getInstance() {
		return instance;
	}

	private final CommandDescriptorPool pool = new CommandDescriptorPool();

	private SQFSyntaxHelper() {
	}

	@Nullable
	public CommandDescriptor getDescriptor(@NotNull String commandName) {
		return pool.get(commandName);
	}

	public void checkSyntax(@NotNull SQFFile file, @NotNull ProblemsHolder problemsHolder) {
		CommandDescriptorCluster cluster = getCommandDescriptors(file.getNode());

		List<SQFStatement> statements = file.getFileScope().getChildStatements();
		new SQFSyntaxChecker(statements, cluster, problemsHolder).begin();
	}

	public void checkSyntax(@NotNull SQFStatement statement, @NotNull ProblemsHolder holder,
							@Nullable CommandDescriptorCluster cluster) {
		cluster = cluster == null ? getCommandDescriptors(statement.getNode()) : cluster;
		new SQFSyntaxChecker(Collections.singletonList(statement), cluster, holder).begin();
	}


	/**
	 * @return an array of all {@link CommandDescriptor} instances for every {@link SQFCommand} contained in the given {@link ASTNode}
	 */
	@NotNull
	public CommandDescriptorCluster getCommandDescriptors(@NotNull ASTNode node) {
		HashSet<String> commands = new HashSet<>();
		{ //collect all commands needed to get syntax for
			PsiUtil.traverseBreadthFirstSearch(node, astNode -> {
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

		return getCommandDescriptors(commands);
	}

	/**
	 * @return an array of all {@link CommandDescriptor} instances for the given set of command names
	 */
	@NotNull
	public CommandDescriptorCluster getCommandDescriptors(@NotNull Set<String> commands) {
		CommandDescriptor[] descriptors;

		if (commands.size() == 0) {
			descriptors = new CommandDescriptor[0];
			return new CommandDescriptorCluster(descriptors);
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

		{//get syntax's concurrently
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
		return new CommandDescriptorCluster(descriptors);
	}

}

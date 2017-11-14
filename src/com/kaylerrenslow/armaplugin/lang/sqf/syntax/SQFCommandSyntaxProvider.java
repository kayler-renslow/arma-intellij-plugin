package com.kaylerrenslow.armaplugin.lang.sqf.syntax;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author Kayler
 * @since 11/13/2017
 */
public class SQFCommandSyntaxProvider {
	private static final SQFCommandSyntaxProvider instance = new SQFCommandSyntaxProvider();

	@NotNull
	public static SQFCommandSyntaxProvider getInstance() {
		return instance;
	}

	private final CommandDescriptorPool pool = new CommandDescriptorPool();

	private SQFCommandSyntaxProvider() {
	}

	@Nullable
	public CommandDescriptor getDescriptor(@NotNull String commandName) {
		return pool.get(commandName);
	}

	private static class CommandDescriptorPool {
		private final DescriptorWrapper[] cache = new DescriptorWrapper[30];
		/**
		 * Cache of commands that are frequently used in SQF. All commands in here should have there syntax XML parsed
		 * once to save overall time when fetching lots of commands at once.
		 */
		private final Map<String, CommandDescriptor> frequentCache = new HashMap<>();
		private final DescriptorWrapper PLACEHOLDER = new DescriptorWrapper(new CommandDescriptor(""));
		private final Random random = new Random();

		public CommandDescriptorPool() {
			Arrays.fill(cache, PLACEHOLDER);
			String[] frequent = {
					"addAction",
					"and",
					"append",
					"blufor",
					"breakOut",
					"breakTo",
					"call",
					"case",
					"count",
					"damage",
					"direction",
					"do",
					"driver",
					"east",
					"else",
					"exitWith",
					"false",
					"findDisplay",
					"for",
					"forEach",
					"format",
					"from",
					"getDir",
					"group",
					"hint",
					"if",
					"in",
					"isNil",
					"isNull",
					"localize",
					"nil",
					"not",
					"opfor",
					"or",
					"player",
					"position",
					"private",
					"select",
					"set",
					"setDir",
					"setPos",
					"sleep",
					"spawn",
					"step",
					"str",
					"switch",
					"then",
					"to",
					"true",
					"typeOf",
					"uiSleep",
					"vehicle",
					"waitUntil",
					"west",
					"while",
			};
			for (String command : frequent) {
				frequentCache.put(command, PLACEHOLDER.descriptor);
			}
		}

		@Nullable
		public CommandDescriptor get(@NotNull String commandName) {
			if (frequentCache.containsKey(commandName)) {
				CommandDescriptor descriptor = frequentCache.get(commandName);
				if (descriptor == PLACEHOLDER.descriptor) {
					//instead of loading them all at application start,
					//we'll load them from file as we need them. This should reduce startup times.
					CommandDescriptor d = getDescriptorFromFile(commandName);
					if (d == null) {
						//we are only going to report frequent commands as errors because we are lazy
						throw new IllegalStateException("frequent command " + commandName + " should have syntax xml");
					}
					frequentCache.put(commandName, d);
					return d;
				}
			}
			DescriptorWrapper ret = null;
			boolean replace = false;
			for (DescriptorWrapper w : cache) {
				if (w == null) {
					continue;
				}
				if (w.descriptor.getCommandName().equalsIgnoreCase(commandName)) {
					w.requestCount++;
					ret = w;
					break;
				}
			}

			if (ret == null) {
				CommandDescriptor d = getDescriptorFromFile(commandName);
				if (d == null) {
					return null;
				}
				ret = new DescriptorWrapper(d);
				replace = true;
			}

			Arrays.sort(cache);

			if (replace) {
				/*
				* Instead of replacing the least recently used command in the cache, we are replacing a randomly
				* selected command in the lower third of the cache. The reason we are doing this is because
				* if we only replaced the least recently used, the least recently used command
				* would constantly be swapped out; also, it would be hard for the rest of the commands to get
				* replaced in the cache.
				*
				* So, we are going to try to "randomly" replace one of the first thirds of commands.
				*/
				int replaceInd = random.nextInt(cache.length / 3);
				cache[replaceInd] = ret;
			}

			return ret.descriptor;
		}

		@Nullable
		private CommandDescriptor getDescriptorFromFile(@NotNull String commandName) {
			InputStream stm = getClass().getClassLoader().getResourceAsStream(
					"/com/kaylerrenslow/armaplugin/lang/sqf/syntax/" + commandName.toLowerCase() + ".xml"
			);
			if (stm == null) {
				return null;
			}
			return SQFCommandSyntaxXMLLoader.importFromStream(new CommandXMLInputStream(stm, commandName));
		}

		private class DescriptorWrapper implements Comparable<DescriptorWrapper> {

			@NotNull
			private final CommandDescriptor descriptor;
			private int requestCount = 0;

			public DescriptorWrapper(@NotNull CommandDescriptor descriptor) {
				this.descriptor = descriptor;
			}

			@Override
			public int compareTo(@NotNull DescriptorWrapper o) {
				//sort from -infinity to +infinity (A to Z)
				return this.requestCount - o.requestCount;
			}
		}
	}
}

package com.kaylerrenslow.armaplugin.lang.sqf.syntax;

import main.SyntaxXMLManager;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Kayler on 06/11/2016.
 */
public class Command {
	private final List<Syntax> syntaxList;
	private final String commandName;
	private String gameVersion;
	private final BIGame game;
	private List<String> parseErrors = new LinkedList<>();
	private boolean deprecated = false;

	private boolean uncertain = false;

	public Command(@NotNull String commandName, @NotNull List<Syntax> syntaxList, @NotNull String gameVersion, @NotNull BIGame game) {
		this.syntaxList = syntaxList;
		this.commandName = commandName;
		this.gameVersion = gameVersion;
		this.game = game;
	}

	private Command(@NotNull String commandName, @NotNull List<Syntax> syntaxList, @NotNull String gameVersion, @NotNull BIGame game, @NotNull List<String> parseErrors) {
		this(commandName, syntaxList, gameVersion, game);
		this.parseErrors = parseErrors;
	}

	@NotNull
	public List<Syntax> getSyntaxList() {
		return syntaxList;
	}

	@NotNull
	public String getCommandName() {
		return commandName;
	}

	@NotNull
	public BIGame getGame() {
		return game;
	}

	@NotNull
	public String getGameVersion() {
		return gameVersion;
	}

	@NotNull
	public List<String> getParseErrors() {
		return parseErrors;
	}

	public boolean isDeprecated() {
		return deprecated;
	}

	public void setDeprecated(boolean deprecated) {
		this.deprecated = deprecated;
	}

	/**
	 * Return true if the syntaxes for the command aren't exactly know and the current syntaxes are estimates
	 */
	public boolean isUncertain() {
		return uncertain;
	}

	public void setUncertain(boolean uncertain) {
		this.uncertain = uncertain;
	}

	@NotNull
	public static Command parse(@NotNull String commandName, @NotNull File file, boolean xml) {
		try {
			if (xml) {
				return SyntaxXMLManager.importFromFile(file);
			} else {
				return doParseWebPage(commandName, file);
			}
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}

	}

	private static Command doParseWebPage(@NotNull String commandName, @NotNull File file) throws Exception {
		List<String> parseErrors = new LinkedList<>();

		Document document = Jsoup.parse(file, "UTF-8");
		Elements elementsDl = document.body().select("._description").first().getElementsByTag("dl");
		String game = "", gameVersion = "";
		List<Syntax> syntaxList = new ArrayList<>();
		int dl = 0;
		for (Element elementDl : elementsDl) {
			if (dl == 0) { //introduced in
				Elements elementsDd = elementDl.getElementsByTag("dd");
				game = elementsDd.get(0).ownText();
				gameVersion = elementsDd.get(1).ownText();
			} else if (dl == 1) { //description
				//do nothing
			} else if (dl < elementsDl.size() - 2) { //syntax
				Elements ddElements = elementDl.getElementsByTag("dd");

				Element syntaxEle = ddElements.first();
				String syntax = trim(syntaxEle.text());//include children because of bold command name

				String returnTypeInfo = ddElements.last().text();
				int returnTypeInfoDashInd = returnTypeInfo.indexOf('-');
				String returnTypeTrimmedText = trim((returnTypeInfoDashInd >= 0 ? returnTypeInfo.substring(0, returnTypeInfoDashInd) : returnTypeInfo));
				ValueHolderType returnType;
				List<ReturnValue> subValues = new LinkedList<>();
				if (returnTypeTrimmedText.contains("Array")) {
					returnType = ValueHolderType.ARRAY;
					String[] tokens = returnTypeTrimmedText.split("[ \t]");
					int i = -1;
					if (tokens.length > 1) {
						for (String s : tokens) {
							i++;
							if (i == 0) {
								continue;
							}
							ValueHolderType valueType = ValueHolderType.getFromDisplayName(s);
							if (valueType != null) {
								subValues.add(new ReturnValueHolder(valueType, ""));
							}

						}
					}
				} else {
					returnType = ValueHolderType.getFromDisplayName(returnTypeTrimmedText);
				}
				String returnTypeDesc = returnTypeInfoDashInd >= 0 ? returnTypeInfo.substring(returnTypeInfoDashInd + 1) : "";

				if (returnType == null) {
					returnType = ValueHolderType.ANYTHING;
					parseErrors.add("return type couldn't be matched");
				}

				ReturnValue returnValue;

				if (returnType == ValueHolderType.ARRAY) {
					returnValue = new ArrayReturnValueHolder(returnTypeDesc, subValues, true);
				} else {
					returnValue = new ReturnValueHolder(returnType, returnTypeDesc);
				}

				List<String> prefixParamNames = new LinkedList<>();
				List<String> postfixParamNames = new LinkedList<>();

				char[] syntaxArr = syntax.toCharArray();
				boolean readingWord = false;
				int start = 0;
				int end = 1;
				char lastChar = 0;

				int i = -1;

				boolean prefixInf = false;
				boolean postfixInf = false;
				int dotCount = 0;
				boolean prefix = true;

				for (char c : syntaxArr) {
					i++;
					final boolean noMoreChars = i == syntaxArr.length - 1;
					final boolean identifierPart = Character.isJavaIdentifierPart(c);
					if (!identifierPart || noMoreChars) {
						if (c == '.' && lastChar == '.') {
							dotCount++;
						} else if (c == '.' && lastChar != '.') {
							dotCount = 1;
						} else {
							dotCount = 0;
						}
						if (dotCount >= 3) {
							if (prefix) {
								prefixInf = true;
							} else {
								postfixInf = true;
							}
						}
						if (!readingWord && !noMoreChars) {
							start++;
							end++;
							lastChar = c;
							continue;
						}

						int wordStart = start;
						int wordEnd = end;

						if (!identifierPart) {
							wordEnd = end - 1;
						}

						readingWord = false;
						String word = trim(syntax.substring(wordStart, wordEnd));
//						System.out.println("Command.doParseWebPage word=" + word+"-");
						if (word.equalsIgnoreCase(commandName)) {
							prefix = false;
						} else {
							if (prefix) {
								prefixParamNames.add(word);
							} else {
								postfixParamNames.add(word);
							}
						}
						start = end;
					} else {
						readingWord = true;
					}
					end++;
					lastChar = c;
				}

				List<Param> prefixParams = new LinkedList<>();
				List<Param> postfixParams = new LinkedList<>();

				for (Element eleParam : elementDl.select(".param")) {
					LinkedList<ValueHolderType> alternateValueTypes = new LinkedList<>();

					final String eleParamText = trim(eleParam.text());
					int colonInd = eleParamText.indexOf(':');
					if (colonInd < 0) {
						if (eleParamText.contains(" ")) {
							colonInd = eleParamText.indexOf(' ');
						} else {
							colonInd = 1;
							parseErrors.add("couldn't locate : for parameter text:" + eleParamText);
						}
					}
					String eleParamName = trim(eleParamText.substring(0, colonInd));
					if (eleParamName.contains(" ")) {
						eleParamName = eleParamName.substring(0, eleParamName.indexOf(' '));
					}
					boolean optional = eleParamText.toLowerCase().contains("optional");
					int dashInd = eleParamText.indexOf('-');
					String description = dashInd >= 0 ? eleParamText.substring(dashInd + 1) : "";

					ValueHolderType valueType = ValueHolderType.ANYTHING;
					try {
						valueType = ValueHolderType.getFromDisplayName(trim(eleParam.children().first().ownText()));
					} catch (NullPointerException e) {
						parseErrors.add("couldn't figure out parameter type for " + eleParamName);
					}
					if (colonInd >= 0 && dashInd >= 0) {
						String t = null;
						try {
							t = trim(eleParamText.substring(colonInd + 1, dashInd));
						} catch (IndexOutOfBoundsException ignore) {

						}
						if (t != null) {
							ValueHolderType expandedValueType = ValueHolderType.getFromDisplayName(t);
							if (expandedValueType == null) {
								String[] tokens = t.split(" ");
								for (String token : tokens) {
									ValueHolderType type = ValueHolderType.getFromDisplayName(token);
									if (type == valueType || type == null) {
										continue;
									}
									alternateValueTypes.add(type);
								}
							} else if (expandedValueType != valueType && expandedValueType != null) {
								valueType = expandedValueType;
							}
						}
					}
					if (valueType == null) {
						valueType = ValueHolderType.ANYTHING;
						parseErrors.add("data type for param '" + eleParamName + "' couldn't be matched");
					}
					checkAndAddParams(prefixParamNames, prefixParams, eleParamName, optional, description, valueType, alternateValueTypes);
					checkAndAddParams(postfixParamNames, postfixParams, eleParamName, optional, description, valueType, alternateValueTypes);
				}
				Param prefixParam = prefixParams.size() == 0 ? null :
						prefixParams.size() > 1 ? new ArrayParam(prefixInf, prefixParams) :
								prefixParams.get(0);
				Param postfixParam = postfixParams.size() == 0 ? null :
						postfixParams.size() > 1 ? new ArrayParam(postfixInf, postfixParams) :
								postfixParams.get(0);
				Syntax syntaxObj = new Syntax(
						prefixParam,
						postfixParam,
						returnValue
				);
				syntaxList.add(syntaxObj);
			}
			dl++;
		}
		return new Command(commandName, syntaxList, gameVersion, GameNameMap.getInstance().getGame(GameNameMap.LookupType.FULL_NAME, game), parseErrors);
	}

	private static void checkAndAddParams(List<String> paramNamesList, List<Param> paramsList, String eleParamName, boolean optional, String description, ValueHolderType valueType, LinkedList<ValueHolderType> alternateValueTypes) {
		for (String paramName : paramNamesList) {
			if (eleParamName.equalsIgnoreCase(paramName)) {
				Param p = new Param(paramName, valueType, description, optional);
				p.getAlternateValueTypes().addAll(alternateValueTypes);
				paramsList.add(p);
				break;
			}
		}
	}

	private static String trim(String s) {
		return s.replaceAll("\\xA0", " ").trim();
	}

	@Override
	public String toString() {
		return "Command{" +
				"syntaxList=" + syntaxList +
				", commandName='" + commandName + '\'' +
				", gameVersion='" + gameVersion + '\'' +
				", game='" + game + '\'' +
				'}';
	}
}

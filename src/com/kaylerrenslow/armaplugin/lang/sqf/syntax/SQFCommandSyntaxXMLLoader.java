package com.kaylerrenslow.armaplugin.lang.sqf.syntax;

import com.kaylerrenslow.armaDialogCreator.util.XmlUtil;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.List;

import static com.kaylerrenslow.armaplugin.lang.sqf.syntax.ValueType.BaseType;

/**
 * @author Kayler
 * @since 11/12/2017
 */
class SQFCommandSyntaxXMLLoader {
	@NotNull
	public static CommandDescriptor importFromStream(@NotNull CommandXMLInputStream is, boolean getCommandDescriptions) throws Exception {
		Document document;

		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
		document = documentBuilder.parse(is);
		document.getDocumentElement().normalize();


		String commandName, gameVersion, gameName, format;

		boolean deprecated, uncertain;

		List<CommandSyntax> syntaxList;

		Element rootElement = document.getDocumentElement();
		commandName = rootElement.getAttribute("name");
		gameVersion = rootElement.getAttribute("version");
		gameName = rootElement.getAttribute("game");
		format = rootElement.getAttribute("format");
		deprecated = XmlUtil.getChildElementsWithTagName(rootElement, "deprecated").size() > 0;
		uncertain = XmlUtil.getChildElementsWithTagName(rootElement, "uncertain").size() > 0;


		List<Element> syntaxElements = XmlUtil.getChildElementsWithTagName(rootElement, "syntax");
		syntaxList = new ArrayList<>(syntaxElements.size());
		for (Element syntaxElement : syntaxElements) {
			final int PREFIX = 0;
			final int POSTFIX = 1;
			Param[] params = new Param[2];
			List<Element> arrayElements = XmlUtil.getChildElementsWithTagName(syntaxElement, "array");
			for (Element arrayElement : arrayElements) {
				int order = Integer.parseInt(arrayElement.getAttribute("order"));
				if (params[order] != null) {
					throw new RuntimeException("duplicate order for command " + is.getCommandName() + ". order=" + order);
				}
				params[order] = getArrayParam(arrayElement, getCommandDescriptions);
			}
			List<Element> paramElements = XmlUtil.getChildElementsWithTagName(syntaxElement, "param");
			for (Element paramElement : paramElements) {
				int order = Integer.parseInt(paramElement.getAttribute("order"));
				if (params[order] != null) {
					throw new RuntimeException("duplicate order for command " + is.getCommandName() + ". order=" + order);
				}
				params[order] = getParam(paramElement, getCommandDescriptions);
			}

			ReturnValueHolder returnValue;

			List<Element> returnElements = XmlUtil.getChildElementsWithTagName(syntaxElement, "return");
			if (returnElements.size() == 0) {
				throw new RuntimeException("no return value");
			}
			returnValue = getReturnValue(returnElements.get(0), getCommandDescriptions);


			syntaxList.add(new CommandSyntax(params[PREFIX], params[POSTFIX], returnValue));
		}

		CommandDescriptor c = new CommandDescriptor(commandName, syntaxList, gameVersion, GameNameMap.getInstance().getGame(GameNameMap.LookupType.LINK_PREFIX, gameName));
		c.setDeprecated(deprecated);
		c.setUncertain(uncertain);

		return c;
	}

	private static ReturnValueHolder getReturnValue(@NotNull Element returnValueElement, boolean getCommandDescriptions) {
		List<Element> arrayElements = XmlUtil.getChildElementsWithTagName(returnValueElement, "array");
		List<Element> valueElements = XmlUtil.getChildElementsWithTagName(returnValueElement, "value");

		ArrayList<ReturnValueHolder> list = new ArrayList<>(1);

		if (arrayElements.size() > 0) {
			getArrayReturnValueFromElement(arrayElements.get(0), list, getCommandDescriptions);
			return list.get(0);
		} else if (valueElements.size() > 0) {
			getReturnValueFromElement(valueElements.get(0), list, getCommandDescriptions);
			return list.get(0);
		} else {
			throw new RuntimeException("no return value saved");
		}
	}

	private static void getReturnValueFromElement(@NotNull Element returnValueElement,
												  @NotNull List<ReturnValueHolder> parentReturnValues,
												  boolean getCommandDescriptions) {
		String orderStr = returnValueElement.getAttribute("order");
		int order = orderStr.length() > 0 ? Integer.parseInt(orderStr) : 0;

		ValueType dataType;
		String desc;

		dataType = getTypeFromElementAttribute(returnValueElement, "type");
		desc = getCommandDescriptions ? XmlUtil.getImmediateTextContent(returnValueElement) : "";

		ReturnValueHolder returnValue = new ReturnValueHolder(dataType == null ? BaseType._ERROR : dataType, desc);

		addAltTypes(returnValueElement, returnValue.getType().getPolymorphicTypes());

		//get literals
		List<Element> literalElements = XmlUtil.getChildElementsWithTagName(returnValueElement, "literal");
		for (Element literalElement : literalElements) {
			returnValue.getLiterals().add(XmlUtil.getImmediateTextContent(literalElement));
		}

		while (order >= parentReturnValues.size()) { //guarantee that the order index exists
			parentReturnValues.add(PLACEHOLDER_RETURN_VALUE);
		}

		parentReturnValues.set(order, returnValue);
	}

	private static void getArrayReturnValueFromElement(@NotNull Element arrayElement,
													   @NotNull List<ReturnValueHolder> parentReturnValues,
													   boolean getCommandDescriptions) {
		String orderStr = arrayElement.getAttribute("order");
		int order = orderStr.length() > 0 ? Integer.parseInt(orderStr) : 0;

		boolean unbounded = valueOfTF(arrayElement.getAttribute("unbounded"));
		List<ReturnValueHolder> myValues = new ArrayList<>();

		String desc = getCommandDescriptions ? XmlUtil.getImmediateTextContent(arrayElement) : "";
		ArrayReturnValueHolder value = new ArrayReturnValueHolder(desc, myValues, unbounded);

		List<Element> valueElements = XmlUtil.getChildElementsWithTagName(arrayElement, "value");
		for (Element arrayChildElement : valueElements) {
			getReturnValueFromElement(arrayChildElement, myValues, getCommandDescriptions);
		}
		List<Element> arrayElements = XmlUtil.getChildElementsWithTagName(arrayElement, "array");
		for (Element arrayChildElement : arrayElements) {
			getArrayReturnValueFromElement(arrayChildElement, myValues, getCommandDescriptions);
		}
		while (order >= parentReturnValues.size()) { //guarantee that the order index exists
			parentReturnValues.add(PLACEHOLDER_RETURN_VALUE);
		}

		parentReturnValues.set(order, value);
	}

	private static Param getParam(@NotNull Element paramElement, boolean getCommandDescriptions) {
		ValueType dataType;
		String paramName, desc;
		boolean optional;

		dataType = getTypeFromElementAttribute(paramElement, "type");
		paramName = paramElement.getAttribute("name");
		optional = valueOfTF(paramElement.getAttribute("optional"));
		desc = getCommandDescriptions ? XmlUtil.getImmediateTextContent(paramElement) : "";

		Param p = new Param(paramName, BaseType._ERROR, desc, optional);
		addAltTypes(paramElement, p.getType().getPolymorphicTypes());

		//get literals
		List<Element> literalElements = XmlUtil.getChildElementsWithTagName(paramElement, "literal");
		for (Element literalElement : literalElements) {
			p.getLiterals().add(XmlUtil.getImmediateTextContent(literalElement));
		}

		return p;
	}

	private static void addAltTypes(@NotNull Element hostElement, @NotNull List<ValueType> alternateDataTypes) {
		List<Element> altTypesElements = XmlUtil.getChildElementsWithTagName(hostElement, "alt-types");
		for (Element altTypeElement : altTypesElements) {
			List<Element> tElements = XmlUtil.getChildElementsWithTagName(altTypeElement, "t");
			for (Element tElement : tElements) {
				ValueType t = getTypeFromElementAttribute(tElement, "type");
				alternateDataTypes.add(t);
			}
		}
	}

	private static ArrayParam getArrayParam(@NotNull Element arrayParamElement, boolean getCommandDescriptions) {
		List<Param> paramList = new ArrayList<>();
		boolean unbounded, optional;

		unbounded = valueOfTF(arrayParamElement.getAttribute("unbounded"));
		optional = valueOfTF(arrayParamElement.getAttribute("optional"));

		List<Element> arrayElements = XmlUtil.getChildElementsWithTagName(arrayParamElement, "array");
		for (Element arrayElement : arrayElements) {
			int order = getOrderForParam(paramList, arrayElement);
			paramList.set(order, getArrayParam(arrayElement, getCommandDescriptions));
		}

		List<Element> paramElements = XmlUtil.getChildElementsWithTagName(arrayParamElement, "param");
		for (Element paramElement : paramElements) {
			int order = getOrderForParam(paramList, paramElement);
			paramList.set(order, getParam(paramElement, getCommandDescriptions));
		}

		return new ArrayParam(unbounded, paramList, optional);

	}

	private static int getOrderForParam(@NotNull List<Param> paramList, @NotNull Element paramElement) {
		int order = Integer.parseInt(paramElement.getAttribute("order"));
		while (order >= paramList.size()) {
			paramList.add(PLACEHOLDER_PARAM);
		}
		return order;
	}

	private static boolean valueOfTF(String tf) {
		return tf.equalsIgnoreCase("t") || tf.equalsIgnoreCase("true");
	}

	/**
	 * Gets a {@link ValueType} from an Element's attribute. This can either return a {@link BaseType} or a {@link CodeType}
	 * if the element has a "code-handler" child tag.
	 *
	 * @param ele                   element that contains a type
	 * @param attributeNameWithType the attribute name that to get a type from
	 * @return the {@link BaseType#valueOf(String)} of the attribute, or {@link BaseType#_ERROR} if the type was invalid.
	 * If the {@link BaseType#valueOf(String)} returns a {@link BaseType#CODE} and a "code-handler" tag is present,
	 * a {@link CodeType} will be returned. Note that if the type in the "code-handler" can't be determined, it will have a {@link BaseType#_ERROR}
	 */
	@NotNull
	private static ValueType getTypeFromElementAttribute(@NotNull Element ele, @NotNull String attributeNameWithType) {
		String typeAsString = ele.getAttribute(attributeNameWithType);
		ValueType type = BaseType.valueOf(typeAsString);
		if (type == null) {
			return BaseType._ERROR;
		}
		if (type != BaseType.CODE) {
			return type;
		}
		List<Element> codeHandlers = XmlUtil.getChildElementsWithTagName(ele, "code-handler");
		if (codeHandlers.isEmpty()) {
			return type;
		}
		return new CodeType(getTypeFromElementAttribute(codeHandlers.get(0), "t"));
	}

	private static final ReturnValueHolder PLACEHOLDER_RETURN_VALUE = new ReturnValueHolder(BaseType.ANYTHING, "PLACEHOLDER");
	private static final Param PLACEHOLDER_PARAM = new Param("PLACEHOLDER", BaseType.ANYTHING, "", true);
}

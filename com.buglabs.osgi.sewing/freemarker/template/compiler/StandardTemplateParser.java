/*
 * FreeMarker: a tool that allows Java programs to generate HTML
 * output using templates.
 * Copyright (C) 1998-2004 Benjamin Geer
 * Email: beroul@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 *
 * 22 October 1999: Modified by Holger Arendt to parse method calls
 * in expressions.
 */

package freemarker.template.compiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import freemarker.template.FunctionTemplateProcessor;
import freemarker.template.InputSource;
import freemarker.template.TemplateException;
import freemarker.template.expression.And;
import freemarker.template.expression.BooleanLiteral;
import freemarker.template.expression.Divide;
import freemarker.template.expression.Dot;
import freemarker.template.expression.DynamicKeyName;
import freemarker.template.expression.EmptyLiteral;
import freemarker.template.expression.Equals;
import freemarker.template.expression.Expression;
import freemarker.template.expression.ExpressionBuilder;
import freemarker.template.expression.GreaterThan;
import freemarker.template.expression.GreaterThanOrEquals;
import freemarker.template.expression.HashLiteral;
import freemarker.template.expression.Identifier;
import freemarker.template.expression.LessThan;
import freemarker.template.expression.LessThanOrEquals;
import freemarker.template.expression.ListLiteral;
import freemarker.template.expression.ListRange;
import freemarker.template.expression.MethodCall;
import freemarker.template.expression.Minus;
import freemarker.template.expression.Modulo;
import freemarker.template.expression.Multiply;
import freemarker.template.expression.Not;
import freemarker.template.expression.NotEquals;
import freemarker.template.expression.NumberLiteral;
import freemarker.template.expression.Or;
import freemarker.template.expression.Plus;
import freemarker.template.expression.StringLiteral;
import freemarker.template.expression.Variable;
import freemarker.template.instruction.AssignBlockInstruction;
import freemarker.template.instruction.AssignInstruction;
import freemarker.template.instruction.BreakInstruction;
import freemarker.template.instruction.CallInstruction;
import freemarker.template.instruction.CaseInstruction;
import freemarker.template.instruction.CommentInstruction;
import freemarker.template.instruction.ContainerInstruction;
import freemarker.template.instruction.DefaultCaseInstruction;
import freemarker.template.instruction.ElseInstruction;
import freemarker.template.instruction.EndInstruction;
import freemarker.template.instruction.ExitInstruction;
import freemarker.template.instruction.FunctionInstruction;
import freemarker.template.instruction.IfElseInstruction;
import freemarker.template.instruction.IfInstruction;
import freemarker.template.instruction.IncludeInstruction;
import freemarker.template.instruction.Instruction;
import freemarker.template.instruction.ListInstruction;
import freemarker.template.instruction.NoParseInstruction;
import freemarker.template.instruction.SwitchInstruction;
import freemarker.template.instruction.TextBlockInstruction;
import freemarker.template.instruction.TransformInstruction;
import freemarker.template.instruction.VariableInstruction;

/**
 * Parses standard template language and generates
 * {@link freemarker.template.instruction.Instruction}s. Uses
 * {@link freemarker.template.expression.ExpressionBuilder} to build
 * expressions.
 * 
 * @version $Id: StandardTemplateParser.java 1189 2005-10-16 01:53:54Z run2000 $
 */
public class StandardTemplateParser implements TemplateParser {

	/** The text to be parsed. */
	protected String text;
	/** The number of characters in the text. */
	protected int textLen;

	/** The Template being parsed. */
	protected FunctionTemplateProcessor template;

	/** The current parse position. */
	protected int parsePos = 0;

	/** The parse position before the current instruction was found. */
	protected int previousParsePos = 0;

	/** The position at which the current instruction was found. */
	protected int foundPos = 0;

	/** The next non-text instruction found by the parser. */
	protected Instruction nextFMInstruction;

	/**
	 * A Map of tag names to {@link Tag} objects, which are stored as flywheels.
	 */
	private static Map tagMap = initTags();

	/**
	 * A Map of two-character operator strings to {@link LongOperator} objects,
	 * which are stored as flywheels.
	 */
	private static Map longOpMap = initLongOps();

	// Template syntax
	protected static final int MAX_TAG_NAME_LENGTH = 10;
	protected static final String VAR_INSTR_START_CHARS = "${";
	protected static final char VAR_INSTR_START_CHAR = '$';
	protected static final char VAR_INSTR_END_CHAR = '}';
	protected static final String LIST_TAG = "list";
	protected static final String LIST_INDEX_KEYWORD = "as";
	protected static final String LIST_END_TAG = "/list";
	protected static final String IF_TAG = "if";
	protected static final String ELSE_TAG = "else";
	protected static final String ELSE_IF_TAG = "elseif";
	protected static final String IF_END_TAG = "/if";
	protected static final String SWITCH_TAG = "switch";
	protected static final String SWITCH_END_TAG = "/switch";
	protected static final String CASE_TAG = "case";
	protected static final String BREAK_TAG = "break";
	protected static final String DEFAULT_TAG = "default";
	protected static final String ASSIGN_TAG = "assign";
	protected static final String ASSIGN_END_TAG = "/assign";
	protected static final String INCLUDE_TAG = "include";
	protected static final String FUNCTION_TAG = "function";
	protected static final String FUNCTION_END_TAG = "/function";
	protected static final String CALL_TAG = "call";
	protected static final String EXIT_TAG = "exit";
	protected static final char TAG_START_CHAR = '<';
	protected static final char TAG_END_CHAR = '>';
	protected static final char END_TAG_START_CHAR = '/';
	protected static final char QUOTE_CHAR = '\'';
	protected static final char DOUBLE_QUOTE_CHAR = '\"';
	protected static final char ESCAPE_CHAR = '\\';
	protected static final char BOOLEAN_ESCAPE_CHAR = '#';
	protected static final char LIST_LITERAL_START_CHAR = '[';
	protected static final char LIST_LITERAL_END_CHAR = ']';
	protected static final char HASH_LITERAL_START_CHAR = '{';
	protected static final char HASH_LITERAL_END_CHAR = '}';
	protected static final String LIST_LITERAL_RANGE = "..";
	protected static final String COMMENT_TAG = "comment";
	protected static final String COMMENT_END_TAG = "/comment";
	protected static final String NOPARSE_TAG = "noparse";
	protected static final String NOPARSE_TAG_END = "/noparse";
	protected static final String FOREACH_TAG = "foreach";
	protected static final String FOREACH_INDEX_KEYWORD = "in";
	protected static final String FOREACH_END_TAG = "/foreach";
	protected static final String TRANSFORM_TAG = "transform";
	protected static final String TRANSFORM_END_TAG = "/transform";
	protected static final String TRUE_LITERAL = "true";
	protected static final String FALSE_LITERAL = "false";
	protected static final String EMPTY_LITERAL = "empty";
	protected static final String LOCAL_KEYWORD = "local";
	protected static final String GLOBAL_KEYWORD = "global";

	/** Length of operators that are more than one character long. */
	protected static final int LONG_OPERATOR_LENGTH = 2;

	/** Default constructor. */
	public StandardTemplateParser() {
	}

	/**
	 * Constructs a new <code>StandardTemplateParser</code> with the given
	 * template and text to be parsed.
	 * 
	 * @param template
	 *            a new template that will received the parsed instructions
	 * @param text
	 *            the text to be parsed
	 * @deprecated use InputSources to pass in the text to be compiled where
	 *             possible
	 */
	public StandardTemplateParser(FunctionTemplateProcessor template, String text) {
		setTemplate(template);
		setText(text);
	}

	/**
	 * Constructs a new <code>StandardTemplateParser</code> with the given
	 * template and text to be parsed.
	 * 
	 * @param template
	 *            a new template that will received the parsed instructions
	 * @param source
	 *            the text to be parsed
	 * @throws IOException
	 *             there was a problem reading the stream
	 */
	public StandardTemplateParser(FunctionTemplateProcessor template, InputSource source) throws IOException {
		String text = getTemplateText(source);
		setTemplate(template);
		setText(text);
	}

	/**
	 * Takes the given Reader, reads it until the end of the stream, and
	 * accumulates the contents in a String. The string is returned when the
	 * stream is exhausted.
	 * 
	 * @param source
	 *            the input stream to be turned into a String
	 * @return a String representation of the given stream
	 * @throws IOException
	 *             something went wrong while processing the stream
	 */
	protected static String getTemplateText(InputSource source) throws IOException {
		Reader reader = source.getReader();

		if (reader == null) {
			InputStream stream = source.getInputStream();
			if (stream == null) {
				throw new IllegalArgumentException("InputSource contains neither character nor byte stream");
			}
			String encoding = source.getEncoding();

			if (encoding == null) {
				reader = new InputStreamReader(stream);
			} else {
				reader = new InputStreamReader(stream, encoding);
			}
		}

		StringBuffer textBuf = new StringBuffer();
		BufferedReader br = new BufferedReader(reader);
		char cbuf[] = new char[1024];
		int nSize;

		try {
			nSize = br.read(cbuf);
			while (nSize > 0) {
				textBuf.append(cbuf, 0, nSize);
				nSize = br.read(cbuf);
			}
		} finally {
			try {
				br.close();
			} catch (IOException e) {
			}
		}

		return textBuf.toString();
	}

	/**
	 * Initialize FM-Classic tag flywheel map.
	 * 
	 * @return a <code>Map</code> of <code>Tag</code> flywheels.
	 */
	private static Map initTags() {
		Map tagMap = new HashMap();

		// Map tag strings to Tag objects.
		tagMap.put(LIST_TAG, new Tag() {
			Instruction parse(StandardTemplateParser p) throws ParseException {
				return p.parseListStart();
			}
		});

		tagMap.put(LIST_END_TAG, new Tag() {
			Instruction parse(StandardTemplateParser p) throws ParseException {
				return new EndInstruction(Instruction.LIST_END);
			}
		});

		tagMap.put(IF_TAG, new Tag() {
			Instruction parse(StandardTemplateParser p) throws ParseException {
				return p.parseIfStart();
			}
		});

		tagMap.put(ELSE_TAG, new Tag() {
			Instruction parse(StandardTemplateParser p) throws ParseException {
				return new ElseInstruction();
			}
		});

		tagMap.put(ELSE_IF_TAG, new Tag() {
			Instruction parse(StandardTemplateParser p) throws ParseException {
				return p.parseElseIf();
			}
		});

		tagMap.put(IF_END_TAG, new Tag() {
			Instruction parse(StandardTemplateParser p) throws ParseException {
				return new EndInstruction(Instruction.IF_END);
			}
		});

		tagMap.put(SWITCH_TAG, new Tag() {
			Instruction parse(StandardTemplateParser p) throws ParseException {
				return p.parseSwitch();
			}
		});

		tagMap.put(SWITCH_END_TAG, new Tag() {
			Instruction parse(StandardTemplateParser p) throws ParseException {
				return new EndInstruction(Instruction.SWITCH_END);
			}
		});

		tagMap.put(CASE_TAG, new Tag() {
			Instruction parse(StandardTemplateParser p) throws ParseException {
				return p.parseCase();
			}
		});

		tagMap.put(BREAK_TAG, new Tag() {
			Instruction parse(StandardTemplateParser p) throws ParseException {
				return BreakInstruction.getInstance();
			}
		});

		tagMap.put(DEFAULT_TAG, new Tag() {
			Instruction parse(StandardTemplateParser p) throws ParseException {
				return new DefaultCaseInstruction();
			}
		});

		tagMap.put(ASSIGN_TAG, new Tag() {
			Instruction parse(StandardTemplateParser p) throws ParseException {
				return p.parseAssign();
			}
		});

		tagMap.put(INCLUDE_TAG, new Tag() {
			Instruction parse(StandardTemplateParser p) throws ParseException {
				return p.parseInclude();
			}
		});

		tagMap.put(FUNCTION_TAG, new Tag() {
			Instruction parse(StandardTemplateParser p) throws ParseException {
				return p.parseFunction();
			}
		});

		tagMap.put(FUNCTION_END_TAG, new Tag() {
			Instruction parse(StandardTemplateParser p) throws ParseException {
				return new EndInstruction(Instruction.FUNCTION_END);
			}
		});

		tagMap.put(CALL_TAG, new Tag() {
			Instruction parse(StandardTemplateParser p) throws ParseException {
				return p.parseCall();
			}
		});

		tagMap.put(EXIT_TAG, new Tag() {
			Instruction parse(StandardTemplateParser p) throws ParseException {
				return ExitInstruction.getInstance();
			}
		});

		tagMap.put(COMMENT_TAG, new Tag() {
			Instruction parse(StandardTemplateParser p) throws ParseException {
				return CommentInstruction.getInstance();
			}
		});

		tagMap.put(COMMENT_END_TAG, new Tag() {
			Instruction parse(StandardTemplateParser p) throws ParseException {
				return new EndInstruction(Instruction.COMMENT_END);
			}
		});

		tagMap.put(FOREACH_TAG, new Tag() {
			Instruction parse(StandardTemplateParser p) throws ParseException {
				return p.parseForeachStart();
			}
		});

		tagMap.put(FOREACH_END_TAG, new Tag() {
			Instruction parse(StandardTemplateParser p) throws ParseException {
				return new EndInstruction(Instruction.FOREACH_END);
			}
		});

		tagMap.put(NOPARSE_TAG, new Tag() {
			Instruction parse(StandardTemplateParser p) throws ParseException {
				return new NoParseInstruction();
			}
		});

		tagMap.put(NOPARSE_TAG_END, new Tag() {
			Instruction parse(StandardTemplateParser p) throws ParseException {
				return new EndInstruction(Instruction.NOPARSE_END);
			}
		});

		tagMap.put(TRANSFORM_TAG, new Tag() {
			Instruction parse(StandardTemplateParser p) throws ParseException {
				return p.parseTransformStart();
			}
		});

		tagMap.put(TRANSFORM_END_TAG, new Tag() {
			Instruction parse(StandardTemplateParser p) throws ParseException {
				return new EndInstruction(Instruction.TRANSFORM_END);
			}
		});

		tagMap.put(ASSIGN_END_TAG, new Tag() {
			Instruction parse(StandardTemplateParser p) throws ParseException {
				return new EndInstruction(Instruction.ASSIGN_END);
			}
		});

		return tagMap;
	}

	/**
	 * Initialize long operator flywheel map.
	 * 
	 * @return a <code>Map</code> of <code>LongOperator</code> flywheels.
	 */
	private static Map initLongOps() {
		Map longOpMap = new HashMap();

		// Map long operator strings to LongOperator objects.
		longOpMap.put("==", new LongOperator() {
			Expression parse() throws ParseException {
				return new Equals();
			}
		});

		longOpMap.put("!=", new LongOperator() {
			Expression parse() throws ParseException {
				return new NotEquals();
			}
		});

		longOpMap.put("&&", new LongOperator() {
			Expression parse() throws ParseException {
				return new And();
			}
		});

		longOpMap.put("||", new LongOperator() {
			Expression parse() throws ParseException {
				return new Or();
			}
		});

		longOpMap.put("lt", new LongOperator() {
			Expression parse() throws ParseException {
				return new LessThan();
			}
		});

		longOpMap.put("le", new LongOperator() {
			Expression parse() throws ParseException {
				return new LessThanOrEquals();
			}
		});

		longOpMap.put("gt", new LongOperator() {
			Expression parse() throws ParseException {
				return new GreaterThan();
			}
		});

		longOpMap.put("ge", new LongOperator() {
			Expression parse() throws ParseException {
				return new GreaterThanOrEquals();
			}
		});

		longOpMap.put("eq", new LongOperator() {
			Expression parse() throws ParseException {
				return new Equals();
			}
		});

		longOpMap.put("ne", new LongOperator() {
			Expression parse() throws ParseException {
				return new NotEquals();
			}
		});

		longOpMap.put("or", new LongOperator() {
			Expression parse() throws ParseException {
				return new Or();
			}
		});

		return longOpMap;
	}

	/**
	 * Sets the text to be parsed.
	 * 
	 * @param text
	 *            the text to be parsed.
	 */
	public void setText(String text) {
		this.text = text;
		textLen = text.length();
	}

	/**
	 * Sets the template to receive the parsed instructions.
	 * 
	 * @param template
	 *            the template being parsed.
	 */
	public void setTemplate(FunctionTemplateProcessor template) {
		this.template = template;
	}

	/**
	 * <p>
	 * Searches the text for an instruction, starting at the current parse
	 * position. If one is found, parses it into an
	 * {@link freemarker.template.instruction.Instruction}. Before changing
	 * <code>parsePos</code>, sets <code>previousParsePos = parsePos</code>.
	 * </p>
	 * 
	 * <p>
	 * If no instruction is found, leaves <code>parsePos</code> unchanged.
	 * </p>
	 * 
	 * @return a <code>Instruction</code> representing the next instruction
	 *         following <code>parsePos</code>, or <code>null</code> if none is
	 *         found.
	 * @throws ParseException
	 *             the next instruction couldn't be parsed
	 */
	public Instruction getNextInstruction() throws ParseException {
		Instruction fmInstruction = nextFMInstruction;

		if (fmInstruction != null) {
			nextFMInstruction = null;
			return fmInstruction;
		}

		fmInstruction = getNextInstructionTag();
		if (foundPos > previousParsePos) {
			nextFMInstruction = fmInstruction;
			String textBlock = text.substring(previousParsePos, foundPos);
			return new TextBlockInstruction(textBlock);
		}
		return fmInstruction;
	}

	/**
	 * <p>
	 * Searches the text for a tagged instruction, starting at the current parse
	 * position. If one is found, parses it into a
	 * {@link freemarker.template.instruction.Instruction}. Before changing
	 * <code>parsePos</code>, sets <code>previousParsePos = parsePos</code>.
	 * </p>
	 * 
	 * <p>
	 * If no instruction is found, <code>parsePos</code> will be equal to
	 * <code>textLen</code>, which will be equal to <code>foundPos</code>.
	 * </p>
	 * 
	 * @return a <code>Instruction</code> representing the next instruction
	 *         following <code>parsePos</code>, or <code>null</code> if none is
	 *         found.
	 * @throws ParseException
	 *             the next instruction couldn't be parsed
	 */
	protected Instruction getNextInstructionTag() throws ParseException {
		previousParsePos = parsePos;
		while (parsePos < textLen) {
			char c = text.charAt(parsePos);

			switch (c) {
			// If this is an HTML-style tag, get its name.
			case TAG_START_CHAR:
				int tagStartPos = parsePos;
				parsePos++;
				findTagNameEnd();
				String tagName = text.substring(tagStartPos + 1, parsePos);

				// If we have a Tag object for this tag,
				// have the Tag object call us back to parse it.
				Tag tag = (Tag) tagMap.get(tagName);
				if (tag != null) {
					foundPos = tagStartPos;
					Instruction instruction = tag.parse(this);
					if (!findTagEnd()) {
						String errorMessage = "Syntax error" + atChar(foundPos);
						throw new ParseException(errorMessage);
					}
					return instruction;
				} else {
					parsePos = tagStartPos;
				}
				break;

			// If this is a variable instruction, parse it.
			case VAR_INSTR_START_CHAR:
				if (text.startsWith(VAR_INSTR_START_CHARS, parsePos)) {
					foundPos = parsePos;
					return parseVariableInstruction();
				}
			}
			parsePos++;
		}
		foundPos = textLen;
		return null;
	}

	/**
	 * Are there any more instructions left to be parsed?
	 * 
	 * @return <code>true</code> if there is more text to parse, otherwise
	 *         <code>false</code>
	 */
	public boolean isMoreInstructions() {
		return (nextFMInstruction != null) || (parsePos < textLen);
	}

	/**
	 * <p>
	 * Searches the text for a matching end instruction, starting at the current
	 * parse position. If we find it, parse it and return. Before changing
	 * <code>parsePos</code>, should set <code>previousParsePos =
	 * parsePos</code>.
	 * </p>
	 * 
	 * <p>
	 * If no instruction is found, set <code>parsePos</code> to
	 * <code>textLen</code> which equals <code>foundPos</code>.
	 * </p>
	 * 
	 * @return a <code>String</code> containing the intermediate text if we find
	 *         the end instruction we're after, otherwise <code>null</code>
	 */
	public String skipToEndInstruction(ContainerInstruction beginInstruction) {
		previousParsePos = parsePos;

		while (parsePos < textLen) {

			// If this is an HTML-style tag, get its name.
			if (text.charAt(parsePos) == TAG_START_CHAR) {
				int tagStartPos = parsePos;
				parsePos++;

				findTagNameEnd();
				if (text.charAt(tagStartPos + 1) == END_TAG_START_CHAR) {
					String tagName = text.substring(tagStartPos + 1, parsePos);
					Tag tag = (Tag) tagMap.get(tagName);

					// If we have a Tag object for this tag, and it's an
					// end tag, have the Tag object call us back to parse it.
					if (tag != null) {
						foundPos = tagStartPos;
						try {
							Instruction endInstruction = tag.parse(this);
							if (beginInstruction.testEndInstruction(endInstruction)) {
								try {
									if (findTagEnd()) {
										return text.substring(previousParsePos, foundPos);
									}
								} catch (ParseException e) {
									// End of file encountered
									return null;
								}
							}
						} catch (ParseException e) {
							// Not the end tag we were looking for, keep
							// going...
						}
					}
				}
				parsePos = tagStartPos + 1;
			} else {
				parsePos++;
			}
		}
		foundPos = textLen;
		return null;
	}

	/**
	 * Adds text to an error message indicating the line number where the error
	 * was found.
	 * 
	 * @return a <code>String</code> containing the message.
	 */
	public String atChar() {
		return atChar(foundPos);
	}

	/**
	 * Advances <code>parsePos</code> through any remaining alphanumeric
	 * characters. Leaves <code>parsePos</code> unchanged if not found.
	 * 
	 * We check for the maximum length that a FM-Classic tag name could be. This
	 * is a simple optimization to ensure that we don't end up with massively
	 * long tag names being returned. Note that we intentionally go one past the
	 * maximum length, to ensure we have a complete name.
	 */
	protected void findTagNameEnd() {
		int tagNameEndPos = parsePos;
		int length = 0;
		char c;

		if ((tagNameEndPos < textLen) && (text.charAt(tagNameEndPos) == END_TAG_START_CHAR)) {
			tagNameEndPos++;
		}

		while ((tagNameEndPos < textLen) && (length <= MAX_TAG_NAME_LENGTH)) {
			c = text.charAt(tagNameEndPos);
			if (!Character.isLetterOrDigit(c)) {
				parsePos = tagNameEndPos;
				return;
			}
			tagNameEndPos++;
			length++;
		}
	}

	/**
	 * Requires a <code>TAG_END_CHAR</code>, optionally preceded by whitespace,
	 * and advances <code>parsePos</code> after the <code>TAG_END_CHAR</code>.
	 * 
	 * @return <code>true</code> if we found the end of the tag, otherwise
	 *         <code>false</code>
	 * @throws ParseException
	 *             an error occurred while scanning for the end tag
	 */
	protected boolean findTagEnd() throws ParseException {
		if (skipToTagEnd()) {
			parsePos++;
			return true;
		}
		return false;
	}

	/**
	 * Requires a <code>TAG_END_CHAR</code>, optionally preceded by whitespace,
	 * and advances <code>parsePos</code> to the <code>TAG_END_CHAR</code>.
	 * 
	 * @return <code>true</code> if we found the end of the tag, otherwise
	 *         <code>false</code>
	 * @throws ParseException
	 *             an error occurred while scanning for the end tag
	 */
	protected boolean skipToTagEnd() throws ParseException {
		skipWhitespace();
		return text.charAt(parsePos) == TAG_END_CHAR;
	}

	/**
	 * Adds text to an error message indicating the line number where the error
	 * was found.
	 * 
	 * @return a <code>String</code> containing the message.
	 */
	protected String atChar(int pos) {
		String lineFeed = System.getProperty("line.separator");
		char newline = lineFeed.charAt(lineFeed.length() - 1);

		int lineNum = 1;
		for (int charParsed = 0; charParsed < pos; charParsed++) {
			if (text.charAt(charParsed) == newline) {
				lineNum++;
			}
		}

		return " at line " + String.valueOf(lineNum);
	}

	// -----------------------------------------------------------------------
	// Parse FreeMarker expressions
	// -----------------------------------------------------------------------

	/**
	 * Parses and builds an {@link freemarker.template.expression.Expression},
	 * which may also be a sub-expression. An expression in parenthesis is
	 * considered a sub-expression.
	 * 
	 * @return the completed <code>Expression</code>.
	 */
	protected Expression parseExpression() throws ParseException {
		int startPos = parsePos;

		// Tokenize the expression
		List tokens = parseElements();

		if (tokens.size() == 0) {
			throw new ParseException("Missing expression" + atChar(startPos));
		}

		// Build the tokens into an expression
		try {
			return ExpressionBuilder.buildExpression(tokens);
		} catch (ParseException e) {
			throw new ParseException("Syntax error in expression" + atChar(startPos), e);
		}
	}

	/**
	 * Retrieve the next {@link freemarker.template.expression.Expression}(s)
	 * following <code>parsePos</code>.
	 * 
	 * @return a List of <code>Expression</code>s containing any elements
	 *         encountered
	 * @throws ParseException
	 *             something went wrong during parsing
	 */
	protected List parseElements() throws ParseException {
		List elements = new LinkedList();
		boolean moreExpected;

		do {
			skipWhitespace();
			char c = text.charAt(parsePos);

			// Look for an identifier.
			if (isIdentifierStartChar(c)) {
				elements.add(parseVariable());
				moreExpected = parseBinaryElement(elements);
				continue;
			}

			// Look for the things we can identify by one character:
			// a single-character operator, a parenthesis, a bracket,
			// or a string literal.
			switch (c) {
			case QUOTE_CHAR:
			case DOUBLE_QUOTE_CHAR:
				elements.add(parseStringLiteral());
				moreExpected = parseBinaryElement(elements);
				break;
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
			case '-':
				elements.add(parseNumberLiteral());
				moreExpected = parseBinaryElement(elements);
				break;
			case '(':
				parsePos++;
				elements.add(parseExpression());
				requireChar(')');
				moreExpected = parseBinaryElement(elements);
				break;
			case '!':
				parsePos++;
				elements.add(new Not());
				moreExpected = true;
				break;
			case LIST_LITERAL_START_CHAR:
				parsePos++;
				elements.add(parseListLiteral());
				return elements;
			case HASH_LITERAL_START_CHAR:
				parsePos++;
				elements.add(parseHashLiteral());
				return elements;
			case BOOLEAN_ESCAPE_CHAR:
				parsePos++;
				elements.add(parseBooleanLiteral());
				moreExpected = parseBinaryElement(elements);
				break;
			default:
				moreExpected = false;
			}
		} while (moreExpected);
		return elements;
	}

	/**
	 * Parse an optional binary element. If one is found, it is parsed and added
	 * to the list of elements.
	 * 
	 * @param elements
	 *            a list of elements to which we add any elements parsed
	 * @return <code>true</code> if a binary operator was found, otherwise
	 *         <code>false</code>
	 * @throws ParseException
	 *             something went wrong during parsing
	 */
	protected boolean parseBinaryElement(List elements) throws ParseException {

		skipWhitespace();
		char c = text.charAt(parsePos);

		// Look for the things we can identify by one character:
		// a single-character operator, a parenthesis, a bracket,
		// or a string literal.
		switch (c) {
		case '+':
			parsePos++;
			elements.add(new Plus());
			return true;
		case '-':
			parsePos++;
			elements.add(new Minus());
			return true;
		case '/':
			parsePos++;
			elements.add(new Divide());
			return true;
		case '*':
			parsePos++;
			elements.add(new Multiply());
			return true;
		case '%':
			parsePos++;
			elements.add(new Modulo());
			return true;
		}

		// Look for a long operator.
		if (parsePos + LONG_OPERATOR_LENGTH > textLen) {
			return false;
		}
		String possibleOpString = text.substring(parsePos, parsePos + LONG_OPERATOR_LENGTH);

		LongOperator longOp = (LongOperator) longOpMap.get(possibleOpString);

		// If we have a matching LongOperator object, ask it for a
		// corresponding Expression object, and return that object.
		if (longOp != null) {
			parsePos += LONG_OPERATOR_LENGTH;
			elements.add(longOp.parse());
			return true;
		}

		// Check for long "and" operator
		if (parsePos + 3 > textLen) {
			return false;
		}
		possibleOpString = text.substring(parsePos, parsePos + 3);
		if ("and".equals(possibleOpString)) {
			parsePos += 3;
			elements.add(new And());
			return true;
		}

		return false;
	}

	/**
	 * Parses a {@link freemarker.template.expression.DynamicKeyName}. Expects
	 * <code>parsePos</code> to be on the open bracket.
	 * 
	 * @return a <code>DynamicKeyName</code>.
	 */
	protected DynamicKeyName parseDynamicKeyName() throws ParseException {
		int startPos = parsePos;

		Expression nameExpression = parseExpression();
		if (text.charAt(parsePos) == ']') {
			parsePos++;
			return new DynamicKeyName(nameExpression);
		} else {
			throw new ParseException("Missing closing delimiter for key expression, " + "or illegal character in expression," + atChar(startPos));
		}
	}

	/**
	 * Parses the {@link freemarker.template.expression.Dot} operator. Expects
	 * <code>parsePos</code> to be one character beyond the dot itself.
	 * 
	 * @return a <code>Dot</code>.
	 */
	protected Dot parseDot() throws ParseException {
		return new Dot(parseIdentifier());
	}

	/**
	 * Parses a {@link freemarker.template.expression.StringLiteral}. Expects
	 * <code>parsePos</code> to be on the open quote. This is so we can
	 * determine whether a single- or double-quote was used to open the String,
	 * and thus what close quote we should look for.
	 * 
	 * @return a <code>StringLiteral</code>.
	 */
	protected Expression parseStringLiteral() throws ParseException {
		int startPos = parsePos;
		char quoteChar = text.charAt(parsePos);

		parsePos++;

		StringBuffer stringValueBuf = null;
		int prevPos = parsePos;
		boolean found = false;
		char currentChar;

		while (parsePos < textLen) {
			currentChar = text.charAt(parsePos);
			if (currentChar == quoteChar) {
				found = true;
				break;
			} else if (currentChar == ESCAPE_CHAR) {
				if (stringValueBuf == null) {
					stringValueBuf = new StringBuffer();
				}
				stringValueBuf.append(text.substring(prevPos, parsePos));
				parsePos++; // Skip over the escape character
				prevPos = parsePos;
			}
			parsePos++;
		}

		if (found) {
			String result;
			if (stringValueBuf != null) {
				if (parsePos > prevPos) {
					stringValueBuf.append(text.substring(prevPos, parsePos));
				}
				result = stringValueBuf.toString();
			} else {
				result = text.substring(prevPos, parsePos);
			}
			parsePos++;
			return new StringLiteral(result).resolveExpression();
		} else {
			throw new ParseException("Unterminated string literal" + atChar(startPos));
		}
	}

	/**
	 * Parses a {@link freemarker.template.expression.ListLiteral}. Expects
	 * <code>parsePos</code> to be just beyond open square bracket.
	 * 
	 * @return a <code>ListLiteral</code>.
	 */
	protected Expression parseListLiteral() throws ParseException {
		int startPos = parsePos;

		try {
			if (skipChar(LIST_LITERAL_END_CHAR)) {
				return new ListLiteral(new ArrayList(0)).resolveExpression();
			}

			Expression exp = parseExpression();

			if (skipKeyword(LIST_LITERAL_RANGE)) {
				Expression exp2 = parseExpression();

				requireChar(LIST_LITERAL_END_CHAR);
				return new ListRange(exp, exp2).resolveExpression();

			} else {
				List arguments = new ArrayList();
				arguments.add(exp);

				while (skipChar(',')) {
					arguments.add(parseExpression());
				}
				requireChar(LIST_LITERAL_END_CHAR);
				return new ListLiteral(arguments).resolveExpression();
			}
		} catch (IllegalArgumentException e) {
			String errorMessage = "List range cannot be constructed from these arguments" + atChar(startPos);
			throw new ParseException(errorMessage, e);
		} catch (ParseException e) {
			String errorMessage = "Syntax error in list literal" + atChar(startPos);
			throw new ParseException(errorMessage, e);
		} catch (TemplateException e) {
			String errorMessage = "Cannot evaluate constant list literal" + atChar(startPos);
			throw new ParseException(errorMessage, e);
		}
	}

	/**
	 * Parses a {@link freemarker.template.expression.HashLiteral}. Expects
	 * <code>parsePos</code> to be just beyond the open brace.
	 * 
	 * @return a <code>HashLiteral</code>.
	 */
	protected Expression parseHashLiteral() throws ParseException {
		int startPos = parsePos;

		try {
			if (skipChar(HASH_LITERAL_END_CHAR)) {
				return new HashLiteral(new ArrayList(0)).resolveExpression();
			}

			List arguments = new ArrayList();
			do {
				arguments.add(parseExpression());
				if ((!skipChar(',')) && (!skipChar('='))) {
					break;
				}
				arguments.add(parseExpression());
			} while (skipChar(','));

			requireChar(HASH_LITERAL_END_CHAR);
			return new HashLiteral(arguments).resolveExpression();

		} catch (IllegalArgumentException e) {
			String errorMessage = "Illegal argument list supplied to hash literal" + atChar(startPos);
			throw new ParseException(errorMessage, e);
		} catch (ParseException e) {
			String errorMessage = "Syntax error in hash literal" + atChar(startPos);
			throw new ParseException(errorMessage, e);
		} catch (TemplateException e) {
			String errorMessage = "Cannot evaluate constant hash literal" + atChar(startPos);
			throw new ParseException(errorMessage, e);
		}
	}

	/**
	 * Parses a {@link freemarker.template.expression.NumberLiteral}. Expects
	 * <code>parsePos</code> to be on the first digit or optional minus sign.
	 * 
	 * @return a <code>NumberLiteral</code>.
	 */
	protected Expression parseNumberLiteral() throws ParseException {
		int startPos = parsePos;
		char currentChar;

		// Deal with negative numbers
		if (text.charAt(parsePos) == '-') {
			parsePos++;
		}

		while (parsePos < textLen) {
			currentChar = text.charAt(parsePos);
			if (Character.isDigit(currentChar)) {
				parsePos++;
			} else {
				break;
			}
		}

		if (parsePos > startPos) {
			try {
				return new NumberLiteral(text.substring(startPos, parsePos)).resolveExpression();
			} catch (NumberFormatException e) {
				throw new ParseException("Could not convert number literal" + atChar(startPos), e);
			}
		} else {
			throw new ParseException("Unterminated number literal " + atChar(startPos));
		}
	}

	/**
	 * Parses a {@link freemarker.template.expression.BooleanLiteral} or an
	 * {@link freemarker.template.expression.EmptyLiteral}. Expects
	 * <code>parsePos</code> to be immediately following the '#' symbol.
	 * 
	 * @return a <code>BooleanLiteral</code> or <code>EmptyLiteral</code>.
	 */
	protected Expression parseBooleanLiteral() throws ParseException {
		int startPos = parsePos;

		while ((parsePos < textLen) && (Character.isLetter(text.charAt(parsePos)))) {
			parsePos++;
		}

		String value = text.substring(startPos, parsePos);

		if (value.equals(TRUE_LITERAL)) {
			return BooleanLiteral.TRUE;
		} else if (value.equals(FALSE_LITERAL)) {
			return BooleanLiteral.FALSE;
		} else if (value.equals(EMPTY_LITERAL)) {
			return EmptyLiteral.EMPTY;
		} else {
			throw new ParseException("Unknown literal encountered " + atChar(startPos));
		}
	}

	/**
	 * Parses an {@link freemarker.template.expression.Expression} and ensures
	 * that it's a {@link freemarker.template.expression.Variable}.
	 * 
	 * @return a <code>Variable</code>.
	 */
	protected Variable parseVariable() throws ParseException {
		int startPos = parsePos;

		// Tokenize the variable
		Variable element = parseIdentifier();
		if (element == null) {
			throw new ParseException("Missing variable" + atChar(startPos));
		}

		List tokens = new ArrayList();
		tokens.add(element);

		while ((element = parseVariableElement()) != null) {
			tokens.add(element);
		}

		// Build the resulting tokens into a variable expression
		Variable variableExpression;
		try {
			variableExpression = ExpressionBuilder.buildVariable(tokens);
		} catch (ParseException e) {
			throw new ParseException("Syntax error in expression" + atChar(startPos), e);
		}

		if (variableExpression instanceof Variable) {
			return (Variable) variableExpression;
		} else {
			throw new ParseException("Variable expected" + atChar(startPos));
		}
	}

	/**
	 * Retrieve the next {@link freemarker.template.expression.Variable}
	 * following <code>parsePos</code>, and ensure its a
	 * {@link freemarker.template.expression.Variable}.
	 * 
	 * @return a new <code>Variable</code>, or <code>null</code> if none is
	 *         found.
	 */
	protected Variable parseVariableElement() throws ParseException {

		skipWhitespace();
		char c = text.charAt(parsePos);

		// Look for the things we can identify by one character:
		// a single-character operator, a parenthesis, a bracket,
		// or a dot.
		switch (c) {
		case '.':
			if (text.substring(parsePos, parsePos + 2).equals(LIST_LITERAL_RANGE)) {
				return null;
			}
			parsePos++;
			return parseDot();
		case '(':
			parsePos++;
			return parseMethodCall();
		case LIST_LITERAL_START_CHAR:
			parsePos++;
			return parseDynamicKeyName();
		default:
			return null;
		}
	}

	/**
	 * Tries to parse an {@link freemarker.template.expression.Identifier}.
	 * Skips any whitespace prior to the identifier.
	 * 
	 * @return an <code>Identifier</code>.
	 */
	protected Identifier parseIdentifier() throws ParseException {
		skipWhitespace();

		int startPos = parsePos;

		if (!isIdentifierStartChar(text.charAt(startPos))) {
			throw new ParseException("Identifier expected" + atChar(startPos));
		}

		while (parsePos < textLen) {
			char c = text.charAt(parsePos);
			if (Character.isLetterOrDigit(c) || c == '_') {
				parsePos++;
				continue;
			}
			if (c == '#') {
				// The '#' character is only allowed at the end of an
				// identifier
				parsePos++;
			}
			return (Identifier) new Identifier(text.substring(startPos, parsePos)).resolveExpression();
		}
		throw new ParseException("Unexpected end of file");
	}

	/**
	 * <p>
	 * Determines whether a character is legal at the start of an identifier. An
	 * identifier is either something like a tag name, such as "foreach", or a
	 * variable name, such as "myHash".
	 * </p>
	 * 
	 * <p>
	 * In this implementation, an identifier can only start with either a letter
	 * or an underscore.
	 * </p>
	 * 
	 * <p>
	 * Note that this method does not affect identifiers inside a dynamic key
	 * name.
	 * </p>
	 */
	protected static boolean isIdentifierStartChar(char c) {
		return (Character.isLetter(c) || c == '_');
	}

	/**
	 * Parses a {@link freemarker.template.expression.MethodCall}.
	 * 
	 * @return a new <code>MethodCall</code> object initialized with the
	 *         arguments.
	 */
	protected MethodCall parseMethodCall() throws ParseException {
		int startPos = parsePos;
		List arguments = new ArrayList();

		try {
			while (true) {
				if (skipChar(')')) {
					break;
				}

				skipWhitespace();
				arguments.add(parseExpression());

				skipChar(',');
			}

		} catch (ParseException e) {
			String errorMessage = "Syntax error in MethodCall statement" + atChar(startPos);
			throw new ParseException(errorMessage, e);
		}

		return new MethodCall(arguments);
	}

	/**
	 * Parses either a variable name or a list literal. This used by the
	 * &lt;list&gt; and &lt;foreach&gt; tags.
	 * 
	 * @return an Expression representing either a variable or a list literal
	 * @throws ParseException
	 *             the next expression element couldn't be parsed
	 */
	protected Expression parseVariableOrList() throws ParseException {
		skipWhitespace();

		char nextChar = text.charAt(parsePos);

		if (nextChar == LIST_LITERAL_START_CHAR) {
			parsePos++;
			return parseListLiteral();
		}

		if (isIdentifierStartChar(nextChar)) {
			return parseVariable();
		}

		throw new ParseException("Expected variable or list literal");
	}

	// -----------------------------------------------------------------------
	// Methods for parsing individual FreeMarker tags
	// -----------------------------------------------------------------------

	/**
	 * Parses a {@link freemarker.template.instruction.VariableInstruction}.
	 * Expects <code>parsePos</code> to be at the beginning of the
	 * <code>VAR_INSTR_START_CHARS</code>.
	 * 
	 * @return a <code>VariableInstruction</code>.
	 */
	protected VariableInstruction parseVariableInstruction() throws ParseException {
		int startPos = parsePos;
		parsePos += VAR_INSTR_START_CHARS.length();

		Expression expression = parseExpression();
		if (text.charAt(parsePos) == VAR_INSTR_END_CHAR) {
			parsePos++;
			try {
				return new VariableInstruction(expression);
			} catch (IllegalArgumentException e) {
				String errorMessage = "Variable expression wasn't a scalar or number" + atChar(startPos);
				throw new ParseException(errorMessage, e);
			}
		} else {
			throw new ParseException("Missing closing delimiter for expression, " + "or illegal character in expression," + atChar(startPos));
		}
	}

	/**
	 * Parses a {@link freemarker.template.instruction.ListInstruction}'s start
	 * tag.
	 * 
	 * @return a <code>ListInstruction</code> initialized with the values from
	 *         the tag.
	 */
	protected ListInstruction parseListStart() throws ParseException {
		int startPos = parsePos;
		Expression listExpression;
		Identifier indexVariable;

		try {
			// Get the variable representing the object to be listed.
			listExpression = parseVariableOrList();

			// Make sure the expression stopped at the list index keyword.
			if (!skipKeyword(LIST_INDEX_KEYWORD)) {
				throw new ParseException("Expected '" + LIST_INDEX_KEYWORD + '\'');
			}
			requireWhitespace();

			// Get the index variable.
			indexVariable = parseIdentifier();
		} catch (ParseException e) {
			String errorMessage = "Syntax error in list statement" + atChar(startPos);
			throw new ParseException(errorMessage, e);
		}
		try {
			return new ListInstruction(listExpression, indexVariable);
		} catch (IllegalArgumentException e) {
			String errorMessage = "List expression wasn't a list" + atChar(startPos);
			throw new ParseException(errorMessage, e);
		}
	}

	/**
	 * Parses a {@link freemarker.template.instruction.ListInstruction}'s start
	 * tag with the "foreach" keyword.
	 * 
	 * @return a <code>ListInstruction</code> initialized with the values from
	 *         the tag.
	 */
	protected ListInstruction parseForeachStart() throws ParseException {
		int startPos = parsePos;
		Expression listExpression;
		Identifier indexVariable;

		try {
			// Get the index variable.
			indexVariable = parseIdentifier();

			// Make sure the expression stopped at the foreach index keyword.
			if (!skipKeyword(FOREACH_INDEX_KEYWORD)) {
				throw new ParseException("Expected '" + FOREACH_INDEX_KEYWORD + '\'');
			}
			requireWhitespace();

			// Get the variable representing the object to be listed.
			listExpression = parseVariableOrList();

		} catch (ParseException e) {
			String errorMessage = "Syntax error in list statement" + atChar(startPos);
			throw new ParseException(errorMessage, e);
		}
		// foreach and list are the same, so we just return a ListInstruction
		return new ListInstruction(listExpression, indexVariable);
	}

	/**
	 * Parses an {@link freemarker.template.instruction.IfElseInstruction}'s
	 * start tag.
	 * 
	 * @return an <code>IfElseInstruction</code> initialized with the expression
	 *         in the tag.
	 */
	protected IfElseInstruction parseIfStart() throws ParseException {
		int startPos = parsePos;
		Expression condition;

		try {
			condition = parseExpression();
		} catch (ParseException e) {
			String errorMessage = "Syntax error in if statement" + atChar(startPos);
			throw new ParseException(errorMessage, e);
		}
		return new IfElseInstruction(condition);
	}

	/**
	 * Parses an {@link freemarker.template.instruction.IfInstruction}
	 * &lt;elseif&gt; tag.
	 * 
	 * @return an <code>IfInstruction</code> initialised with the expression in
	 *         the tag.
	 */
	protected IfInstruction parseElseIf() throws ParseException {
		int startPos = parsePos;
		Expression condition;

		try {
			condition = parseExpression();
		} catch (ParseException e) {
			String errorMessage = "Syntax error in if statement" + atChar(startPos);
			throw new ParseException(errorMessage, e);
		}
		return new IfInstruction(condition);
	}

	/**
	 * Parses an {@link freemarker.template.instruction.AssignInstruction} or
	 * {@link freemarker.template.instruction.AssignBlockInstruction}'s tag.
	 * Determines whether the assign is an expression assignment or a block
	 * assignment and returns the appropriate instruction.
	 * 
	 * @return an <code>AssignInstruction</code> or
	 *         <code>AssignBlockInstruction</code> initialized with the values
	 *         from the tag.
	 */
	protected Instruction parseAssign() throws ParseException {
		int startPos = parsePos;
		Variable variable;
		Expression value;

		try {
			// Get the variable to assign to.
			variable = parseVariable();

			// Skip an optional equals sign.
			skipChar('=');

			// Are we a block instruction?
			if (skipToTagEnd()) {
				return new AssignBlockInstruction(variable);
			}

			// Get the variable or literal to be assigned.
			value = parseExpression();
		} catch (ParseException e) {
			String errorMessage = "Syntax error in assignment" + atChar(startPos);
			throw new ParseException(errorMessage, e);
		}
		try {
			return new AssignInstruction(variable, value);
		} catch (IllegalArgumentException e) {
			String errorMessage = "Cannot assign variable to iterator variable" + atChar(startPos);
			throw new ParseException(errorMessage, e);
		}
	}

	/**
	 * Parses an {@link freemarker.template.instruction.IncludeInstruction}'s
	 * tag.
	 * 
	 * @return an <code>IncludeInstruction</code> initialized with the name in
	 *         the tag.
	 */
	protected IncludeInstruction parseInclude() throws ParseException {
		int startPos = parsePos;
		Expression templateName;
		Expression templateType = null;

		try {
			templateName = parseExpression();
		} catch (ParseException e) {
			String errorMessage = "Syntax error in include statement" + atChar(startPos);
			throw new ParseException(errorMessage, e);
		}

		// Optional second argument: the type of template to include.
		skipChar(';');
		skipWhitespace();
		if (text.charAt(parsePos) == TAG_END_CHAR) {
			try {
				return new IncludeInstruction(template, templateName);
			} catch (IllegalArgumentException e) {
				String errorMessage = "Unexpected type for template name in include instruction" + atChar(startPos);
				throw new ParseException(errorMessage, e);
			}
		}

		try {
			skipKeyword("type");
			requireChar('=');
			templateType = parseExpression();
		} finally {
			try {
				return new IncludeInstruction(template, templateName, templateType);
			} catch (IllegalArgumentException e) {
				String errorMessage = "Unexpected type for template name in include instruction" + atChar(startPos);
				throw new ParseException(errorMessage, e);
			}
		}

	}

	/**
	 * Parses a {@link freemarker.template.instruction.SwitchInstruction}'s tag.
	 * 
	 * @return a <code>SwitchInstruction</code> initialized with the expression
	 *         in the tag.
	 */
	protected SwitchInstruction parseSwitch() throws ParseException {
		int startPos = parsePos;
		Expression testExpression;

		try {
			// Get the test variable.
			testExpression = parseExpression();
		} catch (ParseException e) {
			String errorMessage = "Syntax error in switch statement" + atChar(startPos);
			throw new ParseException(errorMessage, e);
		}
		try {
			return new SwitchInstruction(testExpression);
		} catch (IllegalArgumentException e) {
			String errorMessage = "Switch expression was neither a scalar nor a number" + atChar(startPos);
			throw new ParseException(errorMessage, e);
		}
	}

	/**
	 * Parses a {@link freemarker.template.instruction.CaseInstruction}'s tag.
	 * 
	 * @return a <code>CaseInstruction</code> initialized with the expression in
	 *         the tag.
	 */
	protected CaseInstruction parseCase() throws ParseException {
		int startPos = parsePos;
		Expression expression;

		try {
			// Get the expression.
			expression = parseExpression();
		} catch (ParseException e) {
			String errorMessage = "Syntax error in case statement" + atChar(startPos);
			throw new ParseException(errorMessage, e);
		}
		try {
			return new CaseInstruction(expression);
		} catch (IllegalArgumentException e) {
			String errorMessage = "Illegal case expression in switch statement" + atChar(startPos);
			throw new ParseException(errorMessage, e);
		}
	}

	/**
	 * Parses a {@link freemarker.template.instruction.FunctionInstruction}'s
	 * tag.
	 * 
	 * @return a <code>FunctionInstruction</code> intialized with the argument
	 *         names in the tag.
	 */
	protected FunctionInstruction parseFunction() throws ParseException {
		int startPos = parsePos;
		Identifier functionName;
		List argumentNames = new ArrayList();
		boolean localScope = false;

		try {
			// Get the function's name.
			functionName = parseIdentifier();

			// Parse argument names.
			requireChar('(');
			while (true) {
				if (skipChar(')')) {
					break;
				}

				argumentNames.add(parseIdentifier());

				skipChar(',');
			}

			if (!skipToTagEnd()) {
				// Another keyword to parse -- global or local variable scope
				if (skipKeyword(LOCAL_KEYWORD)) {
					localScope = true;
				} else if (!skipKeyword(GLOBAL_KEYWORD)) {
					String errorMessage = "Incorrect keyword in function declaration" + atChar(startPos);
					throw new ParseException(errorMessage);
				}
			}
		} catch (ParseException e) {
			String errorMessage = "Syntax error in function declaration" + atChar(startPos);
			throw new ParseException(errorMessage, e);
		}
		return new FunctionInstruction(functionName, argumentNames, localScope);
	}

	/**
	 * Parses a {@link freemarker.template.instruction.CallInstruction}'s tag.
	 * This essentially looks like a method call, so we parse it the same way.
	 * 
	 * @return a <code>CallInstruction</code> initialized with the arguments in
	 *         the tag.
	 */
	protected CallInstruction parseCall() throws ParseException {
		int startPos = parsePos;
		Variable functionCall;

		functionCall = parseVariable();

		if (functionCall instanceof MethodCall) {
			return new CallInstruction((MethodCall) functionCall);
		}
		String errorMessage = "Syntax error in call statement" + atChar(startPos);
		throw new ParseException(errorMessage);

	}

	/**
	 * Parses a {@link freemarker.template.instruction.TransformInstruction}'s
	 * tag. This tag consists of one parameter: the variable representing the
	 * {@link freemarker.template.TemplateTransformModel} to be used for the
	 * transformation.
	 * 
	 * @return a <code>TransformInstruction</code>
	 */
	protected TransformInstruction parseTransformStart() throws ParseException {
		Variable transformVariable;
		int startPos = parsePos;

		try {
			// Get the index variable.
			skipWhitespace();

			// Get the variable representing the object to be listed.
			transformVariable = parseVariable();

		} catch (ParseException e) {
			String errorMessage = "Syntax error in transform statement" + atChar(startPos);
			throw new ParseException(errorMessage, e);
		}

		return new TransformInstruction(transformVariable);
	}

	// -----------------------------------------------------------------------
	// Methods for simple buffer management
	// -----------------------------------------------------------------------

	/**
	 * Advances beyond any whitespace; then, if the next character matches a
	 * given character, advances beyond it and returns <code>true</code>,
	 * otherwise returns <code>false</code>.
	 * 
	 * @return <code>true</code> if the character was found, otherwise
	 *         <code>false</code>
	 */
	protected boolean skipChar(char c) throws ParseException {
		skipWhitespace();
		if (text.charAt(parsePos) == c) {
			parsePos++;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Requires a given character, optionally preceded by by whitespace.
	 * 
	 * @throws ParseException
	 *             the required character couldn't be found
	 */
	protected void requireChar(char c) throws ParseException {
		if (!skipChar(c)) {
			throw new ParseException("Character " + c + "could not be found");
		}
	}

	/**
	 * Skip over a given keyword. Skips over any whitespace prior to the keyword
	 * itself.
	 * 
	 * @param keyword
	 *            the keyword to skip over
	 * @return whether we found the keyword
	 * @throws ParseException
	 *             there are no more characters before the keyword is expected.
	 */
	protected boolean skipKeyword(String keyword) throws ParseException {
		int size = keyword.length();

		skipWhitespace();

		int endIndex = parsePos + size;

		if (endIndex >= textLen) {
			return false;
		}

		if (!text.substring(parsePos, endIndex).equals(keyword)) {
			return false;
		}

		parsePos = endIndex;
		return true;
	}

	/**
	 * Advances <code>parsePos</code> beyond any whitespace.
	 * 
	 * @throws ParseException
	 *             there are no more characters after whitespace has been
	 *             skipped.
	 */
	protected void skipWhitespace() throws ParseException {
		while (parsePos < textLen) {
			if (!Character.isWhitespace(text.charAt(parsePos))) {
				return;
			}
			parsePos++;
		}
		throw new ParseException("Unexpected end of file");
	}

	/**
	 * Advances <code>parsePos</code> beyond required whitespace.
	 * 
	 * @throws ParseException
	 *             there are no more characters after whitespace has been
	 *             skipped, or if no whitespace could be found.
	 */
	protected void requireWhitespace() throws ParseException {
		int startPos = parsePos;

		skipWhitespace();
		if (parsePos == startPos) {
			throw new ParseException("Whitespace expected" + atChar(startPos));
		}
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a <code>String</code> representation of the object
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer(75);
		buffer.append("StandardTemplateParser, ");
		buffer.append(textLen);
		buffer.append(" characters to parse, ");
		buffer.append(textLen - parsePos);
		buffer.append(" remaining.");
		return buffer.toString();
	}
}

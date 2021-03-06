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
 */

package freemarker.template.instruction;

import java.io.Serializable;

import freemarker.template.TemplateProcessor;
import freemarker.template.compiler.ParseException;
import freemarker.template.compiler.TemplateBuilder;

/**
 * A superclass for instructions that have a body and a single end instruction,
 * and that can produce output.
 * 
 * @version $Id: GenericStartInstruction.java 987 2004-10-05 10:13:24Z run2000 $
 */
public abstract class GenericStartInstruction implements Instruction, TemplateProcessor, ContainerInstruction, Serializable {

	/**
	 * The template body to be processed
	 * 
	 * @serial the template processor to be called for this instruction
	 */
	protected TemplateProcessor body;

	/** Serialization UUID for this class. */
	private static final long serialVersionUID = -5143432053907326525L;

	/**
	 * Is this an end instruction?
	 * 
	 * @return <code>false</code>, indicating that this is not an end
	 *         instruction
	 */
	public boolean isEndInstruction() {
		return false;
	}

	/**
	 * Determine what type of end instruction this is, if any.
	 * 
	 * @return <code>NONE</code>, indicating that this is not an end instruction
	 */
	public int getEndType() {
		return Instruction.NONE;
	}

	/**
	 * Sets the body to be generated by this instruction.
	 * 
	 * @param body
	 *            the content to be generated
	 */
	public void setBody(TemplateProcessor body) {
		this.body = body;
	}

	/**
	 * A {@link freemarker.template.compiler.TemplateBuilder} can call this
	 * method to have an <code>Instruction</code> call it back to be built. This
	 * implementation calls the
	 * {@link freemarker.template.compiler.TemplateBuilder#buildStatement(GenericStartInstruction)}
	 * method, passing back a reference to itself. This approach is intended to
	 * make type-checking of {@link Instruction} objects unnecessary.
	 * 
	 * @param builder
	 *            the builder to be called back by this method
	 */
	public TemplateProcessor callBuilder(TemplateBuilder builder) throws ParseException {
		return builder.buildStatement(this);
	}

}

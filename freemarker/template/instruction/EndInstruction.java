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
import freemarker.template.compiler.TemplateBuilder;

/**
 * Represents an end instruction. The particular type of end instruction is
 * determined by the <code>endType</code> variable. Values of
 * <code>endType</code> can be taken from the {@link Instruction} interface.
 * 
 * @version $Id: EndInstruction.java 987 2004-10-05 10:13:24Z run2000 $
 */
public final class EndInstruction implements Instruction, Serializable {

	/** @serial the type of end instruction encountered */
	private final int endType;

	/** Serialization UUID for this class. */
	private static final long serialVersionUID = -7462893397220797531L;

	/**
	 * Constructor that takes one of the end types from the {@link Instruction}
	 * interface.
	 * 
	 * @param endType
	 *            the type of end instruction
	 */
	public EndInstruction(int endType) {
		this.endType = endType;
	}

	/**
	 * Is this an end instruction?
	 * 
	 * @return <code>true</code>, to indicate this is an end instruction
	 */
	public boolean isEndInstruction() {
		return true;
	}

	/**
	 * Return the end type that this instruction represents.
	 * 
	 * @return the type of this end instruction
	 */
	public int getEndType() {
		return endType;
	}

	/**
	 * A {@link freemarker.template.compiler.TemplateBuilder} can call this
	 * method to have an <code>Instruction</code> call it back to be built. For
	 * end instructions, there is nothing more to be build, so return
	 * immediately.
	 * 
	 * @param builder
	 *            the builder to be called back by this method
	 */
	public TemplateProcessor callBuilder(TemplateBuilder builder) {
		return null;
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a <code>String</code> representation of the object.
	 */
	public String toString() {
		return "End instruction " + endType;
	}
}

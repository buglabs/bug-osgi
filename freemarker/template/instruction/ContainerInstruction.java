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

/**
 * An implementation for instructions that contain other elements, such as text
 * or other instructions.
 * 
 * @version $Id: ContainerInstruction.java 987 2004-10-05 10:13:24Z run2000 $
 */
public interface ContainerInstruction {

	/**
	 * Is this the right kind of instruction for the given
	 * {@link EndInstruction}?
	 * 
	 * @param endInstruction
	 *            the end instruction we're testing
	 * @return <code>true</code> if the <code>EndInstruction</code> is the right
	 *         kind for this <code>ContainerInstruction</code>, otherwise
	 *         <code>false</code>
	 */
	public boolean testEndInstruction(Instruction endInstruction);

}

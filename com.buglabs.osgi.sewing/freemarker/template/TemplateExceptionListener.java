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

package freemarker.template;

/**
 * Interface for listening for {@link TemplateExceptionEvent}s. These are fired
 * whenever FM-Classic encounters a situation that it can't deal with normally
 * at runtime.
 * 
 * @version $Id: TemplateExceptionListener.java 1087 2005-08-28 12:37:29Z
 *          run2000 $
 * @see TemplateExceptionEvent
 * @see TemplateRuntimeHandler
 */
public interface TemplateExceptionListener extends java.util.EventListener {

	/**
	 * This method is called whenever a {@link TemplateExceptionEvent} is
	 * generated by a FM-Classic template. Implement this method to decide how
	 * to respond to such events.
	 */
	public void exceptionThrown(TemplateExceptionEvent e) throws TemplateException;

}

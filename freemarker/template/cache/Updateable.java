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

package freemarker.template.cache;

/**
 * An interface for objects that need to be reminded to update themselves from
 * time to time.
 * 
 * <p>
 * Two update styles are supported: <i>blank</i> and <i>named</i>. A
 * <i>blank</i> update leaves the decision about what has to be updated to the
 * implementation whereas a <i>named</i> update guarantees that only objects
 * with a given name are updated.
 * </p>
 * 
 * <p>
 * Blank updates are well suited for recurring events where a set of objects has
 * to be refreshed periodically. Named updates correspond to "push" methodology
 * where the decision to update a specific set of objects is made on an ad hoc
 * basis.
 * </p>
 * 
 * @see freemarker.template.FileTemplateCache
 * @version $Id: Updateable.java 987 2004-10-05 10:13:24Z run2000 $
 */

public interface Updateable {

	/**
	 * Asks for a "blank" update. It is up to the implementation to determine
	 * what has to be updated.
	 * 
	 * @throws InterruptedException
	 *             The current thread was interrupted during the update. Callers
	 *             should either throw the exception back up the call stack, or
	 *             set the interrupted status by using the
	 *             <code>Thread.currentThread().interrupt()</code> method.
	 */
	public void update() throws InterruptedException;

	/**
	 * Asks for the named object to be updated.
	 * 
	 * @param name
	 *            the name of the object to update
	 * @throws InterruptedException
	 *             The current thread was interrupted during the update. Callers
	 *             should either throw the exception back up the call stack, or
	 *             set the interrupted status by using the
	 *             <code>Thread.currentThread().interrupt()</code> method.
	 */
	public void update(String name) throws InterruptedException;

	/**
	 * Asks for the named object to be updated.
	 * 
	 * @param name
	 *            the name of the object to update
	 * @param type
	 *            the type of the object to update
	 * @throws InterruptedException
	 *             The current thread was interrupted during the update. Callers
	 *             should either throw the exception back up the call stack, or
	 *             set the interrupted status by using the
	 *             <code>Thread.currentThread().interrupt()</code> method.
	 */
	public void update(String name, String type) throws InterruptedException;
}

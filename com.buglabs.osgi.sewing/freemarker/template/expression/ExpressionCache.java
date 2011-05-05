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

package freemarker.template.expression;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * A simple expression cache. All keys and values are stored as WeakReference
 * objects, allowing cached items to be freed in low-memory situations.
 * 
 * @version $Id: ExpressionCache.java 1184 2005-10-13 12:24:00Z run2000 $
 */
public final class ExpressionCache {

	/**
	 * A weak map of Expressions that is used for storing canonical template
	 * identifiers. We store this in a weak hash map so that they can be
	 * eventually reclaimed by the garbage collector in the event that a
	 * template expires.
	 */
	private static final Map expressionCache = Collections.synchronizedMap(new WeakHashMap());

	/** Private constructor indicating this is a static class. */
	private ExpressionCache() {
	}

	/**
	 * Resolves the current expression, possibly into a different expression
	 * object. This is loosely equivalent to the serialization protocol's
	 * <code>readResolve</code> method. Situations where this may be used are:
	 * <ul>
	 * <li>Caching frequently-used expression objects</li>
	 * <li>Evaluating constant expressions, and returning a constant reference</li>
	 * </ul>
	 */
	public static Expression cacheExpression(Expression expr) {
		WeakReference ref = (WeakReference) expressionCache.get(expr);
		Expression interned = null;

		if (ref != null) {
			interned = (Expression) ref.get();
		}

		if (interned == null) {
			interned = expr;
			ref = new WeakReference(interned);
			expressionCache.put(interned, ref);
		}

		return interned;
	}
}

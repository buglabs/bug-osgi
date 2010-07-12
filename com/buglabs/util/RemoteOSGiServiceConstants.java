/*******************************************************************************
 * Copyright (c) 2008, 2009 Bug Labs, Inc.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *    - Redistributions of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 *    - Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *    - Neither the name of Bug Labs, Inc. nor the names of its contributors may be
 *      used to endorse or promote products derived from this software without
 *      specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/
package com.buglabs.util;

/**
 * <p>
 * RemoteOSGiService provides transparent access to services on remote service
 * platforms. It uses SLP as underlying discovery protocol. Local services can
 * be registered for remoting, applications can register listeners for
 * <code>ServiceTypes</code> to be informed whenever matching services have been
 * discovered.
 * </p>
 * <p>
 * As soon as a service has been discovered and the listener has been informed,
 * the application can fetch the service. In the default case, the service
 * interface is transferred to the receiving peer together with an optional
 * smart proxy class and optional injections. The service then builds a proxy
 * bundle and registers it with the local framework so that the application can
 * get a service reference as if the service was local. Internally, all methods
 * of the service interface are implemented as remote method calls.
 * </p>
 * <p>
 * Services can define smart proxies to move some parts of the code to the
 * client. This is done by an abstract class. All implemented method will be
 * executed on the client, abstract methods will be implemented by remote method
 * calls. Moving parts of the code to the client can be useful for saving
 * service provider platform's resources.
 * </p>
 * <p>
 * Injections are used if the service interface uses classes as method arguments
 * that are not expected to be present on client side. These classes will be
 * automatically injected into the proxy bundle. The registrator can manually
 * inject additional classes.
 * </p>
 * <p>
 * With version 0.5, there is also the possibility to register a service with
 * the MIGRATE_BUNDLE policy. In this case, the bundle that provides the service
 * is moved to the requesting peer.
 * </p>
 * 
 * @author Jan S. Rellermeyer, ETH Zurich
 * @since 0.1
 */
public interface RemoteOSGiServiceConstants {

	// public constants for service registrations

	/**
	 * this property has to be set in order to release a service for remote
	 * access. Currently, the following two policies are supported.
	 * 
	 * @since 0.5
	 */
	String R_OSGi_REGISTRATION = "service.remote.registration"; //$NON-NLS-1$

	/**
	 * policy "service_proxy" means: dynamically build a proxy at client side.
	 * (default)
	 * 
	 * @since 0.5
	 * @deprecated With the new model, service proxies is the only supported
	 *             policy. Any value set to the R_OSGi_REGISTRATION policy will
	 *             have the effect of SERVICE_PROXY_POLICY;
	 */
	String SERVICE_PROXY_POLICY = "service_proxy"; //$NON-NLS-1$

	/**
	 * Can be set to use a smart proxy. Smart proxies have to be abstract
	 * classes implementing the service interface. All abstract methods are
	 * implemented as remote calls, implemented methods remain untouched. This
	 * allows to perform some of the work on client side (inside of implemented
	 * methods). The value of this property in the service property dictionary
	 * has to be a the name of a class.
	 * 
	 * @since 0.5
	 */
	String SMART_PROXY = "service.remote.smartproxy"; //$NON-NLS-1$

	/**
	 * For special purposes, the service can decide to inject other classes into
	 * the proxy bundle that is dynamically created on the client side. For
	 * instance, if types are use as arguments of method calls that are not part
	 * of the standard execution environment and the service does not want to
	 * rely on assumption that the corresponding classes are present on client
	 * side, it can inject these classes. The value of this property in the
	 * service property dictionary has to be an array of <code>Class</code>
	 * objects.
	 * 
	 * @since 0.5
	 */
	String INJECTIONS = "service.remote.injections"; //$NON-NLS-1$

	/**
	 * property for registration of a service UI component that gived the user a
	 * presentation of the service. The value of the property in the service
	 * property dictionary has to be a name of a class implementing
	 * <code>org.service.proposition.remote.ServiceUIComponent</code>. When this
	 * property is set, the presentation is injected into the bundle and the
	 * R-OSGi ServiceUI can display the presentation when the service is
	 * discovered.
	 * 
	 * @since 0.5
	 */
	String PRESENTATION = "service.presentation"; //$NON-NLS-1$

	/**
	 * the property key for the host name of the remote service. This constant
	 * is set by R-OSGi when a service is transferred to a remote peer. So to
	 * find out whether a service is provided by an R-OSGi proxy, check for the
	 * presence of this key in the service properties.
	 * 
	 * @since 1.0
	 */
	String SERVICE_URI = "service.uri"; //$NON-NLS-1$
}

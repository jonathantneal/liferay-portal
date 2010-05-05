/**
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.kernel.jmx;

import java.util.HashMap;
import java.util.Map;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

/**
 * <a href="MBeanRegistry.java.html"><b><i>View Source</i></b></a>
 *
 * @author Michael C. Han
 */
public class MBeanRegistry {

	public ObjectName getObjectName(String objectNameCacheKey) {
		return _objectNameCache.get(objectNameCacheKey);
	}

	public ObjectInstance register(
			String objectNameCacheKey, Object object, ObjectName objectName)
		throws InstanceAlreadyExistsException, MBeanRegistrationException,
			   NotCompliantMBeanException {

		ObjectInstance objectInstance = _mBeanServer.registerMBean(
			object, objectName);

		_objectNameCache.put(
			objectNameCacheKey, objectInstance.getObjectName());

		return objectInstance;
	}

	public void replace(
			String objectCacheKey, Object object, ObjectName objectName)
		throws Exception {

		try {
			register(objectCacheKey, object, objectName);
		}
		catch (InstanceAlreadyExistsException iaee) {
			unregister(objectCacheKey, objectName);

			register(objectCacheKey, object, objectName);
		}
	}

	public void setMBeanServer(MBeanServer mBeanServer) {
		_mBeanServer = mBeanServer;
	}

	public void unregister(
			String objectNameCacheKey, ObjectName defaultObjectName)
		throws InstanceNotFoundException, MBeanRegistrationException {

		ObjectName objectName = _objectNameCache.get(objectNameCacheKey);

		if (objectName == null) {
			_mBeanServer.unregisterMBean(defaultObjectName);
		}
		else {
			_objectNameCache.remove(objectNameCacheKey);

			_mBeanServer.unregisterMBean(objectName);
		}
	}

	private MBeanServer _mBeanServer;
	private Map<String, ObjectName> _objectNameCache =
		new HashMap<String, ObjectName>();

}
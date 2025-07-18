/*
 * Copyright (c) 2024 MyFintech Payment Service
 * All rights reserved.
 *
 * This software is proprietary and confidential. Unauthorized copying of this file,
 * via any medium, is strictly prohibited.
 * @author : Dhanuka Ranasinghe
 * @since : Date: 11/07/2025
 */
package org.myfintech.payment.util.jaxb;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Pool manager for JAXBContext instances to improve performance. JAXBContext
 * creation is expensive, so we pool and reuse instances.
 */
@Slf4j
@Component
public class JAXBContextPool {

	private static final int DEFAULT_MAX_TOTAL = 20;
	private static final int DEFAULT_MAX_IDLE = 10;
	private static final int DEFAULT_MIN_IDLE = 2;
	private static final Duration DEFAULT_MAX_WAIT = Duration.ofSeconds(5);

	private final ConcurrentMap<Class<?>, JAXBContext> contextCache = new ConcurrentHashMap<>();
	private final ConcurrentMap<Class<?>, GenericObjectPool<Unmarshaller>> unmarshallerPools = new ConcurrentHashMap<>();
	private final ConcurrentMap<Class<?>, GenericObjectPool<Marshaller>> marshallerPools = new ConcurrentHashMap<>();

	@PostConstruct
	public void init() {
		log.info("Initializing JAXB Context Pool");
	}

	@PreDestroy
	public void destroy() {
		log.info("Destroying JAXB Context Pool");
		unmarshallerPools.values().forEach(pool -> {
			try {
				pool.close();
			} catch (Exception e) {
				log.error("Error closing unmarshaller pool", e);
			}
		});
		marshallerPools.values().forEach(pool -> {
			try {
				pool.close();
			} catch (Exception e) {
				log.error("Error closing marshaller pool", e);
			}
		});
	}

	/**
	 * Borrows an Unmarshaller from the pool for the given class.
	 * 
	 * @param clazz the class to create unmarshaller for
	 * @return a pooled Unmarshaller instance
	 * @throws Exception if unable to borrow from pool
	 */
	public Unmarshaller borrowUnmarshaller(Class<?> clazz) throws Exception {
		GenericObjectPool<Unmarshaller> pool = unmarshallerPools.computeIfAbsent(clazz, this::createUnmarshallerPool);
		return pool.borrowObject();
	}

	/**
	 * Returns an Unmarshaller back to the pool.
	 * 
	 * @param clazz        the class the unmarshaller was created for
	 * @param unmarshaller the unmarshaller to return
	 */
	public void returnUnmarshaller(Class<?> clazz, Unmarshaller unmarshaller) {
		GenericObjectPool<Unmarshaller> pool = unmarshallerPools.get(clazz);
		if (pool != null && unmarshaller != null) {
			try {
				pool.returnObject(unmarshaller);
			} catch (Exception e) {
				log.error("Error returning unmarshaller to pool", e);
			}
		}
	}

	/**
	 * Borrows a Marshaller from the pool for the given class.
	 * 
	 * @param clazz the class to create marshaller for
	 * @return a pooled Marshaller instance
	 * @throws Exception if unable to borrow from pool
	 */
	public Marshaller borrowMarshaller(Class<?> clazz) throws Exception {
		GenericObjectPool<Marshaller> pool = marshallerPools.computeIfAbsent(clazz, this::createMarshallerPool);
		return pool.borrowObject();
	}

	/**
	 * Returns a Marshaller back to the pool.
	 * 
	 * @param clazz      the class the marshaller was created for
	 * @param marshaller the marshaller to return
	 */
	public void returnMarshaller(Class<?> clazz, Marshaller marshaller) {
		GenericObjectPool<Marshaller> pool = marshallerPools.get(clazz);
		if (pool != null && marshaller != null) {
			try {
				pool.returnObject(marshaller);
			} catch (Exception e) {
				log.error("Error returning marshaller to pool", e);
			}
		}
	}

	/**
	 * Gets or creates a JAXBContext for the given class. Contexts are cached and
	 * reused.
	 */
	private JAXBContext getOrCreateContext(Class<?> clazz) {
		return contextCache.computeIfAbsent(clazz, k -> {
			try {
				log.debug("Creating new JAXBContext for class: {}", k.getName());
				return JAXBContext.newInstance(k);
			} catch (JAXBException e) {
				throw new RuntimeException("Failed to create JAXBContext for " + k.getName(), e);
			}
		});
	}

	private GenericObjectPool<Unmarshaller> createUnmarshallerPool(Class<?> clazz) {
		GenericObjectPoolConfig<Unmarshaller> config = new GenericObjectPoolConfig<>();
		config.setMaxTotal(DEFAULT_MAX_TOTAL);
		config.setMaxIdle(DEFAULT_MAX_IDLE);
		config.setMinIdle(DEFAULT_MIN_IDLE);
		config.setMaxWait(DEFAULT_MAX_WAIT);
		config.setTestOnBorrow(true);
		config.setTestOnReturn(true);

		return new GenericObjectPool<>(new UnmarshallerFactory(clazz), config);
	}

	private GenericObjectPool<Marshaller> createMarshallerPool(Class<?> clazz) {
		GenericObjectPoolConfig<Marshaller> config = new GenericObjectPoolConfig<>();
		config.setMaxTotal(DEFAULT_MAX_TOTAL);
		config.setMaxIdle(DEFAULT_MAX_IDLE);
		config.setMinIdle(DEFAULT_MIN_IDLE);
		config.setMaxWait(DEFAULT_MAX_WAIT);
		config.setTestOnBorrow(true);
		config.setTestOnReturn(true);

		return new GenericObjectPool<>(new MarshallerFactory(clazz), config);
	}

	/**
	 * Factory for creating Unmarshaller instances.
	 */
	private class UnmarshallerFactory extends BasePooledObjectFactory<Unmarshaller> {
		private final Class<?> clazz;

		public UnmarshallerFactory(Class<?> clazz) {
			this.clazz = clazz;
		}

		@Override
		public Unmarshaller create() throws Exception {
			JAXBContext context = getOrCreateContext(clazz);
			return context.createUnmarshaller();
		}

		@Override
		public PooledObject<Unmarshaller> wrap(Unmarshaller unmarshaller) {
			return new DefaultPooledObject<>(unmarshaller);
		}

		@Override
		public void passivateObject(PooledObject<Unmarshaller> p) throws Exception {
			// Reset unmarshaller to clean state
			Unmarshaller unmarshaller = p.getObject();
			unmarshaller.setEventHandler(null);
			unmarshaller.setSchema(null);
			// Clear any other stateful properties
		}

		@Override
		public boolean validateObject(PooledObject<Unmarshaller> p) {
			return p.getObject() != null;
		}
	}

	/**
	 * Factory for creating Marshaller instances.
	 */
	private class MarshallerFactory extends BasePooledObjectFactory<Marshaller> {
		private final Class<?> clazz;

		public MarshallerFactory(Class<?> clazz) {
			this.clazz = clazz;
		}

		@Override
		public Marshaller create() throws Exception {
			JAXBContext context = getOrCreateContext(clazz);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			return marshaller;
		}

		@Override
		public PooledObject<Marshaller> wrap(Marshaller marshaller) {
			return new DefaultPooledObject<>(marshaller);
		}

		@Override
		public void passivateObject(PooledObject<Marshaller> p) throws Exception {
			// Reset marshaller to clean state
			Marshaller marshaller = p.getObject();
			marshaller.setEventHandler(null);
			marshaller.setSchema(null);
			// Reset to default properties
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		}

		@Override
		public boolean validateObject(PooledObject<Marshaller> p) {
			return p.getObject() != null;
		}
	}

	/**
	 * Configuration class for pool settings.
	 */
	public static class PoolConfig {
		private int maxTotal = DEFAULT_MAX_TOTAL;
		private int maxIdle = DEFAULT_MAX_IDLE;
		private int minIdle = DEFAULT_MIN_IDLE;
		private Duration maxWait = DEFAULT_MAX_WAIT;

		// Getters and setters
		public int getMaxTotal() {
			return maxTotal;
		}

		public void setMaxTotal(int maxTotal) {
			this.maxTotal = maxTotal;
		}

		public int getMaxIdle() {
			return maxIdle;
		}

		public void setMaxIdle(int maxIdle) {
			this.maxIdle = maxIdle;
		}

		public int getMinIdle() {
			return minIdle;
		}

		public void setMinIdle(int minIdle) {
			this.minIdle = minIdle;
		}

		public Duration getMaxWait() {
			return maxWait;
		}

		public void setMaxWait(Duration maxWait) {
			this.maxWait = maxWait;
		}
	}
}
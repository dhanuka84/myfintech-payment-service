/*
 * Copyright (c) 2024 MyFintech Payment Service
 * All rights reserved.
 *
 * This software is proprietary and confidential. Unauthorized copying of this file,
 * via any medium, is strictly prohibited.
 * @author : Dhanuka Ranasinghe
 * @since : Date: 11/07/2025
 */
package org.myfintech.payment.util.jaxb

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import jakarta.xml.bind.JAXBContext
import jakarta.xml.bind.Marshaller
import jakarta.xml.bind.Unmarshaller
import org.apache.commons.pool2.BasePooledObjectFactory
import org.apache.commons.pool2.PooledObject
import org.apache.commons.pool2.impl.DefaultPooledObject
import org.apache.commons.pool2.impl.GenericObjectPool
import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

/**
 * Pool manager for JAXBContext instances to improve performance. JAXBContext
 * creation is expensive, so we pool and reuse instances.
 */
@Component
class JAXBContextPool {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(JAXBContextPool::class.java)
        private const val DEFAULT_MAX_TOTAL = 20
        private const val DEFAULT_MAX_IDLE = 10
        private const val DEFAULT_MIN_IDLE = 2
        private val DEFAULT_MAX_WAIT: Duration = Duration.ofSeconds(5)
    }

    private val contextCache = ConcurrentHashMap<Class<*>, JAXBContext>()
    private val unmarshallerPools = ConcurrentHashMap<Class<*>, GenericObjectPool<Unmarshaller>>()
    private val marshallerPools = ConcurrentHashMap<Class<*>, GenericObjectPool<Marshaller>>()

    @PostConstruct
    fun init() {
        log.info("Initializing JAXB Context Pool")
    }

    @PreDestroy
    fun destroy() {
        log.info("Destroying JAXB Context Pool")
        unmarshallerPools.values.forEach { pool ->
            try {
                pool.close()
            } catch (e: Exception) {
                log.error("Error closing unmarshaller pool", e)
            }
        }
        marshallerPools.values.forEach { pool ->
            try {
                pool.close()
            } catch (e: Exception) {
                log.error("Error closing marshaller pool", e)
            }
        }
    }

    fun borrowUnmarshaller(clazz: Class<*>): Unmarshaller {
        val pool = unmarshallerPools.computeIfAbsent(clazz) { createUnmarshallerPool(it) }
        return pool.borrowObject()
    }

    fun returnUnmarshaller(clazz: Class<*>, unmarshaller: Unmarshaller?) {
        unmarshaller?.let {
            try {
                unmarshallerPools[clazz]?.returnObject(it)
            } catch (e: Exception) {
                log.error("Error returning unmarshaller to pool", e)
            }
        }
    }

    fun borrowMarshaller(clazz: Class<*>): Marshaller {
        val pool = marshallerPools.computeIfAbsent(clazz) { createMarshallerPool(it) }
        return pool.borrowObject()
    }

    fun returnMarshaller(clazz: Class<*>, marshaller: Marshaller?) {
        marshaller?.let {
            try {
                marshallerPools[clazz]?.returnObject(it)
            } catch (e: Exception) {
                log.error("Error returning marshaller to pool", e)
            }
        }
    }

    private fun getOrCreateContext(clazz: Class<*>): JAXBContext {
        return contextCache.computeIfAbsent(clazz) { k ->
            try {
                log.debug("Creating new JAXBContext for class: {}", k.name)
                JAXBContext.newInstance(k)
            } catch (e: Exception) {
                throw RuntimeException("Failed to create JAXBContext for ${k.name}", e)
            }
        }
    }

    private fun createUnmarshallerPool(clazz: Class<*>): GenericObjectPool<Unmarshaller> {
        val config = GenericObjectPoolConfig<Unmarshaller>().apply {
            maxTotal = DEFAULT_MAX_TOTAL
            maxIdle = DEFAULT_MAX_IDLE
            minIdle = DEFAULT_MIN_IDLE
            setMaxWait(DEFAULT_MAX_WAIT)
            setTestOnBorrow(true)
            setTestOnReturn(true)
        }
        return GenericObjectPool(UnmarshallerFactory(clazz), config)
    }

    private fun createMarshallerPool(clazz: Class<*>): GenericObjectPool<Marshaller> {
        val config = GenericObjectPoolConfig<Marshaller>().apply {
            maxTotal = DEFAULT_MAX_TOTAL
            maxIdle = DEFAULT_MAX_IDLE
            minIdle = DEFAULT_MIN_IDLE
            setMaxWait(DEFAULT_MAX_WAIT)
            setTestOnBorrow(true)
            setTestOnReturn(true)
        }
        return GenericObjectPool(MarshallerFactory(clazz), config)
    }

    private inner class UnmarshallerFactory(private val clazz: Class<*>) : BasePooledObjectFactory<Unmarshaller>() {
        override fun create(): Unmarshaller = getOrCreateContext(clazz).createUnmarshaller()

        override fun wrap(unmarshaller: Unmarshaller): PooledObject<Unmarshaller> = DefaultPooledObject(unmarshaller)

        override fun passivateObject(p: PooledObject<Unmarshaller>) {
            p.getObject().apply {
                eventHandler = null
                schema = null
            }
        }

        override fun validateObject(p: PooledObject<Unmarshaller>): Boolean = p.getObject() != null
    }

    private inner class MarshallerFactory(private val clazz: Class<*>) : BasePooledObjectFactory<Marshaller>() {
        override fun create(): Marshaller {
            val context = getOrCreateContext(clazz)
            return context.createMarshaller().apply {
                setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
            }
        }

        override fun wrap(marshaller: Marshaller): PooledObject<Marshaller> = DefaultPooledObject(marshaller)

        override fun passivateObject(p: PooledObject<Marshaller>) {
            p.getObject().apply {
                eventHandler = null
                schema = null
                setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
            }
        }

        override fun validateObject(p: PooledObject<Marshaller>): Boolean = p.getObject() != null
    }
}
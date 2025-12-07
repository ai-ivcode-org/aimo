package org.ivcode.beeboop.datastore.session.impl.ehcache

import org.ehcache.CacheManager
import org.ehcache.config.builders.CacheManagerBuilder
import org.ehcache.config.builders.ResourcePoolsBuilder
import org.ivcode.beeboop.datastore.session.PROPERTY_NAME_SESSION_TYPE
import org.ivcode.beeboop.datastore.session.Session
import org.ivcode.beeboop.datastore.session.SessionFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(name = [PROPERTY_NAME_SESSION_TYPE], havingValue = "ehcache", matchIfMissing = true)
class EhcacheSessionFactory(
    private val cacheManager: CacheManager = CacheManagerBuilder.newCacheManagerBuilder().build()
): SessionFactory {

    override fun <T> createCache(name: String, type: Class<T>): Session<T> {
        val ehcache = cacheManager.getCache(name, String::class.java, type)
            ?: cacheManager.createCache(name, org.ehcache.config.builders.CacheConfigurationBuilder
                .newCacheConfigurationBuilder(
                    String::class.java,
                    type,
                    ResourcePoolsBuilder.heap(1000)
                )
            )

        return EhcacheSession(ehcache)
    }
}
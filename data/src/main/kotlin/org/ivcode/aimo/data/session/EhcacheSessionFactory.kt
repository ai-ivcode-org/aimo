package org.ivcode.aimo.data.session

import org.ehcache.CacheManager
import org.ehcache.config.builders.CacheConfigurationBuilder
import org.ehcache.config.builders.CacheManagerBuilder
import org.ehcache.config.builders.ExpiryPolicyBuilder
import org.ehcache.config.builders.ResourcePoolsBuilder
import org.ivcode.common.data.session.PROPERTY_NAME_SESSION_TYPE
import org.ivcode.common.data.session.Session
import org.ivcode.common.data.session.SessionFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.concurrent.TimeUnit

@Component
@ConditionalOnProperty(name = [PROPERTY_NAME_SESSION_TYPE], havingValue = "ehcache", matchIfMissing = true)
class EhcacheSessionFactory(
    private val cacheManager: CacheManager = CacheManagerBuilder.newCacheManagerBuilder().build(true)
): SessionFactory {

    override fun <T> createCache (
        name: String,
        type: Class<T>,
        ttl: Long,
        unit: TimeUnit
    ): Session<T> {
        val ehcache = cacheManager.getCache(name, String::class.java, type)
            ?: cacheManager.createCache(name, CacheConfigurationBuilder
                .newCacheConfigurationBuilder(
                    String::class.java,
                    type,
                    ResourcePoolsBuilder.heap(1000)
                ).withExpiry(
                    ExpiryPolicyBuilder.timeToLiveExpiration(
                        Duration.ofMillis(unit.toMillis(ttl))
                    )
                ).build())

        return EhcacheSession(ehcache)
    }
}
